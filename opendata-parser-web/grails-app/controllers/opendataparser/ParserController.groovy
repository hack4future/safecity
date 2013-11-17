package opendataparser

import org.jsoup.Jsoup

class ParserController {

    private final static TMP_DIR = '/tmp/'

    def parserService

    def index = {
        return [data: null]
    }

    def parseUrl = {
        def cachedHolder = parserService.parse(Jsoup.connect(params.url as String).get())

        if (!cachedHolder?.data) {
            flash.error = 'No data or bad format :('
        }

        if(params.format == 'csv') {
            redirect(action: 'downloadCSV', params: [fileName: cachedHolder?.outputFileName])

        } else {
            render(view: 'index', model: [
                    data: cachedHolder?.data,
                    outputFileName: cachedHolder?.outputFileName
            ])
        }
    }

    def parseFile = {
        def f = request.getFile('file')
        File file = new File("${TMP_DIR}opendataparser.tmp")
        f.transferTo(file)

        def cachedHolder = parserService.parse(Jsoup.parse(file, "UTF-8"), true)

        if (!cachedHolder?.data) {
            flash.error = 'No data or bad format :('
        }

        render(view: 'index', model: [data: cachedHolder?.data, outputFileName: cachedHolder?.outputFileName])
    }

    def downloadCSV = {
        def f = new File(TMP_DIR + (params.fileName as String))
        if(f.isFile() && f.exists()) {
            render(contentType: 'text/plain', text: f.text)
        } else {
            render(contentType: 'text/plain', text: '')
        }
    }
}
