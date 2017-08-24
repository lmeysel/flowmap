package rs.flowmap;

import rs.blif.BLIF;
import rs.blifflow.GraphFunction;
import rs.blifflow.GraphModel;
import rs.flowmap.graph.Graph;
import rs.flowmap.graph.Thingmabob;
import rs.flowmap.labelling.FlowLabeller;
import rs.flowmap.test.Util;

/**
 * 
 * @author Mitja Stachowiak, Ludwig Meysel
 * 
 * @version 03.07.2017
 */
public class Program {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("No input data given!");
			return;
		}
		
		System.out.println("read blif...");
		BLIF dat = new BLIF();
		dat.modelType = new GraphModel.GraphModelCreator();
		dat.functionType = new GraphFunction.GraphFunctionCreator();
		GraphModel rootModel = (GraphModel)dat.addFromFile(args[0]);

		System.out.println("decompose...");
		Util.writeDOT("blubb1.txt", rootModel.iterateGraphNodes());
		rootModel.decompose();

		System.out.println("label...");
		Graph right = rootModel.getRightModel();
		Thingmabob cluster = FlowLabeller.label(right, 3);
		right.writeDOT("graph-debug.txt");
		
		System.out.println("compose...");
		rootModel.composeFrunctionsFromGraph(cluster.getStage());
		
		System.out.println("clean..."); // remove unused nodes from decomposition
		rootModel.cleanFunctions();

		//rootModel.printNetwork();
		//Util.writeDOT("blubb2.txt", rootModel.iterateGraphNodes());

		if (args.length >= 2) {
		 System.out.println("save output...");
		 dat.saveToFolder(args[1]);
		}
	}
}
