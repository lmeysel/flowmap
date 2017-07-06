package rs.flowmap;

import java.util.Iterator;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import rs.blif.BLIF;
import rs.blifflow.GraphModel;

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
  dat.ModelType = new GraphModel.GraphModelCreator();
  if (args.length == 0) return;
  GraphModel rootModel = (GraphModel)dat.addFromFile(args[0]);
  // compute Network
  FlowNetwork fn = rootModel.computeFktNetwork();
  // print result
  printNetwork(fn, rootModel);
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
