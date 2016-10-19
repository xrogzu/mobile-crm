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
    <title>公司信息</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/common_info'+compresspath+'.css?v=1"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/account_info'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
  <div class="common">
    <header class="boxSizing">
      <div class="head_pic"></div>
      <p>
        <span class="title">公司名称</span>
      </p>
    </header>
    <div id="topNav">
      <ul id="nav">
        <li class="all boxSizing active" href="全部">全部</li>
        <li class="record boxSizing" href="记录">跟进</li>
        <li class="contact boxSizing" href="联系人">联系人(0)</li>
        <li class="chance boxSizing" href="商机">销售机会(0)</li>
        <li class="schedule boxSizing" href="详情">详情</li>
      </ul>
    </div>
    <div class="white_ban">
      <div class="right_filter">
        <div class="filter filter1"></div>
        <div class="filter filter2"></div>
        <div class="filter filter3"></div>
        <div class="arrow"></div>
      </div>
      <div class="left_filter">
        <div class="filter filter1"></div>
        <div class="filter filter2"></div>
        <div class="filter filter3"></div>
        <div class="arrow"></div>
      </div>
    </div>
  </div>
  <div class="content boxSizing">
    <ul class="boxSizing record" id="recordList">
      <div class="top boxSizing">
        <div class="title">跟进记录</div>
      </div>
      <div class="main boxSizing"></div>
    </ul>
    <ul class="boxSizing contact" id="contactList">
      <div class="top boxSizing">
        <div class="title">联系人(0)</div>
      </div>
      <div class="main boxSizing"></div>
    </ul>
    <ul class="boxSizing opportunity" id="opportunityList">
      <div class="top boxSizing">
        <div class="title">销售机会(0)</div>
      </div>
      <div class="main boxSizing"></div>
    </ul>
    <ul class="boxSizing details" id="infoList">
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
  <script>
    var apppath = "<%=request.getContextPath()%>";
  </script>
  <script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
  <script>
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/account_info'+compresspath+'.js?v=1"></scr'+'ipt>');
  </script>
</body>
</html>
