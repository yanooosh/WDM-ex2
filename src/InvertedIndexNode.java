import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;



public class InvertedIndexNode
{

	HashMap<URL, Double> pages;
	
	public InvertedIndexNode()
	{
		this.pages = new LinkedHashMap<URL, Double>();
	}
	
	public void addUrl(URL url, double count)
	{
		HashMap<URL, Double> newMap = new LinkedHashMap<URL, Double>();
		boolean isInserted = false;
		
		if (this.pages.containsKey(url))
		{
			if (this.pages.get(url) == count)
			{
				return;
			}
			
			this.pages.remove(url);
			
		}
		
		if (this.pages.size() == 0)
		{
			newMap.put(url, count);
		}
		else
		{
		
			for (Entry<URL, Double> entry : this.pages.entrySet())
			{
				if (entry.getValue() < count && !isInserted)
				{
					newMap.put(url, count);
					isInserted = true;
				}
				
				newMap.put(entry.getKey(), entry.getValue());
			}
		}
		
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
	
	public HashMap<URL, Double> getPages()
	{
		return this.pages;
	}
	
	public double getCountByURL(URL url)
	{
		return this.pages.get(url);
	}
	
	public double getCountByURL(String url)
	{
		double count = 0;
		try
		{
			count = this.pages.get(new URL(url));
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
	
	public HashMap<URL, Double> getTopPages(int x)
	{
		int i = 1;
		HashMap<URL, Double> newMap = new LinkedHashMap<URL, Double>();
		
		for (Entry<URL, Double> entry : this.pages.entrySet())
		{
			newMap.put(entry.getKey(), entry.getValue());
			
			if (i == x)
			{
				break;
			}
			i++;
			
		}
		
		return newMap;
	}
}
