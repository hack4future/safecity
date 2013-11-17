from datetime import date, time
import sys
from time import strptime, mktime
from urllib2 import urlopen
import mechanize
from BeautifulSoup import BeautifulSoup
from dateutil.rrule import rrule, DAILY


def fetch(link, filename):
    url = 'http://54.200.194.23/parser/parser/parseUrl?url=%s&format=csv' % link
    print(url)
    local_file = open(filename, 'w')
    local_file.write(urlopen(url).read())
    local_file.close()


def date_of(link):
    browser = mechanize.Browser()
    browser.set_handle_refresh(False)  # can sometimes hang without this

    d = BeautifulSoup(browser.open(link).read().decode('cp1251')).find('div',
                                                                       attrs={'class': 'ndate'}).renderContents()
    return d if d is None else date.fromtimestamp(mktime(strptime(d, '%d.%m.%Y')))


def make_filename(link, desired_date):
    return desired_date.strftime('fire-sum-%Y-%m-%d.csv')


def fetch_for_date(desired_date, links):
    for link in links:
        if date_of(link) == desired_date:
            fetch(link, make_filename(link, desired_date))
            return True

    return False


def expand_dates(start, end):
    return [d for d in iter(rrule(DAILY, dtstart=start, until=end))]


if __name__ == '__main__':
    from argparse import ArgumentParser
    import logging

    logger = logging.getLogger()

    parser = ArgumentParser(
        prog='fetcher',
        usage='fetcher --start=[DATE] --end=[DATE] --links=links',
        description='fetcher script for using with opendata-parser'
    )

    parser.add_argument('--start', help='Start fetch from this date. In YYYY-MM-DD format.')
    parser.add_argument('--end', help='End fetch at this date. In YYYY-MM-DD format.')
    parser.add_argument('--links', help='File with summary links.')
    parser.add_argument('--verbose', action='store_false',
                        default=False,
                        help='Verbose to stdout if set, else to log file.')

    n = parser.parse_args(sys.argv[1:])

    if not (n.start and n.end and n.links):
        parser.print_help()
        sys.exit(-1)

    if n.verbose:
        logging.basicConfig(level=logging.INFO)
        # TODO: Change this value for something better
        logger.addHandler(logging.FileHandler('fetcher.log'))

    from_date = date.fromtimestamp(mktime(strptime(n.start, '%Y-%m-%d')))
    to_date = date.fromtimestamp(mktime(strptime(n.end, '%Y-%m-%d')))
    lines = open(n.links, 'r').read().split('\n')
    links = [l for l in lines if len(l) != 0]

    date_dict = {}
    for link in links:
        logger.info('Scanning date of %s' % link)
        d = date_of(link)
        if d:
            date_dict[d.strftime('%Y-%m-%d')] = link

    print(date_dict)

    for d in expand_dates(start=from_date, end=to_date):
        logger.info('Fetching for %s ...' % d.strftime('%Y-%m-%d'))
        d_key = d.strftime('%Y-%m-%d')
        if d_key in date_dict.keys():
            fetch(date_dict[d_key], make_filename(date_dict[d_key], d))
            logger.info('\t\t[Success]')
        else:
            logger.info('\t\t[Fail. No link for this date?]')




