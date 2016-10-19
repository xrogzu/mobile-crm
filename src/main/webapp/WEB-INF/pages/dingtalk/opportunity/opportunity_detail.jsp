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
  <title>销售机会详情</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/common_info'+compresspath+'.css?v=1"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/opportunity/opportunity_info'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
<div id="opportunityInfo">
  <div id="picture" class="boxSizing">
    <canvas id="circle"></canvas>
    <div id="progress">
      <p class="title">销售进度</p>
      <div class="percent"></div>
    </div>
  </div>
  <div id="text">
    <p class="opportunityName"></p>
    <p class="title">销售阶段</p>
    <p class="saleStage"></p>
    <p class="title">预计金额</p>
    <p class="money"></p>
    <p class="advance">销售机会推进</p>
  </div>
</div>
<div id="topNav" class="boxSizing">
  <ul id="nav">
    <li class="all boxSizing active" href="全部">全部</li>
    <li class="record boxSizing" href="记录">跟进</li>
    <li class="contact boxSizing" href="联系人">联系人(0)</li>
    <li class="info boxSizing" href="详情">详情</li>
  </ul>
</div>
<div class="content boxSizing">
  <ul class="boxSizing record recordArea" id="recordList">
    <div class="top boxSizing">
      <div class="title">跟进记录</div>
    </div>
    <div class="main boxSizing"></div>
  </ul>
  <ul class="boxSizing contact contactArea" id="contactList">
    <div class="top boxSizing">
      <div class="title">联系人(0)</div>
    </div>
    <div class="main boxSizing"></div>
  </ul>
  <ul class="boxSizing details infoArea" id="infoList">
    <div class="top boxSizing">
      <div class="title">详情</div>
    </div>
    <div class="main boxSizing"></div>
    <div class="staff boxSizing">
      <div class="owner boxSizing">
        <span class="label">负责人:</span>
        <span class="value"></span>
      </div>
      <div class="member boxSizing">
        <span class="label">团队成员:</span>
        <span class="value"></span>
      </div>
    </div>
  </ul>
</div>
<footer class="boxSizing">
  <div class="tel"><em></em><p>电话</p></div>
  <div class="signin"><em></em><p>签到</p></div>
  <div class="record"><em></em><p>记录</p></div>
</footer>
<div id="saleStage">
  <div class="top boxSizing">
    <span class="title">请选择销售阶段</span>
    <em id="exitSaleStage"></em>
  </div>
  <ul class="bottom boxSizing"></ul>
</div>
<div class="shadow"></div>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/opportunity/opportunity_info'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
