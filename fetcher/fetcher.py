import sys
from urllib2 import urlopen


def filename_from_url(arg):
    return '2222.csv'


for arg in sys.argv[1:]:
    url = 'http://54.200.194.23/parser/parser/parseUrl?url=%s&format=csv' % arg
    print(url)
    localFile = open(filename_from_url(arg), 'w')
    localFile.write(urlopen(url).read())
    localFile.close()
