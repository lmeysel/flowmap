package rs.blif;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rs.binfunction.BinFunction;
import rs.graphnode.GraphNode;

/**
 * The class Model represents one BLIF-Model. Only data-fields necessary for Espresso are implemented yet.
 * @author Mitja Stachowiak
 */
public class Model {
 private BLIF parent;    public BLIF parent() { return this.parent; }
 private String name;   public String name() { return this.name; }
 public final FreeingArrayList<BinFunction> functions = new FreeingArrayList<BinFunction>();
 public final FreeingArrayList<Latch> latches = new FreeingArrayList<Latch>();
 public final FreeingArrayList<SubCircuit> subCircuits = new FreeingArrayList<SubCircuit>();
 public final FreeingArrayList<GraphNode.InputNode> inputs = new FreeingArrayList<GraphNode.InputNode>();
 public final FreeingArrayList<GraphNode.OutputNode> outputs = new FreeingArrayList<GraphNode.OutputNode>();
 protected Boolean inputsAndOutputsDeclared = false;
 public boolean isSeparateFile = false;
 boolean saved = false;

 public Model(String name, BLIF parent) throws Exception {
  if (parent.models.containsKey(name)) throw new Exception("Model " + name + " already exists in BLIF-project!");
  this.parent = parent;
  this.name = name;
  parent.models.put(name, this);
 }
 
 public byte correctLinkeage () {
  byte b = 0;
  // search nodes with un-linked inputs (input = UnknownNode)
  Iterator<GraphNode> it = this.iterateGraphNodes();
  while (it.hasNext()) {
   GraphNode cn = it.next();
   if (cn instanceof GraphNode.InputNode) continue; // inputs and from-subCircuit links should have no inputs 
   List<GraphNode> l = cn.in();
   for (int i = 0; i < l.size(); i++) {
    GraphNode n = l.get(i);
    if (n instanceof GraphNode.UnknownNode) { // this node is not linked and has to be replaced!
     GraphNode n_ = this.getNodeByName(n.name());
     if (n_ == null) {
      if (!inputsAndOutputsDeclared) {
       n_ = new GraphNode.InputNode(n.name());
       this.inputs.add((GraphNode.InputNode)n_);
       l.set(i, n_); // correct predecessor node was created and added to model's input nodes
      } else b |= 1;
     } else l.set(i, n_); // correct predecessor node found; replace l[i]
    }
   }
  }
  // search nodes without outputs
  it = this.iterateGraphNodes();
  while (it.hasNext()) {
   GraphNode cn = it.next();
   if (cn instanceof GraphNode.OutputNode) continue; // outputs and to-subCircuit links should have no outputs 
   Set<GraphNode> s = cn.out();
   if (s.size() == 0) {
    if (!inputsAndOutputsDeclared) {
     GraphNode.OutputNode n_ = new GraphNode.OutputNode(cn);
     this.outputs.add(n_);
    } else b |= 2;
   }
  }
  inputsAndOutputsDeclared = true;
  return b;
 }
 
 public Iterator<GraphNode> iterateGraphNodes () {
  return new NodeIterator(this);
 }
 
 public GraphNode getNodeByName (String name) {
  for (int i = 0; i < this.inputs.size(); i++) if (this.inputs.get(i).name().equals(name)) return this.inputs.get(i);
  for (int i = 0; i < this.functions.size(); i++) if (this.functions.get(i).name().equals(name)) return this.functions.get(i);
  for (int i = 0; i < this.latches.size(); i++) if (this.latches.get(i).name().equals(name)) return this.latches.get(i);
  for (int i = 0; i < this.subCircuits.size(); i++) {
   SubCircuit sc = this.subCircuits.get(i);
   for (int j = 0; j < sc.out.size(); j++) if (sc.out.get(j).name().equals(name)) return sc.out.get(j);
  }
  // don't regard outputs here because they have the same name like their predecessors.
  return null;
 }

 public void appendToFile(FileWriter fileWriter, boolean firstModel) throws IOException {
  //write opening of model
  if (!firstModel) fileWriter.write(".model "+this.name+"\n");
  if (this.inputs.size() != 0) {
   String s = ".inputs";
   for (int i = 0; i < this.inputs.size(); i++) s += " "+this.inputs.get(i).name();
   fileWriter.write(s+"\n");
  }
  if (this.outputs.size() != 0) {
   String s = ".outputs";
   for (int i = 0; i < this.outputs.size(); i++) s += " "+this.outputs.get(i).name();
   fileWriter.write(s+"\n");
  }
  // write functions
  for (int i = 0; i < functions.size(); i++) {
   BinFunction f = functions.get(i);
   String s = "\n.names";
   for (int j = 0; j < f.numInputs(); j++) s += " "+f.in().get(j).name();
   s += " "+f.name();
   fileWriter.write(s+"\n");
   for (int j = 0; j < f.on().size(); j++) {
    s = "";
    for (int k = 0; k < f.numInputs(); k++) switch (f.on().get(j).getVar(k)) {
     case BinFunction.INV :
      s += "!";
      break;
     case BinFunction.ZERO :
      s += "0";
      break;
     case BinFunction.ONE :
      s += "1";
      break;
     case BinFunction.DC :
      s += "-";
      break;
    }
    fileWriter.write(s+" 1\n");
   }
  }
  // write closing of model
  if (!firstModel) fileWriter.write(".end\n");
 }
 
 
 
 private static class NodeIterator implements Iterator<GraphNode> {
  private final Model model;
  private List<?> currentList;
  private int currentPos = -1;
  private int currentItem = -1;
  private GraphNode next = null;
  public NodeIterator (Model model) {
   this.model = model;
   currentList = model.inputs;
   internalNext();
  }
  private void internalNext () {
   next = null;
   if (currentList == model.inputs) {
    currentPos++;
    if (currentPos < currentList.size()) { next = ((GraphNode.InputNode)currentList.get(currentPos)); return; }
    else { currentList = model.functions; currentPos = -1; }
   }
   if (currentList == model.functions) {
    currentPos++;
    if (currentPos < currentList.size()) { next = ((BinFunction)currentList.get(currentPos)); return; }
    else { currentList = model.latches; currentPos = -1; }
   }
   if (currentList == model.latches) {
    currentPos++;
    if (currentPos < currentList.size()) { next = ((Latch)currentList.get(currentPos)); return; }
    else { currentList = model.subCircuits; currentPos = 0; }
   }
   if (currentList == model.subCircuits) {
    if (currentPos < currentList.size()) {
     currentItem++;
     int inSize = ((SubCircuit)currentList.get(currentPos)).in.size();
     if (currentItem >= inSize + ((SubCircuit)currentList.get(currentPos)).out.size()) {
      currentPos++;
      currentItem = -1;
      internalNext();
      return;
     }
     if (currentItem < inSize) next = ((SubCircuit)currentList.get(currentPos)).in.get(currentItem);
     else next = ((SubCircuit)currentList.get(currentPos)).out.get(currentItem - inSize);
    } else { currentList = model.outputs; currentPos = -1; }
   }
   if (currentList == model.outputs) {
    currentPos++;
    if (currentPos < currentList.size()) { next = ((GraphNode.OutputNode)currentList.get(currentPos)); return; }
    else { next = null; }
   }
  }
  @Override public boolean hasNext() { return this.next != null; }
  @Override public GraphNode next () {
   GraphNode r = this.next;
   this.internalNext();
   return r;
  }
 }
 
 
 
 protected static class FktListCreator {
  protected List<BinFunction> newList () { return new ArrayList<BinFunction>(); }
 }
 
 
 
 public static class ModelCreator {
  public Model newModel (String name, BLIF parent) {
   Model m;
   try {
    m = new Model(name, parent);
   } catch (Exception e) {
    m = parent.models().get(name);
   }
   return m;
  }
 }
 
 
 
 
 @SuppressWarnings("hiding")
 public static class FreeingArrayList<GraphNode> extends ArrayList<GraphNode> {
  private static final long serialVersionUID = 2614432379282826238L;
  public FreeingArrayList (int size) {
   super(size);
  }
  public FreeingArrayList () {
   this(0);
  }
  @Override public boolean remove (Object n) {
   if (n != null) ((rs.graphnode.GraphNode)n).free();
   return super.remove(n);
  }
  @Override public GraphNode remove (int i) {
   GraphNode n = super.remove(i);
   if (n != null) ((rs.graphnode.FreeableNode) n).free();
   return n;
  }
  @Override public GraphNode set (int i, GraphNode n) {
   GraphNode p = super.set(i, n);
   if (p != null && p != n) ((rs.graphnode.FreeableNode) p).free();
   return p;
  }
  public void replace (int i, GraphNode n) {
   rs.graphnode.GraphNode p = (rs.graphnode.GraphNode)super.set(i, n);
   if (p == null) return;
   Object[] out = p.out().toArray();
   for (int j = 0; j < out.length; j++) {
    int k = ((rs.graphnode.GraphNode)out[j]).in().indexOf(p);
    if (k != -1) ((rs.graphnode.GraphNode)out[j]).in().set(k, (rs.graphnode.GraphNode)n);
   }
   p.free();
  }
  @Override public void clear () {
   for (int i = 0; i < this.size(); i++) this.set(i, null);
   super.clear();
  }
  @Override public boolean removeAll(Collection<?> c) { throw new RuntimeException("This operation is not allowed on current list!"); }
 }
}