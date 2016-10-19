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
  <title>服务</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/service/service_index'+compresspath+'.css"/>');
  </script>

</head>
<body>
<div class="logoArea">
  <div class="logo"></div>
  <p class="name">销售易</p>
  <p class="version">V1.0</p>
</div>
<div class="btnList">
<div id="xymj" class="list boxSizing">
  <em class="icon"></em>
  <a class="btn">
    <span>小易秘籍</span>
    <em class="jiantou"></em>
  </a>
</div>
<div id="yjfk" class="list boxSizing">
  <em class="icon"></em>
  <a class="btn">
    <span>意见反馈</span>
    <em class="jiantou"></em>
  </a>
</div>
<%--<div class="list boxSizing">--%>
  <%--<em class="icon"></em>--%>
  <%--<a class="btn">--%>
    <%--<span>联系我们</span>--%>
    <%--<em class="jiantou"></em>--%>
  <%--</a>--%>
<%--</div>--%>
<div id="aboutXSY" class="list boxSizing">
  <em class="icon"></em>
  <a class="btn">
    <span>关于销售易</span>
    <em class="jiantou"></em>
  </a>
</div>
</div>
<%--<div id="contactUs">--%>
  <%--<div class="top boxSizing">--%>
    <%--<div class="logo"></div>--%>
    <%--<p class="title">销售易CRM</p>--%>
    <%--<p class="information">--%>
      <%--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;仁科互动（北京）信息科技有限公司，公司--%>
      <%--旗下核心产品销售易CRM，是全球首款完美--%>
      <%--融合社交和移动技术的云端CRM产品，帮助--%>
      <%--企业提升销售团队执行力和业绩。将移动互--%>
      <%--联、社交网络和云技术智慧融合，彻底重构--%>
      <%--PC时代的传统CRM（客户关系管理系统），--%>
      <%--解决其复杂难用、推行困难的弊病，打造最--%>
      <%--新一代移动CRM。--%>
    <%--</p>--%>
  <%--</div>--%>
  <%--<ul class="bot">--%>
    <%--<li>24小时业务咨询:<a href="tel:400-050-0907">400-050-0907</a></li>--%>
    <%--<li>24小时技术支持:<a href="tel:400-050-0907">400-082-6869</a></li>--%>
    <%--<li>公司网址:<a href="http://www.xiaoshouyi.com">www.xiaoshouyi.com</a></li>--%>
    <%--<li>公司邮箱:<a href="mailto:info@xiaoshouyi.com">info@xiaoshouyi.com</a></li>--%>
  <%--</ul>--%>
<%--</div>--%>
<%--<div id="aboutShadow">--%>
  <%--<div class="about">--%>
    <%--<div class="top">--%>
      <%--<p class="title">关于</p>--%>
      <%--<p class="info"><span class="label">产品名称：</span><span class="value boxSizing">销售易CRM钉钉版</span></p>--%>
      <%--<p class="info"><span class="label">版&nbsp;&nbsp;本&nbsp;&nbsp;号：</span><span class="value boxSizing">V1.0</span></p>--%>
      <%--<p class="info"><span class="label">开&nbsp;&nbsp;发&nbsp;&nbsp;者：</span><span class="value boxSizing">北京仁科互动网络技术有限公司</span></p>--%>
      <%--<p class="copyRight">Copyright(C)2016</p>--%>
    <%--</div>--%>
    <%--<div class="bot">确定</div>--%>
  <%--</div>--%>
<%--</div>--%>
<%@ include file="../common/footer.jsp"%>
<script>
  var apppath = "<%=request.getContextPath()%>";
</script>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script>
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/service/service_index'+compresspath+'.js?v=1"></scr'+'ipt>');
</script>
</body>
</html>
