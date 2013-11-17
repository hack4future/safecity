#!/usr/bin/python2
# -*- coding: utf-8 -*-
from dateutil.rrule import rrule, MONTHLY
import mechanize
from BeautifulSoup import BeautifulSoup
from utils import flatten


import logging
logger = logging.getLogger()


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
    logger.info('...Crawling Page %s' % url)
    html = browser.open(url).read().decode(expected_enc)
    soup = BeautifulSoup(html)
    return [block.a['href'] for block in soup.findAll('div', attrs={'class': 'nitm'}) if
            reduce(lambda x, y: x or y,
                   [block.h3.renderContents() == s for s in ['Суточная сводка', 'Cуточна сводка', 'СУТОЧНАЯ СВОДКА']],
                   False)]


def crawl_month(browser, url, expected_enc='cp1251'):
    """'Crawls over every page in paging block '"""

    logger.info('...Crawling Month %s' % url)

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
        [
            crawl_month(browser_setup(mechanize.Browser()), '/'.join([service.base_url, month_url]), service.enc)
            for month_url in expand_dates(month_url_format=service.month_url, from_date=from_date, to_date=to_date)
        ]
    )


if __name__ == '__main__':
    from datetime import date
    from time import mktime, strptime
    import sys
    from services import FireService
    from argparse import ArgumentParser

    parser = ArgumentParser(
        prog='crawler (open data)',
        usage='crawler --start=YYYY-MM-DD --end=YYYY-MM-DD',
        description='Crawler script for fetching links with daily summary.'
    )

    parser.add_argument('--start', help='Start date for fetching. In YYYY-MM-DD format')
    parser.add_argument('--end', help='End date for fetching. In YYYY-MM-DD format')

    n = parser.parse_args(sys.argv[1:])

    if not n.start or not n.end:
        parser.print_help()
        sys.exit(-1)

    logging.basicConfig(level=logging.INFO)
    logger.info('...Start')
    print('\n'.join(
        crawl(service=FireService,
              from_date=date.fromtimestamp(mktime(strptime(n.start, '%Y-%m-%d'))),
              to_date=date.fromtimestamp(mktime(strptime(n.end, '%Y-%m-%d')))
        )))
