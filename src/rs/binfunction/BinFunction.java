package rs.binfunction;

import java.util.HashSet;
import java.util.List;

import rs.graphnode.GraphNode;

/**
 * BinFunction describes a logic function, that has an onset and a don't-care set.
 * @author Mitja Stachowiak, Ludwig Meysel
 */
public class BinFunction implements GraphNode {
 protected Set on;    public Set on () { return this.on; }
 protected Set dc;    public Set dc () { return this.dc; }
 public int numInputs () { return this.on.width(); }
 private final SelfLinkingList in;
 private final java.util.Set<GraphNode> out = new HashSet<GraphNode>();
 protected String name = null;
 // implementation of GraphNode
 @Override public String name () { return this.name; }
 @Override public List<GraphNode> in () { return this.in; }
 @Override public java.util.Set<GraphNode> out() { return this.out; } // to be overridden
 @Override public void free () {
  if (!out.isEmpty()) throw new RuntimeException("Cannot free GraphNode while it is used by other nodes!");
  in().clear();
 }
 // definition of two-bit representations
 public final static int INV = 0;
 public final static int ONE = 2;
 public final static int ZERO = 1;
 public final static int DC = 3;
 private final static int inverseONE = 1;
 private final static int inverseZERO = 2;
 
 public BinFunction (int numInputs) {
  this.on = new Set(numInputs);
  this.dc = new Set(numInputs);
  this.in = new SelfLinkingList(this, numInputs);
  for (int i = 0; i < numInputs; i++) this.in.add(null);
 }
 public BinFunction (List<GraphNode> in, String name) {
  this(in.size());
  for (int i = 0; i < in.size(); i++) this.in.set(i, in.get(i));
  this.name = name;
 }

 
 /**
  * Computes the off-set of this function by applying the Presto-Algorithm on the function.
  * @return
  * the off-set, which is not (highly) optimized but intersect-free.
  */
 public IntersectFreeSet computeOff () {
  IntersectFreeSet off = new IntersectFreeSet(numInputs());
  Cube c = new Cube(numInputs()); // initialized with DCs
  addToOffset(off, c);
  return off;
 }
 private void addToOffset(IntersectFreeSet off, Cube c) {
  // regard on-set
  for (int i = 0; i < this.on.size(); i++) {
   Cube a = c.and(this.on.get(i));
   if (!a.isValid()) continue; // c has no intersection with on[i]
   if (a.equals(c)) return; // c is completely covered by one existing on[i]
   for (int j = 0; j < c.width; j++) if (c.getVar(j) == BinFunction.DC && a.getVar(j) != BinFunction.DC) {
    // split into smaller cubes and try again...
    Cube c1 = c.clone();
    Cube c2 = c.clone();
    c1.setVar(j, BinFunction.ONE);
    c2.setVar(j, BinFunction.ZERO);
    addToOffset(off, c1);
    addToOffset(off, c2);
    return;
   }
  }
  // regard dc-set
  for (int i = 0; i < this.dc.size(); i++) {
   Cube a = c.and(this.dc.get(i));
   if (!a.isValid()) continue; // c has no intersection with dc[i]
   if (a.equals(c)) return; // c is completely covered by one existing dc[i]
   for (int j = 0; j < c.width; j++) if (c.getVar(j) == BinFunction.DC && a.getVar(j) != BinFunction.DC) {
    // split into smaller cubes and try again...
    Cube c1 = c.clone();
    Cube c2 = c.clone();
    c1.setVar(j, BinFunction.ONE);
    c2.setVar(j, BinFunction.ZERO);
    addToOffset(off, c1);
    addToOffset(off, c2);
    return;
   }
  }
  // c has no intersects with on- or dc-set
  off.forceAdd(c); // after the this algorithm, off will automatically be intersect-free (no further check required)
 }
 
 /**
  * Complementation: Computes the off-set from on-set and dc-set by applying DeMorgan's law (Extremely slow)
  * @return
  * The off-set
  * @throws Exception
  * Fails, if there are invalid cubes in the function.
  */
 public Set computeOff_deMorgan () throws Exception {
  Set onDc = new Set(numInputs()); // onDc contains all implicants that are not in the offset. So not onDC is the offset, which has to be expanded to get a disjunctive form
  onDc.addAll(this.on);
  onDc.addAll(this.dc);
  // ToDo: sort cube for reaching don't cares early and merging overlaps
  Cube n;
  n = new Cube(numInputs());
  Set off = new Set(numInputs());
  multiply(0, n, onDc, off);
  return off;
 }
 /**
  * Computes recursively the disjunctive, expanded function of src' i.E. (ab'c + ac+ a'bc')' --> (a'+b+c') & (a'+c') & (a+b'+c) --> c'b' + c'a + c'a'b' + bc'a + ba'c + ba'c + a'c'b' 
  * Do first call with impl = 0, prod = DC DC DC..., dst = {}
  * @param c
  * maybe create the output-cubes as expanded cubes
  * @param impl
  * the index of the current implicant in src
  * @param prod
  * the product of the previous implicants
  * @param src
  * the source interpreted as negated konjunctive function
  * @param dst
  * the expanded disjunctive result
  * @throws Exception
  */
 private void multiply (int impl, Cube prod, Set src, Set dst) throws Exception {
  if (impl >= src.size()) {
   dst.add(prod);
   return;
  }
  for (int i = 0; i < numInputs(); i++) {
   Cube n = prod.clone(Cube.class);
   switch (src.get(impl).getVar(i)) {
    case inverseONE :
     n.andVar(i, ONE);
     break;
    case inverseZERO :
     n.andVar(i, ZERO);
     break;
    case DC :
     continue; // don't expand DCs
    default :
     throw new Exception("Invalid function input!");
   }
   if (n.getVar(i) == INV) continue;
   multiply(impl+1, n, src, dst);
  }
 }
 
 @Override
 public String toString () {
  String r = "";
  if (on.intersects(dc)) r += "[on and dc intersect!]";
  r += this.name() + " = ";
  r += on.toString(this.in);
  if (dc.size() > 0) r += "     DC_"+dc.toString(this.in);
  return r;
 }
 
 public boolean isEquivalent(BinFunction foreign) {
  if (this.numInputs() != foreign.numInputs()) return false;
  // map input names
  int[] map = new int[this.numInputs()];
  boolean eqMap = true;
  for (int i = 0; i < this.numInputs(); i++) {
   if (this.in.get(i) == null) return false;
   map[i] = -1;
   for (int j = 0; j < foreign.numInputs(); j++) {
    if (foreign.in.get(j) == null) return false;
    if (this.in.get(i) == foreign.in.get(j)) {
     map[i] = j;
     if (i != j) eqMap = false;
    }
   }
   if (map[i] == -1) return false;
  }
  // check weather each cube of this.on is covered by foreign.on+dc
  for (int i = 0; i < this.on.size(); i++) {
   Cube c = this.on().get(i);
   Cube m;
   if (eqMap) m = c;
   else {
    m = new Cube(this.numInputs());
    for (int j = 0; j < this.numInputs(); j++) m.setVar(map[j], c.getVar(j));
   }
   if (!foreign.on().covers(m, null, new Set.ForeignCoverer() {
    @Override public boolean isCovered (Cube c) {
     return foreign.dc().covers(c); // check, if the dc-set covers c
    }
   })) return false;
  }
  // check weather each cube of foreign.on is covered by this.on+dc
  BinFunction _this = this;
  for (int i = 0; i < foreign.on.size(); i++) {
   Cube c = foreign.on().get(i);
   Cube m;
   if (eqMap) m = c;
   else {
    m = new Cube(this.numInputs());
    for (int j = 0; j < this.numInputs(); j++) m.setVar(j, c.getVar(map[j]));
   }
   if (!_this.on().covers(m, null, new Set.ForeignCoverer() {
    @Override public boolean isCovered (Cube c) {
     return _this.dc().covers(c); // check, if the dc-set covers c
    }
   })) return false;
  }
  return true;
 }
 
 public int cost() {
  int r = 0;
  for (int i = 0; i < on.size(); i++) r += on.width() - on.get(i).cardinality2();
  return r;
 }
 
 
 
 
 public static class FunctionCreator {
  public BinFunction newFunction (int numInputs, Object parent) {
   return new BinFunction(numInputs);
  }
  public BinFunction newFunction (List<GraphNode> in, String name, Object parent) {
   return new BinFunction(in, name);
  }
 }
}