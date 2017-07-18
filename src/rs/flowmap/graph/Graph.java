package rs.flowmap.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
	 * {@inheritDoc}
	 */
	public EdgeList getEdges() {
		return edges;
	}

	/**
	 * {@inheritDoc}
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

			// define vertices
			for (Vertex v : vertices) {
				wtr.write("n" + v.getId() + " [label=<" + v.getHeight() + ">]" + br);
			}

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
}
