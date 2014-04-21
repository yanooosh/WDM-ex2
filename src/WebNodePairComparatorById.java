import java.util.Comparator;

public class WebNodePairComparatorById implements Comparator<WebNodePair> {

	@Override
	public int compare(WebNodePair a, WebNodePair b) {
		return a.id.compareTo(b.id);
	}

}