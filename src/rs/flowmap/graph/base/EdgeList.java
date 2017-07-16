package rs.flowmap.graph.base;

import java.util.Collection;
import java.util.Vector;

/**
 * Syntactic sougar...
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class EdgeList extends Vector<Edge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3072350712161495196L;

	/**
	 * Creates a new object from the {@see EdgeList} class.
	 */
	public EdgeList() {
	}

	/**
	 * Creates a new object from the {@see EdgeList} class.
	 * @param initialCapacity
	 */
	public EdgeList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new object from the {@see EdgeList} class.
	 * @param c
	 */
	public EdgeList(Collection<? extends Edge> c) {
		super(c);
	}

	/**
	 * Creates a new object from the {@see EdgeList} class.
	 * @param initialCapacity
	 * @param capacityIncrement
	 */
	public EdgeList(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

}
