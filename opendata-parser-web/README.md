# opendata-parser-web #

Web application on stack: Java 7, Groovy, Grails.

Application parses MCHS website pages by request.
### URL ### http://54.200.194.23/parser/

Installation & deployment.
1. Install Java 7 JDK, set JAVA_HOME.
2. Install Groovy 1.8.6, set GROOVY_HOME.
3. Install Grails 2.2.3, set GRAILS_HOME.
4. Include (place into project-dir/lib/ directory) library jsoup-1.7.3.jar.
5. In bash: 
    cd project-dir;
    grails prod war; - to build app
    grails run-app; - to run in dev mode
6. Built .war will be in project-dir/target
7. Deploy .war on web-app-server (jetty-6.1.26 tested).


Input.
1) URL to parse;
2) output data format (---/csv):
    - if '---' - renders parsed data as table;
    - if 'csv' - render parsed data as .csv;
3) file - you can upload file (.html) to parse;

Output.
Returns parsed data as table or as .csv.
Output data mapped by three columns: 'date', 'address', 'description'.

API.
Use it for sending automatic requests.
Example of request: http://54.200.194.23/parser/parser/parseUrl?url=http://url.url/&format=csv
Method: GET.
Parameters:
- url: URL to parse;
- format: format of output file - 'csv' only for now.