import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebNode
{

	private URL url;
	private ArrayList<WebNode> links;

	public WebNode()
	{
		this.url = null;
		this.links = null;
	}

	public WebNode(String url)
	{
		try
		{
			this.url = new URL(url);
			this.links = new ArrayList<WebNode>();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WebNode(URL url)
	{
		this.url = url;
		this.links = new ArrayList<WebNode>();
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public ArrayList<WebNode> getLinks()
	{
		return links;
	}

	public void setLinks(ArrayList<WebNode> links)
	{
		this.links = links;
	}

	public int addLink(WebNode link)
	{
		if (this.url == null)
		{
			return -1;
		}
		
		if (this.links == null)
		{
			this.links = new ArrayList<WebNode>();
		}
		
		this.links.add(link);
		
		return 0;
	}

}
