import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Collections;


public class InvertedIndexNode
{

	List<WebNodePair> pages;
	
	public InvertedIndexNode()
	{
		this.pages = new ArrayList<WebNodePair>();
	}
	
	public void addUrl(URL url, double count)
	{
		List<WebNodePair> newMap = new ArrayList<WebNodePair>();
		boolean isInserted = false;
		
		if (this.pages.contains(new WebNodePair(url.toExternalForm(), count)))
		{
			return;		
		}
		

		
		newMap.add(new WebNodePair(url.toExternalForm(), count));		
		
		this.pages = newMap;
	}
	
	public void addUrl(String url, double count)
	{
		try
		{
			addUrl(new URL(url), count);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<WebNodePair> getPages()
	{
		//List<WebNodePair> list = new ArrayList<WebNodePair>();
		Collections.sort(this.pages, Collections.reverseOrder(new WebNodePairComparator()));
		return this.pages;
	}
	
	public double getCountByURL(URL url)
	{
		for (WebNodePair pair : this.pages)
		{
			if (pair.id.equals(url.toExternalForm()))
			{
				return pair.rank;
			}
		}
		return -1;
	}
	
	public double getCountByURL(String url)
	{
		double count = 0;
		try
		{
			count = this.getCountByURL(new URL(url));
			
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
	
	public List<WebNodePair> getTopPages(int x)
	{
		List<WebNodePair> newMap = getPages();
		
		return newMap.subList(0, x);
	}
	
	public boolean containsPair(String url)
	{
		for (WebNodePair pair : this.pages)
		{
			if (pair.id.equals(url))
			{
				return true;
			}
		}
		return false;
	}
}
