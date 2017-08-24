package rs.graphnode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This is the basic interface for Blif-Graph Objects
 * @author Mitja Stachowiak
 */
public interface GraphNode extends FreeableNode {
 public String name ();
 public List<GraphNode> in (); // the inputs are a Self-Linking (SelfLinkingArrayList or OneItemList). Adding a new GraphNode to in will also create a link in the new node's out-Table.
 public Set<GraphNode> out (); // the outputs (ReadonlyHashSet) are managed by the inputs. Only the SelfLinkingArrayList or OneItemList are allowed to modify the outputs.
 public void free ();
 
 
 public static class UnknownNode implements GraphNode {
  private final String name;
  private final Set<GraphNode> out = new ReadonlyHashSet();
  public UnknownNode (String name) {
   this.name = name;
  }
  @Override public String name() { return this.name; }
  @Override public List<GraphNode> in() { return null; }
  @Override public Set<GraphNode> out() { return out; }
  @Override public void free () { if (!out.isEmpty()) throw new RuntimeException("Cannot free GraphNode while it is used by other nodes!"); }
 }
 
 
 public static class InputNode implements GraphNode {
  private final String name;
  private final Set<GraphNode> out = new ReadonlyHashSet();
  public InputNode (String name) {
   this.name = name;
  }
  @Override public String name() { return this.name; }
  @Override public List<GraphNode> in() { return null; }
  @Override public Set<GraphNode> out() { return out; }
  @Override public void free () { if (!out.isEmpty()) throw new RuntimeException("Cannot free GraphNode while it is used by other nodes!"); }
 }
 
  
 public static class OutputNode implements GraphNode {
  private final OneItemList in = new OneItemList(this);
  public OutputNode (GraphNode name) {
   this.in.set(0, name);
  }
  @Override public String name() { return this.in.get(0).name(); }
  @Override public List<GraphNode> in() { return in; }
  @Override public Set<GraphNode> out() { return null; }
  @Override public void free () { in.set(0, null); }
 }
 
 
 
 
 public static class SelfLinkingArrayList extends ArrayList<GraphNode> {
  private static final long serialVersionUID = 2614432379282826237L;
  private final GraphNode parent;
  public SelfLinkingArrayList (GraphNode parent, int size) {
   super(size);
   this.parent = parent;
  }
  public SelfLinkingArrayList (GraphNode parent) { this(parent, 0); }
  @Override public boolean add(GraphNode n) {
   if (n != null) ((ReadonlyHashSet)n.out())._add(parent);
   return super.add(n);
  }
  @Override public void add(int i, GraphNode n) {
   if (n != null) ((ReadonlyHashSet)n.out())._add(parent);
   super.add(i, n);
  }
  @Override public boolean remove (Object n) {
   boolean b = super.remove(n);
   if (b && n != null) ((ReadonlyHashSet)((GraphNode)n).out())._remove(parent);
   return b;
  }
  @Override public GraphNode remove (int i) {
   GraphNode n = super.remove(i);
   if (n != null) ((ReadonlyHashSet)n.out())._remove(parent);
   return n;
  }
  @Override public GraphNode set (int i, GraphNode n) {
   GraphNode p = super.set(i, n);
   if (p != null) ((ReadonlyHashSet)p.out())._remove(parent);
   if (n != null) ((ReadonlyHashSet)n.out())._add(parent);
   return p;
  }
  @Override public void clear () {
   for (int i = 0; i < this.size(); i++) {
    ((ReadonlyHashSet)this.get(i).out())._remove(parent);
    super.set(i, null);
   }
   super.clear();
  }
  @Override public boolean removeAll(Collection<?> c) { throw new RuntimeException("This operation is not allowed on current list!"); }
 }
 
 
 
 
 @SuppressWarnings("rawtypes")
 public static class OneItemList implements List<GraphNode> {
  private GraphNode item;
  private final GraphNode parent;
  public OneItemList (GraphNode parent) { this.parent = parent; }
  @Override public boolean add(GraphNode arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public void add(int arg0, GraphNode arg1) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public boolean addAll(Collection arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public boolean addAll(int arg0, Collection arg1) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public void clear() { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public boolean contains(Object arg0) { return item == arg0; }
  @Override public boolean containsAll(Collection arg0) {
   if (arg0.size() > 1) return false;
   if (arg0.size() == 1 && arg0.iterator().next() != item) return false;
   return true;
  }
  @Override public GraphNode get(int arg0) {
   if (arg0 == 0) return item;
   return null;
  }
  @Override
  public int indexOf(Object arg0) {
   if (arg0 == item) return 0;
   return -1;
  }
  @Override public boolean isEmpty() { return false; }
  @Override public Iterator<GraphNode> iterator() { return new OneItemIterator(item); }
  public static class OneItemIterator implements ListIterator<GraphNode> {
   private GraphNode el;
   private boolean n = true;
   public OneItemIterator (GraphNode el) { this.el = el; }
   @Override public boolean hasNext() { return n; }
   @Override
   public GraphNode next() {
    if (n) {
     n = false;
     return el;
    }
    return null;
   }
   @Override public void add(GraphNode arg0) {}
   @Override public boolean hasPrevious() { return false; }
   @Override
   public int nextIndex() {
    if (n) return 0;
    return 1;
   }
   @Override public GraphNode previous() { return null; }
   @Override public int previousIndex() { return -1; }
   @Override public void remove() { throw new RuntimeException("This operation is not allowed on current list!"); }
   @Override public void set(GraphNode arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  }
  @Override public int lastIndexOf(Object arg0) { return this.indexOf(arg0); }
  @Override public ListIterator<GraphNode> listIterator() { return new OneItemIterator(item); }
  @Override public ListIterator<GraphNode> listIterator(int arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public boolean remove(Object arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public GraphNode remove(int arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public boolean removeAll(Collection arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public boolean retainAll(Collection arg0) { throw new RuntimeException("This operation is not allowed on current list!"); }
  @Override public GraphNode set(int arg0, GraphNode arg1) {
   if (arg0 == 0) {
    GraphNode p = item;
    if (p != null) ((ReadonlyHashSet)p.out())._remove(parent);
    item = arg1;
    if (item != null) ((ReadonlyHashSet)item.out())._add(parent);
    return p;
   }
   return null;
  }
  @Override public int size() { return 1; }
  @Override public List<GraphNode> subList(int arg0, int arg1) {
   if (arg0 == 0 && arg1 == 1) return this;
   return null;
  }
  @Override public GraphNode[] toArray() {
   GraphNode[] r = new GraphNode[1];
   r[0] = item;
   return r;
  }
  @SuppressWarnings("unchecked")
  @Override public Object[] toArray(Object[] arg0) { Object[] a = new Object[1]; a[0] = item; return a; }
 }
 
 
 public static class ReadonlyHashSet extends HashSet<GraphNode> {
  private static final long serialVersionUID = -1265904781158449279L;
  @Override public void clear () { throw new RuntimeException("Output-set cannot be modified! Modify sucessor's inputs instead."); }
  @Override public boolean remove (Object o) { throw new RuntimeException("Output-set cannot be modified! Modify sucessor's inputs instead."); }
  @Override public boolean add (GraphNode o) { throw new RuntimeException("Output-set cannot be modified! Modify sucessor's inputs instead."); }
  @Override public boolean addAll (Collection<? extends GraphNode> c) { throw new RuntimeException("Output-set cannot be modified! Modify sucessor's inputs instead."); }
  @Override public boolean removeAll (Collection<?> c) { throw new RuntimeException("Output-set cannot be modified! Modify sucessor's inputs instead."); }
  void _clear () { super.clear(); }
  boolean _remove (Object o) { return super.remove(o); }
  boolean _add (GraphNode o) { return super.add(o); }
  boolean _addAll (Collection<? extends GraphNode> c) { return super.addAll(c); }
  boolean _removeAll (Collection<?> c) { return super.removeAll(c); }
 }
}