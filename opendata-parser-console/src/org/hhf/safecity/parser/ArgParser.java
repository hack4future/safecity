package org.hhf.safecity.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * User: Ilya Arkhanhelsky
 * Date: 16.11.13
 * Time: 22:47
 */
public class ArgParser
{
    private final String[] links;

    public ArgParser(String[] args)
    {
        Options opts = new Options();
        opts.addOption("f", true, "File with list of links");
        try
        {
            CommandLine cmd = new PosixParser().parse(opts, args);
            links = cmd.hasOption('f') ? readLinksFromFile(cmd.getOptionValue('f')) : args;
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String[] readLinksFromFile(String f)
    {
        try
        {
            return readLinksFromStream(new BufferedReader(new InputStreamReader(new FileInputStream(f))));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String[] readLinksFromStream(BufferedReader reader) throws IOException
    {
        List<String> links = new ArrayList<>();
        while (reader.ready())
        {
            String line = reader.readLine();
            if (!line.isEmpty())
            {
                links.add(line);
            }
        }

        return links.toArray(new String[links.size()]);
    }

    public String[] links()
    {
        return links;
    }
}
