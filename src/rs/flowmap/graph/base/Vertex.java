package rs.flowmap.graph.base;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public interface Vertex {
	public EdgeList getOutbounds();
	public EdgeList getInbounds();
	public VertexList getSuccessors();
	public VertexList getPredecessors();
	
	/**
	 * Gets an identifier which must be unique within the graph.
	 */
	public int getId();
	
	/**
	 * Gets the height of the node starting with 0 for the primary inputs. 
	 */
	public int getHeight();
	/**
	 * Sets the height of the node.
	 */
	public void setHeight(int height);
	/**
	 * Gets the label for the LUT packing.
	 */
	public int getLabel();
	/**
	 * Sets the label for the LUT packing.
	 */
	public void setLabel(int label);
}
