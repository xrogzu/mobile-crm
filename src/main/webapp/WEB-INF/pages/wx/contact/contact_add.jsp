<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/30
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.util.SessionUtils" %>--%>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>添加联系人</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/add'+compresspath+'.css?v=1"/>');
  </script>
<script src="<%=request.getContextPath()%>/js/wx/datepicker.js"></script>
</head>
<body>
<div id="basic_info" class="boxSizing">
    <div class="title boxSizing"></div>
</div>
<div class="load-shadow">
    <div class="load">
        <div class="load-inner ball-spin-fade-load">
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
        </div>
      </div>
 </div>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common_list'+compresspath+'.css?v=1"/>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/contact_add'+compresspath+'.js?v=2"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/iscroll'+compresspath+'.js"></scr'+'ipt>');
</script>
</body>
</html>
