package rs.flowmap.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Graph {
	protected EdgeList edges;
	protected VertexList vertices;

	/**
	 * Creates a new object from the {@see Graph} class.
	 */
	public Graph() {
		this.edges = new EdgeList();
		this.vertices = new VertexList();
	}

	/**
	 * Gets all edges of the graph.
	 */
	public EdgeList getEdges() {
		return edges;
	}

	/**
	 * Gets all vertices of the graph
	 */
	public VertexList getVertices() {
		return vertices;
	}

	/**
	 * Writes the graph as DOT file for better debugging.
	 */
	public void writeDOT(String filename) {
		try {
			String br = System.getProperty("line.separator");
			BufferedWriter wtr = new BufferedWriter(new FileWriter(filename));

			wtr.write("digraph G {" + br);

			HashMap<Integer, VertexList> level = new HashMap<>();

			// define vertices
			for (Vertex v : vertices) {
				if (!level.containsKey(v.getHeight()))
					level.put(v.getHeight(), new VertexList());
				level.get(v.getHeight()).add(v);
				//wtr.write("n" + v.getId() + " [label=<" + v.getId() + " | " + v.getLabel() + ">]" + br);
				wtr.write("n" + v.getId() + " [label=<" + v.getId() + " | " + (v.getHorrible() != null ? v.getHorrible().name() : "") + ">]" + br);
			}

			// define ranks
			level.forEach((Integer lvl, VertexList lst) -> {
				try {
					wtr.write("{ rank=same; ");
					for (Vertex v : lst)
						wtr.write("n" + v.getId() + "; ");
					wtr.write("}" + br);

				} catch (IOException x) {
					System.err.println(x.getMessage());
				}
			});

			// define edges
			for (Edge e : edges) {
				wtr.write("n" + e.getSource().getId() + " -> n" + e.getTarget().getId() + br);
			}

			wtr.write("}" + br);

			wtr.close();
		} catch (IOException x) {
			System.err.println(x.getMessage());
		}
	}

	/**
	 * Removes a vertex and all in- and outbound edges.
	 */
	public void removeVertex(Vertex v) {
		vertices.remove(v);
		v.inbound.forEach((Edge e) -> edges.remove(e));
		v.outbound.forEach((Edge e) -> edges.remove(e));
		v.destroy();
	}
}
