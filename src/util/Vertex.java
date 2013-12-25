package util;

public class Vertex {
	private int id;

	public Vertex(int id) {
		this.id = id;
	}

	public String toString() {
		return "V" + id;
		//return id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}