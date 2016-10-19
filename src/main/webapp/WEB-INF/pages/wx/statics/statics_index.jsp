<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/2/29
  Time: 10:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.wx.helper.JSApiHelper" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  
  <title>首页</title>

  <jsp:include page="../ddcommon/AppJSApiConfig.jsp" flush="true" >
    <jsp:param name="url" value="/wx/static/index.action" />
  </jsp:include>
  <script>
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/index'+compresspath+'.css?v=1"/>');
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/jquery-weui'+compresspath+'.css"/>');
  </script>

</head>
<body style="overflow: hidden;">
<header>
  <div id="sort" class="boxSizing">
    <div class="behavior active boxSizing">今日工作</div>
    <div class="performance  boxSizing">统计分析</div>
  </div>
</header>
<%--行为统计--%>
<div class="swiper-container">
  <section class="index-body swiper-wrapper">
    <div id="behaviorPage" class="boxSizing page swiper-slide">
        <!-- <div class="load-shadow" style="display: none;">
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
         </div> -->
        <%--首页日程--%>
        <div class="schedule-body">
        </div>
     
    </div>
<%--业绩统计--%>
<div id="performancePage" class="boxSizing page swiper-slide">
<div id="scroll">
  <%--工作简报--%>
    <div id="workreport" class="workreport boxSizing">
      <div class="title boxSizing">简报</span>
        <div class="selector boxSizing" id="workreport_select">
          <span>本周</span>
          <ul class="options boxSizing">
            <div class="triangle"></div>
            <li class="boxSizing" value="12">本日</li>
            <li class="boxSizing" value="1">本周</li>
            <li class="boxSizing" value="4">本月</li>
            <li class="boxSizing" value="7">本季</li>
            <li class="boxSizing" value="10">本年</li>
          </ul>
        </div>
      </div>
     <div  class="workreport-content">
        <div class="workreport-content-item">
          <div class="workreport-item-left"><span class="acc_ico"></span></div>
          <div class="workreport-item-center" id="count_account">
            <span class="workreport-item-title">
              <span class="workreport-select-date" value="1">本周</span>
              <span>新增客户</span>
            </span>
            <span class="workreport-item-count">
              <span class="num" id="maccount_num"></span>
              <span>个</span>
            </span>
          </div>
          <div class="workreport-item-right" id="acount_account">
             <span class="workreport-item-title">
              <span>客户总数</span>
            </span>
            <span class="workreport-item-count">
              <span class="num" id="account_num"></span>
              <span>个</span>
            </span>
          </div>
        </div>
         <div class="workreport-content-item">
          <div class="workreport-item-left"><span class="con_ico"></span></div>
          <div class="workreport-item-center" id="count_contact">
            <span class="workreport-item-title">
              <span class="workreport-select-date" value="1">本周</span>
              <span>新增联系人</span>
            </span>
            <span class="workreport-item-count">
              <span class="num" id="mcontact_num"></span>
              <span>个</span>
            </span>
          </div>
          <div class="workreport-item-right" id="acount_contact">
             <span class="workreport-item-title">
              <span>联系人总数</span>
            </span>
            <span class="workreport-item-count">
              <span class="num" id="contact_num"></span>
              <span>个</span>
            </span>
          </div>
        </div>
        <div class="workreport-content-item">
          <div class="workreport-item-left"><span class="opp_ico"></span></div>
          <div class="workreport-item-center" id="count_opp">
            <span class="workreport-item-title">
              <span class="workreport-select-date" value="1">本周</span>
              <span>新增商机</span>
            </span>
            <span class="workreport-item-count">
              <span class="num" id="mopp_num"></span>
              <span>个</span>
            </span>
          </div>
          <div class="workreport-item-right" id="acount_opp">
             <span class="workreport-item-title">
              <span>商机总数</span>
            </span>
            <span class="workreport-item-count">
              <span class="num" id="opp_num"></span>
              <span>个</span>
            </span>
          </div>
        </div>
     </div>
    </div>
    <%--业绩排名--%>
    <div id="ranking" class="ranking boxSizing">
      <div class="title boxSizing">业绩排名<span class="yuan">(元)</span>
        <div class="selector boxSizing">
          <span>本月</span>
          <ul class="options boxSizing">
            <div class="triangle"></div>
            <li class="boxSizing">本月</li>
            <li class="boxSizing">上月</li>
            <li class="boxSizing">本季</li>
            <li class="boxSizing">上季</li>
            <li class="boxSizing">本年</li>
            <li class="boxSizing">上年</li>
          </ul>
        </div>
      </div>
      <ul class="list boxSizing"></ul>
      <ul class="noDataRanking">
        <li><div></div></li>
        <li><div></div></li>
        <li><div></div></li>
        <li><div></div></li>
        <p>该范围内没有赢单的商机</p>
      </ul>
    </div>
     <%--行为汇总--%>
      <div class="actSum">
      <div class="title boxSizing">行为汇总
        <div class="selector boxSizing">
          <span>本周</span>
          <ul class="options boxSizing">
            <div class="triangle"></div>
            <li class="boxSizing" value="1">本周</li>
            <li class="boxSizing" value="2">上周</li>
            <li class="boxSizing" value="4">本月</li>
            <li class="boxSizing" value="3">上月</li>
          </ul>
        </div>
      </div>
   
        <ul class="boxSizing list"></ul>
   
    </div>
    
    <%--销售漏斗--%>
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
    <div class="funnelTop boxSizing">销售漏斗

      <div class="selector boxSizing">
        <span>本月</span>
        <ul class="options boxSizing">
          <div class="triangle"></div>
          <li class="boxSizing" value="4">本月</li>
          <li class="boxSizing" value="5">下月</li>
          <li class="boxSizing" value="7">本季</li>
          <li class="boxSizing" value="8">下季</li>
          <li class="boxSizing" value="10">本年</li>
          <li class="boxSizing" value="11">下年</li>
        </ul>
      </div>
       <div class="title boxSizing">
        <div class="btn">个数</div>
      </div>
    </div>
    <div id="saleFunnel" class="boxSizing">
     <!--  <div class="title boxSizing" style="display: none;">
        <div class="btn">个数</div>
      </div> -->
      <div class="funnelArea">
        <div id="funnel">
          <div class="funnel1"></div>
          <div class="funnel2"></div>
          <div class="funnel3"></div>
          <div class="funnel4"></div>
        </div>
        <div id="leftLine">
          <div class="line line1"><span class="info"></span><em class="ball"></em></div>
          <div class="line line2"><span class="info"></span><em class="ball"></em></div>
          <div class="line line3"><span class="info"></span><em class="ball"></em></div>
          <div class="line line4"><span class="info"></span><em class="ball"></em></div>
        </div>
        <div id="rightLine">
          <div class="line line1"><span class="info"></span><em class="ball"></em></div>
          <div class="line line2"><span class="info"></span><em class="ball"></em></div>
          <div class="line line3"><span class="info"></span><em class="ball"></em></div>
          <div class="line line4"><span class="info"></span><em class="ball"></em></div>
        </div>
      </div>
      <div class="noDataFunnel">
        <div class="emptyFunnel">
          <div class="funnel4"></div>
          <div class="funnel3"></div>
          <div class="funnel2"></div>
          <div class="funnel1"></div>
          <p>该范围内没有商机</p>
        </div>
      </div>
    </div>
    <div id="aim">
      <ul class="boxSizing">
        <li class="actual boxSizing">
          <p class="value"></p>
          <p class="label">实际完成(元)</p>
          <div class="bor"></div>
        </li>
        <li class="predict boxSizing">
          <p class="value"></p>
          <p class="label">预计完成(元)</p>
          <div class="bor"></div>
        </li>
        <li class="whole boxSizing">
          <p class="value"></p>
          <p class="label">漏斗总值(元)</p>
        </li>
      </ul>
    </div>

    <%--重点商机--%>
    <div class="opportunitybox">
       <ul class="boxSizing opportunity" id="list">
      <div class="title boxSizing">
        <span class="words">重点商机<span>(Top10)</span></span>
        <div class="selector boxSizing">
          <span>本月</span>
          <ul class="options boxSizing">
            <div class="triangle"></div>
            <li class="boxSizing" value="4">本月</li>
            <li class="boxSizing" value="5">下月</li>
            <li class="boxSizing" value="7">本季</li>
            <li class="boxSizing" value="8">下季</li>
            <li class="boxSizing" value="10">本年</li>
            <li class="boxSizing" value="11">下年</li>
          </ul>
        </div>
      </div>
      <div class="main boxSizing"></div>
    </ul>
    </div>
    </div>
</div>
</section>
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
<%@ include file="../common/footer.jsp"%>

<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/index'+compresspath+'.js?v=2"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/jquery-weui'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/swiper'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/iscroll'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
<script type="text/javascript">
  var scroll = new  IScroll('#performancePage', {
       mouseWheel:true,
        bounce: true, //是否超过实际位置反弹
        bounceLock: true, //当内容少于滚动是否可以反弹
        momentum: true, //动量效果，拖动惯性
        lockDirection: true,
         //当水平滚动和垂直滚动同时生效时，当拖动开始是否锁定另一边的拖动
        useTransform: true, //是否使用CSS形变
        useTransition: true, //是否使用CSS变换
        topOffset: 0, //已经滚动的基准值
        checkDOMChanges: true, //是否自动检测内容变化
        bindToWrapper:true,
　　});

</script>

</body>
</html>
