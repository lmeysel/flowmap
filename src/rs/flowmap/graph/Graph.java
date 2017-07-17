package rs.flowmap.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import rs.flowmap.graph.base.EdgeList;
import rs.flowmap.graph.base.VertexList;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Graph implements rs.flowmap.graph.base.Graph {
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
	@Override
	public EdgeList getEdges() {
		return edges;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
			for (rs.flowmap.graph.base.Vertex vertex : vertices) {
				Vertex v = (Vertex)vertex;
				wtr.write("n" + v.getId() + " [label=<" + v.getHeight() + ">]" + br);
			}

			// define edges
			for (rs.flowmap.graph.base.Edge edge : edges) {
				Edge e = (Edge)edge;
				wtr.write("n" + e.getSource().getId() + " -> n" + e.getTarget().getId() + br);
			}

			wtr.write("}" + br);

			wtr.close();
		} catch (IOException x) {
			System.err.println(x.getMessage());
		}
	}
}
