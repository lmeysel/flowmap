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
        if (args.length == 1) {
         System.out.println("No lookup-tgable-size given!");
         return;
        }
        
        boolean visualizeProgress = true;
		
		System.out.println("read blif...");
		BLIF dat = new BLIF();
		dat.modelType = new GraphModel.GraphModelCreator();
		dat.functionType = new GraphFunction.GraphFunctionCreator();
		GraphModel rootModel = (GraphModel)dat.addFromFile(args[0]);
		if (visualizeProgress) Util.writeDOT("graphVis/1_input.txt", rootModel.iterateGraphNodes());

		System.out.println("decompose...");
		rootModel.decompose();
		if (visualizeProgress) Util.writeDOT("graphVis/2_deconmposed.txt", rootModel.iterateGraphNodes());
		
		System.out.println("label...");
		Graph right = rootModel.getRightModel();
		int nLut = Integer.parseInt(args[1]);
		Thingmabob cluster = FlowLabeller.label(right, nLut);
		if (visualizeProgress) right.writeDOT("graphVis/3_labeled.txt");
		
		System.out.println("compose...");
		rootModel.composeFrunctionsFromGraph(cluster);
        if (visualizeProgress) Util.writeDOT("graphVis/4_composed.txt", rootModel.iterateGraphNodes());
		
		System.out.println("clean..."); // remove unused nodes from decomposition
		rootModel.cleanFunctions();
        if (visualizeProgress) Util.writeDOT("graphVis/5_cleaned.txt", rootModel.iterateGraphNodes());

		if (args.length >= 3) {
		 System.out.println("save output...");
		 dat.saveToFolder(args[2]);
		}
	}
}
