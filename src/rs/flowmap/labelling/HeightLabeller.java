package rs.flowmap.labelling;

import java.util.HashSet;
import java.util.stream.Collectors;

import rs.flowmap.graph.Graph;
import rs.flowmap.graph.Vertex;
import rs.flowmap.graph.VertexList;

/**
 * Helper class for labelling the height in a graph.
 * 
 * @author Ludwig Meysel
 * @version 16.07.2017
 */
@Deprecated
public class HeightLabeller {

	public static void label(Graph graph) {
		VertexList v = graph.getVertices();
		HashSet<Vertex> stage = v.stream().filter((Vertex vtx) -> vtx.getInbounds().size() == 0).collect(Collectors.toCollection(HashSet::new));
		while (stage.size() != 0) {
			// java forces me to finalize this variable and therefore it sucks.
			final HashSet<Vertex> javaSucks = new HashSet<Vertex>();
			stage.forEach((Vertex vtx) -> {
				int h = vtx.getHeight() + 1;
				vtx.getSuccessors().forEach((Vertex suc) -> {
					javaSucks.add(suc);
					suc.setHeight(Math.max(h, suc.getHeight()));
				});
			});
			stage = javaSucks;
		}

		v.sort((Vertex v1, Vertex v2) -> v1.getHeight() - v2.getHeight());
	}
}
