<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/4/19
  Time: 14:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.util.*" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals" %>


<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>登录中</title>
    <script>
        document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common.css?v=1"/>');
        document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/login.css?v=1"/>');
    </script>
</head>
<body>
<body>
<div class="loading">
<!--  <span class="ico"></span>
 <span class="bottom"></span>
  <i class="left"></i> -->
   <img src="data:image/gif;base64,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" alt="">
<span class="des">努力加载中...</span>
</div>

</body>

</body>
<script type="text/javascript">
    <!--
    window.onload = function (){
        window.top.location ='<%=request.getContextPath()%>/wx/statics/index.action';
    }
    //-->
</script>
</html>
