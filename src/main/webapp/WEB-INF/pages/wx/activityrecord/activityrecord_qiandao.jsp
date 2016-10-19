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
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>签到</title>
    <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/qiandao.css?v=1"/>
    <!-- <link rel="stylesheet" href="https://cache.amap.com/lbs/static/main1119.css?v=3"/> -->
    <script>
        var apppath = "<%=request.getContextPath()%>";
    </script>
    <script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
    <script type="text/javascript" src="https://webapi.amap.com/maps?v=1.3&key=39f2cf0000442ff2fd96f705b78953c8"></script>
   <!--  <script type="text/javascript" src="https://cache.amap.com/lbs/static/addToolbar.js"></script> -->

</head>
<body>
<div id="current_time" class="boxSizing">
    <span class="label">当前时间</span>
    <span class="value"></span>
</div>
<div id="my_position" class="boxSizing">
    <div class="main">
        <span class="label">我的位置</span>
        <span class="value"></span>
    </div>
</div>
<div id="account" class="boxSizing">
    <div class="main">
        <span class="label">关联业务</span>
        <span class="value"></span>
    </div>
</div>
<div id="comment" class="boxSizing">
    <div class="main">
        <textarea name="" class="value" id="textarea" placeholder="请填写内容...">我刚刚拜访了这个客户</textarea>
        <span id="num">0/2000</span>
        <div class="clear_float"></div>
    </div>

</div>
<button id="confirm" class="confirm">签到</button>
<div id="container"></div>
<div id="panel"></div>

<script src="<%=request.getContextPath()%>/js/wx/common.js?v=1"></script>
<script src="<%=request.getContextPath()%>/js/wx/qiandao.js?v=3"></script>
<%--<script>--%>
    <%--document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');--%>
    <%--document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/qiandao'+compresspath+'.js?v=1"></scr'+'ipt>');--%>
<%--</script>--%>
</body>
</html>
