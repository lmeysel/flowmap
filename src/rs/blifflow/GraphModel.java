package rs.blifflow;

import java.util.Iterator;

import rs.binfunction.BinFunction;
import rs.blif.*;
import rs.graphnode.GraphNode;

/**
 * The class GraphModel does not simply store different functions but it also links the functions by
 * their variable names and adds the ability of creating a FlowMap that is linked with the
 * functions.
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
