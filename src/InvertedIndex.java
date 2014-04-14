import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;

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
		String page = getPage(url);
		
		for (String word : page.split("\\s+"))
		{
			word = word.toLowerCase();
			if (!words.containsKey(word))
			{
				addWord(word);
			}
			
			this.words.get(word).addUrl(url, getRankForPage(word, page));
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
	
	public void addUrlToWord(URL url, String word)
	{
		double count = 0;
		String page = getPage(url);
		word = word.toLowerCase();
		
		count = getRankForPage(word, page);
		
		if (!this.words.containsKey(word))
		{
			addWord(word);
		}
		
		this.words.get(word).addUrl(url, count);
	}
	
	public void addUrlToWord(String url, String word)
	{
		try
		{
			addUrlToWord(new URL(url), word);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getPage(URL url)
	{
		StringBuilder output = new StringBuilder();
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = input.readLine();
			
			while (line != null)
			{
				output.append(line);
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

	private double getRankForPage(String word, String page)
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
		
		return countWord/countAll;
	}
}
