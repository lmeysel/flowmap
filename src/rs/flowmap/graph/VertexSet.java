package rs.flowmap.graph;

import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 18.07.2017
 */
public class VertexSet extends HashSet<Vertex> {

	/**
	 * Creates a new object from the {@see VertexSet} class.
	 */
	public VertexSet() {
	}

	/**
	 * Creates a new object from the {@see VertexSet} class.
	 * @param c
	 */
	public VertexSet(Collection<? extends Vertex> c) {
		super(c);
	}

	/**
	 * Creates a new object from the {@see VertexSet} class.
	 * @param initialCapacity
	 */
	public VertexSet(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new object from the {@see VertexSet} class.
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public VertexSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

}
