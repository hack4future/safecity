Safe City
=========

"Safe City" is an initiative to represent information from
emergency organizations in convenient form for data
processing and visualization (transform to "open data").

The project pursues three goals:

 - automatic processing of data from official web pages,
   transformation of data to machine readable format,
   with researching of this open data format itself

 - representation of the data based on exported format for
   generic audience - web pages with diagrams and stats

 - create maintainable and extendable service that can be
   reused by people with low to zero maintenance


MCHS Belarus
============

Why MCHS site is not open data?

1. inconsistent URL scheme for daily summary

It is impossible to determine address of daily summary
without human a operator [ ]. For examples, current URL for
daily summary:

    http://mchs.gov.by/rus/main/ministry/regional_management/str_minsk/news_minsk/~year__m22=2013~month__m22=10~page__m22=1~news__m22=19699

This selects summary for 31th of October 2013. Parameters:

    ~year__m22=2013~month__m22=10~page__m22=1~news__m22=19699

Logically, params `year`, `month` should somehow relate to
date selection. In fact, they just choose which element in
navigational calendar is colored red. You still get the
summary for 31th of October.

How to fix? Proposed URL scheme:

    http://mchs.gov.by/rus/minsk/news?date=2013-10-31&page=summary

'summary' means emergency summary, which is a relevant
news or summary entry. Actually, it will be more
convenient to get the data as:

    http://mchs.gov.by/summary

Description of available export formats for region,
example statistics/visualization base on exposed format
and URLs get data for each region in open data formats:

    http://mchs.gov.by/summary/minsk

The same for Minsk, available formats for minsk, URLs for
each format. Example of format selection by URL
parameters for the city:

    /summary/minsk?date=2013-10-31&format=csv

2. exported tables are not consistent

Table with the same info for one day can be completely
different on another. In particular:

  1. Column names are different for the same content
  2. Column positions are different
  3. Table structure varies (rowspans)
  4. For the same column type with limited choices,
     every choice may be spelled differently or with
     errors

2. no open export formats

No fields descriptions, no standard, version. No exported
formats: CSV, XML, JSON - these are minimally required.
Well, at least CSV. Ideally custom format with commented
fields. For academics, annotated with RDF meanings.

3. data not validated, quality is not sufficient

Data contains spelling errors [ ]. Category names are not
selected from existing limited set, but probably typed
manually. There should be a form for validation/import of
manual data, so that operator can get automatic feedback
from the system.


Safe City project structure
===========================

[ ] Get data from MCHS site
[ ] Transform data into useful format
[ ] Build something awesome
[ ] Export data in open format for other people

[ ] Provide place for feedback: link to further works
    based on open data, and for request to add new fields
    and expand open data structures (new versions)
    

Technical: Workflow
===================

Data transformation process:

    +--------+    +---------+    +---------+
    | Order  |--->| Fetch   |--->| Parse   |--->
    +--------+    +---------+    +---------+    

    +--------+    +---------+    +---------+
    | Map    |--->| Norm    |--->| Export  |--->
    +--------+    +---------+    +---------+    

    +--------+    +---------+    +---------+
    | Read   |--->| Build   |--->| Render  |
    +--------+    +---------+    +---------+    

1. Order - form request to fetch for given date:

   [ ] cron
   [ ] user visit from site
   [ ] explicitly filled form

2. Fetch - detect address where the data resides:
 
   [ ] configurable URL

3. Parse - transform data into machine readable format:

   [ ] split page into tables
    [ ] detect table types by information type
   [ ] different parsers for each table info type
    [ ] different parsers for the same table info type
    [ ] choose most suitable parser
   [ ] validate result is correct
   [ ] report result

4. Map - map custom filed names into data field names

5. Norm - normalize data is custom fiels names to be
   in appropriate meaningful format. Normalize dates,
   normalize addresses, reconcile (recover) missing
   fields

6. Export - output in CSV, XML, other cool formats

7. Read - fetch CSV and other cool format info app

8. Build - statistics, tables, diagrams, animation,
   report

9. Render - output HTML page, PDF, upload report, add
   entry to DB


Technical: Components
=====================

Order - [ ] Drupal Site Form
Fetch - [ ] Drupal outpuing request
     or [x] crawler (Python + libs in README.md)
Parse - [x] OpendataParser (Java, Groovy, GRails, jsoup)
Map   - [ ] OpendataParser
Norm  - [ ] OpendataParser
Export - [ ] OpendataParser
Read  - [ ] crawler
         [x] request to OpendataParser
         [x] save to disk
         [ ] return to Drupal
Build - [ ] Drupal
Render - [ ] Drupal
