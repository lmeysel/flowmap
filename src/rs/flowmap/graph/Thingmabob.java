package rs.flowmap.graph;

import java.util.HashMap;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 23.08.2017
 */
public class Thingmabob {

	private VertexList stage;
	private HashMap<Vertex, VertexSet> cluster;

	/**
	 * Creates a new object from the {@see Thingmabob} class.
	 */
	public Thingmabob(VertexList stage, HashMap<Vertex, VertexSet> cluster) {
		this.stage = stage;
		this.cluster = cluster;
	}

	/**
	 * 
	 * @return
	 */
	public VertexList getStage() {
		return stage;
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<Vertex, VertexSet> getCluster() {
		return cluster;
	}

}
