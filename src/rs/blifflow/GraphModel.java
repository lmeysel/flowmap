package rs.blifflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;

import rs.binfunction.BinFunction;
import rs.blif.BLIF;
import rs.blif.Latch;
import rs.blif.Model;
import rs.blif.SubCircuit;
import rs.flowmap.graph.Edge;
import rs.flowmap.graph.EdgeList;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.Vertex;
import rs.flowmap.graph.VertexList;
import rs.graphnode.GraphNode;

/**
 * The class GraphModel adds several functions for Graph-Support to the Model-Class.
 * 
 * @author Mitja Stachowiak
 *
 */
public class GraphModel extends Model {

	public GraphModel(String name, BLIF parent) throws Exception {
		super(name, parent);
	}

	/**
	 * splitts all BinFunctions of this model into 2-input-LUTs
	 */
	public void decompose() {
		for (int i = this.functions.size() - 1; i >= 0; i--)
			((GraphFunction)this.functions.get(i)).decompose();
	}

	public void printNetwork() {
		Iterator<GraphNode> it = this.iterateGraphNodes();
		while (it.hasNext()) {
			GraphNode n = it.next();
			String s = n.name() + " (";
			if (n instanceof BinFunction)
				s += "function";
			else if (n instanceof Latch)
				s += "latch";
			else if (n instanceof SubCircuit.ToSubcircuit)
				s += "to '" + ((SubCircuit.ToSubcircuit)n).subCktLink.subModel.name() + ">" + ((SubCircuit.ToSubcircuit)n).subInput.name() + "'";
			else if (n instanceof SubCircuit.FromSubcircuit)
				s += "from '" + ((SubCircuit.FromSubcircuit)n).subCktLink.subModel.name() + ">" + ((SubCircuit.FromSubcircuit)n).subOutput.name() + "'";
			else if (n instanceof GraphNode.InputNode)
				s += "input";
			else if (n instanceof GraphNode.OutputNode)
				s += "output";
			s += ")";
			System.out.println(s);
			if (n.out() != null) {
				Iterator<GraphNode> ito = n.out().iterator();
				while (ito.hasNext()) {
					System.out.println("     --> " + ito.next().name());
				}
			}
		}
	}

	/**
	 * Gets the Graph in the right model.
	 * 
	 * @return A graph object containing the right model.
	 */
	public Graph getRightModel() {
		Graph ret = new Graph();
		EdgeList edges = ret.getEdges();
		VertexList vertices = ret.getVertices();

		final HashMap<GraphNode, Vertex> map = new HashMap<>();
		Function<GraphNode, Vertex> get = (GraphNode g) -> {
			if (map.containsKey(g))
				return map.get(g);
			else {
				Vertex r = new Vertex(g);
				map.put(g, r);
				return r;
			}
		};

		Iterator<GraphNode> it = this.iterateGraphNodes();
		while (it.hasNext()) {
			GraphNode nde = it.next();
			Vertex v = get.apply(nde);
			vertices.add(v);
			if (nde.in() != null)
				for (GraphNode gn : nde.in())
					edges.add(new Edge(get.apply(gn), v));
		}
		return ret;
	}

	/**
	 * Overrides Model.ModelCreator to enable the BLIF-interpreter creating GraphModels instead of
	 * Models
	 * 
	 * @author Mitja Stachowiak
	 *
	 */
	public static class GraphModelCreator extends ModelCreator {
		@Override
		public Model newModel(String name, BLIF parent) {
			Model m;
			try {
				m = new GraphModel(name, parent);
			} catch (Exception e) {
				m = parent.models().get(name);
			}
			return m;
		}
	}
}
