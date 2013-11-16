from dateutil.rrule import rrule, MONTHLY
import mechanize


def expand_dates(month_url_format, from_date, to_date):
    return [d.strftime(month_url_format) for d in iter(rrule(MONTHLY, dtstart=from_date, until=to_date))]


def crawl_month(url):
    br = mechanize.Browser()
    br.set_handle_refresh(False)  # can sometimes hang without this

    response = br.open(url)
    print response.read().decode('cp1251')

    return []


def crawl(service, from_date, to_date):
    for month_url in expand_dates(month_url_format=service.month_url, from_date=from_date, to_date=to_date):
        crawl_month('/'.join([service.base_url, month_url]))