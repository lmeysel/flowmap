package rs.blifflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;

import rs.binfunction.BinFunction;
import rs.binfunction.Cube;
import rs.binfunction.Set;
import rs.blif.BLIF;
import rs.blif.Latch;
import rs.blif.Model;
import rs.blif.SubCircuit;
import rs.flowmap.graph.Edge;
import rs.flowmap.graph.EdgeList;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.Thingmabob;
import rs.flowmap.graph.Vertex;
import rs.flowmap.graph.VertexList;
import rs.graphnode.GraphNode;
import rs.graphnode.GraphNode.OutputNode;

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
	 * splitts all BinFunctions of this model into 1 and 2-input-LUTs
	 */
	public void decompose() {
		for (int i = this.functions.size() - 1; i >= 0; i--)
			((GraphFunction)this.functions.get(i)).decompose();
	}

	/**
	 * Prints the Blif-Network.
	 */
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
	 * composes the logic of functions in current Graph according to a given, composed graph and adds the composed functions to this model
	 * @param out
	 * the outbound-vertices of the given graph
	 */
	public void composeFrunctionsFromGraph(Thingmabob cluster) {
	 HashMap<Vertex, GraphNode> composedList = new HashMap<Vertex, GraphNode>(); // list of all Vertices, that already have a composed GraphFunction
     VertexList out = cluster.getStage();
	 Iterator<Vertex> it = out.iterator();
     while (it.hasNext()) { // iterate all outbound Vertices of the composed Graph
      Vertex v = it.next();
      composeVertex(v, composedList, cluster);
     }
	}
	
	/**
	 * Recursively composes the subtree before v.
	 * @param v
	 * The Vertex, that's predecessor-subtree should be composed.
	 * @param composedList
	 * A list, that stores for each processed Vertex it's related, composed function
	 * @param cluster
	 * @return
	 * The composed Node, already inserted into this Model's functions; can be null in some seldom cases (Virtual OutputNode is target of composition but has no sub-tree to be composed).
     * @TODO
     * Currently, this function don't receives the real composition-target vertices in all cases but the ones, that
     * are linked with i.e. the OutputNodes. From there, the function must find the output GraphFunction of Vertex,
     * which is not possible, if the Vertex is a latch, which can be an input as well. This function then will
     * return this latch, assuming it to be an input and don't computing any composition.
	 */
	private GraphNode composeVertex (Vertex v, HashMap<Vertex, GraphNode> composedList, Thingmabob cluster) {
	 // find the output GraphFunction of Vertex
	 GraphFunction f;
	 if (v.getHorrible() instanceof GraphFunction) f = (GraphFunction)v.getHorrible();
	 else if (v.getHorrible() instanceof OutputNode) {
	  f = (GraphFunction)v.getHorrible().in().get(0);
	  // System.out.println("Warning: Always provide the composeVertex-Function with the network's outbound function-nodes, not with the OutputNodes!");
	 } else {
	  composedList.put(v, v.getHorrible());
	  return v.getHorrible();
	 }
     // get and compose Vertex' inputs
	 Object[] vin = cluster.getCluster().get(v).toArray();
	 ArrayList<GraphNode> in = new ArrayList<GraphNode>(vin.length);
	 for (int i = 0; i < vin.length; i++) {
	  GraphNode nn = composedList.get(vin[i]);
	  if (nn == null) nn = composeVertex((Vertex)vin[i], composedList, cluster);
      in.add(nn);
	 }
	 // create target merge function and move all links to non-function nodes to the new node
	 GraphFunction mergeFkt = new GraphFunction(in, f.name(), this);
	 this.functions.add(mergeFkt);
	 Object[] fout = f.out().toArray();
	 for (int i = 0; i < fout.length; i++) if (!(fout[i] instanceof BinFunction)) {
	  int j = ((GraphNode)fout[i]).in().indexOf(f);
	  if (j == -1) throw new RuntimeException("Linkeage Error: f has an output which doesn't have f as an input!");
	  ((GraphNode)fout[i]).in().set(j, mergeFkt);
	 }
	 // compose logic
	 HashMap<GraphNode, Set> composedSets = new HashMap<GraphNode, Set>();
	 for (int i = 0; i < mergeFkt.numInputs(); i++) {
	  Set sin = new Set(mergeFkt.numInputs());
	  Cube c = new Cube(mergeFkt.numInputs()); // init with don't care
	  c.setVar(i, BinFunction.ONE);
	  sin.add(c); // sin is now a set, that represents only the i-th input of mergeFkt
	  GraphNode fp = ((Vertex)vin[i]).getHorrible();
	  if (fp == f) return null; // Output function and it's virtual OutputNode are still in the composed tree together!
	  composedSets.put(fp, sin); // link the uncomposed function (that is in the decomposed tree) with the related on-set
	 }
     Set on = composeSet(f, mergeFkt.in().size(), composedSets);
     mergeFkt.on().addAll(on);
	 return mergeFkt;
	}
	
	/**
     *
	 * @param fkt
	 * The decomposed function, that should be replaced by the composed one.
	 * @param nin
	 * Numer of inputs of the composed function
	 * @param composedSets
	 * A list, that stores for each decomposed function the related, composed on-set. Put in the corner of the subtree for primary call.
	 * @return
	 */
	private Set composeSet (GraphFunction fkt, int nin, HashMap<GraphNode, Set> composedSets) {
	 Set[] sin = new Set[fkt.numInputs()];
	 Set[] sinv = new Set[fkt.numInputs()]; // complemented sets (compute on demand)
	 // get all input sets, compose them, if not available
	 for (int i = 0; i < fkt.numInputs(); i++) {
	  sin[i] = composedSets.get(fkt.in().get(i));
	  if (sin[i] == null) {
	   if (!(fkt.in().get(i) instanceof GraphFunction)) throw new RuntimeException("Can only compose Functions! Add all inputs to composedSets before calling composeSet.");
	   sin[i] = composeSet((GraphFunction)fkt.in().get(i), nin, composedSets);
	  }
	 }
	 // do the and-conjunction (not-complementation included)
	 Set[] sand = new Set[fkt.on().size()];
	 for (int i = 0; i < fkt.on().size(); i++) {
	  sand[i] = new Set(nin);
	  sand[i].add(new Cube(nin)); // sand[i] here is a tautology (always 1)
      Cube c = fkt.on().get(i);	  
	  for (int j = 0; j < c.width; j++) {
	   if (c.getVar(j) == BinFunction.DC) continue;
	   Set s;
	   if (c.getVar(j) == BinFunction.ONE) s = sin[j];
	   else {
	    if (sinv[j] == null) sinv[j] = sin[j].not(); // compute complemented set on demand
	    s = sinv[j];
	   }
	   sand[i] = sand[i].and(s); // compute the and-conjunction of sand[i] and s
	  }
	 }
     // do the or-disjunction
	 Set comp = new Set(nin); // comp here is always zero
	 for (int i = 0; i < sand.length; i++) comp.addAll(sand[i]);
	 // store composed set to prevent re-computation
	 composedSets.put(fkt, comp);
	 return comp;
	}
	
    /**
     * Removes all functions, that are not used by any other node
     */
	public void cleanFunctions () {
	 boolean found;
	 do {
	  found = false;
	  for (int i = this.functions.size()-1; i >= 0; i--) if (this.functions.get(i).out().size() == 0) {
	   this.functions.remove(i);
	   found = true;
	  }
	 } while (found);
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
