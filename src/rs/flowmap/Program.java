package rs.flowmap;

import java.util.Iterator;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
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
  // read data
  BLIF dat = new BLIF();
  dat.modelType = new GraphModel.GraphModelCreator();
  dat.functionType = new GraphFunction.GraphFunctionCreator();
  if (args.length == 0) return;
  GraphModel rootModel = (GraphModel)dat.addFromFile(args[0]);
  // compute Network
  FlowNetwork fn = rootModel.computeFktNetwork();
  // print result
  printNetwork(fn, rootModel);
  // FF
  FordFulkerson ff = new FordFulkerson(fn, 4, 5);
 }
 
 private static void printNetwork(FlowNetwork fn, GraphModel m) {
  for (int i = 0; i < fn.V(); i++) {
   Object o = m.getObjectByNetworkNumber(i);
   System.out.println("("+i+") " + m.getObjectsStringDescription(o));
   Iterator<FlowEdge> it = fn.adj(i).iterator();
   while (it.hasNext()) {
    int to = it.next().to();
    if (to == i) continue;
    System.out.println("     --> ("+to+") "+m.getObjectsStringDescription(m.getObjectByNetworkNumber(to)));
   }
  }
 }
}
