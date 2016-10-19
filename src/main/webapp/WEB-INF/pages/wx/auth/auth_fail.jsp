<%@ page import="com.alibaba.fastjson.JSONObject,com.rkhd.ienterprise.apps.ingage.enums.*" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/4/19
  Time: 14:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common.css" />
    <title>授权失败</title>
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
<%
    SynErrorMsg error = (SynErrorMsg) request.getAttribute("error");
%>
<body>

<div class="grant-error">
    <div class="ico">
        <span>授权失败了</span>
        <%
                if( SynErrorMsg.NO_USERID.equals(error) || SynErrorMsg.NO_AUTH_USER_INFO.equals(error)){
        %>
        <span>管理员不在授权授权范围内</span>
        <%
                }else {
        %>
        <span>您授权失败了，请联系微信企业号管理员</span>
        <%

            }
        %>

    </div>
</div>


</body>
</html>
