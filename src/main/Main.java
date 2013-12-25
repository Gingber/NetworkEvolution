/**
 * 
 */
package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import community.ba.SingleCommunityBA;
import community.random.SingleCommunityRandom;
import util.ConstructNativeGraph;
import util.Edge;
import util.TxtReader;
import util.TxtWriter;
import util.Vertex;
import network.ba.BAGenerator;
import network.random.RandomVertexGraph;


import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;



/**
 * @author Gingber
 *
 */
public class Main {
	
	private final static String networkfile  = "file/1w/network.dat";
	private final static String communityfile  = "file/1w/community.dat";
	private final static int maxdegree = 1000;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();

		UndirectedSparseMultigraph<String, String> graph = new UndirectedSparseMultigraph<String, String>();
		List<String> edgelist = TxtReader.loadVectorFromFile(new File(networkfile), "UTF-8");
		ArrayList<String> vertexSource = new ArrayList<String>();
		ArrayList<String> vertexTarget = new ArrayList<String>();
		
		for(int i=0; i< edgelist.size(); i++)    {   
			String[] edgeArray = edgelist.get(i).split("\t");
			if (graph.findEdge(edgeArray[0], edgeArray[1]) == null) {
				graph.addEdge(edgeArray[0] + "," + edgeArray[1], edgeArray[0], edgeArray[1], EdgeType.UNDIRECTED);
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
		TxtWriter.saveToFile(sb.toString(), new File("file/10w/degree.dat"), "UTF-8");
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("¹¹ÔìÍ¼: " + totalTime);
		
		
		for(int per = 1; per <= 10; per++) {
			System.out.println("--------------" + per + "----------------");
			System.out.println(graph.getVertexCount());
			System.out.println(graph.getEdgeCount());
			
			RandomVertexGraph rvg = new RandomVertexGraph();
			rvg.RandomVertexGenerator(graph, maxdegree, per);
			
			/*// again initialization graph
			graph = new UndirectedSparseMultigraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}

			BAGenerator bag= new BAGenerator();
			bag.GenerateBarabasiAlbert(graph, maxdegree, per);
			
			// again initialization graph
			graph = new UndirectedSparseMultigraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}
			
			SingleCommunityRandom scr = new SingleCommunityRandom();
			scr.SingleCommunityRandomIncrem(graph, communityfile, maxdegree, per);
			
			// again initialization graph
			graph = new UndirectedSparseMultigraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}
			
			SingleCommunityBA scba = new SingleCommunityBA();
			scba.SingleCommunityBAGenerator(graph, communityfile, maxdegree, per);*/
			
			// again initialization graph
			graph = new UndirectedSparseMultigraph<String, String>();
			for(int i = 0; i < vertexSource.size() && i < vertexTarget.size(); i++) {
				graph.addEdge(vertexSource.get(i) + "," + vertexTarget.get(i), 
						vertexSource.get(i), vertexTarget.get(i), EdgeType.UNDIRECTED);
			}
		}
	}
}
