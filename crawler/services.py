from collections import namedtuple

Service = namedtuple('Service', 'base_url month_url')

FireService = Service(
    base_url='http://mchs.gov.by/rus/main/ministry/regional_management/str_minsk/news_minsk/',
    month_url='~year__m22=%Y~month__m22=%m'
)
