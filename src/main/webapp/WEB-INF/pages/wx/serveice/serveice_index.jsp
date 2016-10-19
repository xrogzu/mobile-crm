<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/2/29
  Time: 10:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>我</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/weui'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/jquery-weui'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/service/service_index'+compresspath+'.css"/>');
  </script>
    <style>

    </style>
</head>
<body>
<div class="me-warp">
  <header>
     <div class="swiper-container">
      <!-- Additional required wrapper -->
      <div class="swiper-wrapper">
        <!-- Slides -->
        <div class="swiper-slide"><a href="http://m.xiaoshouyi.com/community/crm-gsdt/337.html"><img src="../../images/wx/2x/service/swiper_1.png" /></a></div>
        <div class="swiper-slide"><a href="http://m.xiaoshouyi.com/community/crm-gsdt/338.html"><img src="../../images/wx/2x/service/swiper_2.png" /></a></div>
        <div class="swiper-slide"><a href="http://m.xiaoshouyi.com/community/crm-gsdt/339.html"><img src="../../images/wx/2x/service/swiper_3.png" /></a></div>
      </div>
      <!-- If we need pagination -->
      <div class="swiper-pagination"></div>
    </div>
  </header>
  <section>
    <div class="me-body">
        <div class="me-content">
          <span class="me-content-header">
              工作
          </span>
          <div class="me-content-body">
             <div class="me-content-body-item">
             <!--   <span class="me-message-tip">5</span> -->
               <a href="javascript:;" class="me-feed-ico">
                 <i></i>
                 <p>跟进记录</p>
               </a>
             </div>
              <div class="me-content-body-item">
               <a href="javascript:;" class="me-schedule-ico">
                 <i></i>
                 <p>日程</p>
               </a>
             </div>
              <div class="me-content-body-item">
              <!--  <a href="javascript:;" class="me-workreport-ico">
                 <i></i>
                 <p>工作报告</p>
               </a> -->
             </div>
          </div>
        </div>
         <div class="me-content">
          <span class="me-content-header">
              服务
          </span>
          <div class="me-content-body">
             <div class="me-content-body-item">
               <a href="javascript:;" class="me-feedback-ico">
                 <i></i>
                 <p>意见反馈</p>
               </a>
             </div>
            <div class="me-content-body-item">
               <a href="javascript:;" class="me-about-ico">
                 <i></i>
                 <p>关于</p>
               </a>
             </div>
              <div class="me-content-body-item">
               <a href="javascript:;" class="me-experience-ico" style="display: none;">
                 <i></i>
                 <p>快速体验</p>
               </a>
             </div>
          </div>
        </div>
    </div>
  </section>
</div>
<%@ include file="../common/footer.jsp"%>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/service/service_index'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/jquery-weui'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/swiper'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
<script type="text/javascript">
      $(".swiper-container").swiper({
        loop: false,
        autoplay: 3000
      });
</script>
</body>
</html>
