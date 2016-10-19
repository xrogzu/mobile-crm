<%@ page import="com.alibaba.fastjson.JSONObject,com.rkhd.ienterprise.apps.ingage.enums.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    PcAdminErrorEnum  error = (PcAdminErrorEnum)request.getAttribute("error");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <link rel="stylesheet" type="text/css" href="../css/wx/common.css" />
    <title>登录失败页面</title>
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
        <span>登录失败了</span>
        <span>登录失败了,请联系我们. <% if(error !=null){%>您的错误编码为<%=error.getErrorCode()%>
        <%}%></span>

    </div>
</div>


</body>
</html>
