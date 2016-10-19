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
    <title>日程详情页</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/shcedule/shcedule_info'+compresspath+'.css"/>');

  </script>
</head>
<body ontouchstart>
  <div class="shcedule-detail-warp">
  	 <header class="shcedule-detail-header">
  	 	<div class="shcedule-detail-header-cell">
  	 		<div class="shcedule-detail-header-left">
  	 			<p class="shcedule-detail-style"></p>
  	 			<p class="shcedule-detail-title"></p>
  	 			<p class="shcedule-detail-time">
  	 				<span class="shcedule-detail-time-start"></span>
  	 				<span class="shcedule-detail-time-line">-</span>
  	 				<span class="shcedule-detail-time-end"></span>
  	 			</p>
  	 		</div>
  	 		<div class="shcedule-detail-header-right">
  	 			<a href="javascript:;" class="more" id="shcedule_more"></a>
  	 			<ul class="options boxSizing lower">
		          <div class="triangle"></div>
		          <li class="boxSizing"><a href="javascript:;" id="schedule_edit">编辑</a></li>
		          <li class="boxSizing"><a href="javascript:;" id="schedule_exit">退出</a></li>
		          <li class="boxSizing"><a href="javascript:;" id="schedule_delete">删除</a></li>
		        </ul>
  	 		</div>
  	 	</div>
  	 </header>
  	 <div class="shcedule-detail-body">
  	 	<div class="shcedule-detail-body-cell">
  	 		<span class="shcedule-detail-cell-title" id="belongId">关联业务</span>
  	 		<span class="" id="objectId"></span>
  	 	</div>
  	 	<div class="shcedule-detail-body-cell">
  	 		<span class="shcedule-detail-cell-title">参与人</span>
  	 		<span class="shcedule-detail-cell-des" id="members">
  	 		</span>
  	 	</div>
        <div class="shcedule-detail-body-cell" id="waitmemberArray">
        <span class="shcedule-detail-cell-title">待确认</span>
        <span class="shcedule-detail-cell-des" id="waitmembers">
        </span>
      </div>
  	 	<div class="shcedule-detail-body-cell">
  	 		<span class="shcedule-detail-cell-title">重复</span>
  	 		<span class="shcedule-detail-cell-des" id="frequency"></span>
  	 	</div>
      <div class="shcedule-detail-body-cell" id="recurStopCondition_cell">
        <span class="shcedule-detail-cell-title">终止条件</span>
        <span class="shcedule-detail-cell-des" id="recurStopCondition"></span>
      </div>
      <div class="shcedule-detail-body-cell" id="recurStopValue_cell">
        <span class="shcedule-detail-cell-title">终止时间</span>
        <span class="shcedule-detail-cell-des" id="recurStopValue"></span>
      </div>
  	 	<div class="shcedule-detail-body-cell">
  	 		<span class="shcedule-detail-cell-title">备注</span>
  	 		<span class="shcedule-detail-cell-des" id="description"></span>
  	 	</div>
  	 	<div class="shcedule-detail-body-cell">
  	 		<span class="shcedule-detail-cell-title">私密</span>
  	 		<span class="shcedule-detail-cell-des" id="isPrivate"></span>
  	 	</div>
  	 </div>
      <footer class="shcedule-detail-footer" style='display: none;'>
      	<div class="shcedule-detail-comment">
      		<div class="shcedule-detail-comment-input">
      			<input type="text" placeholder="输入评论内容">
      		</div>
      		<div class="shcedule-detail-comment-audio">
      			<a href="javascript:;" class="shcedule-detail-audio-ico"></a>
      		</div>
      	</div>
      </footer>  
  </div>
<div class="load-shadow">
 <div class="load">
        <div class="load-inner ball-spin-fade-load">
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
        </div>
      </div>
</div>
<div class="shcedule-operation-area">
  <a href="javascript:;" class="shcedule-reject">拒绝</a>
  <a href="javascript:;" class="shcedule-accept">接受</a>
</div>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="//cdn.bootcss.com/jquery/1.11.0/jquery.min.js"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
      document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/shcedule/shcedule_info'+compresspath+'.js?v=3"></scr'+'ipt>');
</script>
</body>
</html>
