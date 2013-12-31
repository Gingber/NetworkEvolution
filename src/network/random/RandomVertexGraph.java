/**
 * 
 */
package network.random;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import main.AverageClusteringCoefficientCalculator;

import org.apache.commons.collections15.Transformer;

import util.Edge;
import util.TxtWriter;
import util.Vertex;


import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author Gingber
 *
 */
public class RandomVertexGraph {
	
	private final static double scalePercent = 0.1;
	private final static int nodeNum = 1000000;
	
	public void RandomVertexGenerator(UndirectedSparseGraph<String, String> graph, int degree, int percent) throws IOException {
		 //�������Ե��������ԭʼ������
	    int orgVertexCount = graph.getVertexCount();
	    int orgEdgeCount = graph.getEdgeCount();
	    int incremental = (int)(orgVertexCount*percent*scalePercent);
	    
	    Random mRandom = new Random();
	    
		int maxdegree = (int)(1.5*degree);
		int mindegree = (int)(0.5*degree);
	    for(int i = 0; i < incremental; i++) {	//���ӽڵ���
	    	String addVertex = "" + (graph.getVertexCount() + 1);	// ���ӽڵ���
	    	Integer newdegree = showRandomInteger(mindegree, maxdegree, mRandom);	// ����������ӽڵ�Ķ�

	    	for(int j = 0; j < newdegree; j++) {
	    		Integer orgId = showRandomInteger(1, orgVertexCount, mRandom);	// ���ѡ��ԭʼ�����нڵ����������ڵ�
	    		Vector<Integer> sameVertex = new Vector<Integer>();
	    		while (sameVertex.contains(orgId)) {	//�����ظ������
	    			orgId = showRandomInteger(1, orgVertexCount, mRandom);
	    		}
    			sameVertex.add(orgId);
    			String orgVertex = "" + orgId;
    			if (graph.findEdge(addVertex, orgVertex) == null) {
    				String edge = "E" + (graph.getEdgeCount()+1);
    	    		graph.addEdge(edge, addVertex, orgVertex, EdgeType.UNDIRECTED);
    			}
    			sameVertex=null;
	    	}
	    }

	    System.out.println(graph.getVertexCount());
  		System.out.println(graph.getEdgeCount());
  		
  		AverageClusteringCoefficientCalculator avgcc = new AverageClusteringCoefficientCalculator(graph);
		avgcc.getCC();
		TxtWriter.appendToFile(avgcc.getCC() + ", ", new File("file/100w/network_random/AverageClusteringCoefficient.dat"), "UTF-8");
		
		
/*		Transformer<String, Double> distances = DistanceStatistics.averageDistances(graph);
		double sum = 0;
		for(String v : graph.getVertices()) {
			//System.out.println(v + "\t" + distances.transform(v).doubleValue());
			sum += distances.transform(v).doubleValue();
		}
		sum = sum/graph.getEdgeCount();
		TxtWriter.appendToFile(sum + ", ", new File("file/10w/network_random/averageDistances.dat"), "UTF-8");
*/  		
  		StringBuilder sbdegree = new StringBuilder();
		for(String v : graph.getVertices()) {
			if (Integer.parseInt(v) <= nodeNum) {
				sbdegree.append(graph.degree(v));
				sbdegree.append("\n");
			}
		}
		TxtWriter.saveToFile(sbdegree.toString(), new File("file/100w/network_random/degree_" + percent + ".dat"), "UTF-8");
		

	    Collection<String> edges = graph.getEdges();
	    StringBuilder sbedge = new StringBuilder();
	    for(String edge : edges) {
	    	Pair<String> nodepair = graph.getEndpoints(edge);
	    	sbedge.append(nodepair.getFirst());
	    	sbedge.append(",");
	    	sbedge.append(nodepair.getSecond());
	    	sbedge.append("\n");
	    }
	    
	    TxtWriter.saveToFile(sbedge.toString(), new File("file/100w/network_random/edge_" + percent + ".dat"), "UTF-8");
	}
	
	 private Integer showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if (aStart > aEnd) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	 /*   //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);  */  
	    
	    Integer randomNumber = aRandom.nextInt(aEnd - aStart +1) + aStart;

	    return randomNumber;
		 
	 }
}
