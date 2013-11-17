package opendataparser.parser

import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * User: pyotruk
 * Date: 2013-11-16
 */
class Parser {

    private static final Logger log = LoggerFactory.getLogger(Parser.class)

    static List<MappedDataItem> parse(Document doc) {
        log.info("Data preparing...")
        def preparedData = prepare(doc)
        if(!preparedData) {
            log.info("Bad data, failed when preparing.")
            return null
        }
        log.info("Preparing finished successfully.")

        def mappedData = prepareMapped(preparedData)
        log.info("Data has been mapped.")

        mappedData = filterRows(mappedData)
        log.info("Data rows has been filtered.")

        //TODO next stage -> normalization
        log.info("Ready.")

        return mappedData
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

        if(!data.isValid()) {
            return null
        }

        data.prepare()

        return data
    }

    static private List<MappedDataItem> prepareMapped(PreparedData data) {
        def res = []

        data.tableRows.each { row ->
            def item = new MappedDataItem(date: Date.parse('dd.MM.yyyy', data.pubDate))

            row.eachWithIndex { String cell, int i ->
                def key = MappedDataItem.dataMapping.find { map ->
                    map.value.find { data.columnsNames[i].toLowerCase().contains(it) }
                }?.key
                if(key) {
                    if(item."$key") {
                        item."$key" += ',' + cell
                    } else {
                        item."$key" = cell
                    }
                }
            }
            res << item
        }

        return res
    }

    static private List<MappedDataItem> filterRows(List<MappedDataItem> data) {
        return data.grep {
            !it.address.contains('Всего:')
        }
    }


}
