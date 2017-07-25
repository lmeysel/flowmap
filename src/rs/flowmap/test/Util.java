package rs.flowmap.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;

/**
 * 
 * @author Ludwig Meysel
 * 
 * @version 18.07.2017
 */
public class Util {
	/**
	 * Writes a FlowNetwork as DOT file as helper for debugging.
	 * 
	 * @param filename
	 *           The destination file.
	 * @param ff
	 *           The FordFulkerson containting the net to write as DOT
	 */
	public static void writeDOT(String filename, FordFulkerson ff, FlowNetwork net) {
		try {
			String br = System.getProperty("line.separator");
			BufferedWriter wtr = new BufferedWriter(new FileWriter(filename));

			wtr.write("digraph G {" + br);

			// define edges
			for (FlowEdge e : net.edges()) {
				wtr.write(e.from() + " -> " + e.to() + " [label=<" + (int)e.capacity() + "/" + (int)e.flow() + ">]" + br);
			}
			for (int i = 0; i < net.V(); i++)
				wtr.write(i + " [color=" + (ff.inCut(i) ? "red" : "black") + "]" + br);

			wtr.write("}" + br);

			wtr.close();
		} catch (IOException x) {
			System.err.println(x.getMessage());
		}
	}
}
