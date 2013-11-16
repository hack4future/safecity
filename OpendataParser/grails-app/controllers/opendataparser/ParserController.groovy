package opendataparser

import org.jsoup.Jsoup

class ParserController {

    def index = {
        return [data: '']
    }

    def parseUrl = {
//        String url = params.url
        def url = "http://mchs.gov.by/rus/main/ministry/regional_management/str_minsk/news_minsk/"
        def queryParams =[
                '~year__m22':'2013',
                '~month__m22':'11',
                '~page__m22':'1',
                '~news__m22':'20056'
        ]

        def data = []
        Jsoup.connect(url).data(queryParams).get().select('.nitm table').each {
            it.select('tr').each {
                data << it.text()
            }
        }

        render(view: 'index', model: [data: data])
    }

    def parseFile = {
        def f = request.getFile('file')
        File file = new File('/tmp/opendataparser.tmp')
        f.transferTo(file)

        def data = []
        Jsoup.parse(file, "UTF-8").select('.nitm table').subList(1, 3).each {
            it.select('tr').each {
                data << it.text()
            }
        }

        render(view: 'index', model: [data: data])
    }
}
