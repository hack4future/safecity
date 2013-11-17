package org.hhf.safecity.parser;

import opendataparser.parser.PreparedData;
import opendataparser.parser.Parser
import org.jsoup.nodes.Document;

/**
 * User: Ilya Arkhanhelsky
 * Date: 17.11.13
 * Time: 0:36
 */
public class ParserWrapper
{
	public PreparedData parse(Document doc)
    {
        return new Parser().parse(doc);
    }
}
