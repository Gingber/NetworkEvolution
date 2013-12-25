/**
 * 
 */
package util;

import java.io.File;
import java.io.IOException;


import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.MatrixFile;

/**
 * @author Gingber
 *
 */
public class ConstructNativeGraph {
	
	

	public Graph<Vertex, Edge> GenerateGraph(String filename) throws IOException {
		
		Matrix matrix = new Matrix();
		String matrixfile = matrix.createMatrix(filename);
		
		@SuppressWarnings("unchecked")
		MatrixFile<Vertex, Edge> mf = new MatrixFile(null,
				Factories.graphFactory, Factories.vertexFactory,
				Factories.edgeFactory);
		
		Graph<Vertex, Edge> g = mf.load(matrixfile);
		
	    System.out.println(g.toString());
	    String edges[] = g.toString().split("\n");
	    String[] edgesplitString = edges[1].replaceAll("Edges:", "").split(" ");
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < edgesplitString.length; i++) {
	    	// 获取方括号里的所有字符（方括号包含在内），去掉方括号以外的所有字符
	    	String pureedge = edgesplitString[i].replaceAll("^.*\\[", "").replaceAll("].*", "").replaceAll("V", "");
	    	System.out.println("Edge: " + pureedge);
	    	sb.append(pureedge);
	    	if(i != edgesplitString.length-1)
	    		sb.append("\n");
	    }
	      
		/*System.out.println("-----------------------degree distribution-----------------------");
		for (Vertex v : g.getVertices()) {
			System.out.println("degree " + v + ": " + g.degree(v) + "\t" + 
					"In degree " + v + ": " + g.inDegree(v) + "\t" + 
					"Out degree " + v + ": " + g.outDegree(v) + "\t" +
					g.getNeighbors(v));
			//System.out.println("degree " + v + ": " + graphDemo.g.degree(v));
		}
		System.out.println(g.getVertexCount());
	    System.out.println(g.getEdgeCount());*/
	    
	    //System.out.println(sb.toString());
	    TxtWriter.saveToFile(sb.toString(), new File("D:/edge.dat"), "UTF-8");
	    
	    return g;
	}
}
