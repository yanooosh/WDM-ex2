
public class WebNodePair {

	String id;
	double rank;
	int count;
	
	public WebNodePair(String id, double rank) {
		this.id = id;
		this.rank = rank;
		this.count = 0;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
