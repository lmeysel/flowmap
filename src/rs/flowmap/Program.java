package rs.flowmap;

import rs.blif.BLIF;
import rs.blifflow.GraphModel;
import rs.flowmap.test.Util;
import rs.blifflow.GraphFunction;

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
		// read data
		BLIF dat = new BLIF();
		dat.modelType = new GraphModel.GraphModelCreator();
		dat.functionType = new GraphFunction.GraphFunctionCreator();

		GraphModel rootModel = (GraphModel)dat.addFromFile(args[0]);
		// decompose
		Util.writeDOT("blubb1.txt", rootModel.iterateGraphNodes());
		rootModel.decompose();
		//rootModel.printNetwork();
		Util.writeDOT("blubb2.txt", rootModel.iterateGraphNodes());
		// save output
		if (args.length >= 2) {
			dat.saveToFolder(args[1]);
		}
	}
}
