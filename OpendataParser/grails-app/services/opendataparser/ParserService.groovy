package opendataparser

import org.jsoup.nodes.Document

class ParserService {

    private final static OUTPUT_FILE_DIR = '/tmp/'

    ParserData parse(Document doc) {
        def data = new ParserData()

        data.pubDate = doc.select('.ndate').text()

        def tablesList = doc.select('.nitm table')
        def table

        if(tablesList.size() > 1) {
            table = tablesList[1]
        } else {
            return null
        }

        table.select('tr:eq(0) > td').each {
            data.columnsNames << it.text()
        }
        table.select('tr:gt(0)').each {
            def row = []
            it.select('td').each {
                row << it.text()
            }
            data.addRow(row)
        }

        return data
    }

    File createOutputCsv(ParserData data) {
        if(!data) {
            return nullf
        }
        def f = new File("${OUTPUT_FILE_DIR}opendata-parser-${System.currentTimeMillis()}.csv")
        CsvDriver.write(f, data.getRows(), data.getColumnsNames())
        return f
    }
}
