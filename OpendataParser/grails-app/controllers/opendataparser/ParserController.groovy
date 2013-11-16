package opendataparser

import org.jsoup.Jsoup

class ParserController {

    def parserService

    def index = {
        return [data: '']
    }

    def parseUrl = {
//        def url = "http://mchs.gov.by/rus/main/ministry/regional_management/str_minsk/news_minsk/~page__m22=1~news__m22=20056"

        def data = parserService.parse(Jsoup.connect(params.url as String).get())

        render(view: 'index', model: [data: data])
    }

    def parseFile = {
        def f = request.getFile('file')
        File file = new File('/tmp/opendataparser.tmp')
        f.transferTo(file)

        def data = parserService.parse(Jsoup.parse(file, "UTF-8"))

        render(view: 'index', model: [data: data])
    }
}
