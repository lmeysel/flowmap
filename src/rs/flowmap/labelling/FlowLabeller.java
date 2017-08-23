package rs.flowmap.labelling;

import java.util.HashMap;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;
import rs.flowmap.graph.Edge;
import rs.flowmap.graph.EdgeList;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.MappedFlowNetwork;
import rs.flowmap.graph.Thingmabob;
import rs.flowmap.graph.Vertex;
import rs.flowmap.graph.VertexList;
import rs.flowmap.graph.VertexSet;
import rs.flowmap.test.Util;

/**
 * Provides functionality to label a graph using maximum flow and looking for k-feasable cuts. This
 * labeller will set the label-property of the vertices in the graph.
 * 
 * @author Ludwig Meysel
 * 
 * @version 17.07.2017
 */
public class FlowLabeller {

	/**
	 * Labels the given graph using max flow and looking for k-feasable cuts.
	 * 
	 * @param graph
	 *           The graph to label.
	 * @param k
	 *           The maximum flow.
	 */
	public static Thingmabob label(Graph graph, int k) {
		// assumes the vertices are already sorted by height (done by HeightLabeller)
		HashMap<Vertex, VertexSet> clusters = new HashMap<>();
		VertexList stage = new VertexList(); // filled with POs in labelling-phase
		graph.getVertices().stream().sorted((Vertex v1, Vertex v2) -> v1.getHeight() - v2.getHeight()).forEach((Vertex v) -> {
			if (v.getPredecessors().size() == 0) {
				v.getSuccessors().forEach((Vertex s) -> s.setLabelAtLeast(1));
				return; // for primary inputs no FordFulkerson is necessary
			}

			MappedFlowNetwork fn = getFlowNet(v);

			FordFulkerson ff = new FordFulkerson(fn, fn.V() - 2, fn.V() - 1);

			VertexSet c = null;
			int delta = fn.getOffset();
			if (ff.value() <= k) {
				c = new VertexSet();
				v.setLabelAtLeast(v.getLabel());

				for (int i = 0; i < delta; i++)
					if (ff.inCut(i) && !ff.inCut(i + delta)) {
						c.add(fn.getVertexByID(i));
					}
			} else {
				v.setLabelAtLeast(v.getLabel() + 1);
				c = new VertexSet(v.getPredecessors());
			}
			v.getSuccessors().forEach((Vertex s) -> s.setLabelAtLeast(v.getLabel()));
			clusters.put(v, c);

			if (v.getOutbounds().size() == 0) {
				stage.add(v);
			}

			// test
			if (v.getId() == 17)
				Util.writeDOT("fn-debug.txt", ff, fn);
		});

		Thingmabob ret = new Thingmabob(new VertexList(stage), clusters);

		Graph g = new Graph();
		EdgeList edges = g.getEdges();
		VertexList vertices = g.getVertices();
		HashMap<Vertex, Vertex> vtxMap = new HashMap<>(); // maps vertices within original and LUT-packed graph
		VertexSet packed = new VertexSet();
		while (stage.size() != 0) {
			Vertex v = stage.remove(0), vn = null;
			if (packed.contains(v))
				continue;

			if (!vtxMap.containsKey(v)) {
				vn = new Vertex(v.getHorrible());
				vertices.add(vn);
				vtxMap.put(v, vn);
			} else
				vn = vtxMap.get(v);

			String debug = "";

			for (Vertex p : clusters.get(v)) {
				if (p.getInbounds().size() != 0) // Do not stage PIs
					stage.add(p);

				debug += ", " + p.getId();
				Vertex tmp = null;
				if (!vtxMap.containsKey(p)) {
					tmp = new Vertex(p.getHorrible());
					vertices.add(tmp);
					vtxMap.put(p, tmp);
				} else
					tmp = vtxMap.get(p);

				edges.add(new Edge(tmp, vn));
			}
			System.out.println(vn.getId() + ": " + v.getId() + " <= {" + (debug.length() > 1 ? debug.substring(1) : "") + " }");
			packed.add(v);
		}
		g.writeDOT("packed.txt");

		return ret;
	}

	/**
	 * Gets the {@see MappedFlowNetwork} for the given Vertex.
	 * 
	 * @param v
	 *           The vertex for which the flow net is required.
	 * @return A FlowNetwork representing the subgraph for the max flow calculation.
	 */
	private static MappedFlowNetwork getFlowNet(Vertex v) {
		HashMap<Vertex, Integer> vs1 = new HashMap<>();
		VertexSet vs2 = new VertexSet();
		vs2.add(v);
		int lbl = v.getLabel(); // current label

		// separate predecessors (lower or same label) 
		v.getAllPredecessors().forEach((Vertex p) -> {
			if (p.getLabel() < lbl)
				vs1.put(p, vs1.size());
			else
				vs2.add(p);
		});

		// FlowNet consists of all (non-collabsible nodes * 2) + source + destination 
		MappedFlowNetwork ret = new MappedFlowNetwork(vs1.size() * 2 + 2);

		// Setup flow network
		// Alignment: [vs.size(): "normal nodes" | vs.size(): "cloned nodes" | 1: source | 1: destination]
		int delta = vs1.size(), src = delta + delta, dst = src + 1;
		vs1.forEach((Vertex vertex, Integer index) -> {
			ret.addVertex(vertex);
			ret.addEdge(new FlowEdge(index, delta + index, 1));
			VertexList lst = vertex.getPredecessors();
			if (lst.size() == 0)
				ret.addEdge(new FlowEdge(src, index, 100));
			else
				lst.forEach((Vertex p) -> ret.addEdge(new FlowEdge(vs1.get(p) + delta, index, 100)));

			// add one edge to dst when at least one successor is in vs2
			for (Vertex s : vertex.getSuccessors()) {
				if (vs2.contains(s)) {
					ret.addEdge(new FlowEdge(delta + index, dst, 100));
					break;
				}
			}
		});

		return ret;
	}

}
