package opendataparser

import opendataparser.parser.MappedDataItem
import opendataparser.parser.Parser
import opendataparser.parser.PreparedData
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ParserService {

    private final static OUTPUT_FILE_DIR = '/tmp/'
    private static final Logger log = LoggerFactory.getLogger(ParserService.class)

    List<MappedDataItem> parse(Document doc) {
        def data = Parser.parse(doc)
        return data
    }

    File createOutputCsv(List<MappedDataItem> data) {
        if(!data) {
            return null
        }
        def f = new File("${OUTPUT_FILE_DIR}opendata-parser-${System.currentTimeMillis()}.csv")
        CsvDriver.write(f, data)
        return f
    }
}
