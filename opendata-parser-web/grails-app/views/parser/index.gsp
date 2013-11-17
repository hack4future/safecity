<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <style type="text/css">
        table {
            border-collapse: collapse;
        }

    </style>
</head>
<body>

<g:form action="parseUrl" method="GET">
    <input type="text" name="url" value="" placeholder="url"/>
    <g:select name="format" from="['---', 'csv']" value="---"/>
    <g:submitButton name="parseUrl" value="parseUrl"/>
</g:form>

<g:uploadForm action="parseFile">
    <input type="file" name="file"/>
    <g:submitButton name="parseFile" value="parseFile"/>
</g:uploadForm>

<g:if test="${data}">
    <h2>Пожары</h2>
    <table border="1">
        <tr>
            <g:each in="${data.first().dataMap.keySet()}">
                <th>${it}</th>
            </g:each>
        </tr>
        <g:each in="${data}" var="row">
            <tr>
                <g:each in="${row.dataMap.values()}">
                    <td>${it}</td>
                </g:each>
            </tr>
        </g:each>
    </table>
</g:if>
<g:else>${flash.error}</g:else>

<g:if test="${outputFileName}">
    <br/>
    <g:link action="downloadCSV" params="[fileName: outputFileName]">
        Show .CSV
    </g:link>
</g:if>

</body>
</html>