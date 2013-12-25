package util;

import java.util.HashMap;


public class Edge {
	static int edgeCount = 0;
	int id;
	Vertex from, to;
	
	public Edge(Vertex from, Vertex to) {
		this.from = from;
		this.to = to;
		this.id = ++edgeCount;
	}

	public Edge(int id) {
		this.id = id;
	}

	public String toString() {
		return "E" + id;
		//return from + " -> " + to;
	}

	public Vertex getFrom() {
		return from;
	}

	public void setFrom(Vertex from) {
		this.from = from;
	}

	public Vertex getTo() {
		return to;
	}

	public void setTo(Vertex to) {
		this.to = to;
	}
	
	
}