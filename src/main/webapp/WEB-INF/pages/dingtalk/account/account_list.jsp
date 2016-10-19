<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/30
  Time: 10:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>公司列表</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common_list'+compresspath+'.css?v=1"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/account_list'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
  <div class="common">
    <div id="search" class="boxSizing"><span>搜索公司和联系人</span></div>
    <div id="trigger" class="boxSizing">
      <div class="btn active boxSizing" id="company">公司</div>
      <div class="btn boxSizing" id="linkman">联系人</div>
      <div id="filter" class="filter normal"></div>
      <div id="filter_area" class="boxSizing">
        <div class="shadow">
          <div class="title">公司类型</div>
          <div class="content">
            <div id="all" class="filter_btn boxSizing active">全部</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div id="content">
    <ul id="list"></ul>
  </div>
  <%@ include file="../common/footer.jsp"%>
  <script>
    var apppath = "<%=request.getContextPath()%>";
  </script>
  <script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
  <script>
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/account_list'+compresspath+'.js?v=1"></scr'+'ipt>');
  </script>
</body>
</html>
