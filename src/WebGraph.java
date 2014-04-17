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

	public void setPages(ArrayList<WebNode> pages)
	{
		this.pages = pages;
	}
	
	public void addPage(WebNode page)
	{
		this.pages.add(page);
	}
	
}
