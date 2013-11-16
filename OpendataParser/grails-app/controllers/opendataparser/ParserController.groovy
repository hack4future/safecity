package opendataparser

import org.jsoup.Jsoup

class ParserController {

    def parserService

    def index = {
        return [data: null]
    }

    def parseUrl = {
        def data = parserService.parse(Jsoup.connect(params.url as String).get())

        render(view: 'index', model: [data: data])
    }

    def parseFile = {
        def f = request.getFile('file')
        File file = new File('/tmp/opendataparser.tmp')
        f.transferTo(file)

        def data = parserService.parse(Jsoup.parse(file, "UTF-8"))
        if(!data) {
            flash.error = 'No data or bad format :('
        }

        render(view: 'index', model: [data: data])
    }
}
