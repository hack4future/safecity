from datetime import date
from crawler import crawl
from services import FireService

crawl(service=FireService, from_date=date(year=2013, month=11, day=1), to_date=date(year=2013, month=11, day=1))