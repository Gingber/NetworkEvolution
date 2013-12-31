/**
 * 
 */
package community.ba;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import main.AverageClusteringCoefficientCalculator;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import util.Edge;
import util.Factories;
import util.LFRNetwork;
import util.TxtReader;
import util.TxtWriter;
import util.Vertex;

/**
 * @author Gingber
 *
 */
public class SingleCommunityBA {
	
	private static int maxCommunityNumber = 0;
	private Graph<String, String> mGraph = null;
	private int mElapsedTimeSteps;
	private int mNumEdgesToAttachPerStep = 10;
	private Random mRandom;
	private int random_seed = 0;
	private final static int nodeNum = 1000000;
	private final static double scalePercent = 0.1;
	
	public void SingleCommunityBAGenerator(UndirectedSparseGraph<String, String> graph, String filename,
			int maxdegree, int percent) throws IOException {
		
		mGraph = graph;	// 图的全局变量
		mRandom = new Random(random_seed);

		LFRNetwork lfrnetwork = null;
		Vector<LFRNetwork> lfrNetworks = new Vector<LFRNetwork>();
		
		Map<String, Integer> vertexCommunity = new HashMap<String, Integer>();
		List<String> list = TxtReader.loadVectorFromFile(new File(filename), "UTF-8");
		
		for (int i = 0; i < list.size(); i++) {
			String[] doublevetrix = list.get(i).split("\t");
			String vertex = doublevetrix[0];
			int community = Integer.parseInt(doublevetrix[1]);
			vertexCommunity.put(vertex, community);
			if (maxCommunityNumber < community)
				maxCommunityNumber = community;
		}
		
		// 建立社区与节点的对应关系
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
			List<String> vertex_index = new ArrayList<String>();
			for(int j = 0; j < lfrNetworks.size(); j++) {
				// 获取选定社区中的节点，为其建立索引
				if (lfrNetworks.get(j).getCommunity() == randomCommunity) {
					index_vertex.put(index++, lfrNetworks.get(j).getVertex());	//随机选定的社区内的节点
					vertex_index.add(lfrNetworks.get(j).getVertex());
				}
			}

			// 重构网络
			evolveGraph(vertex_index, index_vertex);
	    }

	    System.out.println(graph.getVertexCount());
		System.out.println(graph.getEdgeCount());
		
		AverageClusteringCoefficientCalculator avgcc = new AverageClusteringCoefficientCalculator(graph);
		avgcc.getCC();
		TxtWriter.appendToFile(avgcc.getCC() + ", ", new File("file/100w/community_ba/AverageClusteringCoefficient.dat"), "UTF-8");
	
		
/*		Transformer<String, Double> distances = DistanceStatistics.averageDistances(graph);
		double sum = 0;
		for(String v : graph.getVertices()) {
			//System.out.println(v + "\t" + distances.transform(v).doubleValue());
			sum += distances.transform(v).doubleValue();
		}
		sum = sum/graph.getEdgeCount();
		TxtWriter.appendToFile(sum + ", ", new File("file/10w/community_ba/averageDistances.dat"), "UTF-8");
*/		
		StringBuilder sbdegree = new StringBuilder();
		for(String v : graph.getVertices()) {
			if (Integer.parseInt(v) <= nodeNum) {
				sbdegree.append(graph.degree(v));
				sbdegree.append("\n");
			}
		}
		TxtWriter.saveToFile(sbdegree.toString(), new File("file/100w/community_ba/degree_" + percent + ".dat"), "UTF-8");
		
		
		Collection<String> edges = graph.getEdges();
		StringBuilder sbedge = new StringBuilder();
		for(String edge : edges) {
	    	Pair<String> nodepair = graph.getEndpoints(edge);
	    	sbedge.append(nodepair.getFirst());
	    	sbedge.append(",");
	    	sbedge.append(nodepair.getSecond());
	    	sbedge.append("\n");
	    }

		TxtWriter.saveToFile(sbedge.toString(), new File("file/100w/community_ba/edge_" + percent + ".dat"), "UTF-8");
	}
	
	public int[] randomChooseCommunity() {
		int[] intRet = new int[10]; 
        int intRd = 0; //存放随机数
        int count = 0; //记录生成的随机数个数
        int flag = 0; //是否已经生成过标志
        while(count < intRet.length) {
        	Random rdm = new Random(System.currentTimeMillis());
            intRd = Math.abs(rdm.nextInt())%maxCommunityNumber;
            for(int i = 0; i < count; i++) { 
            	if(intRet[i] == intRd) {
            		flag = 1;
                    break;
                } else {
                    flag = 0;
                }
            }
            if(flag == 0) {
                intRet[count] = intRd;
                count++;
            }
        }
        return intRet;
	}
	
	private void createRandomEdge(List<String> preexistingNodes,
    	String newVertex, Set<Pair<String>> added_pairs) {
    	String attach_point;
        boolean created_edge = false;
        Pair<String> endpoints;
        int newVertexDegree = showRandomInteger(1, preexistingNodes.size(), mRandom);	// 随机生成增加节点的度 
        for(int i = 0; i < newVertexDegree; i++) {
        	do {
            	attach_point = preexistingNodes.get(mRandom.nextInt(preexistingNodes.size()));
                
                endpoints = new Pair<String>(newVertex, attach_point);
                
                // if parallel edges are not allowed, skip attach_point if <newVertex, attach_point>
                // already exists; note that because of the way edges are added, we only need to check
                // the list of candidate edges for duplicates.
                if (!(mGraph instanceof MultiGraph))
                {
                	if (added_pairs.contains(endpoints))
                		continue;
                	if (mGraph.getDefaultEdgeType() == EdgeType.UNDIRECTED && 
                		added_pairs.contains(new Pair<String>(attach_point, newVertex)))
                		continue;
                }

                double degree = mGraph.inDegree(attach_point); //无向图中度、出度、入度相同
                
                // subtract 1 from numVertices because we don't want to count newVertex
                // (which has already been added to the graph, but not to vertex_index)
                double attach_prob = (degree + 1) / (mGraph.getEdgeCount() + mGraph.getVertexCount() - 1);
                if (attach_prob >= mRandom.nextDouble())
                    created_edge = true;
            }
            while (!created_edge);

            added_pairs.add(endpoints);
            
            if (mGraph.getDefaultEdgeType() == EdgeType.UNDIRECTED) {
            	added_pairs.add(new Pair<String>(attach_point, newVertex));
            }
        }
    }

    private void evolveGraph(List<String> vertex_index, Map<Integer, String> index_vertex) {
    	List<String> preexistingNodes = vertex_index;
    	String newVertex = "" + (mGraph.getVertexCount() + 1);

        mGraph.addVertex(newVertex);

        // generate and store the new edges; don't add them to the graph
        // yet because we don't want to bias the degree calculations
        // (all new edges in a timestep should be added in parallel)
        Set<Pair<String>> added_pairs = new HashSet<Pair<String>>(mNumEdgesToAttachPerStep*3);
        
        //for (int i = 0; i < mNumEdgesToAttachPerStep; i++) 
        	createRandomEdge(preexistingNodes, newVertex, added_pairs);
        
        for (Pair<String> pair : added_pairs)
        {
    		String v1 = pair.getFirst();
        	String v2 = pair.getSecond();
        	if (mGraph.getDefaultEdgeType() != EdgeType.UNDIRECTED || 
        			!mGraph.isNeighbor(v1, v2))
        		mGraph.addEdge(v1 + "," + v2, pair);
        }
        vertex_index.add(newVertex);
        index_vertex.put(new Integer(vertex_index.size() - 1), newVertex);
    }

    public int numIterations() {
        return mElapsedTimeSteps;
    }

    public Graph<String, String> create() {
        return mGraph;
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
