<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/2/29
  Time: 10:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>小易秘籍</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/service/service_miji'+compresspath+'.css"/>');
  </script>

</head>
<body>
<div class="container">
  <div id="slides">
    <img src="http://www.xiaoshouyi.com/xymj/images/2411.jpg" >
    <img src="http://www.xiaoshouyi.com/xymj/images/232.jpg" >
    <img src="http://www.xiaoshouyi.com/xymj/images/146.jpg" >
  </div>
</div>
<div class="list boxSizing">
  <em class="icon"></em>
  <a id="zatan" class="btn">
    <p class="top">销售杂谈</p>
    <p class="bot">汇聚销售管理热文及实战技巧</p>
    <em class="jiantou"></em>
  </a>
</div>
<div class="list boxSizing">
  <em class="icon"></em>
  <a id="yanlun" class="btn">
    <p class="top">销售彦论</p>
    <p class="bot">最实用的销售管理心得</p>
    <em class="jiantou"></em>
  </a>
</div>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script src="<%=request.getContextPath()%>/js/wx/service/jquery.slides.min.js"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/service/service_xymj'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
