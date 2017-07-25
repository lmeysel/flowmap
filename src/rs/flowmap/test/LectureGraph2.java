package rs.flowmap.test;

import rs.flowmap.graph.Edge;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.Vertex;
import rs.flowmap.graph.VertexList;
import rs.flowmap.labelling.FlowLabeller;
import rs.flowmap.labelling.HeightLabeller;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 18.07.2017
 */
public class LectureGraph2 extends Graph {
	/**
	 * Creates a new object from the {@see LectureGraph2} class.
	 */
	public LectureGraph2() {
		VertexList v = vertices;
		for (int i = 0; i < 17; i++)
			v.add(new Vertex());

		edges.add(new Edge(v.get(10), v.get(9)));
		edges.add(new Edge(v.get(10), v.get(2)));
		edges.add(new Edge(v.get(0), v.get(9)));
		edges.add(new Edge(v.get(3), v.get(11)));
		edges.add(new Edge(v.get(2), v.get(16)));
		edges.add(new Edge(v.get(9), v.get(1)));
		edges.add(new Edge(v.get(2), v.get(1)));
		edges.add(new Edge(v.get(14), v.get(0)));
		edges.add(new Edge(v.get(6), v.get(15)));
		edges.add(new Edge(v.get(12), v.get(6)));
		edges.add(new Edge(v.get(7), v.get(5)));
		edges.add(new Edge(v.get(11), v.get(4)));
		edges.add(new Edge(v.get(5), v.get(4)));
		edges.add(new Edge(v.get(11), v.get(16)));
		edges.add(new Edge(v.get(8), v.get(3)));
		edges.add(new Edge(v.get(6), v.get(5)));
		edges.add(new Edge(v.get(13), v.get(0)));
		edges.add(new Edge(v.get(14), v.get(3)));
		edges.add(new Edge(v.get(13), v.get(2)));
		edges.add(new Edge(v.get(15), v.get(11)));
		edges.add(new Edge(v.get(13), v.get(15)));
		edges.add(new Edge(v.get(7), v.get(6)));
	}

	public static void main(String[] args) {
		Graph graph = new LectureGraph2();
		HeightLabeller.label(graph);
		FlowLabeller.label(graph, 3);
		graph.writeDOT("graph-debug.txt");
		System.out.println("LectureGraph2 test done. See graph-debug.txt for results.");

	}
}
