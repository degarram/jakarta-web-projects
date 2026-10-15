<%--
  Main entry page for the Jakarta web application.

  The page uses UTF-8 for both the HTTP response and JSP source, displays a
  welcome heading, and provides navigation to HelloServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <%-- Browser title shown for the application home page. --%>
  <title>JSP - Hello World</title>
</head>
<body>
<%-- Welcome message rendered on the application home page. --%>
<h1><%= "Hello World!" %></h1>
<br/>
<%-- Relative link to the servlet mapped to /hello-servlet. --%>
<a href="hello-servlet">Hello Servlet</a>
</body>
</html>