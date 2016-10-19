<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/1/16
  Time: 17:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
  <title></title>
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common.css"/>
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/list_page.css"/>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/wx/ddcommon/AppJSLogin.js"></script>
  <script>
    dd.ready(function() {
      dd.biz.navigation.setRight({
        show:false
      })
    })
  </script>
  <script>
    var apppath = "<%=request.getContextPath()%>";
  </script>
</head>
<body>
<script src="<%=request.getContextPath()%>/js/wx/jquery-1.11.3.js"></script>
<script src="<%=request.getContextPath()%>/js/wx/common.js?v=1"></script>
<script src="<%=request.getContextPath()%>/js/wx/list_page.js"></script>
</body>
</html>
