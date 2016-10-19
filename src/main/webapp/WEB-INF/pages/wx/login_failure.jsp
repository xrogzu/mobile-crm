<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.rkhd.ienterprise.apps.ingage.enums.*" %>
<%

    IndexError error = (IndexError )request.getAttribute("LoginError");
%>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common.css" />
    <title>失败页面</title>
    <style type="text/css">
        body {
            font-family: "微软雅黑";
            color: #333;
            background: #fff;
            height: 100%;
            width: 100%;
        }
    </style>
</head>

<body>
<div class="grant-error">
    <div class="ico">
        <span>无法登录</span>
        <% if(error == null ){%>
            <span>登录失败,您可能未被授权使用销售易,请联系管理员</span>
        <%} else  if(  error.equals(IndexError.NO_LICENESE)){%>
            <span>登录失败，请管理员给您进行授权license</span>
        <%}else  if(  error.equals(IndexError.LICENESE_EXPIRE)){%>
        <span>登录失败，管理员给您的授权已经过期了</span>
        <%}else  if(  error.equals(IndexError.LICENESE_NO_GET_AUTHORIZATION)){%>
            <span>登录失败，请管理员对您进行授权</span>
        <%} else {%>
            <span>登录失败,您可能未被授权使用销售易,请联系管理员,<%=error.getErrorCode()%></span>
        <% }%>
    </div>
</div>
</body>

</html>
