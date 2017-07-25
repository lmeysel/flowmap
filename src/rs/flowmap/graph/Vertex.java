package rs.flowmap.graph;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Vertex {
	private static int idCnt = 0;

	protected EdgeList inbound, outbound;
	protected VertexList successors, predecessors;
	protected VertexSet allPredecessors;
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
	 * Gets the topological height of this node.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the identifier of this node.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the inbound edges.
	 */
	public EdgeList getInbounds() {
		return this.inbound;
	}

	/**
	 * Gets the flow label of this node.
	 */
	public int getLabel() {
		return label;
	}

	/**
	 * Gets the outbound edges.
	 */
	public EdgeList getOutbounds() {
		return this.outbound;
	}

	/**
	 * Gets the predecessors of this vertex.
	 */
	public VertexList getPredecessors() {
		return this.predecessors;
	}

	/**
	 * Gets <b>all</b> (direct and indirect) predecessor nodes of this node. The set of all
	 * predecessors will be created at first time of this method call.
	 */
	public VertexSet getAllPredecessors() {
		if (allPredecessors == null) {
			allPredecessors = new VertexSet();
			predecessors.forEach((Vertex v) -> {
				allPredecessors.addAll(v.getAllPredecessors());
				allPredecessors.add(v);
			});
		}
		return allPredecessors;
	}

	/**
	 * Gets the sucessors of this vertex.
	 */
	public VertexList getSuccessors() {
		return this.successors;
	}

	/**
	 * Sets the topological height of this node.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets the flow label of this node.
	 */
	public void setLabel(int label) {
		this.label = label;
	}

	/**
	 * Sets the label of the vertex when it is larger than the vertex' current label is.
	 * 
	 * @param label
	 *           The new label of the vertex.
	 */
	public void setLabelAtLeast(int label) {
		this.label = Math.max(label, this.label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "{ ID: " + id + ", height: " + height + ", label: " + label + " }";
	}

	/**
	 * Removes all edges from and to this node from the sucessor and predecessors.
	 */
	public void destroy() {
		inbound.forEach((Edge e) -> e.source.outbound.remove(e));
		outbound.forEach((Edge e) -> e.target.inbound.remove(e));
		successors.forEach((Vertex v) -> v.predecessors.remove(this));
		predecessors.forEach((Vertex v) -> v.successors.remove(this));
	}

}
