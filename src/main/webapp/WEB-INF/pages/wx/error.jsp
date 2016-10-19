<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/2/29
  Time: 10:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.wx.helper.JSApiHelper" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>授权失败页面</title>
   <jsp:include page="../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <style type="text/css">
     body {
        font-family: "微软雅黑";
        color: #333;
        background: #fff;
        height: 100%;
        width: 100%;
    }
    
    * {
        margin: 0;
        padding: 0;
    }
  </style>
  <script>
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
  </script>
  <script>
  var apppath = "<%=request.getContextPath()%>";
  </script>
</head>
<body>
   <div class="grant-error">
        <div class="ico">
        <span>无法登陆</span>
        <span>您未被授权成功，请联系微信企业号管理员</span>
        </div>
       
    </div>
</body>
</html>
