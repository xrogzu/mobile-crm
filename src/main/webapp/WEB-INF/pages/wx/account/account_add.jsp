<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/30
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>新建客户</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/weui.min.css"/>
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/jquery-weui.css"/>
  <script>
        var apppath = "<%=request.getContextPath()%>";
  </script>
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/add'+compresspath+'.css?v=1"/>');
  </script>
 
</head>
<body>
  <div id="basic_info" class="boxSizing">
    <div class="title boxSizing"></div>
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
 <div class="business-query-wrap">
   <div id="search" class="boxSizing">
    <div class="ipt_area boxSizing">
      <img class="search_icon" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QUM5RUQ0QkFCMkU1MTFFNUE4MDNFMkU5NkI5N0FEOEMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QUQzNzZCMDhCMkU1MTFFNUE4MDNFMkU5NkI5N0FEOEMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpBQzlFRDRCOEIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpBQzlFRDRCOUIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PtM4qkMAAAJ4SURBVHjavJc5aBRRGMdnNyK6BsEjqBgPMGCIMbEyXgGTRrwWxG20FEEUAgoWaiwsVo2FikHRIAop1MIDVNTOKxFRBONRpBA8oiAe8URFEP1/8FsY4svM7GZ2P/jx3s7xfjPzrm8TbR2nvZCoExmxQEwWUzjeJ96IO+KceOLlEYkA8WKxG2GU6BI7xe0oFycdx1LiuLiO9JM4KtJiuhgpRohpYqU4wjWN4qY4xvm83nicuCIaxC+xT+wX30LaKRebxQ4e7K5YIfqjvHHKJ30p5otdEaQW30VWzBUvuPcqDxEqPoS0j8/W4+UfT7n3NW0dCBM3ifV83jTyQsOkq8RPsYEHGVScpWwv8E0HxgNx0MYQM8MpnsPo/SL2evGFDcyPvHGtS5yhflF8jlH8VZyhvtolXkT9khd/XKNsdIln+vol7nhMWe0Sj6f+rgjit5Rjg+bxcK+EkWTkWUwqQvsTKPtd4l7qNUUQ11P2usTd1NNFEC/xbZn/ic/7xOUxSkeLtdQvuMQP2cbGiC0xircyY7pc2UluVLdSbhezY5DWIP7ra9spviFOsn/aZ6kcgnQiy6+11enq34HzuEXcE1VcXFuAdBaDtYrfthxXhIl/iOXILbe6z2caFUGYopvsnhnikfjNA1zmfGCyZ4tJszjBp8qSyhwWy+iCMqjkWDup0h7u6RDzxDr6uIFdqixqetuEOGp62016e8t3bJtvj7dMdVPuxLCAhmzALSRRyFCvJhO1+MCKZMKzgyT0bWKq2AivOBYozkXPENOhFv6BpOkOy+dOJUuwEf0Raxi0CaZtcynEuRlj/zqesf12lkps8V4sFc9tif4nwABdmYTwet4UPgAAAABJRU5ErkJggg==" alt="">
      <input id="txt" class="boxSizing" type="text">
      <img class="delete_icon" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6RUFERTYwRjRCODJEMTFFNTkyRDE4RjEwMDhDNDFDMjgiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6RUM2MTJFQzBCODJEMTFFNTkyRDE4RjEwMDhDNDFDMjgiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpFQURFNjBGMkI4MkQxMUU1OTJEMThGMTAwOEM0MUMyOCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpFQURFNjBGM0I4MkQxMUU1OTJEMThGMTAwOEM0MUMyOCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PhXVNWsAAAIZSURBVHjatJdLSwJhFIZHLXNnwaTLIOi2SoQWtS7dt+jyE7Sw/kHgLygp/QlZC/dp+4KuuwyhfV5Id2oIvSfOwCTzXWbSF542znceczzHM777u0dDI5MgCbbAGpgH0/xaG3yAB1ABN+BbVdCnEFPxY5AGs4ZemuACnPKbcoxfUmAPVMGJCynF5DNVrqEtDoA8uARRw3uiXCPPNaViuuAapIzRJcU1AzIx3ZdtY/Shmmci8S44NMaXA3b8EdO3Nyc48Am6LgRdPuOUnNWGlphaJiKQzoF1bhNVWmCDzzjJI+z6FQe5T0V9vAJeeXg0FdJN8AKWbQNmOOQKkjgh6dMpnkYxhdyS0jWr4JbPOoVcCUusGggyuZPUVNRMkjiuOY2c5F6klPgE/iy6GIUVllpyw4OUskDisMs5bJcb/ClUXEgpYb+HQeAbxTQhccfF9fZ7GtP4tovSIXHNo7Si2WpOqZH4yaPU1Gg1UZ5JXPYo1e1zp5QtcUtwQU8hlcl7gpoN2stI3AfngotoZ3rTbBmTeznGZ0T7Fm0kfWvZo4H+LvmFol4PufhZ7AjWpgYPrLbf9p9lJLtTyEWrhCS7Wsb6JOwD5IrX0nGFahdFO9cRKI1BWuLawmVvAHZAYYTSAtccqPbqAW8J+6D+D2Gda6SHpaonCbofSyAr6XPRwMny2aLXZ6fhh7aE7aFthl/7sj20lZm+quCPAAMAOfSaKctRp80AAAAASUVORK5CYII=" alt="" style="display: none;">
    </div>
    <div id="back">取消</div>
  </div>
  <div class="business-query-content"></div>
 </div>
 <div class="business-querydetail-wrap" id="business-querydetail-wrap">
    <div id="business-querydetail-wrap-content">
       <div class="business-querydetail-header">
        <span class="business-search-name"></span>
        <div class="business-search-maininfo">
          <ul>
            <li>
              <span>法定代表人</span>
              <span id="search-fddbr"></span>
            </li>
            <li>
              <span>注册资本</span>
              <span id="search-zczb"></span>
            </li>
          </ul>
        </div>
    </div>
    <div class="business-querydetail-body">
        <div class="business-querydetail-content">
          <span class="querydetail-header">基本信息</span>
          <div class="querydetail-info-item">
            <span>办公地址</span>
            <span id="detail_bgdz"></span>
          </div>
          <div class="querydetail-info-item">
            <span>电话</span>
            <span id="detail_tel"></span>
          </div>
          <div class="querydetail-info-item">
            <span>传真</span>
            <span id="detail_fax"></span>
          </div>
          <div class="querydetail-info-item">
            <span>邮件</span>
            <span id="detail_email"></span>
          </div>
          <div class="querydetail-info-item">
            <span>公司网址</span>
            <span id="detail_website"></span>
          </div>
          <div class="querydetail-info-item">
            <span>微博</span>
            <span id="detail_weibo"></span>
          </div>
           <div class="querydetail-info-item">
            <span>邮政编码</span>
            <span id="detail_postcode"></span>
          </div>
           <div class="querydetail-info-item">
            <span>注册号</span>
            <span id="detail_regNo"></span>
          </div>
           <div class="querydetail-info-item">
            <span>组织机构代码</span>
            <span id="detail_orgNo"></span>
          </div>
          <div class="querydetail-info-item">
            <span>社会信用代码</span>
            <span id="detail_creditNo"></span>
          </div>
           <div class="querydetail-info-item">
            <span>登记机关</span>
            <span id="detail_regiAuth"></span>
          </div>
           <div class="querydetail-info-item">
            <span>登记状态</span>
            <span id="detail_regiStatus"></span>
          </div>
           <div class="querydetail-info-item">
            <span>营业开始时间</span>
            <span id="detail_busiStartDate"></span>
          </div>
          <div class="querydetail-info-item">
            <span>营业结束时间</span>
            <span id="detail_busiEndDate"></span>
          </div>
          <div class="querydetail-info-item">
            <span>成立日期</span>
            <span id="detail_busiBuildDate"></span>
          </div>
           <div class="querydetail-info-item">
            <span>核准日期</span>
            <span id="detail_checkDate"></span>
          </div>
        </div>
        <div class="business-querydetail-content">
             <span class="querydetail-header">股东</span>
              <div class="querydetail-parter-body"></div>
        </div>
         <div class="business-querydetail-content">
             <span class="querydetail-header">变更</span>
              <div class="querydetail-changeRecords-body"></div>
        </div>
    </div>
    <div class="business-querydetail-footer">
      <a href="javascript:;" class="querydetail-back">取消</a>
      <a href="javascript:;" class="querydetail-write">回填</a>
    </div>
    </div>
 </div>
  <script>
    var apppath = "<%=request.getContextPath()%>";
  </script>
  <script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
  <script>
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/account_add'+compresspath+'.js?v=2"></scr'+'ipt>');
  document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/iscroll'+compresspath+'.js?v=1"></scr'+'ipt>');
  </script>
  <script src="<%=request.getContextPath()%>/js/wx/jquery-weui.js"></script>
  <script src="<%=request.getContextPath()%>/js/wx/city-picker.js"></script>
  <script>
//      var scroll = new  IScroll('#business-querydetail-wrap', {
//         hScroll: true, //是否水平滚动
//         vScroll: true, //是否垂直滚动
//         x: 0, //滚动水平初始位置
//         y: 0, //滚动垂直初始位置
//         bounce: true, //是否超过实际位置反弹
//         bounceLock: false, //当内容少于滚动是否可以反弹
//         momentum: true, //动量效果，拖动惯性
//         lockDirection: true,
//          //当水平滚动和垂直滚动同时生效时，当拖动开始是否锁定另一边的拖动
//         useTransform: true, //是否使用CSS形变
//         useTransition: false, //是否使用CSS变换
//         topOffset: 0, //已经滚动的基准值
//         checkDOMChanges: false, //是否自动检测内容变化
// 　　});
  </script>
</body>
</html>
