<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/30
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.util.SessionUtils" %>--%>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>添加联系人</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/add'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
<div class="block">
  <div id="contactName" class="item boxSizing">
    <div class="bor">
      <span class="tag">姓名<span class="red">*</span></span>
      <input type="text" placeholder="必填" maxlength="50"/>
    </div>
  </div>
  <div id="phone" class="item boxSizing">
    <div class="bor">
      <span class="tag">手机<span class="red">*</span></span>
      <input type="tel" placeholder="必填" maxlength="30"  oninput='this.value=this.value.replace(/\D/gi,"")'/>
    </div>
  </div>
  <div id="accountName" class="item boxSizing">
    <div class="bor">
      <span class="tag">公司<span class="red">*</span></span>
      <button>点击选择</button>
    </div>
  </div>
  <div id="department" class="item boxSizing">
    <div class="bor">
      <span class="tag">部门</span>
      <input type="text" placeholder="点击输入" maxlength="20"/>
    </div>
  </div>
  <div id="post" class="item boxSizing">
    <span class="tag">职务</span>
    <input type="text" placeholder="点击输入" maxlength="50"/>
  </div>
</div>
<div class="block">
  <div id="tel" class="item boxSizing">
    <div class="bor">
      <span class="tag">座机</span>
      <input type="tel" placeholder="点击输入" maxlength="30"/>
    </div>
  </div>
  <div id="email" class="item boxSizing">
    <div class="bor">
      <span class="tag">邮箱</span>
      <input type="text" placeholder="点击输入" maxlength="100"/>
    </div>
  </div>
  <div id="address" class="item boxSizing">
    <span class="tag">地址</span>
    <input type="text" placeholder="点击输入" maxlength="250"/>
  </div>
</div>
<div class="block">
  <div id="ownerPost" class="item boxSizing">
    <span class="tag">所属部门<span class="red">*</span></span>
    <button></button>
  </div>
</div>
<div id="remark" class="block boxSizing">
  <textarea name="" id="textarea" placeholder="请填写备注内容…" maxlength="1000"></textarea>
  <span id="num">0/1000</span>
</div>
<button id="confirm" class="confirm">保存</button>
<button id="add_contact" class="confirm">保存并添加新联系人</button>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/contact_add'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
