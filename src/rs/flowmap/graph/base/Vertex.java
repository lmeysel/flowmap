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
}
