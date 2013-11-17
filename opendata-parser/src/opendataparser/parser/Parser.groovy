package opendataparser.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * User: pyotruk
 * Date: 2013-11-16
 */
class Parser {

    private static final Logger log = LoggerFactory.getLogger(Parser.class)

    static List<NormalizedDataItem> parse(Document doc) {
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

        def normalizedData = normalize(mappedData)
        log.info("Data has been normalized.")
        log.info("Ready.")

        return normalizedData
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

    static private List<NormalizedDataItem> normalize(List<MappedDataItem> data) {
        def res = []
        data.each { item ->
            def normItem = new NormalizedDataItem(
                    date: item.date,
                    type: 'Пожар',
                    description: item.description,
                    cause: item.description
            )
            LinkedList<String> address = item.address.split(',')
            normItem.district = address.pollFirst()
            normItem.street = address.pollFirst()
            normItem.building = address.pollFirst()

            def m = (item.address =~ /ЖЭС\-[\d]+/)
            if(m.find()) {
                normItem.zhes = m.group(0)
            }

            def geoPoint = geocode("город Минск, ${normItem.street}, ${normItem.building}")
            normItem.latitude = geoPoint.latitude
            normItem.longitude = geoPoint.longitude

            res << normItem
        }
        return res
    }

    static private Map geocode(final String address) {
        def geoPoint = Jsoup.connect('http://geocode-maps.yandex.ru/1.x/')
                .data(['geocode': address]).get()
                .select('point > pos').text()
        LinkedList<String> geoPointSplitted = geoPoint.split(' ')
        return [
                longitude: geoPointSplitted.pollFirst().toDouble(),
                latitude: geoPointSplitted.pollFirst().toDouble()
        ]
    }


}
