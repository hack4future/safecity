package opendataparser

class ParserData {

    private static final BLANK_FIELD_MARK = '-'

    String pubDate
    List<String> columnsNames = []
    List<List<String>> tableRows = []

    /**
     * Use this method to add row
     * @param row
     */
    void addRow(List<String> row) {
        int diff = columnsNames.size() - row.size()
        if (diff > 0) {
            diff.times {
                row.add(0, BLANK_FIELD_MARK)
            }
        }
        tableRows << row
    }

    List<List<String>> getRows() {
        fillEmptyRows()
        return tableRows
    }

    /**
     * Fills blank rows before getting
     * @return
     */
    private void fillEmptyRows() {
        def tmp = tableRows.first()
        tableRows = tableRows.collect { row ->
            int i = -1
            row = row.collect {
                ++i
                if ((it == BLANK_FIELD_MARK) && (tmp[i] != BLANK_FIELD_MARK)) {
                    tmp[i]
                } else {
                    it
                }
            }
            tmp = row
            return row
        }
    }

}
