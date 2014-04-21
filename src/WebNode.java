import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebNode
{

	private URL url;
	private ArrayList<WebNode> outgoingNeighbors;// incomingNeighbors
	private ArrayList<WebNode> incomingNeighbors; // TODO:update class
	double auth;
	double hub;
	private boolean visited;

	public WebNode()
	{
		this.url = null;
		this.outgoingNeighbors = null;
		this.incomingNeighbors = null;
		visited = false;
	}

	public WebNode(String url)
	{
		try
		{
			this.url = new URL(url);
			this.outgoingNeighbors = new ArrayList<WebNode>();
			this.incomingNeighbors = new ArrayList<WebNode>();
			visited = false;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	public WebNode(URL url)
	{
		this.url = url;
		this.outgoingNeighbors = new ArrayList<WebNode>();
		this.incomingNeighbors = new ArrayList<WebNode>();
		visited = false;
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public ArrayList<WebNode> getOutgoingNeighbors()
	{
		return outgoingNeighbors;
	}

	public void setOutgoingNeighbors(ArrayList<WebNode> links)
	{
		this.outgoingNeighbors = links;
	}

	public ArrayList<WebNode> getIncomingNeighbors()
	{
		return incomingNeighbors;
	}

	public void setIncomingNeighbors(ArrayList<WebNode> incomingNeighbors)
	{
		this.incomingNeighbors = incomingNeighbors;
	}

	public int addLink(WebNode link)
	{
		if (this.url == null || link.getUrl() == null)
		{
			return -1;
		}

		if (this.outgoingNeighbors == null)
		{
			this.outgoingNeighbors = new ArrayList<WebNode>();
		}

		this.outgoingNeighbors.add(link);

		if (link.incomingNeighbors == null)
		{
			link.incomingNeighbors = new ArrayList<WebNode>();
		}

		link.incomingNeighbors.add(this);

		return 0;
	}

	public double getAuth()
	{
		return auth;
	}

	public void setAuth(double auth)
	{
		this.auth = auth;
	}

	public double getHub()
	{
		return hub;
	}

	public void setHub(double hub)
	{
		this.hub = hub;
	}

	public boolean isVisited()
	{
		return visited;
	}

	public void flipVisited()
	{
		this.visited = true;
	}

	@Override
	public String toString()
	{
		return this.getUrl().toString();
	}

}
