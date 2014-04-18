import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Search
{

	private static final String URL_BASE = "http://simple.wikipedia.org";
	private static final String URL_START = "http://simple.wikipedia.org/wiki/Albert_einstein";
	private static final int MIN_WEB_PAGES = 100;
	public static final int best = 5;

	public static void main(String[] args)
	{
		WebGraph G = new WebGraph();
		InvertedIndex inverted = new InvertedIndex();
		crawl(G, inverted);
		PrintToFile(G.getPages(), "urls.txt");
		ArrayList<WebNodePair> result = Hits(G);
		PrintToFile(result, "rank.txt");
		Scanner scanner = new Scanner(System.in);
		while (true)
		{
			System.out.println("\n < Enter Words For Search > ");
			String words = scanner.nextLine();
			System.out.println();
			if (words.equals("exit"))
			{
				scanner.close();
				return;
			}
			String[] splitWords = words.split(" ");
			ArrayList<WebNodePair> ta = new ArrayList<>();

			// TODO: add changes for TA
		}
	}

	private static <T> void PrintToFile(ArrayList<T> array, String filename)
	{
		try
		{
			PrintWriter writer = new PrintWriter(filename);
			for (T item : array)
			{
				writer.println(item.toString());
			}
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/****************************************************
	 * * Crawl * *
	 ****************************************************/
	public static void crawl(WebGraph webs, InvertedIndex words)
	{

		String url = URL_START;
		webs.addPage(new WebNode(url));
		WebNode node = webs.getNextUnVisitedPage();

		do
		{
			words.addURL(url);
			webs.getPage(url).flipVisited();
			
			String page = words.getHtmlPage(url);
			int startIndex = page.indexOf("href=\"/wiki/");

			while (startIndex != -1)
			{				
				int endIndex = getHrefEndIndex(page, startIndex);
				String neighbourUrl = URL_BASE + page.substring(startIndex + 6, endIndex);

				if (neighbourUrl.charAt(neighbourUrl.length() - 4) != '.')
				{
					WebNode neighbourNode = new WebNode(neighbourUrl);
					node.addLink(neighbourNode);
					webs.addPage(neighbourNode);	
				}

				page = page.substring(endIndex + 1);
				startIndex = page.indexOf("href=\"/wiki/");
			}

			node = webs.getNextUnVisitedPage();

		} while (node != null && webs.getPages().size() < MIN_WEB_PAGES);

		
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
	 * * HITS * *
	 ****************************************************/
	public static ArrayList<WebNodePair> Hits(WebGraph g)
	{

		ArrayList<WebNode> pages = g.getPages();
		for (WebNode p : pages)
		{
			p.auth = 1;
			p.hub = 1;
		}
		HubsAndAuthorites(pages);

		ArrayList<WebNodePair> result = new ArrayList<WebNodePair>();
		for (WebNode w : pages)
		{
			result.add(new WebNodePair(w.getUrl().toString(), w.auth));
		}

		Collections.sort(result, new WebNodePairComparator());
		return result;

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

	/*
	 * We chose an epsilon s.t. the difference between two iterations is smaller
	 * than that number
	 */
	private static boolean SmallerThanEpsilon(ArrayList<WebNode> g, ArrayList<WebNode> prevG)
	{
		boolean flag = true;
		double epsilon = 0.0000000000001;
		for (WebNode n : g)
		{
			for (WebNode m : prevG)
			{

				if (n.getUrl().toString().compareTo(m.getUrl().toString()) == 0
						&& (Math.abs(n.auth - m.auth) >= epsilon))
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

	private static List<WebNodePair> TA(int k, ArrayList<WebNodePair>... pairs)
	{
		ArrayList<WebNodePair> list = aggregate(pairs);
		Collections.sort(list, new WebNodePairComparator());
		return list.subList(0, k);
	}

	private static ArrayList<WebNodePair> aggregate(ArrayList<WebNodePair>... pairs)
	{
		if (pairs.length == 0)
			return new ArrayList<WebNodePair>();

		// take minimum size
		ArrayList<WebNodePair> shortest = pairs[0];
		for (int i = 1; i < pairs.length; i++)
		{
			ArrayList<WebNodePair> shortestTmp = pairs[i];
			if (shortestTmp.size() < shortest.size())
				shortest = shortestTmp;

		}

		int numOfArgs = pairs.length;
		ArrayList<WebNodePair> result = new ArrayList<WebNodePair>();
		for (int i = 0; i < shortest.size(); i++)
		{
			result.add(new WebNodePair(shortest.get(i).id, 0));
		}

		for (int i = 0; i < pairs.length; i++)
		{
			ArrayList<WebNodePair> p = pairs[i];

			for (int j = 0; j < p.size(); j++)
			{
				WebNodePair pair = new WebNodePair("", 0);
				for (WebNodePair w : p)
				{
					if (w.id.equals(result.get(j).id))
					{
						pair = w;
					}
				}
				result.get(j).rank += pair.rank;
			}
		}

		for (int i = 0; i < result.size(); i++)
		{
			result.get(i).rank /= numOfArgs; // average
		}
		return result;
	}

}
