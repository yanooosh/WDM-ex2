import java.util.Comparator;


public class WebNodePairComparator implements Comparator<WebNodePair> {

	@Override
	public int compare(WebNodePair a, WebNodePair b) {
		if (a.rank == b.rank) return 0;
		return a.rank > b.rank ? 1 : -1;
	}

}
