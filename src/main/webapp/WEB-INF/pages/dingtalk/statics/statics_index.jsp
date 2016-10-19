<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/2/29
  Time: 10:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">

  <title>首页</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
  document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/index'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
<header>
  <div id="sort" class="boxSizing">
    <div class="performance active boxSizing">业绩统计</div>
    <div class="behavior boxSizing">行为统计</div>
  </div>
</header>
<%--行为统计--%>
<div id="behaviorPage" class="boxSizing page">
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
  <%--跟进记录--%>
  <ul class="boxSizing record" id="recordList">
    <div class="top boxSizing">
      <div class="title boxSizing"><span>跟进记录(0)</span>
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
    </div>
    <div class="main boxSizing"></div>
  </ul>
</div>
<%--业绩统计--%>
<div id="performancePage" class="boxSizing page">
  <%--销售漏斗--%>
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
  </div>
  <div id="saleFunnel" class="boxSizing">
    <div class="title boxSizing">
      <div class="btn">个数</div>
    </div>
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
        <p>该范围内没有销售机会</p>
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
      <p>该范围内没有赢单的销售机会</p>
    </ul>
  </div>
  <ul class="boxSizing opportunity" id="list">
    <div class="title boxSizing">
      <span class="words">重点销售机会<span>(Top10)</span></span>
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
<%@ include file="../common/footer.jsp"%>

<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/index'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
