<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/11
  Time: 17:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.alibaba.fastjson.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.dto.*" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.utils.ErrorCode" %>
<%
    EntityReturnData jsonData =  (EntityReturnData) request.getAttribute("jsondata");
    if(!jsonData.isSuccess()){
        String error_code = jsonData.getErrorCode();
        if("300001".equals(error_code)){
            jsonData.setSuccess(false);
            ErrorObj errorObj = new ErrorObj();
            errorObj.setKey("NOOPERATIONPERMISSIONS");
            errorObj.setMsg("没有查看权限");
            errorObj.setStatus(ErrorCode.NOOPERATIONPERMISSIONS.getErrorCode());
            jsonData.setEntity(errorObj);
        }
    }
%>
<%=JSON.toJSONString(jsonData)%>
