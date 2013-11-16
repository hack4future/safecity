<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
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

<table border="1">
    <g:each in="${data}">
        <tr><td>${it}</td></tr>
    </g:each>
</table>

</body>
</html>