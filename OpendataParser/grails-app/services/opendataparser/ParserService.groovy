package opendataparser

import opendataparser.parser.Parser
import opendataparser.parser.PreparedData
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ParserService {

    private final static OUTPUT_FILE_DIR = '/tmp/'
    private static final Logger log = LoggerFactory.getLogger(ParserService.class)

    PreparedData parse(Document doc) {
        def data = Parser.parse(doc)
        return data
    }

    File createOutputCsv(PreparedData data) {
        if(!data) {
            return null
        }
        def f = new File("${OUTPUT_FILE_DIR}opendata-parser-${System.currentTimeMillis()}.csv")
        CsvDriver.write(f, data.tableRows, data.getColumnsNames())
        return f
    }
}
