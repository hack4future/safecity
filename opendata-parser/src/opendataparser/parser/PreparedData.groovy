package opendataparser.parser

class PreparedData {

    private static final BLANK_FIELD_MARK = '-'

    String pubDate
    List<String> columnsNames = []
    List<List<String>> tableRows = []

    /**
     * Use this method to add row
     */
    void addRow(List<String> row) {
        tableRows << deMergeRow(row)
    }

    void prepare() {
        fillEmptyRows()
    }

    boolean isValid() {
        if(!columnsNames) { return false }
        if(!tableRows) { return false }
        return true
    }

    /**
     * Fills merged rows [rowspan]
     */
    private List<String> deMergeRow(List<String> row) {
        int diff = columnsNames.size() - row.size()
        if (diff > 0) {
            diff.times {
                row.add(0, BLANK_FIELD_MARK)
            }
        }
        return row
    }

    /**
     * Fills blank rows before getting
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
