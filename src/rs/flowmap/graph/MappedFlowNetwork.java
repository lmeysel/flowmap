package rs.flowmap.graph;

import java.util.HashMap;

import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 18.07.2017
 */
public class MappedFlowNetwork extends FlowNetwork {
	private HashMap<Vertex, Integer> v2i = new HashMap<>();
	private HashMap<Integer, Vertex> i2v = new HashMap<>();

	/**
	 * Creates a new object from the {@see MappedFlowNetwork} class.
	 * 
	 * @param V
	 */
	public MappedFlowNetwork(int V) {
		super(V);
	}

	/**
	 * Creates a new object from the {@see MappedFlowNetwork} class.
	 * 
	 * @param in
	 */
	public MappedFlowNetwork(In in) {
		super(in);
	}

	/**
	 * Creates a new object from the {@see MappedFlowNetwork} class.
	 * 
	 * @param V
	 * @param E
	 */
	public MappedFlowNetwork(int V, int E) {
		super(V, E);
	}

	/**
	 * Adds a mapping for a vertex (vertex to internal ID)
	 * 
	 * @param vertex
	 *           The vertex to add and map.
	 * @return The internal ID set for this vertex.
	 */
	public int addVertex(Vertex vertex) {
		if (v2i.containsKey(vertex))
			return v2i.get(vertex);
		else {
			int id = v2i.size();
			v2i.put(vertex, id);
			i2v.put(id, vertex);
			return id;
		}
	}

	/**
	 * Gets the ID of a given vertex.
	 * 
	 * @param vertex
	 *           the Vertex whose ID is required.
	 */
	public int getVertexID(Vertex vertex) {
		return v2i.get(vertex);
	}

	/**
	 * Gets a Vertex with the specified internal ID.
	 * 
	 * @param id
	 *           The internal id of the desired vertex.
	 */
	public Vertex getVertexByID(int id) {
		return i2v.get(id);
	}
}
