<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/3/31
  Time: 15:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>关于销售易</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/service/service_about'+compresspath+'.css"/>');
  </script>
</head>
<body>
<img class="topImg" src="http://m.xiaoshouyi.com/mobile/img/lmbanner-gywm.jpg" alt="关于销售易"/>
<ul class="botList">
  <li class="item1 item boxSizing"><a href="http://m.xiaoshouyi.com/about-us/companyinfo/" class="boxSizing">企业介绍<em></em></a></li>
  <li class="item1 item boxSizing"><a href="http://m.xiaoshouyi.com/advantage/" class="boxSizing">产品优势<em></em></a></li>
  <li class="item1 item boxSizing"><a href="http://m.xiaoshouyi.com/xsycrm/why/" class="boxSizing">为什么选择销售易<em></em></a></li>
  <li class="item2 item boxSizing"><div><span class="label">公司网址</span><a href="http://www.xiaoshouyi.com" class="blue value">www.xiaoshouyi.com</a></div></li>
</ul>

<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/service/service_about'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
