import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.xml.sax.InputSource;

import com.sun.corba.se.spi.orbutil.fsm.Input;

public class InvertedIndex
{
	TreeMap<String, InvertedIndexNode> words;
	
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
		String page = getPageContent(url);
		
		for (String word : page.split("\\s+"))
		{
/*			word = word.toLowerCase();
			if (!words.containsKey(word))
			{
				addWord(word);
			}
			
			this.words.get(word).addUrl(url, getRankForPage(word, page));*/
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
		//String page = getPageContent(url);
		word = word.toLowerCase();
		
		count = getRankForPage(word, page);
		
		if (!this.words.containsKey(word))
		{
			addWord(word);
		}
		
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

		int countWord = 0;
		int countAll = page.split("\\s+").length;
		
		page = page.toLowerCase();
		word = word.toLowerCase();
		
		while (page.contains(word))
		{
			page = page.substring(page.indexOf(word) + word.length());
			countWord++;
		}
		
		return ((double)(countWord))/countAll;
	}
}
