package rs.flowmap.graph;

import rs.flowmap.graph.base.EdgeList;
import rs.flowmap.graph.base.VertexList;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Vertex implements rs.flowmap.graph.base.Vertex {
	private static int idCnt = 0;

	protected EdgeList inbound, outbound;
	protected VertexList successors, predecessors;
	protected int height, label;
	private int id;

	/**
	 * Creates a new object from the {@see Vertex} class.
	 */
	public Vertex() {
		this.inbound = new EdgeList();
		this.outbound = new EdgeList();
		this.predecessors = new VertexList();
		this.successors = new VertexList();
		this.id = idCnt++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EdgeList getOutbounds() {
		return this.outbound;
	}

	/**
	 * Adds an outbound edge to this node. The edge's target will automatically be added to
	 * successors list.
	 * 
	 * @param e
	 *           The outbound edge to add.
	 */
	public void addOutbound(Edge e) {
		this.successors.add(e.getTarget());
		this.outbound.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EdgeList getInbounds() {
		return this.inbound;
	}

	/**
	 * Adds an inbound edge to this node. The edge's source will automatically be added to
	 * predecessors list.
	 * 
	 * @param e
	 *           The inbound edge to add.
	 */
	public void addInbound(Edge e) {
		this.predecessors.add(e.getSource());
		this.inbound.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VertexList getSuccessors() {
		return this.successors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VertexList getPredecessors() {
		return this.predecessors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLabel(int label) {
		this.label = label;
	}

}
