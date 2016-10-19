
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>新建客户列表</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
   <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common_list'+compresspath+'.css?v=1"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/account/account_list'+compresspath+'.css?v=1"/>');
  </script>
</head>
<body>
 <div id="content" class="index-account-content">
 	  <div id="pullDown"></div>
    <ul id="list"></ul>
    <div id="pullUp"></div>
 </div>
</body>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/index_account'+compresspath+'.js?v=1"></scr'+'ipt>');
   document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/iscroll'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</html>
