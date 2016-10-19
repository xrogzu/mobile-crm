<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/7/25
  Time: 16:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
   <meta charset="utf-8">
   <meta name = "format-detection" content = "telephone=no">
   <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>我的日程</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <link rel="stylesheet" href="//cdn.bootcss.com/weui/0.4.3/style/weui.min.css">
  <link rel="stylesheet" href="//cdn.bootcss.com/jquery-weui/0.8.0/css/jquery-weui.min.css">
  <script>
    // document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/weui'+compresspath+'.css"/>');
    // document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/jquery-weui'+compresspath+'.css"/>');
     document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/shcedule/shcedule_list'+compresspath+'.css"/>');
  </script>
</head>
<body ontouchstart>
  <div class="shcedule-warp">
  	  <header class="shcedule-header">
  	  	<div class="shcedule-header-left">
  	  		<input id="date3" type="hidden" readonly="">
           <div class="shcedule-header-current"> 
            <span class="shcedule-year"></span><span>年</span>
            <span class="shcedule-month"></span><span>月</span>
           </div>
        </div>
  	  	<div class="shcedule-header-right">
  	  	    <a href="javascript:;" class="shcedule-return"></a>
  	  		<a href="javascript:;" class="shcedule-filter"></a>
  	  		<a href="javascript:;" class="shcedule-add"></a>
  	  	</div>
  	  </header>
  	  <div class="shcedule-datepicker-body">
  	  	 <div class="shcedule-datepicker" id="inline-calendar">
  	  	 </div>
  	  </div>
  	 <div class="schedule-body">
    </div>
  </div>
<%@ include file="../common/footer.jsp"%>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="//cdn.bootcss.com/jquery/1.11.0/jquery.min.js"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/shcedule/shcedule_list'+compresspath+'.js?v=2"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/jquery-weui'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
