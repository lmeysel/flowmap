package rs.flowmap;

import rs.blif.BLIF;
import rs.blifflow.GraphModel;
import rs.blifflow.GraphFunction;

/**
 * 
 * @author Mitja Stachowiak, Ludwig Meysel
 * 
 * @version 03.07.2017
 */
public class Program {
 public static void main(String[] args) {
  if (args.length == 0) { System.out.println("No input data given!"); return; }
  // read data
  BLIF dat = new BLIF();
  dat.modelType = new GraphModel.GraphModelCreator();
  dat.functionType = new GraphFunction.GraphFunctionCreator();
  if (args.length == 0) return;
  GraphModel rootModel = (GraphModel)dat.addFromFile(args[0]);
  // decompose
  rootModel.decompose();
  rootModel.printNetwork();
  // save output
  if (args.length >= 2) {
   dat.saveToFolder(args[1]);
  }
 }
}
