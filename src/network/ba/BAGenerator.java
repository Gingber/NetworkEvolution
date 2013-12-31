/**
 * 
 */
package network.ba;


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

import util.Edge;
import util.Factories;
import util.LFRNetwork;
import util.TxtWriter;
import util.Vertex;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * @author Gingber
 *
 */
public class BAGenerator {
	
	private int mElapsedTimeSteps;
	private int random_seed = 0;
	private int num_tests = 1000;
	private int num_timesteps = 100;
	private Graph<String, String> mGraph = null;
    private int mNumEdgesToAttachPerStep = 1;
    private Random mRandom;
    protected List<String> vertex_index;
    protected Map<String, Integer> index_vertex;
    private final static int nodeNum = 100000;
    
	
    LFRNetwork graphInfo =null; 
	Vector<LFRNetwork> graphInfos = new Vector<LFRNetwork>();
	
	public void GenerateBarabasiAlbert(UndirectedSparseGraph<String, String> graph, int maxdegree, int percent) throws IOException {
		
        mGraph = graph;
        initialize(graph);
        mRandom = new Random(random_seed);
                     
        for (int i = 1; i <= num_tests; i++) {
            evolveGraph(num_timesteps*percent, maxdegree);
        }
        
        System.out.println(graph.getVertexCount());
  		System.out.println(graph.getEdgeCount());
  		
  		AverageClusteringCoefficientCalculator avgcc = new AverageClusteringCoefficientCalculator(graph);
		avgcc.getCC();
		TxtWriter.appendToFile(avgcc.getCC() + ", ", new File("file/100w/network_ba/AverageClusteringCoefficient.dat"), "UTF-8");
		
		
/*		Transformer<String, Double> distances = DistanceStatistics.averageDistances(graph);
		double sum = 0;
		for(String v : graph.getVertices()) {
			//System.out.println(v + "\t" + distances.transform(v).doubleValue());
			sum += distances.transform(v).doubleValue();
		}
		sum = sum/graph.getEdgeCount();
		TxtWriter.appendToFile(sum + ", ", new File("file/10w/network_ba/averageDistances.dat"), "UTF-8");
*/  		
  		StringBuilder sbdegree = new StringBuilder();
		for(String v : graph.getVertices()) {
			if (Integer.parseInt(v) <= nodeNum) {
				sbdegree.append(graph.degree(v));
				sbdegree.append("\n");
			}
		}
		TxtWriter.saveToFile(sbdegree.toString(), new File("file/100w/network_ba/degree_" + percent + ".dat"), "UTF-8");	

		
	    Collection<String> edges = graph.getEdges();
	    StringBuilder sbedge = new StringBuilder();
	    for(String edge : edges) {
	    	Pair<String> nodepair = graph.getEndpoints(edge);
	    	sbedge.append(nodepair.getFirst());
	    	sbedge.append(",");
	    	sbedge.append(nodepair.getSecond());
	    	sbedge.append("\n");
	    }
	    
	    TxtWriter.saveToFile(sbedge.toString(), new File("file/100w/network_ba/edge_" + percent + ".dat"), "UTF-8");
	}
	
	 
	private void initialize(Graph<String, String> graph) {
	    
        vertex_index = new ArrayList<String>();
        index_vertex = new HashMap<String, Integer>();
        int i = 0;
        for (String v : graph.getVertices()) {
        	 vertex_index.add(v);
             index_vertex.put(v, i++);
        }
            
        mElapsedTimeSteps = 0;
    }

    private void createRandomEdge(Collection<String> preexistingNodes,
    	String newVertex, Set<Pair<String>> added_pairs, int maxdegree) {
    	String attach_point;
        boolean created_edge = false;
        Pair<String> endpoints;
        int newVertexDegree = showRandomInteger((int)(0.5*maxdegree), (int)(1.5*maxdegree), mRandom);	// 随机生成增加节点的度
        for(int i = 0; i < newVertexDegree; i++) {
        	do {
            	attach_point = vertex_index.get(mRandom.nextInt(vertex_index.size()));
                
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

    public void evolveGraph(int numTimeSteps, int maxdegree) {

        for (int i = 0; i < numTimeSteps; i++) {
            evolveGraph(maxdegree);
            mElapsedTimeSteps++;
        }
    }

    private void evolveGraph(int maxdegree) {
        Collection<String> preexistingNodes = mGraph.getVertices();
        String newVertex = "" + (mGraph.getVertexCount() + 1);

        mGraph.addVertex(newVertex);

        // generate and store the new edges; don't add them to the graph
        // yet because we don't want to bias the degree calculations
        // (all new edges in a timestep should be added in parallel)
        Set<Pair<String>> added_pairs = new HashSet<Pair<String>>(mNumEdgesToAttachPerStep*3);
        
        for (int i = 0; i < mNumEdgesToAttachPerStep; i++) 
        	createRandomEdge(preexistingNodes, newVertex, added_pairs, maxdegree);
        
        for (Pair<String> pair : added_pairs)
        {
    		String v1 = pair.getFirst();
        	String v2 = pair.getSecond();
        	if (mGraph.getDefaultEdgeType() != EdgeType.UNDIRECTED || 
        			!mGraph.isNeighbor(v1, v2))
        		mGraph.addEdge(v1 + "," + v2, pair);
        }
        vertex_index.add(newVertex);
        index_vertex.put(newVertex, new Integer(vertex_index.size() - 1));
    }

    public int numIterations() {
        return mElapsedTimeSteps;
    }

    public Graph<String, String> create() {
        return mGraph;
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
