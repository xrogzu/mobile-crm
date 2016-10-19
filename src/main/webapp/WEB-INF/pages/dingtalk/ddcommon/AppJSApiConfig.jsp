<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.util.*" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.enums.ActivityRecordType" %>
<script type="text/javascript">
  var  _config = <%=  com.rkhd.ienterprise.apps.ingage.dingtalk.helper.auth.AuthHelper.getConfig(request) %>;

  if(_config == null || _config.agentId == "null"){
   // alert("加载用户信息失败，请退出重试");
  }else{
    var ddcommparams = 'dd_nav_bgcolor=FF45aaff&corpid='+_config.corpId+"&d1="+new Date().getTime() ;
  }
  var compresspath;
  var onlinename = 'thirdparty.xiaoshouyi.com';
  if(String(window.location).indexOf(onlinename)>0){
    compresspath = ".min";
  }else{
    compresspath = '';
  }
  //alert("_config:" + JSON.stringify(_config));
</script>
<script type="text/javascript" src="https://g.alicdn.com/ilw/cdnjs/zepto/1.1.6/zepto.min.js"></script>
<script type="text/javascript" src="https://g.alicdn.com/ilw/ding/0.7.5/scripts/dingtalk.js"></script>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/logger.js"></script>--%>

<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dingtalk/ddcommon/AppJSApiMethod.js"></script>--%>
<script>
  var scriptPath = "<script type='text/javascript' src='<%=request.getContextPath()%>/js/dingtalk/ddcommon/AppJSApiConfig"+compresspath+".js'/>";
  document.write(scriptPath);

  var xsyUser = <%=SessionUtils.getWxSessionUser(request)%> ;
  xsyUser = (xsyUser == null )?null:xsyUser;
  var activeRecordTypeOje = {};
  <%
  ActivityRecordType[] atypes = ActivityRecordType.values();
  for(ActivityRecordType type :atypes){
  %>
  activeRecordTypeOje["<%=type.name()%>"] = <%=type.getOrder()%>;

  <%
  }
  %>
  //  alert(activeRecordTypeOje["SIGN_IN"]);
</script>
<%--growingio的代码，用于监控流量--%>
<script type='text/javascript'>
  var _vds = _vds || [];
  window._vds = _vds;
  (function(){
    _vds.push(['setAccountId', '2615cb47558b45ffb4da3f8ca1ad57a3']);
    (function() {
      var vds = document.createElement('script');
      vds.type='text/javascript';
      vds.async = true;
      vds.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'dn-growing.qbox.me/vds.js';
      var s = document.getElementsByTagName('script')[0];
      s.parentNode.insertBefore(vds, s);
    })();
  })();
</script>