import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebGraph {

	private ArrayList<WebNode> pages;
	
	public WebGraph()
	{
		this.pages = new ArrayList<WebNode>();
	}
	
	public WebGraph(ArrayList<WebNode> pages)
	{
		this.pages = pages;
	}

	public ArrayList<WebNode> getPages()
	{
		return this.pages;
	}
	
	public WebNode getPage(URL url)
	{
		for (WebNode node : this.pages)
		{
			if (node.getUrl().equals(url))
			{
				return node;
			}
		}
		
		return null;
	}
	
	public WebNode getPage(String url)
	{
		WebNode node = null;
		try
		{
			node = getPage(new URL(url));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return node;
	}

	public void setPages(ArrayList<WebNode> pages)
	{
		this.pages = pages;
	}
	
	public void addPage(WebNode page)
	{
		for (WebNode node : this.pages)
		{
			if (node.getUrl().equals(page.getUrl()))
			{
				return;
			}
		}
		this.pages.add(page);
	}
	
	public WebNode getNextUnVisitedPage()
	{
		for (WebNode node : this.pages)
		{
			if (!node.isVisited())
			{
				return node;
			}
		}
		
		return null;
	}
	
}
