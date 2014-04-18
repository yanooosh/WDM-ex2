import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebNode
{

	private URL url;
	private ArrayList<WebNode> outgoingNeighbors;//incomingNeighbors
	private ArrayList<WebNode> incomingNeighbors; // TODO:update class
	double auth;
	double hub;

	public WebNode()
	{
		this.url = null;
		this.outgoingNeighbors = null;
	}

	public WebNode(String url)
	{
		try
		{
			this.url = new URL(url);
			this.outgoingNeighbors = new ArrayList<WebNode>();
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
		this.outgoingNeighbors = new ArrayList<WebNode>();
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

	public ArrayList<WebNode> getIncomingNeighbors() {
		return incomingNeighbors;
	}

	public void setIncomingNeighbors(ArrayList<WebNode> incomingNeighbors) {
		this.incomingNeighbors = incomingNeighbors;
	}

	public int addLink(WebNode link)
	{
		if (this.url == null)
		{
			return -1;
		}
		
		if (this.outgoingNeighbors == null)
		{
			this.outgoingNeighbors = new ArrayList<WebNode>();
		}
		
		this.outgoingNeighbors.add(link);
		
		return 0;
	}
	
	public double getAuth() {
		return auth;
	}

	public void setAuth(double auth) {
		this.auth = auth;
	}

	public double getHub() {
		return hub;
	}

	public void setHub(double hub) {
		this.hub = hub;
	}
	
	@Override
	public String toString() {
		return getUrl().toString();
	}

}
