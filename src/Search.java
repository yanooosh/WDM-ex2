import java.util.ArrayList;

public class Search {

	public static void main(String[] args) {
		WebGraph G = new WebGraph();
		Hits(G);

	}

	public static void Hits(WebGraph g) {
		ArrayList<WebNode> pages = g.getPages();
		for (WebNode p : pages) {
			p.auth = 1;
			p.hub = 1;
		}
		HubsAndAuthorites(pages); 
	}

	public static void HubsAndAuthorites(ArrayList<WebNode> G) {
		double norm;
		while(true){
			ArrayList<WebNode> prevG = clone(G);
		//for (int i = 0; i < k; i++) {
			norm = 0;
			// update all authority values first
			for (WebNode p : G) {
				p.auth = 0;
				// p.incomingNeighbors is the set of pages that link to p
				for (WebNode q : p.getIncomingNeighbors()) {
					p.auth += q.hub;
				}
				
				norm += p.auth * p.auth; // calculate the sum of the squared
											// auth values to normalise
			}
			
			norm = Math.sqrt(norm);
			for (WebNode p : G) {
				p.auth = p.auth / norm; // normalise the auth values
			}
			
			norm = 0;
			for (WebNode p : G) {
				p.hub = 0;
				for (WebNode r : p.getOutgoingNeighbors()) {
					// p.outgoingNeighbors is the set of pages that p links to
					p.hub += r.auth;
				}
				
				norm += p.hub * p.hub; // calculate the sum of the squared hub
										// values to normalise
			}
			
			norm = Math.sqrt(norm);
			for (WebNode p : G) {
				p.hub = p.hub / norm;
			}
			if (SmallerThanEpsilon(G,prevG)) break;
		}
	}

	private static boolean SmallerThanEpsilon(ArrayList<WebNode> g, ArrayList<WebNode> prevG) {
		boolean flag = true;
		double epsilon = 0.0000000000001;
		for (WebNode n : g){
			for (WebNode m : prevG)
			{
				if (n.getUrl().toString().compareTo(m.getUrl().toString()) == 0 &&
						(Math.abs(n.hub - m.hub) >= epsilon ||
						Math.abs(n.auth - m.auth) >= epsilon)){
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	private static ArrayList<WebNode> clone(ArrayList<WebNode> g) {
		ArrayList<WebNode> prevG = new ArrayList<WebNode>();
		for (WebNode p : g) {
			WebNode w = new WebNode(p.getUrl());
			w.auth = p.auth;
			w.hub = p.hub;
		}
		return prevG;
	}
}
