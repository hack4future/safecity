package opendataparser.parser

import org.jsoup.nodes.Document

/**
 * User: pyotruk
 * Date: 2013-11-16
 */
class Parser {

    static List<MappedDataItem> parse(Document doc) {
        def preparedData = prepare(doc)
        def mappedData = prepareMapped(preparedData)
        return mappedData
        //TODO next stage -> normalization
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
                        item."$key" += cell
                    } else {
                        item."$key" = cell
                    }
                }
            }
            res << item
        }

        return res
    }


}
