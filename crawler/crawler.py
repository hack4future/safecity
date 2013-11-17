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
    """Expands pages from provided pages block. Actually we need only td's"""
    return [td.a['href'] for td in pages_block.findAll('td')]


def crawl_page(browser, url, expected_enc):
    html = browser.open(url).read().decode(expected_enc)
    soup = BeautifulSoup(html)

    link_dict = {}
    for block in soup.findAll('div', attrs={'class': 'nitm'}):
        if reduce(lambda x, y: x or y,
                  [s in block.h3.renderContents() for s in ['Суточная сводка', 'Cуточна сводка', 'СУТОЧНАЯ СВОДКА']],
                  False):
            d = date.fromtimestamp(
                mktime(strptime(block.find('div', attrs={'class': 'ndate'}).renderContents(), '%d.%m.%Y')))
            link_dict[d.strftime('%Y-%m-%d')] = block.a['href']

    return link_dict


def crawl_month(browser, url, expected_enc='cp1251'):
    """'Crawls over every page in paging block '"""

    logger.info('...Crawling Month %s' % url)

    html = browser.open(url).read().decode(expected_enc)
    soup = BeautifulSoup(html)

    page_urls = expand_pages(check_one_and_only(soup.findAll('div', attrs={'class': 'paging'})))
    result = []
    for idx, url in enumerate(page_urls):
        logger.info('...Crawling Page [%s/%s] %s' % (idx + 1, len(page_urls), url))
        result.append(crawl_page(browser, url, expected_enc))
    return result


def browser_setup(browser):
    """Obviously browser setup"""
    browser.set_handle_refresh(False)  # can sometimes hang without this

    return browser


def crawl(service, from_date, to_date):
    dicts = [
        crawl_month(browser_setup(mechanize.Browser()), '/'.join([service.base_url, month_url]), service.enc)
        for month_url in expand_dates(month_url_format=service.month_url, from_date=from_date, to_date=to_date)
    ]

    return reduce(lambda x, y: dict(x.items() + y.items()), flatten(dicts), {})


if __name__ == '__main__':
    from datetime import date, time
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
    parser.add_argument('--verbose', action='store_false',
                        default=False,
                        help='Verbose to stdout if set, else to log file.')

    n = parser.parse_args(sys.argv[1:])

    if not n.start or not n.end:
        parser.print_help()
        sys.exit(-1)

    if not n.verbose:
        logging.basicConfig(level=logging.INFO)
        # TODO: Change this value for something better
        logger.addHandler(logging.FileHandler('crawler.log'))

    logger.info('...Start')
    crawled = crawl(service=FireService,
                    from_date=date.fromtimestamp(mktime(strptime(n.start, '%Y-%m-%d'))),
                    to_date=date.fromtimestamp(mktime(strptime(n.end, '%Y-%m-%d'))))

    print('[')
    for k in crawled.keys():
        print('{\'date\': \'%s\', \'url\' : \'%s\'}\n' % (k, crawled[k]))

    print(']')
