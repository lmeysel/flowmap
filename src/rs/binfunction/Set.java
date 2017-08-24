package rs.binfunction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import rs.graphnode.GraphNode;

/**
 * The class Set represents one set of several cubes.
 * @author Mitja Stachowiak
 *
 */
public class Set extends ArrayList<Cube> {
 private static final long serialVersionUID = -8446625631682699485L;
 private final int width;    public int width () { return this.width; }
 
 
 public Set (int width) {
  super();
  this.width = width;
 }
 
 @Override
 public boolean add(Cube c) {
  if (c.width != this.width) return false;
  return super.add(c);
 }
 
 public boolean intersects (Cube c) {
  for (int i = 0; i < this.size(); i++) if (this.get(i).and(c).isValid()) return true;
  return false;
 }
 public boolean intersects (Set s) {
  for (int i = 0; i < s.size(); i++) if (this.intersects(s.get(i))) return true;
  return false;
 }
 
 /**
  * Checks, weather the given cube u is completely covered by this set.
  * If u is element of this, the function checks, weather u is covered by this without u
  * @param u
  * @return
  * true, if u is covered
  */
 public boolean covers(final Cube u) { return covers(u, u, 0, this.size(), null); }
 public boolean covers(final Cube u, int from, int to) { return covers(u, u, from, to, null); }
 public boolean covers(final Cube u, final Cube ignore, final ForeignCoverer foreignCoverer) { return covers(u, ignore, 0, this.size(), foreignCoverer); }
 public boolean covers(final Cube u, final Cube ignore, int from, int to, final ForeignCoverer foreignCoverer) {
  int i = 0;
  int intersectVar = -1;
  CUBEITERATOR: for (i = from; i < to; i++) {
   Cube a = this.get(i);
   if (a == ignore) continue; // this \ ignore
   a = u.and(a);
   boolean covered = true;
   for (int j = 0; j < u.width; j++) {
    int v = a.getVar(j);
    if (v == BinFunction.INV) continue CUBEITERATOR; // u has no intersection with cube[i]
    if (u.getVar(j) == BinFunction.DC && v != BinFunction.DC) { covered = false; intersectVar = j; } // u has partial intersection with cube[i]
   }
   if (covered) return true; // u is completely covered by cube[i]
  }
  if (intersectVar == -1) {
   // u has no intersects with existing cubes
   if (foreignCoverer == null) return false;
   else return foreignCoverer.isCovered(u);
  } else {
   // split into smaller cubes and try again...
   Cube c1 = u.clone();
   Cube c2 = u.clone();
   c1.setVar(intersectVar, BinFunction.ONE);
   c2.setVar(intersectVar, BinFunction.ZERO);
   return covers(c1, ignore, from, to, foreignCoverer) && covers(c2, ignore, from, to, foreignCoverer);
  }
 }
 
 @Override
 public Set clone () {
  Set r;
  try {
   r = (Set)this.getClass().getConstructor(int.class).newInstance(width);
  } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
   return null;
  }
  for (int i = 0; i < size(); i++) r.add(this.get(i).clone());
  return r;
 }
 
 @Override
 public String toString () { return toString(null, null, 0, this.size()); }
 public String toString (List<GraphNode> names) { return toString(names, null, 0, this.size()); }
 public String toString (List<GraphNode> names, Cube ignore, int from, int to) {
  String s = "";
  boolean inv = false;
  boolean one = false;
  String v;
  for (int i = from; i < to; i++) {
   if (this.get(i) == ignore) continue; 
   if (!s.equals("")) s += " + ";
   int dcCnt = 0;
   for (int j = 0; j < this.width; j++) {
    if (names != null && names.size() >= this.width && names.get(j) != null) v = names.get(j).name();
    else v = ""+j;
    switch (((Cube)this.get(i)).getVar(j)) {
     case BinFunction.INV :
      s += v+"!";
      inv = true;
      break;
     case BinFunction.ONE :
      s += v+" ";
      break;
     case BinFunction.ZERO :
      s += v+"'";
      break;
     case BinFunction.DC :
      dcCnt++;
      break;
    }
    if (dcCnt == this.width) one = true;
   }
  }
  if (inv) s = "INVALID  ("+s+")";
  else if (one) s = "ONE  ("+s+")";
  else if (s.length() == 0) s = "ZERO";
  return s;
 }
 
 /**
  * Basic complementation algorithm:
  * recursively adds those parts of c to off, that have no intersection with any of the given sets notOff.
  * @Hint: Start c with a tautology.
  */
 static void addToOffset(IntersectFreeSet off, final Set[] notOff, Cube c) {
  for (int n = 0; n < notOff.length; n++) for (int i = 0; i < notOff[n].size(); i++) {
   Cube a = c.and(notOff[n].get(i));
   if (!a.isValid()) continue; // c has no intersection with notOff[n][i]
   if (a.equals(c)) return; // c is completely covered by one existing notOff[n][i]
   for (int j = 0; j < c.width; j++) if (c.getVar(j) == BinFunction.DC && a.getVar(j) != BinFunction.DC) {
    // split into smaller cubes and try again...
    Cube c1 = c.clone();
    Cube c2 = c.clone();
    c1.setVar(j, BinFunction.ONE);
    c2.setVar(j, BinFunction.ZERO);
    addToOffset(off, notOff, c1);
    addToOffset(off, notOff, c2);
    return;
   }
  }
  // c has no intersects with any of the notOff-sets
  off.forceAdd(c); // after the this algorithm, off will automatically be intersect-free (no further check required)
 }

 /**
  * Computes the complement of this set
  * @return
  * the complement-set
  */
 public Set not() {
  IntersectFreeSet off = new IntersectFreeSet(this.width);
  Cube c = new Cube(this.width); // initialized with DCs
  Set[] notOff = new Set[1];
  notOff[0] = this;
  addToOffset(off, notOff, c);
  return off;
 }

 /**
  * Computes the and-conjunction of this and the foreign set by DeMorgan's multiplication.
  * @param foreign
  * @return
  * The resulting set.
  */
 public Set and(Set foreign) {
  Set a = new Set(this.width);
  for (int i = 0; i < this.size(); i++) for (int j = 0; j < foreign.size(); j++) {
   Cube ca = this.get(i).and(foreign.get(j));
   if (ca.isValid()) a.add(ca);
  }
  return a;
 }
 
 
 
 
 /**
  * Functionpointer: Override isCovered to run a covering-check over more than one set.
  * @author Mitja Stachowiak
  */
 public static abstract class ForeignCoverer {
  protected abstract boolean isCovered(Cube c);
 }
}
