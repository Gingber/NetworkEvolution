package main;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.graph.Graph;

public class AverageClusteringCoefficientCalculator<V, E> {
	
	Graph<V, E> graph;
	
	//constructor
	public AverageClusteringCoefficientCalculator(Graph<V, E> g){
		
		this.graph = g;
		
	}
	
	//calculator
	public double getCC(){
		
		Metrics m = new Metrics();
		
		Map<V, Double> mapOfVAndCCs = m.clusteringCoefficients(graph);
		Collection<Double> CCs = mapOfVAndCCs.values();
		Iterator<Double> it = CCs.iterator();
		double CCSum = 0;
		
		//sum of CC
		while(it.hasNext()){
			CCSum += it.next();
		}
		
		int num = graph.getVertexCount();
		
		return CCSum/num;
	}

}
