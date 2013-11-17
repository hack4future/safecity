package opendataparser

import opendataparser.parser.NormalizedDataItem
import opendataparser.parser.Parser
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static java.util.UUID.randomUUID

class ParserService {

    private static final Logger log = LoggerFactory.getLogger(ParserService.class)

    private final static OUTPUT_FILE_DIR = '/tmp/'

    def cache = new Cache<String, CachedHolder>(3600 * 24 * 7) // cache TTL = 1 week

    CachedHolder parse(Document doc, boolean localFile = false) {
        def uri = doc.location()
        def holder = null
        if(!localFile) {
            holder = cache.get(uri)

        } else {
            log.info("Local file parsing [$uri]")
        }

        if(!holder) {
            log.info("Not found in cache by key (uri) [$uri]")
            def data = Parser.parse(doc)
            holder = new CachedHolder(
                    outputFileName: createOutputCsv(data).name,
                    data: data
            )
            cache.put(uri, holder)

        } else {
            log.info("FOUND in cache by key (uri) [$uri]")
        }
        return holder
    }

    private File createOutputCsv(List<NormalizedDataItem> data) {
        if(!data) {
            return null
        }
        def f = new File("${OUTPUT_FILE_DIR}opendata-parser-${randomUUID()}.csv")
        CsvDriver.write(f, data)
        return f
    }
}
