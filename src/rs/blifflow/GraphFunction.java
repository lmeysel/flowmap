package rs.blifflow;

import rs.binfunction.BinFunction;

public class GraphFunction extends BinFunction {
 GraphModel parent;
 private boolean decomposed = false;
 private int height = 0; public int height () { return this.height; }

 public GraphFunction(int numInputs) {
  super(numInputs);
 }
 
 public void decompose() {
/*  if (decomposed) return;
  decomposed = true;
  this.dc.clear();
  // be sure all previous functions were decomposed for dealing with the correct heights in the following
  for (int i = 0; i < inFkt.length; i++) if (inFkt[i] != null) inFkt[i].decompose();
  // repeat while function has more than two inputs
  while (this.numInputs() > 2) {
   // get two inputs with the lowest heights
   int i1 = -1, i2 = -1;
   int h1 = Integer.MAX_VALUE, h2 = Integer.MAX_VALUE;
   for (int i = 0; i < inFkt.length; i++) {
    int h = 0;
    if (inFkt[i] != null) h = inFkt[i].height;
    if (h < h1) {
     h1 = h;
     i1 = i;
    } else if (h < h2) {
     h2 = h;
     i2 = i;
    }
   }
   // 1.) Search for existing functions of i1, i2 and extract them from this
   // 2.) Search for ...
  }*/
 }
 
 public void updateHeight() {
 /* int height = 0;
  for (int i = 0; i < inFkt.length; i++) {
   int h = 0;
   if (inFkt[i] != null) h = inFkt[i].height;
   if (h >= height) height = h + 1;
  }
  if (this.height == height) return;
  this.height = height;
  Iterator<GraphFunction> it = outFkt.iterator();
  while(it.hasNext()) {
   it.next().updateHeight();
  }*/
 }
 
 
 
 public static class GraphFunctionCreator extends FunctionCreator {
  public GraphFunction newFunction (int numInputs) {
   return new GraphFunction(numInputs);
  }
 }
}
