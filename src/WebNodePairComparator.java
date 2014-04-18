import java.util.Comparator;


public class WebNodePairComparator implements Comparator<WebNodePair> {

	@Override
	public int compare(WebNodePair a, WebNodePair b) {
		return a.rank < b.rank ? -1 : a.rank == b.rank ? 0 : 1;
	}

}
