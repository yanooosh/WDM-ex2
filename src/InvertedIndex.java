import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.xml.sax.InputSource;

import com.sun.corba.se.spi.orbutil.fsm.Input;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class InvertedIndex
{

	TreeMap<String, InvertedIndexNode> words;

	List<String> STOP_WORDS = Arrays.asList("a", "about", "above", "above", "across", "after",
			"afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also",
			"although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and",
			"another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are",
			"around", "as", "at", "back", "be", "became", "because", "become", "becomes",
			"becoming", "been", "before", "beforehand", "behind", "being", "below", "beside",
			"besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can",
			"cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail",
			"do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven",
			"else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone",
			"everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire",
			"first", "five", "for", "former", "formerly", "forty", "found", "four", "from",
			"front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he",
			"hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself",
			"him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc",
			"indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter",
			"latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might",
			"mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my",
			"myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no",
			"nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off",
			"often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise",
			"our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please",
			"put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious",
			"several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so",
			"some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere",
			"still", "such", "system", "take", "ten", "than", "that", "the", "their", "them",
			"themselves", "then", "thence", "there", "thereafter", "thereby", "therefore",
			"therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those",
			"though", "three", "through", "throughout", "thru", "thus", "to", "together", "too",
			"top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up",
			"upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when",
			"whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein",
			"whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever",
			"whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet",
			"you", "your", "yours", "yourself", "yourselves", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "1.", "2.", "3.", "4.", "5.", "6.", "11", "7.", "8.", "9.", "12", "13",
			"14", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "terms", "CONDITIONS", "conditions",
			"values", "interested.", "care", "sure", ".", "!", "@", "#", "$", "%", "^", "&", "*",
			"(", ")", "{", "}", "[", "]", ":", ";", ",", "<", ".", ">", "/", "?", "_", "-", "+",
			"=", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "contact", "grounds", "buyers",
			"tried", "said,", "plan", "value", "principle.", "forces", "sent:", "is,", "was",
			"like", "discussion", "tmus", "diffrent.", "layout", "area.", "thanks", "thankyou",
			"hello", "bye", "rise", "fell", "fall", "psqft.", "http://", "km", "miles");

	public InvertedIndex()
	{
		this.words = new TreeMap<String, InvertedIndexNode>();
	}

	public TreeMap<String, InvertedIndexNode> getWords()
	{
		return this.words;
	}

	public void addWord(String word)
	{
		if (this.words.containsKey(word))
		{
			return;
		}

		InvertedIndexNode node = new InvertedIndexNode();
		this.words.put(word.toLowerCase(), node);
	}

	public void addURL(URL url)
	{
		String page = getPageContent(url).toLowerCase();

		for (String word : page.split("\\s+"))
		{
			/*
			 * word = word.toLowerCase(); if (!words.containsKey(word)) {
			 * addWord(word); }
			 * 
			 * this.words.get(word).addUrl(url, getRankForPage(word, page));
			 */

			if (STOP_WORDS.contains(word) || (words.containsKey(word) && words.get(word).containsPair(url.toExternalForm())))
			{
				continue;
			}
			
			if (word.endsWith(",") || word.endsWith(".") || word.endsWith("!") || word.endsWith("?") || word.endsWith("\""))
			{
				word = word.substring(0, word.length() - 1);
			}
			
			if (word.startsWith("\""))
			{
				word = word.substring(1);
			}
			
			addUrlToWord(url, word, page);
		}
	}

	public void addURL(String url)
	{
		try
		{
			addURL(new URL(url));
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addUrlToWord(URL url, String word, String page)
	{
		double count = 0;
		// String page = getPageContent(url);
		//word = word.toLowerCase();

		count = getRankForPage(word, page);

		addWord(word);		

		this.words.get(word).addUrl(url, count);
	}

	public void addUrlToWord(String url, String word, String page)
	{
		try
		{
			addUrlToWord(new URL(url), word, page);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPageContent(String url)
	{
		String content = "";

		try
		{
			content = Jsoup.connect(url).get().text();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

	public String getPageContent(URL url)
	{
		return getPageContent(url.toExternalForm());
	}

	public String getHtmlPage(URL url)
	{
		StringBuilder output = new StringBuilder();
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = input.readLine();

			while (line != null)
			{
				output.append(line);
				line = input.readLine();
			}

			input.close();

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}

	public String getHtmlPage(String url)
	{
		String output = "";
		try
		{
			output = getHtmlPage(new URL(url));
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public double getRankForPage(String word, String page)
	{
		// page = page.toLowerCase();
		//word = word.toLowerCase();

		int countWord = Collections.frequency(Arrays.asList(page.split("\\s+")), word);
		int countAll = page.split("\\s+").length;

		/*
		 * while (page.contains(word)) { page =
		 * page.substring(page.indexOf(word) + word.length()); countWord++; }
		 */
		return ((double) (countWord)) / countAll;
	}

	public List<WebNodePair> getRanks(String word, WebGraph g) {
		List<WebNodePair> result = new ArrayList<WebNodePair>();
		for (WebNode w : g.getPages()){
			String url = w.getUrl().toExternalForm();
			if (this.words.containsKey(word) && this.words.get(word).getPages().size() != 0)
			{
				WebNodePair p = new WebNodePair(url, this.words.get(word).getCountByURL(url));
				result.add(p);
			}
			
		}
		
		return result;
	}
}
