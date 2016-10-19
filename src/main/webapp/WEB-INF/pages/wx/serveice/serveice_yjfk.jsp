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
  <title>意见反馈</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/service/service_yjfk'+compresspath+'.css"/>');
  </script>

</head>
<body>
<div class="top">
<div id="opinion">
  <textarea name="" id="textarea" placeholder="请简要描述您的问题与意见" maxlength="300" ></textarea>
  <span id="num">0/300</span>
</div>
<div class="contactWay">
  <span class="label">联系方式</span>
  <input class="value" type="" maxlength="200" placeholder="请输入手机号或邮箱"/>
</div>
<button id="send">提交</button>
</div>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/service/service_yjfk'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
