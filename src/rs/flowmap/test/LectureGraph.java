package rs.flowmap.test;

import rs.flowmap.graph.Edge;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.Vertex;
import rs.flowmap.graph.base.EdgeList;
import rs.flowmap.graph.base.VertexList;
import rs.flowmap.labelling.HeightLabeller;

/**
 * Example Graph as it is depicted in lecture chap. 4, slide 42, inside dashed line.
 * 
 * @author Ludwig Meysel
 * @version 16.07.2017
 */
public class LectureGraph extends Graph {
	public static void main(String[] args) {
		Graph g = new LectureGraph();
		HeightLabeller.label(g);
		
		g.writeDOT("graph-debug.txt");
		System.out.println("LectureGraph test done. See graph-debug.txt for results.");
	}

	/**
	 * Creates a new object from the {@see LectureGraph} class.
	 */
	public LectureGraph() {
		VertexList v = super.vertices;
		EdgeList e = super.edges;

		for (int i = 0; i < 13; i++)
			v.add(new Vertex());

		// shuffled order for harder test
		e.add(new Edge(v.get(0), v.get(5)));
		e.add(new Edge(v.get(1), v.get(5)));
		e.add(new Edge(v.get(5), v.get(8)));
		e.add(new Edge(v.get(6), v.get(8)));
		e.add(new Edge(v.get(8), v.get(9)));
		e.add(new Edge(v.get(7), v.get(11)));
		e.add(new Edge(v.get(10), v.get(11)));
		e.add(new Edge(v.get(3), v.get(7)));
		e.add(new Edge(v.get(4), v.get(7)));
		e.add(new Edge(v.get(7), v.get(9)));
		e.add(new Edge(v.get(1), v.get(6)));
		e.add(new Edge(v.get(2), v.get(6)));
		e.add(new Edge(v.get(9), v.get(10)));
		e.add(new Edge(v.get(11), v.get(12)));
	}
}
