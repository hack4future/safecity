from dateutil.rrule import rrule, MONTHLY
import mechanize
from BeautifulSoup import BeautifulSoup
from utils import flatten


def expand_dates(month_url_format, from_date, to_date):
    return [d.strftime(month_url_format) for d in iter(rrule(MONTHLY, dtstart=from_date, until=to_date))]


def check_one_and_only(param):
    """Sometimes we expect exactly one element. So let's check it"""
    l = list(param)
    assert len(l) == 1
    return l[0]


def expand_pages(pages_block):
    """Expands pages from provided pages block. Actualy we need only td's"""
    return [td.a['href'] for td in pages_block.findAll('td')]


def crawl_page(browser, url, expected_enc):
    html = browser.open(url).read().decode(expected_enc)
    soup = BeautifulSoup(html)
    return [block.a['href'] for block in soup.findAll('div', attrs={'class': 'nitm'})]


def crawl_month(browser, url, expected_enc='cp1251'):
    """'Crawls over every page in paging block '"""

    html = browser.open(url).read().decode(expected_enc)
    soup = BeautifulSoup(html)

    return [crawl_page(browser, page_url, expected_enc) for page_url in
            expand_pages(check_one_and_only(soup.findAll('div', attrs={'class': 'paging'})))]


def browser_setup(browser):
    """Obviously browser setup"""
    browser.set_handle_refresh(False)  # can sometimes hang without this

    return browser


def crawl(service, from_date, to_date):
    return flatten(
        [crawl_month(browser_setup(mechanize.Browser()), '/'.join([service.base_url, month_url])) for month_url in
         expand_dates(month_url_format=service.month_url, from_date=from_date, to_date=to_date)])
