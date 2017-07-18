package rs.flowmap.graph;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Edge {
	protected Vertex source, target;
	protected int weight = 100; // initial value large enaugh for k << 100

	/**
	 * Creates a new object from the {@see Edge} class. <b>Source and target vertices are notified
	 * automatically about this edge!</b>
	 */
	public Edge(Vertex source, Vertex target) {
		this.source = source;
		this.target = target;

		this.source.addOutbound(this);
		this.target.addInbound(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public Vertex getSource() {
		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	public Vertex getTarget() {
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setWeight(int weight) {
		this.weight = weight;

	}

}
