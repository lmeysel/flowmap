package rs.flowmap.labelling;

import java.util.HashMap;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.MappedFlowNetwork;
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
	public static void label(Graph graph, int k) {
		// assumes the vertexes are already sorted by height (done by HeightLabeller)
		HashMap<Vertex, VertexSet> cluster = new HashMap<>();
		VertexSet po = new VertexSet(); // primary outputs
		graph.getVertices().stream().forEach((Vertex v) -> {
			if (v.getPredecessors().size() == 0) {
				v.getSuccessors().forEach((Vertex s) -> s.setLabelAtLeast(1));
				return; // for primary inputs no FordFulkerson is necessary
			}

			MappedFlowNetwork fn = getFlowNet(v);

			FordFulkerson ff = new FordFulkerson(fn, fn.V() - 2, fn.V() - 1);
			int lbl = ff.value() <= k ? v.getLabel() : v.getLabel() + 1;
			v.setLabelAtLeast(lbl);
			v.getSuccessors().forEach((Vertex s) -> s.setLabelAtLeast(lbl));

			if (v.getOutbounds().size() == 0)
				po.add(v);

			//if (v.getId() == 16)
			//	Util.writeDOT("fn-debug.txt", ff, fn);
		});

		VertexSet stage = po;
		while (stage.size() != 0) {
			VertexSet next = new VertexSet();
			
		}
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
