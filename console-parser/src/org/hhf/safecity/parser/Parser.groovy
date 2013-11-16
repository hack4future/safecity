package org.hhf.safecity.parser

import opendataparser.CsvDriver
import opendataparser.ParserData
import opendataparser.ParserService
import org.jsoup.nodes.Document

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
		new Parser(new ArgParser(args).parse()).run();
	}

	private void run()
	{
		for (String link : links)
		{
			write(new File(makeFilename(link)), parse(link));
		}
	}

	private static void write(File file, ParserData parse)
	{
		CsvDriver.write(file, parse.getRows(), parse.getColumnsNames());

	}

	private static String makeFilename(String link)
	{
		return "test-" + Integer.toHexString(link.hashCode()) + ".csv";
	}

	private static ParserData parse(String link)
	{
		   new ParserService().parse(new Document(link));
	}
}
