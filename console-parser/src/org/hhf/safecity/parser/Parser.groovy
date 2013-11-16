package org.hhf.safecity.parser
import opendataparser.CsvDriver
import opendataparser.ParserData
import opendataparser.ParserService
import org.jsoup.Jsoup

import static java.util.Arrays.asList
/**
 * User: Ilya Arkhanhelsky
 * Date: 16.11.13
 * Time: 22:41
 */
public class Parser
{
	private final List<String> links;

	public Parser(String... links)
	{
		this.links = asList(links);
	}


	public static void main(String[] args)
	{
		new Parser(new ArgParser(args).links()).run();
	}

	private void run()
	{
		for (String link : links)
		{
			ParserData data = parse(link)
			write(new File(makeFilename(data.pubDate)), data);
		}
	}

	private static void write(File file, ParserData parse)
	{
		CsvDriver.write(file, parse.getRows(), parse.getColumnsNames());

	}

	private static String makeFilename(String date)
	{
		return String.format("sum-%s.csv", date);
	}

	private static ParserData parse(String link)
	{
		   new ParserService().parse(Jsoup.connect(link).execute().parse());
	}
}
