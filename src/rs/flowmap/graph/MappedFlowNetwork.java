package rs.flowmap.graph;

import java.util.HashMap;

import edu.princeton.cs.algs4.FlowNetwork;

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
	 *           The number of vertices. Assumes the last two nodes are source and destination nodes,
	 *           the other ones are the split network between. Therefore the minimum number of
	 *           vertices must be an even number and at least 4. The Positions of the nodes should be
	 *           aligned as following:<br/>
	 *           [n: original nodes | n: split nodes | 1: source | 1: destination], whereas n is the
	 *           number of the nodes in the original network between source and target.
	 */
	public MappedFlowNetwork(int V) {
		super(V);
		if (V % 2 != 0 || V < 4)
			throw new IllegalArgumentException("Parameter V must be an even number and greater than 4.");
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
		if (v2i.containsKey(vertex))
			return v2i.get(vertex);
		return -1;
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

	/**
	 * Gets the offset between original nodes and split nodes (i.e. <code>n</code> as described in
	 * ctor)
	 * 
	 * @return The number of original nodes in the graph, <code>V()/2-1</code>
	 */
	public int getOffset() {
		return (V() >> 1) - 1;
	}
}
