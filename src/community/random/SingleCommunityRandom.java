/**
 * 
 */
package community.random;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import main.AverageClusteringCoefficientCalculator;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import util.Edge;
import util.LFRNetwork;
import util.TxtReader;
import util.TxtWriter;
import util.Vertex;

/**
 * @author Gingber
 *
 */
public class SingleCommunityRandom {
	
	private final static double scalePercent = 0.1;
    private final static int nodeNum = 1000000;
	
	public void SingleCommunityRandomIncrem(UndirectedSparseGraph<String, String> graph, String filename, 
			int maxdegree, int percent) throws IOException {
		
		LFRNetwork lfrnetwork = null;
		Vector<LFRNetwork> lfrNetworks = new Vector<LFRNetwork>();
		
		Map<String, Integer> vertexCommunity = new HashMap<String, Integer>();
		List<String> list = TxtReader.loadVectorFromFile(new File(filename), "UTF-8");
		int maxCommunityNumber = 0;
		for (int i = 0; i < list.size(); i++) {
			String[] doublevetrix = list.get(i).split("\t");
			String vertex = doublevetrix[0];
			int community = Integer.parseInt(doublevetrix[1]);
			vertexCommunity.put(vertex, community);
			if (maxCommunityNumber < community)
				maxCommunityNumber = community;
		}
		
		for (String v : graph.getVertices()) {
        	lfrnetwork = new LFRNetwork(v, vertexCommunity.get(v), graph.degree(v));
        	lfrNetworks.add(lfrnetwork);
        }
		
		int orgVertexCount = graph.getVertexCount();
	    int orgEdgeCount = graph.getEdgeCount();
	    int incremental = (int)(orgVertexCount*percent*scalePercent);
	    
	    for(int i = 0; i < incremental; i++) {	//增加节点数
	    	Random random = new Random();
	    	// 随机选择一个社区
	    	int randomCommunity = showRandomInteger(1, maxCommunityNumber, random);
	    	
	    	int index = 0;
			Map<Integer, String> index_vertex = new HashMap<Integer, String>();
			for(int j = 0; j < lfrNetworks.size(); j++) {
				// 获取选定社区中的节点，为其建立索引
				if (lfrNetworks.get(j).getCommunity() == randomCommunity) {
					index_vertex.put(index++, lfrNetworks.get(j).getVertex());
				}
			}
			
			ArrayList<Integer> linkVertex = createRandom(index_vertex.size());
			String newvertex = "" + (graph.getVertexCount()+1);
			
			for(int t = 0; t < linkVertex.size(); t++) {	// 数组linkVertex长度等于新增节点的度
				String attach_point = index_vertex.get(linkVertex.get(t));
				graph.addEdge(attach_point + "," + newvertex, attach_point, newvertex, EdgeType.UNDIRECTED);
			}  	
	    }
 
		System.out.println(graph.getVertexCount());
  		System.out.println(graph.getEdgeCount());
  		
  		AverageClusteringCoefficientCalculator avgcc = new AverageClusteringCoefficientCalculator(graph);
		avgcc.getCC();
		TxtWriter.appendToFile(avgcc.getCC() + ", ", new File("file/100w/community_random/AverageClusteringCoefficient.dat"), "UTF-8");
		
		
/*		Transformer<String, Double> distances = DistanceStatistics.averageDistances(graph);
		double sum = 0;
		for(String v : graph.getVertices()) {
			//System.out.println(v + "\t" + distances.transform(v).doubleValue());
			sum += distances.transform(v).doubleValue();
		}
		sum = sum/graph.getEdgeCount();
		TxtWriter.appendToFile(sum + ", ", new File("file/10w/community_random/averageDistances.dat"), "UTF-8");
*/  		
  		StringBuilder sbdegree = new StringBuilder();
		for(String v : graph.getVertices()) {
			if (Integer.parseInt(v) <= nodeNum) {
				sbdegree.append(graph.degree(v));
				sbdegree.append("\n");
			}
		}
		TxtWriter.saveToFile(sbdegree.toString(), new File("file/100w/community_random/degree_" + percent + ".dat"), "UTF-8");		
	
		
	    Collection<String> edges = graph.getEdges();
	    StringBuilder sbedge = new StringBuilder();
	    for(String edge : edges) {
	    	Pair<String> nodepair = graph.getEndpoints(edge);
	    	sbedge.append(nodepair.getFirst());
	    	sbedge.append(",");
	    	sbedge.append(nodepair.getSecond());
	    	sbedge.append("\n");
	    }
	    
	    TxtWriter.saveToFile(sbedge.toString(), new File("file/100w/community_random/edge_" + percent + ".dat"), "UTF-8");
}
	
	public ArrayList<Integer> createRandom(int singleCommunityScale) {
		Random random = new Random();
		int singleDegree = showRandomInteger(1, singleCommunityScale, random);
		
		ArrayList<Integer> attachRet = new  ArrayList<Integer>();
		
		int intRd = 0; //存放随机数
        int count = 0; //记录生成的随机数个数
        while(count < singleDegree) {
        	Random rdm = new Random(System.currentTimeMillis());
            intRd = Math.abs(rdm.nextInt())%singleCommunityScale;
            
            if(attachRet.contains(intRd)) {
            	continue;
            }
            else {
            	attachRet.add(intRd);
            	count++;
            }
        }
        return attachRet;
	}
	
	private static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
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
