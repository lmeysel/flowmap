package rs.blifflow;

import java.util.Iterator;
import java.util.List;

import rs.binfunction.BinFunction;
import rs.binfunction.Cube;
import rs.graphnode.GraphNode;

public class GraphFunction extends BinFunction {
 private boolean          decomposed = false;
 private final GraphModel parent;
 private int              height     = 0;    public int height() { return this.height; }
 public static boolean    reUseExistingFunctions = true;


 public GraphFunction(int numInputs, GraphModel parent) {
  super(numInputs);
  this.parent = parent;
 }

 public GraphFunction(List<GraphNode> in, String name, GraphModel parent) {
  super(in, name);
  this.parent = parent;
 }

 /**
  * Replaces this function with a tree of two-input functions:
  * Splits this function into two-input boolean functions. The successor-nodes of this are linked to the
  * output of the two-input tree and this function gets removed from the model.
  */
 public void decompose() {
  if (decomposed) return;
  int nameAdd = 1;
  decomposed = true;
  this.dc.clear();
  // be sure all previous functions were decomposed for dealing with the correct heights in the following
  for (int i = 0; i < in().size(); i++) if (in().get(i) instanceof GraphFunction) ((GraphFunction) in().get(i)).decompose();
  // search and-tree for decomposable functions
  GraphNode[] andTree = new GraphNode[this.on.size()];
  for (int i = 0; i < this.on.size(); i++) {
   Object[] fktIn = this.in().toArray();
   boolean[] negatedIn = new boolean[this.in().size()];
   Cube c = this.on.get(i);
   for (int j = 0; j < c.width; j++)
    switch (c.getVar(j)) {
    case DC:
     fktIn[j] = null;
     break;
    case ZERO:
     negatedIn[j] = true;
     break;
    case INV:
     throw new RuntimeException("Function is invalid!");
    }
   do {
    int[] j = twoLowestHeights(fktIn);
    if (j[1] != -1) { // c depends at least of two inputs and j[0], j[1] are the inputs with the lowest height
     // create an two-input-and-function, that replaces j[0], j[1]
     GraphFunction and = new GraphFunction(2, parent);
     and.in().set(0, (GraphNode) fktIn[j[0]]);
     and.in().set(1, (GraphNode) fktIn[j[1]]);
     Cube ca = new Cube(2);
     if (negatedIn[j[0]]) ca.setVar(0, ZERO);
     else ca.setVar(0, ONE);
     if (negatedIn[j[1]]) ca.setVar(1, ZERO);
     else ca.setVar(1, ONE);
     and.on().add(ca);
     nameAdd = nextName(this.name + "_", nameAdd);
     and.name = this.name + "_" + nameAdd;
     // check, weather an equivalent and-function already exists...
     boolean b = true;
     if (reUseExistingFunctions) {
      GraphFunction eq = equivalentFktExists(and);
      if (eq != null) {
       and.free();
       and = eq;
       nameAdd--;
       b = false;
      }
     }
     // replace the two inputs with the one and...
     fktIn[j[0]] = and;
     fktIn[j[1]] = null;
     negatedIn[j[0]] = false;
     negatedIn[j[1]] = false;
     and.updateHeight(); // also sets decomposed to true
     if (b) parent.functions.add(and);
    } else { // c depends on just one input j[0]; save this and stop search here
     if (negatedIn[j[0]]) { // for not regarding negated inputs in or-decomposition anymore
      GraphFunction not = new GraphFunction(1, parent);
      not.in().set(0, (GraphNode) fktIn[j[0]]);
      Cube cn = new Cube(1);
      cn.setVar(0, ZERO);
      not.on.add(cn);
      nameAdd = nextName(this.name + "_", nameAdd);
      not.name = this.name + "_" + nameAdd;
      not.updateHeight(); // also sets decomposed to true
      andTree[i] = not;
      parent.functions.add(not);
     } else
      andTree[i] = (GraphNode) fktIn[j[0]];
     break;
    }
   } while (true);
  }
  // decompose or-tree
  do {
   int[] j = twoLowestHeights(andTree);
   if (j[1] != -1) { // andTree consists of at least two inputs
    // Create a two-input-or-function of j[0], j[1]
    GraphFunction or = new GraphFunction(2, parent);
    or.in().set(0, andTree[j[0]]);
    or.in().set(1, andTree[j[1]]);
    Cube co = new Cube(2);
    co.setVar(0, ONE);
    or.on.add(co);
    co = new Cube(2);
    co.setVar(1, ONE);
    or.on.add(co);
    nameAdd = nextName(this.name + "_", nameAdd);
    or.name = this.name + "_" + nameAdd;
    // check, weather an equivalent or-function already exists
    boolean b = true;
    if (reUseExistingFunctions) {
     GraphFunction eq = equivalentFktExists(or);
     if (eq != null) {
      or.free();
      or = eq;
      nameAdd--;
      b = false;
     }
    }
    // replace the two inputs with the one or...
    andTree[j[0]] = or;
    andTree[j[1]] = null;
    or.updateHeight(); // also sets decomposed to true
    if (b) parent.functions.add(or);
   } else { // function is completely decomposed
    // move all links from followers of this to the remaining two-input-or
    Iterator<GraphNode> it = this.out().iterator();
    while (it.hasNext()) {
     GraphNode n = it.next();
     n.in().set(n.in().indexOf(this), andTree[j[0]]);
    }
    // remove this from graph
    parent.functions.remove(this);
    if (andTree[j[0]] instanceof GraphFunction) ((GraphFunction) andTree[j[0]]).name = this.name;
    break;
   }
  } while (true);
 }

 /**
  * Increments nameAdd until the node nameBase+nameAdd doesn't exist in current model.
  * @param nameBase
  * @param nameAdd
  * @return
  * incremented nameAdd
  */
 private int nextName(String nameBase, int nameAdd) {
  do {
   nameAdd++;
  } while (parent.getNodeByName(nameBase+nameAdd) != null);
  return nameAdd;
 }

 /**
  * Checks, weather an equivalent function to fkt (same inputs, same boolean function of this inputs) already exists
  * @param fkt
  * @return
  * the equivalent function to fkt; null if not exists
  */
 private GraphFunction equivalentFktExists(GraphFunction fkt) {
  Iterator<GraphNode> it = fkt.in().get(0).out().iterator();
  while (it.hasNext()) {
   GraphNode n = it.next();
   if (n == fkt) continue;
   if (n instanceof GraphFunction && ((GraphFunction) n).isEquivalent(fkt)) return (GraphFunction) n;
  }
  return null;
 }

 /**
  * @param in
  * @return
  * Returns the two nodes with the lowest height in the in-Array.
  * All non-Function Nodes are expected to have height 0
  */
 private int[] twoLowestHeights(Object[] in) {
  int h1 = Integer.MAX_VALUE, j1 = -1;
  int h2 = Integer.MAX_VALUE, j2 = -1;
  for (int j = 0; j < in.length; j++) {
   if (in[j] == null) continue;
   int h = 0;
   if (in[j] instanceof GraphFunction) h = ((GraphFunction) in[j]).height();
   if (h < h1) {
    h2 = h1;
    j2 = j1;
    h1 = h;
    j1 = j;
   } else if (h < h2) {
    h2 = h;
    j2 = j;
   }
  }
  int[] r = new int[2];
  r[0] = j1;
  r[1] = j2;
  return r;
 }

 /**
  * Sets the height of this function to the max(height of predecessors) + 1 and checks recursively the height of all
  * successor-functions.
  */
 public void updateHeight() {
  this.decomposed = this.in().size() <= 2;
  int height = 0;
  for (int i = 0; i < in().size(); i++) {
   int h = 0;
   if (in().get(i) instanceof GraphFunction) h = ((GraphFunction) in().get(i)).height;
   if (h >= height) height = h + 1;
  }
  if (this.height == height) return;
  this.height = height;
  Iterator<GraphNode> it = out().iterator();
  while (it.hasNext()) {
   GraphNode n = it.next();
   if (n instanceof GraphFunction) ((GraphFunction) n).updateHeight();
  }
 }

 
 
 
 public static class GraphFunctionCreator extends FunctionCreator {
  @Override
  public GraphFunction newFunction(int numInputs, Object parent) {
   return new GraphFunction(numInputs, (GraphModel) parent);
  }

  @Override
  public BinFunction newFunction(List<GraphNode> in, String name, Object parent) {
   return new GraphFunction(in, name, (GraphModel) parent);
  }
 }
}
