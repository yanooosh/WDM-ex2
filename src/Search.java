import java.util.ArrayList;

public class Search
{

	private static final String 	URL_BASE 		= "http://simple.wikipedia.org";
	private static final String 	URL_START 		= "http://simple.wikipedia.org/wiki/Albert_einstein";
	private static final int 		MIN_WEB_PAGES 	= 100;
	
	public static void main(String[] args)
	{
		WebGraph G = new WebGraph();
		InvertedIndex II = new InvertedIndex();
		
		crawl(G, II);
		
		Hits(G);

	}

	/****************************************************
	 * 													*
	 * 						Crawl						*
	 * 													*
	 ****************************************************/
	public static void crawl(WebGraph webs, InvertedIndex words)
	{
		
		String url = URL_START;
		webs.addPage(new WebNode(url));
		WebNode node = webs.getNextUnVisitedPage();
		
		do
		{
			
			String page = words.getHtmlPage(url);
			int startIndex = page.indexOf("href=\"/wiki/");
			
			while (startIndex != -1)
			{
				int endIndex = getHrefEndIndex(page, startIndex);
				String neighbourUrl = URL_BASE + page.substring(startIndex + 6, endIndex);
				
				WebNode neighbourNode = new WebNode(neighbourUrl);
				node.addLink(neighbourNode);
				webs.addPage(neighbourNode);
				
				page = page.substring(endIndex + 1);
				startIndex = page.indexOf("href=\"/wiki/");
			}
			
			node = webs.getNextUnVisitedPage();
				
		} while (node != null && webs.getPages().size() < MIN_WEB_PAGES);
		
		for (WebNode n : webs.getPages())
		{
			words.addURL(n.getUrl());
			webs.getPage(n.getUrl()).flipVisited();
		}
		
	}

	private static int getHrefEndIndex(String page, int startIndex)
	{
		int i = startIndex + 7;
		
		while (page.charAt(i) != '"')
		{
			i++;
		}
		
		return i;
	}

	/****************************************************
	 * 													*
	 * 						HITS						*
	 * 													*
	 ****************************************************/
	public static void Hits(WebGraph g)
	{
		ArrayList<WebNode> pages = g.getPages();
		for (WebNode p : pages)
		{
			p.auth = 1;
			p.hub = 1;
		}
		HubsAndAuthorites(pages);
	}

	public static void HubsAndAuthorites(ArrayList<WebNode> G)
	{
		double norm;
		while (true)
		{
			ArrayList<WebNode> prevG = clone(G);
			// for (int i = 0; i < k; i++) {
			norm = 0;
			// update all authority values first
			for (WebNode p : G)
			{
				p.auth = 0;
				// p.incomingNeighbors is the set of pages that link to p
				for (WebNode q : p.getIncomingNeighbors())
				{
					p.auth += q.hub;
				}

				norm += p.auth * p.auth; // calculate the sum of the squared
											// auth values to normalise
			}

			norm = Math.sqrt(norm);
			for (WebNode p : G)
			{
				p.auth = p.auth / norm; // normalise the auth values
			}

			norm = 0;
			for (WebNode p : G)
			{
				p.hub = 0;
				for (WebNode r : p.getOutgoingNeighbors())
				{
					// p.outgoingNeighbors is the set of pages that p links to
					p.hub += r.auth;
				}

				norm += p.hub * p.hub; // calculate the sum of the squared hub
										// values to normalise
			}

			norm = Math.sqrt(norm);
			for (WebNode p : G)
			{
				p.hub = p.hub / norm;
			}
			if (SmallerThanEpsilon(G, prevG))
				break;
		}
	}

	private static boolean SmallerThanEpsilon(ArrayList<WebNode> g,
			ArrayList<WebNode> prevG)
	{
		boolean flag = true;
		double epsilon = 0.0000000000001;
		for (WebNode n : g)
		{
			for (WebNode m : prevG)
			{
				if (n.getUrl().toString().compareTo(m.getUrl().toString()) == 0
						&& (Math.abs(n.hub - m.hub) >= epsilon || Math
								.abs(n.auth - m.auth) >= epsilon))
				{
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	private static ArrayList<WebNode> clone(ArrayList<WebNode> g)
	{
		ArrayList<WebNode> prevG = new ArrayList<WebNode>();
		for (WebNode p : g)
		{
			WebNode w = new WebNode(p.getUrl());
			w.auth = p.auth;
			w.hub = p.hub;
		}
		return prevG;
	}
}
