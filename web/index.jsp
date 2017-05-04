<%--
  Created by IntelliJ IDEA.
  User: JiYongGuang
  Date: 2017/5/2
  Time: 13:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <h1>index.jsp</h1>
  <%
    System.out.println("index.jsp");
  %>

  <%-- 使用get的方式访问 --%>
  <a href="IndexServlet?username=冀永光">超链接(get方式请求)</a>


  <%--   使用post方式提交表单  --%>
  <form action="/IndexServlet" method="post">
    用户名：<<input type="text" name="username" value="冀永光">
      <input type="submit"value="post方式提交">
  </form>

  </body>
</html>
