<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/30
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>联系人信息</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/common_info'+compresspath+'.css?v=1"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/contact_info'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
<div class="common">
  <header class="boxSizing">
    <div class="link_more boxSizing">
       <span class="more_ico"></span>
        <ul class="options boxSizing lower">
          <div class="triangle"></div>
          <li class="boxSizing"><a href="javascript:;" id="new_business">新建商机</a></li>
          <li class="boxSizing"><a href="javascript:;" id="set_contact">编辑联系人</a></li>
          <li class="boxSizing"><a href="javascript:;" id="delete_contact">删除联系人</a></li>
        </ul>
     </div>
    <div class="head_pic"></div>
    <p class="post">联系人职位</p>
    <p>
      <span class="accountName">客户名称</span>
    </p>
  </header>
</div>
 <div id="topNav">
    <ul id="nav">
     <!--  <li class="all boxSizing active" href="全部">全部</li> -->
      <li class="schedule boxSizing active" href="详情">详情</li>
      <li class="record boxSizing" href="记录">跟进</li>
      <li class="chance boxSizing" href="商机">商机(0)</li>
      
    </ul>
  </div>
<!-- <div class="content boxSizing">
  <ul class="boxSizing record" id="recordList">
    <div class="top boxSizing">
      <div class="title">跟进记录</div>
    </div>
    <div class="main boxSizing"></div>
  </ul>
  <ul class="boxSizing opportunity" id="opportunityList">
    <div class="top boxSizing">
      <div class="title">商机(0)</div>
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
      <%--<div class="member boxSizing">--%>
        <%--<span class="label">团队成员:</span>--%>
        <%--<span class="value"></span>--%>
      <%--</div>--%>
    </div>
  </ul>
</div> -->
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
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/contact_info'+compresspath+'.js?v=2"></scr'+'ipt>');
</script>
</body>
</html>
