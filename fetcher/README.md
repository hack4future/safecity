fetcher
=====

Script for fetching data from opendata-parser service. Using json file from crawler.

Usage
======

python2 fetcher.py %some_file.json%


Required json format
======
Json file should look like this:
  [
    {
      'date' : '2013-01-11',
      'url' : 'http://mchs.gov.by/rus/main/ministry/regional_management/str_minsk/news_minsk/~year__m22=2013~month__m22=1~page__m22=4~news__m22=12020'
    },
    ...
  ]



Deps
======

 * Python 2.7
 * Mechanize
 * BeautifulSoup
 * Python dateutils
