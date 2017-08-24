package rs.blif;

import java.util.ArrayList;
import java.util.List;

import rs.graphnode.FreeableNode;
import rs.graphnode.GraphNode;

/**
 * This class represents a sub-circuit. In the Blif-Graph, each node can have multiple inputs and one output, that can be used by several other nodes.
 * A Sub-Circuit can have multiple outputs and is therefore not representable as a BLIF-Graph object. Instead all sub-circuit's inputs are
 * ToSubcircuit-nodes, that inherit from OutputNode and all outputs are FromSubcircuit-nodes, that inherit from InputNode. Therefore the sub-circuit
 * appears as several inputs and outputs of the BLIF-Network.
 * @author Mitja Stachowiak
 */
public class SubCircuit implements FreeableNode {
 public final Model model;
 public final Model subModel;
 public final List<ToSubcircuit> in = new ArrayList<ToSubcircuit>();
 public final List<FromSubcircuit> out = new ArrayList<FromSubcircuit>();

 public SubCircuit(Model model, Model subModel) {
  if (model == null || subModel == null) throw new NullPointerException();
  this.model = model;
  this.subModel = subModel;
  model.subCircuits.add(this);
 }

 public boolean setRel(String subModelInp, String modelParam) {
  for (int i = 0; i < subModel.inputs.size(); i++) if (subModel.inputs.get(i).name().equals(subModelInp)) {
   in.add(new ToSubcircuit(new GraphNode.UnknownNode(modelParam), subModel.inputs.get(i), this));
   return true;
  }
  for (int i = 0; i < subModel.outputs.size(); i++) if (subModel.outputs.get(i).name().equals(subModelInp)) {
   out.add(new FromSubcircuit(modelParam, subModel.outputs.get(i), this));
   return true;
  }
  return false;
 }

 public GraphNode getRelByParam(String modelParam) {
  for (int i = 0; i < this.in.size(); i++) if (this.in.get(i).name().equals(modelParam)) return this.in.get(i);
  for (int i = 0; i < this.out.size(); i++) if (this.out.get(i).name().equals(modelParam)) return this.out.get(i);
  return null;
 }

 public GraphNode getSubRelByParam(String modelParam) {
  for (int i = 0; i < this.in.size(); i++) if (this.in.get(i).name().equals(modelParam)) return this.in.get(i).subInput;
  for (int i = 0; i < this.out.size(); i++) if (this.out.get(i).name().equals(modelParam)) return this.out.get(i).subOutput;
  return null;
 }

 public boolean isValid() {
  if (this.in.size() != subModel.inputs.size()) return false;
  if (this.out.size() != subModel.outputs.size()) return false;
  for (int i = 0; i < this.in.size(); i++) if (!this.subModel.inputs.contains(this.in.get(i).subInput)) return false;
  for (int i = 0; i < this.out.size(); i++) if (!this.subModel.outputs.contains(this.out.get(i).subOutput)) return false;
  return true;
 }
 
 @Override public void free() {
  for (int i = 0; i < out.size(); i++) out.get(i).free();
  for (int i = 0; i < in.size(); i++) in.get(i).free();
 }

 
 
 
 public static class ToSubcircuit extends GraphNode.OutputNode {
  public final SubCircuit subCktLink;
  public InputNode subInput;
  public ToSubcircuit(GraphNode name, InputNode subInput, SubCircuit subCktLink) {
   super(name);
   this.subInput = subInput;
   this.subCktLink = subCktLink;
  }
 }

 public static class FromSubcircuit extends GraphNode.InputNode {
  public final SubCircuit subCktLink;
  public OutputNode subOutput;
  public FromSubcircuit(String name, OutputNode subOutput, SubCircuit subCktLink) {
   super(name);
   this.subOutput = subOutput;
   this.subCktLink = subCktLink;
  }
 }
}
