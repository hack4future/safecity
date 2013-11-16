package opendataparser

import org.jsoup.nodes.Document

class ParserService {

    def parse(Document doc) {
        def data = []
        doc.select('.nitm table').subList(1, 3).each {
            it.select('tr').each {
                data << it.text()
            }
        }
        return data
    }
}
