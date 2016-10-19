<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/1/22
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>新建销售机会</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/opportunity/opportunity_add'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
<ul class="boxSizing section">
  <li id="opportunityName" class="boxSizing">
    <div class="bor boxSizing">
      <span class="label">机会名称<span class="red">*</span></span>
      <input type="text" placeholder="必填" class="value" maxlength="50"/>
    </div>
  </li>
  <li id="accountName" class="boxSizing">
    <div class="bor boxSizing">
      <span class="label">公司<span class="red">*</span></span>
      <div class="value btn">点击选择</div>
    </div>
  </li>
  <li id="money" class="boxSizing">
    <div class="bor boxSizing">
      <span class="label">预计金额<span class="red">*</span></span>
      <input type="tel" placeholder="必填" class="value" maxlength="10" oninput='this.value=this.value.replace(/\D/gi,"")'/>
    </div>
  </li>
  <li id="date" class="boxSizing">
    <div class="bor boxSizing">
      <span class="label">结单日期<span class="red">*</span></span>
      <div class="value"></div>
    </div>
  </li>
</ul>
<ul class="boxSizing section">
  <li id="ownerPost" class="boxSizing">
    <span class="label">所属部门<span class="red">*</span></span>
    <button class="value btn">点击选择</button>
  </li>
</ul>
<ul class="boxSizing section">
  <div id="remark" class="boxSizing">
    <textarea name="" id="textarea" placeholder="请填写备注内容…" maxlength="1000"></textarea>
    <span id="num">0/1000</span>
  </div>
</ul>
<button id="confirm">保存</button>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/opportunity/opportunity_add'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
