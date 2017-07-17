package rs.flowmap.graph.base;

import java.util.Collection;
import java.util.Vector;

/**
 * Syntactic sougar....
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class VertexList extends Vector<Vertex> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7858446161006409305L;

	/**
	 * Creates a new object from the {@see VertexList} class.
	 */
	public VertexList() {
	}

	/**
	 * Creates a new object from the {@see VertexList} class.
	 * @param arg0
	 */
	public VertexList(int arg0) {
		super(arg0);
	}

	/**
	 * Creates a new object from the {@see VertexList} class.
	 * @param arg0
	 */
	public VertexList(Collection<? extends Vertex> arg0) {
		super(arg0);
	}

	/**
	 * Creates a new object from the {@see VertexList} class.
	 * @param arg0
	 * @param arg1
	 */
	public VertexList(int arg0, int arg1) {
		super(arg0, arg1);
	}

}
