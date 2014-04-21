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
		List<WebNodePair> resultHits = Hits(G);
		PrintToFile(G.getPages(), "urls.txt", -1);
		PrintToFile(resultHits, "rank.txt", best);
		Scanner scanner = new Scanner(System.in);
		while (true)
		{
			System.out.print("\nEnter Words For Search > ");
			String input = scanner.nextLine();
			System.out.println();
			if (input.equals("exit"))
			{
				scanner.close();
				return;
			}

			List<WebNodePair> resultForOutput = CalcTaResult(input.toLowerCase(), G, resultHits, inverted);
			if (resultForOutput == null)
			{
				System.out.println("no pages found for your search :(");
			}
			else
			{
				for (int i = 0; i < resultForOutput.size(); i++)
				{
					System.out.println(resultForOutput.get(i));
				}
			}
		}
	}

	private static List<WebNodePair> CalcTaResult(String input, WebGraph G, List<WebNodePair> hits,
			InvertedIndex inverted)
	{
		String[] words = input.split(" ");
		List<List<WebNodePair>> taCategories = getListIntersection(hits, inverted, words);
		if (taCategories == null)
		{
			return null;
		}
		List<WebNodePair> ta = TA(best, taCategories);
		return ta.subList(0, Math.min(ta.size(), best));
	}

	private static List<List<WebNodePair>> getListIntersection(List<WebNodePair> hits, InvertedIndex inverted,
			String[] words)
	{
		boolean flag = false;
		for (int i = 0; i < words.length; i++)
		{
			if (inverted.getWords().containsKey(words[i]))
			{
				flag = true;
				for (WebNodePair pair : hits)
				{
					if (inverted.getWords().get(words[i]).containsPair(pair.id))
					{
						pair.count++;
					}
				}
			}
		}

		if (!flag)
		{
			return null;
		}

		List<WebNodePair> newHits = new ArrayList<WebNodePair>();
		List<List<WebNodePair>> newByName = new ArrayList<List<WebNodePair>>();

		for (int i = 0; i < words.length; i++)
		{
			newByName.add(new ArrayList<WebNodePair>());
		}

		for (WebNodePair pair : hits)
		{
			if (pair.count == words.length)
			{
				newHits.add(pair);
				for (int i = 0; i < words.length; i++)
				{
					if (inverted.getWords().get(words[i]).getCountByURL(pair.id) != -1)
					{
						newByName.get(i).add(
								new WebNodePair(pair.id, inverted.getWords().get(words[i]).getCountByURL(pair.id)));

					}
				}
			}
		}

		for (WebNodePair pair : hits)
		{
			pair.count = 0;
		}

		newByName.add(newHits);

		for (List<WebNodePair> list : newByName)
		{
			Collections.sort(list, Collections.reverseOrder(new WebNodePairComparator()));
		}

		return newByName;
	}

	private static <T> void PrintToFile(List<T> array, String filename, int top)
	{
		int numToPrint = top == -1 ? array.size() : top;
		try
		{
			PrintWriter writer = new PrintWriter(filename);
			for (int i = 0; i < numToPrint; i++)
			{
				writer.println(array.get(i).toString());
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
			url = node.getUrl().toExternalForm();
			words.addURL(url);
			node.flipVisited();
			String page = words.getHtmlPage(url);
			int startIndex = page.indexOf("href=\"/wiki/");

			while (startIndex != -1)
			{
				int endIndex = getHrefEndIndex(page, startIndex);
				String neighbourUrl = URL_BASE + page.substring(startIndex + 6, endIndex);

				if (neighbourUrl.charAt(neighbourUrl.length() - 4) != '.')
				{
					if (webs.getPages().size() <= MIN_WEB_PAGES)
					{
						WebNode neighbourNode = new WebNode(neighbourUrl);
						node.addLink(neighbourNode);
						webs.addPage(neighbourNode);
					}
					else
					{
						WebNode neighbourNode = webs.getPage(neighbourUrl);
						if (neighbourNode != null)
						{
							node.addLink(neighbourNode);
						}
					}

				}

				page = page.substring(endIndex + 1);
				startIndex = page.indexOf("href=\"/wiki/");
			}

			node = webs.getNextUnVisitedPage();
		} while (node != null);

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
	public static List<WebNodePair> Hits(WebGraph g)
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

		Collections.sort(result, Collections.reverseOrder(new WebNodePairComparator()));
		return result;

	}

	public static void HubsAndAuthorites(ArrayList<WebNode> G)
	{
		double norm;
		while (true)
		{
			ArrayList<WebNode> prevG = clone(G);
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
				if (n.getUrl().equals(m.getUrl()) && (Math.abs(n.auth - m.auth) >= epsilon))
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

			prevG.add(w);
		}
		return prevG;
	}

	private static List<WebNodePair> TA(int k, List<List<WebNodePair>> pairs)
	{
		int n = pairs.get(0).size();
		double[] taBounds = new double[n];
		
		// initialize TA bounds 
		for (int i = 0; i < pairs.size(); i++)
		{
			for (int j = 0; j < n; j++)
			{
				double rank = pairs.get(i).get(j).rank;
				taBounds[j] += rank < 0.01 ? 100 * rank : (rank < 0.1 ? 10 * rank : rank);
			}
		}

		ArrayList<WebNodePair> aggList = aggregate(pairs);
		ArrayList<WebNodePair> result = new ArrayList<WebNodePair>();
		int count;
		for (int j = 0; j < n; j++)
		{
			count = 0;
			for (int i = 0; i < pairs.size(); i++)
			{
				WebNodePair curr = pairs.get(i).get(j);
				if (result.size() < best)
				{
					boolean flag = false;
					for (WebNodePair p : result)
					{
						if (p.id.equals(curr.id))
						{
							flag = true;
							break;
						}

					}
					if (flag)
					{
						continue;
					}
					for (WebNodePair w : aggList)
					{
						if (curr.id.equals(w.id))
						{
							result.add(w);
						}
					}
				}
				else
				{
					Collections.sort(result, Collections.reverseOrder(new WebNodePairComparator()));
					if (result.get(result.size() - 1).rank < curr.rank)
					{
						boolean flag = false;
						for (WebNodePair p : result)
						{
							if (p.id.equals(curr.id))
							{
								flag = true;
								break;
							}

						}
						if (flag)
						{
							continue;
						}
						result.remove(result.size() - 1);
						for (WebNodePair w : aggList)
						{
							if (curr.id.equals(w.id))
							{
								result.add(w);
							}
						}
					}
				}
				for (WebNodePair w : result)
				{
					if (w.rank > taBounds[j])
						count++;
				}
				if (count == best)
					break;
			}

		}

		Collections.sort(result, Collections.reverseOrder(new WebNodePairComparator()));
		return result;
	}

	
	/*
	 * Aggregate Function:
	 * We normalize the rank values to be in the same section, and then we sum them 
	 * This way for example the HITS results won't be the only factor that influence the results,
	 * because the numerical values of the HITS are much bigger than the words ranks
	 * */
	private static ArrayList<WebNodePair> aggregate(List<List<WebNodePair>> pairs)
	{
		for (List<WebNodePair> list : pairs){
			Collections.sort(list, new WebNodePairComparatorById());
		}
		int n = pairs.size();
		if (n == 0)
			return new ArrayList<WebNodePair>();
		int sizeOfEach = pairs.get(0).size();

		// initialize
		ArrayList<WebNodePair> result = new ArrayList<WebNodePair>();
		for (int i = 0; i < sizeOfEach; i++)
		{
			result.add(new WebNodePair(pairs.get(0).get(i).id, 0));
		}

		for (int i = 0; i < n; i++)
		{
			List<WebNodePair> p = pairs.get(i);
			for (int j = 0; j < p.size(); j++)
			{
				double rank = p.get(j).rank;
				double rankNormalized = rank < 0.01 ? 100 * rank : (rank < 0.1 ? 10 * rank : rank);
				result.get(j).rank += rankNormalized;
			}
		}
		for (List<WebNodePair> list : pairs){
			Collections.sort(list, Collections.reverseOrder(new WebNodePairComparator()));
		}
		return result;
	}

}
