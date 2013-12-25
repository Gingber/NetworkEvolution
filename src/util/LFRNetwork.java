/**
 * 
 */
package util;


/**
 * @author Gingber
 *
 */
public class LFRNetwork {
	private String vertex;
	private int community;
	private int degree;
	private int indegree;
	private int outdegree;
	

	public LFRNetwork(String vertex, int community, int degree) {
		super();
		this.vertex = vertex;
		this.community = community;
		this.degree = degree;
	}
	public LFRNetwork(String vertex, int degree, int indegree,
			int outdegree) {
		super();
		this.vertex = vertex;
		this.degree = degree;
		this.indegree = indegree;
		this.outdegree = outdegree;
	}
	public String getVertex() {
		return vertex;
	}
	public void setVertex(String vertex) {
		this.vertex = vertex;
	}
	public int getCommunity() {
		return community;
	}
	public void setCommunity(int community) {
		this.community = community;
	}
	public int getDegree() {
		return degree;
	}
	public void setDegree(int degree) {
		this.degree = degree;
	}
	public int getIndegree() {
		return indegree;
	}
	public void setIndegree(int indegree) {
		this.indegree = indegree;
	}
	public int getOutdegree() {
		return outdegree;
	}
	public void setOutdegree(int outdegree) {
		this.outdegree = outdegree;
	}
}
