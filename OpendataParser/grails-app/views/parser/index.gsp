<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <style type="text/css">
        .pub-date {
            font-weight: bold;
            color: red;
        }
        table {
            border-collapse: collapse;
        }

    </style>
</head>
<body>

<g:form action="parseUrl">
    <input type="text" name="url" value="" placeholder="url"/>
    <g:submitButton name="parseUrl" value="parseUrl"/>
</g:form>

<g:uploadForm action="parseFile">
    <input type="file" name="file"/>
    <g:submitButton name="parseFile" value="parseFile"/>
</g:uploadForm>

<g:if test="${data}">
    <div class="pub-date">${data.pubDate}</div>

    <h2>Пожары (прочие)</h2>
    <table border="1">
        <tr>
            <g:each in="${data.columnsNames}">
                <th>${it}</th>
            </g:each>
        </tr>
        <g:each in="${data.tableRows}" var="row">
            <tr>
                <g:each in="${row}">
                    <td>${it}</td>
                </g:each>
            </tr>
        </g:each>
    </table>
</g:if>
<g:else>${flash.error}</g:else>

</body>
</html>