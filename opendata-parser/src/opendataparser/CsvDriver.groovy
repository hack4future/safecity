package opendataparser

import opendataparser.parser.MappedDataItem

class CsvDriver {

    private final static SEPARATOR = ';'

    static String write(File file, List<MappedDataItem> data)
    throws IllegalArgumentException {
        def d = []
        data.each { item ->
            def row = []
            item.dataMap.each {
                row << it.value
            }
            d << row
        }
        return write(file, d, data.first().dataMap.keySet() as List)
    }

    static String write(File file, List<List<String>> data, List columnsNames)
    throws IllegalArgumentException {
        if (!data) {
            throw new IllegalArgumentException("Data is empty!")
        }

        if (!file.name.endsWith('.csv')) {
            file = new File(file.path + '.csv')
        }
        if (!file.exists()) {
            file.createNewFile()
        }

        file.withWriter { out ->
            out.writeLine(columnsNames.join(SEPARATOR))

            data.each { row ->
                out.writeLine(row.join(SEPARATOR))
            }
        }
        return file.path
    }

}
