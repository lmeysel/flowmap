package rs.flowmap.graph;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Edge implements rs.flowmap.graph.base.Edge {
	protected Vertex source, target;
	protected int weight = 100; // initial value large enaugh for k << 100

	/**
	 * Creates a new object from the {@see Edge} class. <b>Source and target vertices are notified
	 * automatically about this edge!</b>
	 */
	public Edge(rs.flowmap.graph.base.Vertex source, rs.flowmap.graph.base.Vertex target) {
		this.source = (Vertex)source;
		this.target = (Vertex)target;

		this.source.addOutbound(this);
		this.target.addInbound(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vertex getSource() {
		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vertex getTarget() {
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWeight() {
		return weight;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWeight(int weight) {
		this.weight = weight;

	}

}
