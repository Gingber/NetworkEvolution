/**
 * 
 */
package network.random;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import util.Edge;
import util.TxtWriter;
import util.Vertex;


import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @author Gingber
 *
 */
public class RandomVertexGraph {
	
	public void RandomVertexGenerator(UndirectedSparseMultigraph<String, String> graph, int maxdegree, int percent) throws IOException {
		 //�������Ե��������ԭʼ������
	    int orgVertexCount = graph.getVertexCount();
	    int orgEdgeCount = graph.getEdgeCount();
	    int incremental = (int)(orgVertexCount*percent*0.1);
	    
	    long startTime = System.currentTimeMillis();
	    
	    for(int i = 0; i < incremental; i++) {	//���ӽڵ���	
	    	String addVertex = "" + (++orgVertexCount);	// ���ӽڵ���
	    	Random random = new Random();
	    	int degree = showRandomInteger((int)(0.5*maxdegree), (int)(1.5*maxdegree), random);	// ����������ӽڵ�Ķ�
	    	Vector<Integer> sameVertex = new Vector<Integer>();
	    	for(int j = 0; j < degree; j++) {
	    		int orgId = showRandomInteger(1, orgVertexCount, random);	// ���ѡ��ԭʼ�����нڵ����������ڵ�
	    		while (sameVertex.contains(orgId)) {	//�������ظ������
	    			orgId = showRandomInteger(1, orgVertexCount, random);
	    		}
    			sameVertex.add(orgId);
    			String orgVertex = "" + orgId;
    			if (graph.findEdge(addVertex, orgVertex) == null) {
    	    		graph.addEdge(addVertex + "," + orgVertex, addVertex, orgVertex, EdgeType.UNDIRECTED);
    			}
	    	}
	    }
	    
	    long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("���Ӳ��Խڵ�: " + totalTime);
	   
	    System.out.println(graph.getVertexCount());
  		System.out.println(graph.getEdgeCount());
  		
  		long startTime2 = System.currentTimeMillis();
  		
  		StringBuilder sbdegree = new StringBuilder();
		for(String v : graph.getVertices()) {
			if (Integer.parseInt(v) <= 10000) {
				sbdegree.append(graph.degree(v));
				sbdegree.append("\n");
			}
		}
		TxtWriter.saveToFile(sbdegree.toString(), new File("file/1w/network_random/degree_" + percent + ".dat"), "UTF-8");
		
		
		long endTime2 = System.currentTimeMillis();
		long totalTime2 = endTime2 - startTime2;
		System.out.println("д���ļ�: " + totalTime2);
		
	    /*Collection<String> edges = graph.getEdges();
	    StringBuilder sbedge = new StringBuilder();
	    for(String edge : edges) {
	    	sbedge.append(edge);
	    	sbedge.append("\n");
	    }
	    
	    TxtWriter.saveToFile(sbedge.toString(), new File("file/1w/network_random/network_" + percent + ".dat"), "UTF-8");*/
	}
	
	 private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if (aStart > aEnd) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);    
	    return randomNumber;
		 
	 }
}
