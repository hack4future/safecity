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
    return 'fire-sum-%s.csv' % desired_date


def fetch_for_date(desired_date, links):
    for link in links:
        if date_of(link) == desired_date:
            fetch(link, make_filename(link, desired_date))
            return True

    return False


def expand_dates(start, end):
    return [d for d in iter(rrule(DAILY, dtstart=start, until=end))]


if __name__ == '__main__':
    import json

    j = json.loads(open(sys.argv[1]).read())
    for item in j:
            fetch(item['url'], make_filename(None, item['date']))