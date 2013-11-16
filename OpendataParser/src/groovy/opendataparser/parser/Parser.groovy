package opendataparser.parser

import org.jsoup.nodes.Document

/**
 * User: pyotruk
 * Date: 2013-11-16
 */
class Parser {

    static PreparedData parse(Document doc) {
        return prepare(doc)
        //TODO next stage -> mapping
    }

    static private PreparedData prepare(Document doc) {
        def data = new PreparedData()

        data.pubDate = doc.select('.ndate').text()

        def tablesList = doc.select('.nitm table')
        def table

        // selecting needed table from other
        if(tablesList.size() > 1) {
            table = tablesList[1]
        } else {
            return null
        }

        // parsing column names
        table.select('tr:eq(0) > td').each {
            data.columnsNames << it.text()
        }
        // parsing rest table data
        table.select('tr:gt(0)').each {
            def row = []
            it.select('td').each {
                row << it.text()
            }
            data.addRow(row)
        }

        data.prepare()

        return data
    }


}
