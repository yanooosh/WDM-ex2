
public class WebNodePair {

	String id;
	double rank;
	
	public WebNodePair(String id, double rank) {
		this.id = id;
		this.rank = rank;
	}
	
	@Override
	public String toString() {
		return id + " " + rank;
	}
	
}
