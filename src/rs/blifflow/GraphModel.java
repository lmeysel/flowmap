package rs.blifflow;

import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import rs.binfunction.BinFunction;
import rs.blif.*;

/**
 * The class GraphModel does not simply store different functions but it also links the functions by their
 * variable names and adds the ability of creating a FlowMap that is linked with the functions.
 * @author Mitja Stachowiak
 *
 */

public class GraphModel extends Model {
 private int networkVerticesCount;

 public GraphModel(String name, BLIF parent) throws Exception {
  super(name, parent);
 }
 
 /**
  * @hint Caches the result in networkVerticesCount and the offsets of the SubCircuits in SubCircuit.offset.
  * @param offset
  * The network number of the first object in this model.
  * @return
  * returns the number of functions and latches of this model plus all its sub models.
  */
 private int initNetworkVerticesCount () {
  this.networkVerticesCount = this.functions.size() + this.latches.size();
  for (int i = 0; i < this.subCircuits.size(); i++) {
   SubCircuit sc = this.subCircuits.get(i);
   this.networkVerticesCount += ((GraphModel)sc.subModel).initNetworkVerticesCount();
  }
  return this.networkVerticesCount;
 }
 
 /**
  * @hint
  * Do not change, add or remove any object (function, latch, sub-circuit, etc.) in the BLIF-structure
  * as long as you work with the flow network!
  * @hint
  * The mapping from networking numbers to BLIF-objects is not injective, due to the ability of using
  * the same model (sub-circuit) several times in the project.
  * @return
  * returns a FlowNetwork including sub-models. Use getObjectByNetworkNumber(i) to get the BLIF-object,
  * that is linked with the network number i.
  */
 public FlowNetwork computeFktNetwork () {
  int n = this.inputs.size() + this.initNetworkVerticesCount() + this.outputs.size();
  FlowNetwork fn = new FlowNetwork(n);
  // add all edges except of circuit breaking edges; mark there edges as unsolved links
  HirachyItem h = new HirachyItem(null, this, this.inputs.size());
  addEdges(fn, h);
  // convert unsolved links into edges
  boolean b;
  do {
   b = convertUnsolvedLinks(fn, h);
  } while (b);
  return fn;
 }
 
 /**
  * matches unsolved links:
  *  - uses the CubCircuit's relations to link links in the sub model with links in the parent model
  *  - moves solves network numbers along the link paths
  * @param fn
  * @param h
  * @return
  * returns true, if at least one more linkage was found in current iteration
  */
 private boolean convertUnsolvedLinks (FlowNetwork fn, HirachyItem h) {
  if (this != h.model) throw new NullPointerException(); // this must not happen
  boolean r = false;
  for (int i = 0; i < h.subHirachies.size(); i++) {
   HirachyItem hs = h.subHirachies.get(i);
   SubCircuit sc = hs.subCircuit ;
   for (int j = 0; j < hs.unsolvedLinks.size(); j++) {
    UnsolvedLink ul = hs.unsolvedLinks.get(j);
    if (ul.n1 != -1 && ul.n2 != -1) continue; // link has been solved
    // apply sub-circuit name mapping: replace sub-model's input and output links by the parent's link endings
    for (int k = 0; k < sc.relCnt(); k++) {
     if (ul.n1 == -1 && k < sc.subModel.inputs.size() && ul.o1 == sc.subModel.inputs.get(k)) {
      ul.o1 = sc.getRel(k);
      r = true;
     }
     if (ul.n2 == -1 && k >= sc.subModel.inputs.size() && ul.o2 == sc.subModel.outputs.get(k-sc.subModel.inputs.size())) {
      ul.o2 = sc.getRel(k);
      r = true;
     }
    }
    // search for matching links
    for (int k = 0; k < h.unsolvedLinks.size(); k++) {
     UnsolvedLink pl = h.unsolvedLinks.get(k);
     if (pl.n1 != -1 && pl.n2 != -1) continue;
     if (ul.n1 == -1 && ul.o1 == pl.o2) {
      ul.n1 = pl.n1;
      r = true;
     }
     if (pl.n1 == -1 && ul.o2 == pl.o1) {
      pl.n1 = ul.n1;
      r = true;
     }
     if (pl.n1 != -1 && pl.n2 != -1) fn.addEdge(new FlowEdge(pl.n1, pl.n2, 1.0));
    }
    // add solved links
    if (ul.n1 != -1 && ul.n2 != -1) fn.addEdge(new FlowEdge(ul.n1, ul.n2, 1.0));
   }
   if (hs.model.convertUnsolvedLinks(fn, hs)) r = true;
  }
  return r;
 }
 
 /**
  * @param o
  * @param h
  * @return
  * returns the number of the network vertex, that corresponds to o in contect h
  */
 private int getNetworkNumberByObject (Object o, HirachyItem h) {
  if (this != h.model) throw new NullPointerException(); // this must not happen
  if (o instanceof String && h.parent == null) {
   // don't use inputs.indexOf here, because this will do a string-comparison, not an instance-comparison!
   for (int i = 0; i < this.inputs.size(); i++) if (this.inputs.get(i) == o) return i;
   for (int i = 0; i < this.outputs.size(); i++) if (this.outputs.get(i) == o) return h.offset + this.networkVerticesCount + i;
  }
  if (o instanceof BinFunction) return this.functions.indexOf(o) + h.offset;
  if (o instanceof Latch) return this.latches.indexOf(o) + this.functions.size() + h.offset;
  return -1;
 }
 
 /**
  * Adds edges within the same model as network edges to fn and links to other models as unsolved links to h
  * @param fn
  * @param h
  * @param o1
  * @param o2
  */
 private void addEdge (FlowNetwork fn, HirachyItem h, Object o1, Object o2) {
  if (this != h.model) throw new NullPointerException(); // this must not happen
  UnsolvedLink ul = new UnsolvedLink();
  ul.o1 = o1;
  ul.o2 = o2;
  ul.n1 = getNetworkNumberByObject(o1, h);
  ul.n2 = getNetworkNumberByObject(o2, h);
  if (ul.n1 != -1 && ul.n2 != -1) fn.addEdge(new FlowEdge(ul.n1, ul.n2, 1));
  else h.unsolvedLinks.add(ul);
 }
 
 /**
  * processes all objects of the current model, that can have other objects as input
  * @param fn
  * @param h
  */
 private void addEdges (FlowNetwork fn, HirachyItem h) {
  if (this != h.model) throw new NullPointerException(); // this must not happen
  // process all objects of current model, that can have other objects as input
  for (int i = 0; i < this.functions.size(); i++) {
   BinFunction o2 = this.functions.get(i);
   for (int j = 0; j < o2.numInputs(); j++) this.addEdge(fn, h, this.getObjectByParamName(o2.names()[j]), o2);
  }
  for (int i = 0; i < this.latches.size(); i++) {
   Latch o2 = this.latches.get(i);
   this.addEdge(fn, h, this.getObjectByParamName(o2.input), o2);
  }
  for (int i = 0; i < this.subCircuits.size(); i++) {
   SubCircuit sc = this.subCircuits.get(i);
   for (int j = 0; j < sc.subModel.inputs.size(); j++) {
    String o2 = sc.getRel(j);
    this.addEdge(fn, h, this.getObjectByParamName(o2), o2);
   }
  }
  for (int i = 0; i < this.outputs.size(); i++) {
   String o2 = this.outputs.get(i);
   this.addEdge(fn, h, this.getObjectByParamName(o2), o2);
  }
  // process sub-circuits
  int offset = h.offset + this.functions.size() + this.latches.size();
  for (int i = 0; i < this.subCircuits.size(); i++) {
   HirachyItem hs = new HirachyItem(h, (GraphModel)this.subCircuits.get(i).subModel, offset);
   hs.subCircuit = this.subCircuits.get(i);
   offset += hs.model.networkVerticesCount;
   hs.model.addEdges(fn, hs);
  }
 }
 
 /**
  * @hint More then one different number can reference the same object, because sub-circuits can be
  * used several times in the project.
  * @param i
  * the network vertex number
  * @return
  * the object, which is associated with this number; this can either be a String (=model's input or output param),
  * a BinFunction or a Latch.
  */
 public Object getObjectByNetworkNumber (int i) {
  if (i < this.inputs.size()) return this.inputs.get(i);
  if (i < this.inputs.size() + this.networkVerticesCount) return this.getObjectByNetworkNumber(i, this.inputs.size());
  if (i < this.inputs.size() + this.networkVerticesCount + this.outputs.size()) return this.outputs.get(i - this.networkVerticesCount - this.inputs.size());
  return null; // not found
 }
 private Object getObjectByNetworkNumber (int i, int offset) {
  // ignore inputs and outputs in sub-circuits!
  if (i-offset < this.functions.size()) return this.functions.get(i-offset);
  else offset += this.functions.size();
  if (i-offset < this.latches.size()) return this.latches.get(i-offset);
  else offset += this.latches.size();
  for (int j = 0; j < this.subCircuits.size(); j++) {
   GraphModel m = (GraphModel)this.subCircuits.get(j).subModel;
   if (i-offset < m.networkVerticesCount) return m.getObjectByNetworkNumber(i, offset);
   else offset += m.networkVerticesCount;
  }
  return null; // not found
 }
 
 /**
  * Returns the object, that corresponds to the name n in the current model. If n is the output of a sub-circuit,
  * the SubCircuit.rel[i]-String is returned, that defines this output name.
  * @param n
  * the string name of an object
  * @return
  * the object
  */
 private Object getObjectByParamName (String n) {
  for (int i = 0; i < this.inputs.size(); i++) if (n.equals(this.inputs.get(i))) return this.inputs.get(i);
  for (int i = 0; i < this.functions.size(); i++) if (n.equals(this.functions.get(i).name())) return this.functions.get(i);
  for (int i = 0; i < this.latches.size(); i++) if (n.equals(this.latches.get(i).output)) return this.latches.get(i);
  for (int i = 0; i < this.subCircuits.size(); i++) {
   SubCircuit sc = this.subCircuits.get(i);
   String s = sc.getRelByParam(n);
   if (s != null) return s;
  }
  return null;
 }
 
 /**
  * @param o
  * object of this model or of one of this model's sub-circuits
  * @return
  * returns a string, that describes the type and the name of the given object o.
  */
 public String getObjectsStringDescription(Object o) {
  if (o instanceof String) {
   if (this.inputs.contains(o)) return "input "+(String)o;
   if (this.outputs.contains(o)) return "output "+(String)o;
  }
  if (o instanceof BinFunction) {
   return "function "+((BinFunction)o).name();
  }
  if (o instanceof Latch) {
   return "latch "+((Latch)o).output;
  }
  return "?";
 }
 
 
 
 /**
  * Stores additional information during the creation of a FlowNetwork. This is necessary,
  * because of the non-injectivity of the mapping.
  * @author Mitja Stachowiak
  *
  */
 private static class HirachyItem {
  public SubCircuit subCircuit = null;
  public final HirachyItem parent;
  public final int offset;
  public final GraphModel model;
  public final List<UnsolvedLink> unsolvedLinks = new ArrayList<UnsolvedLink>();
  public final List<HirachyItem> subHirachies = new ArrayList<HirachyItem>();
  public HirachyItem (HirachyItem parent, GraphModel model, int offset) {
   this.parent = parent;
   this.model = model;
   this.offset = offset;
   if (parent != null) parent.subHirachies.add(this);
  }
 }
 
 
 
 
 private static class UnsolvedLink {
  public Object o1;
  public Object o2;
  public int n1;
  public int n2;
 }
 
 
 
 
 /**
  * Overrides Model.ModelCreator to enable the BLIF-interpreter creating GraphModels instead of Models
  * @author Mitja Stachowiak
  *
  */
 public static class GraphModelCreator extends ModelCreator {
  @Override
  public Model newModel (String name, BLIF parent) {
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
