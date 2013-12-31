/**
 * 
 */
package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import community.ba.SingleCommunityBA;
import community.random.SingleCommunityRandom;
import util.ConstructNativeGraph;
import util.Edge;
import util.TxtReader;
import util.TxtWriter;
import util.Vertex;
import network.ba.BAGenerator;
import network.random.RandomVertexGraph;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;



/**
 * @author Gingber
 *
 */
public class Main {
	
	private final static String networkfile  = "file/100w/network.dat";
	private final static String communityfile  = "file/100w/community.dat";
	private final static int maxdegree = 50;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();

		UndirectedSparseGraph<String, String> graph = new UndirectedSparseGraph<String, String>();
		List<String> edgelist = TxtReader.loadVectorFromFile(new File(networkfile), "UTF-8");
		ArrayList<String> vertexSource = new ArrayList<String>();
		ArrayList<String> vertexTarget = new ArrayList<String>();
		for(int i=0; i< edgelist.size(); i++)    {   
			String[] edgeArray = edgelist.get(i).split("\t");
			if (graph.findEdge(edgeArray[0], edgeArray[1]) == null) {
				String edge = "E" + (graph.getEdgeCount()+1);
				graph.addEdge(edge, edgeArray[0], edgeArray[1], EdgeType.UNDIRECTED);
				vertexSource.add(edgeArray[0]);
				vertexTarget.add(edgeArray[1]);
			}
		}
		System.out.println(graph.getVertexCount());
		System.out.println(graph.getEdgeCount());
		
		StringBuilder sb = new StringBuilder();
		for(String v : graph.getVertices()) {
			sb.append(graph.degree(v));
			sb.append("\n");
		}
		TxtWriter.saveToFile(sb.toString(), new File("file/100w/degree.dat"), "UTF-8");
		
		AverageClusteringCoefficientCalculator avgcc = new AverageClusteringCoefficientCalculator(graph);
		avgcc.getCC();
		TxtWriter.saveToFile(Double.toString(avgcc.getCC()), new File("file/100w/AverageClusteringCoefficient.dat"), "UTF-8");
		
/*		Transformer<String, Double> distances = DistanceStatistics.averageDistances(graph);
		double sum = 0;
		for(String v : graph.getVertices()) {
			//System.out.println(v + "\t" + distances.transform(v).doubleValue());
			sum += distances.transform(v).doubleValue();
		}
		sum = sum/graph.getEdgeCount();
		TxtWriter.saveToFile(Double.toString(sum), new File("file/10w/averageDistances.dat"), "UTF-8");
*/		
		Collection<String> edges = graph.getEdges();
	    StringBuilder sbedge = new StringBuilder();
	    for(String edge : edges) {
	    	Pair<String> nodepair = graph.getEndpoints(edge);
	    	sbedge.append(nodepair.getFirst());
	    	sbedge.append(",");
	    	sbedge.append(nodepair.getSecond());
	    	sbedge.append("\n");
	    }
	    
	    TxtWriter.saveToFile(sbedge.toString(), new File("file/100w/edge.dat"), "UTF-8");
		
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("ππ‘ÏÕº: " + totalTime);
		
		for(int per = 1; per <= 10; per++) {
			System.out.println("--------------" + per + "----------------");
			System.out.println(graph.getVertexCount());
			System.out.println(graph.getEdgeCount());
			
			RandomVertexGraph rvg = new RandomVertexGraph();
			rvg.RandomVertexGenerator(graph, maxdegree, per);
			
			// again initialization graph
			graph = new UndirectedSparseGraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}

			BAGenerator bag= new BAGenerator();
			bag.GenerateBarabasiAlbert(graph, maxdegree, per);
			
			// again initialization graph
			graph = new UndirectedSparseGraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}
			
			SingleCommunityRandom scr = new SingleCommunityRandom();
			scr.SingleCommunityRandomIncrem(graph, communityfile, maxdegree, per);
			
			// again initialization graph
			graph = new UndirectedSparseGraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}
			
			SingleCommunityBA scba = new SingleCommunityBA();
			scba.SingleCommunityBAGenerator(graph, communityfile, maxdegree, per);
			
			// again initialization graph
			graph = new UndirectedSparseGraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				String edge = "E" + (graph.getEdgeCount()+1);
				graph.addEdge(edge, vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}
		}
	}
}
