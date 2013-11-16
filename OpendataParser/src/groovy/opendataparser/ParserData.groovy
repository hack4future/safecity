package opendataparser

class ParserData {

    String pubDate
    List<String> columnsNames = []
    List<List<String>> tableRows = []

    /**
     * Use this method to add row
     * @param row
     */
    void addRow(List<String> row) {
        tableRows << row
    }

}
