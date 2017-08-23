package rs.flowmap.test;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import rs.flowmap.graph.EdgeList;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.VertexList;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 18.07.2017
 */
public class SimpleGraph extends Graph {

	/**
	 * Creates a new object from the {@see SimpleGraph} class.
	 */
	public SimpleGraph() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FlowNetwork net = new FlowNetwork(4);
		net.addEdge(new FlowEdge(0, 1, 4));
		net.addEdge(new FlowEdge(0, 2, 2));
		net.addEdge(new FlowEdge(1, 2, 3));
		net.addEdge(new FlowEdge(2, 3, 6));
		net.addEdge(new FlowEdge(1, 3, 1));

		FordFulkerson ff = new FordFulkerson(net, 0, 3);
		System.out.println(ff.value());
	}

}
