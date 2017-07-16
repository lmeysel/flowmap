package rs.flowmap.graph.base;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public interface Edge {
	public Vertex getSource();
	public Vertex getTarget();
	
	/**
	 * Gets the edge's weight.
	 */
	public int getWeight();
	/**
	 * Sets the edge's weight.
	 */
	public void setWeight(int weight);
}
