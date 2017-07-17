package rs.blif;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rs.graphnode.GraphNode;

public class Latch implements GraphNode {
 private GraphNode.OneItemList in = new GraphNode.OneItemList(this);
 private Set<GraphNode> out = new HashSet<GraphNode>();
 public GraphNode input () { return in.get(0); }
 public void input (GraphNode n) { in.set(0, n); }
 public String output;
 public String type = "as"; // fe = falling edge,  re = rising edge,  ah = active high,  al = active low,  as = asynchronous
 public String control = "NIL";
 public byte initVal = 3; // 0 = zero  1 = one  2 = don't care  3 = unknown
 @Override public String name() { return output; }
 @Override public List<GraphNode> in () { return this.in; }
 @Override public Set<GraphNode> out() { return this.out; }
}
