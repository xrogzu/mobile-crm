<%@ page import="com.rkhd.ienterprise.apps.ingage.wx.helper.JSApiHelper" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.dingtalk.util.*,com.rkhd.ienterprise.apps.ingage.dingtalk.enums.*,com.rkhd.ienterprise.apps.ingage.wx.interceptors.UrlInterceptor,
com.rkhd.ienterprise.apps.ingage.wx.sdk.config.*,com.rkhd.ienterprise.apps.ingage.wx.utils.*" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser,com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction" %>
<%

  String     uri_base =  UrlInterceptor.getHostUrl();
  uri_base = uri_base.replace("http:","https:");

  String   action_url  = (String)request.getAttribute("action_url");
  String  url = uri_base+action_url;

  SessionUser sessionUser = (SessionUser) request.getSession().getAttribute(WxBaseAction.wxSessionUserKey);
  String corpid = "";
  if(sessionUser!=null){
    corpid = sessionUser.getThirdcorpid();
//    System.out.println("corpid="+corpid);
  }
%>
<script  src="https://res.wx.qq.com/open/js/jweixin-1.1.0.js" type="application/x-javascript"></script>
<%--<script type="text/javascript" src="https://g.alicdn.com/ilw/cdnjs/zepto/1.1.6/zepto.min.js"></script>--%>
<script type="text/javascript">
  var compresspath = '';
  var xsyUser = <%=SessionUtils.getWxSessionUser(request)%>;
  console.dir(xsyUser);
  var  _config = <%= JSApiHelper.getConfig(request,url,null,null) %>;


  var ddcommparams = 'dd_nav_bgcolor=FF45aaff&corpid='+_config.corpId+"&d1="+new Date().getTime() ;
  wx.config({
    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
    appId: '<%=corpid%>', // 必填，企业号的唯一标识，此处填写企业号corpid
    timestamp: _config.timestamp, // 必填，生成签名的时间戳
    nonceStr: _config.nonceStr, // 必填，生成签名的随机串
    signature: _config.signature,// 必填，签名，见附录1
    jsApiList: ['checkJsApi','chooseImage','getLocation'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
  });
  wx.ready(function() {
    wx.hideOptionMenu();
    if (typeof wxReadyFn != 'undefined') {
      wxReadyFn()
    }
  });

  wx.error(function(res){

    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
    if( typeof wxErrorFn != 'undefined'){
      wxErrorFn()
    }
  });
  var activeRecordTypeOje = {};
  <%
  ActivityRecordType[] atypes = ActivityRecordType.values();

 for(ActivityRecordType type :atypes){

  %>
  activeRecordTypeOje["<%=type.name()%>"] = <%=type.getOrder()%>;
  <%
  }
  %>

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