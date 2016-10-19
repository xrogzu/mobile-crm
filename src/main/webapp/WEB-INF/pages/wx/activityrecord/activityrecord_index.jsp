<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/1/28
  Time: 15:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name = "format-detection" content = "telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>跟进记录</title>
    <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
    <script>
      document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
      document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/index'+compresspath+'.css?v=1"/>');
    </script>

</head>
<body>
  <ul class="boxSizing record" id="recordList">
    <div class="top boxSizing">
      <div class="title boxSizing"><span>跟进记录(0)</span>
        <div class="selector boxSizing">
          <span>本周</span>
          <ul class="options boxSizing">
            <div class="triangle"></div>
            <li class="boxSizing" value="1">本周</li>
            <li class="boxSizing" value="2">上周</li>
            <li class="boxSizing" value="4">本月</li>
            <li class="boxSizing" value="3">上月</li>
          </ul>
        </div>
      </div>
    </div>
    <div class="main boxSizing"></div>
  </ul>
</body>
<script>
    var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/service/service_activerecord'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</html>
