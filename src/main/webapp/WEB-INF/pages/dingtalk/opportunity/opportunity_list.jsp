<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/1/22
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>销售机会</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common_list'+compresspath+'.css?v=1"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/opportunity/opportunity_list'+compresspath+'.css?v=1"/>');
  </script>
</head>
<body>
<div class="common">
  <div id="search" class="boxSizing"><span>搜索销售机会</span></div>
  <div class="sorter">
    <div class="by_stage boxSizing sort"><span class="active">按阶段</span></div>
    <div class="by_money boxSizing sort"><span>按金额</span></div>
    <div class="by_time boxSizing sort"><span>按更新</span></div>
    <div class="screen boxSizing"><span>筛选</span></div>
  </div>
</div>
<div id="content">
  <ul class="boxSizing opportunity" id="list">
    <div class="main boxSizing"></div>
  </ul>
</div>
<%@ include file="../common/footer.jsp"%>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/opportunity/opportunity_list'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
