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
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>添加跟进记录</title>
    <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
    <script>
        document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
        document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/create'+compresspath+'.css?v=1"/>');
    </script>

</head>
<body>
<div id="relevance" class="boxSizing">
    <span class="label">关联业务</span>
    <span class="value"></span>
</div>
<div id="comment" class="boxSizing">
    <div class="main">
        <textarea name="" class="value" id="textarea" placeholder="请填写内容..." autofocus="autofocus"></textarea>
        <span id="num">0/2000</span>
        <div class="clear_float"></div>
    </div>

</div>
<button id="confirm" class="confirm">确定</button>
<script>
    var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/create'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
