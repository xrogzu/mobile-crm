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
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>搜索</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/search'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
  <div id="search" class="boxSizing">
    <div class="ipt_area boxSizing">
      <img class="search_icon" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QUM5RUQ0QkFCMkU1MTFFNUE4MDNFMkU5NkI5N0FEOEMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QUQzNzZCMDhCMkU1MTFFNUE4MDNFMkU5NkI5N0FEOEMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpBQzlFRDRCOEIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpBQzlFRDRCOUIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PtM4qkMAAAJ4SURBVHjavJc5aBRRGMdnNyK6BsEjqBgPMGCIMbEyXgGTRrwWxG20FEEUAgoWaiwsVo2FikHRIAop1MIDVNTOKxFRBONRpBA8oiAe8URFEP1/8FsY4svM7GZ2P/jx3s7xfjPzrm8TbR2nvZCoExmxQEwWUzjeJ96IO+KceOLlEYkA8WKxG2GU6BI7xe0oFycdx1LiuLiO9JM4KtJiuhgpRohpYqU4wjWN4qY4xvm83nicuCIaxC+xT+wX30LaKRebxQ4e7K5YIfqjvHHKJ30p5otdEaQW30VWzBUvuPcqDxEqPoS0j8/W4+UfT7n3NW0dCBM3ifV83jTyQsOkq8RPsYEHGVScpWwv8E0HxgNx0MYQM8MpnsPo/SL2evGFDcyPvHGtS5yhflF8jlH8VZyhvtolXkT9khd/XKNsdIln+vol7nhMWe0Sj6f+rgjit5Rjg+bxcK+EkWTkWUwqQvsTKPtd4l7qNUUQ11P2usTd1NNFEC/xbZn/ic/7xOUxSkeLtdQvuMQP2cbGiC0xircyY7pc2UluVLdSbhezY5DWIP7ra9spviFOsn/aZ6kcgnQiy6+11enq34HzuEXcE1VcXFuAdBaDtYrfthxXhIl/iOXILbe6z2caFUGYopvsnhnikfjNA1zmfGCyZ4tJszjBp8qSyhwWy+iCMqjkWDup0h7u6RDzxDr6uIFdqixqetuEOGp62016e8t3bJtvj7dMdVPuxLCAhmzALSRRyFCvJhO1+MCKZMKzgyT0bWKq2AivOBYozkXPENOhFv6BpOkOy+dOJUuwEf0Raxi0CaZtcynEuRlj/zqesf12lkps8V4sFc9tif4nwABdmYTwet4UPgAAAABJRU5ErkJggg==" alt=""/>
      <input id="txt" class="boxSizing" type="text" placeholder="搜索公司和联系人"/>
      <img class="delete_icon" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6RUFERTYwRjRCODJEMTFFNTkyRDE4RjEwMDhDNDFDMjgiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6RUM2MTJFQzBCODJEMTFFNTkyRDE4RjEwMDhDNDFDMjgiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpFQURFNjBGMkI4MkQxMUU1OTJEMThGMTAwOEM0MUMyOCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpFQURFNjBGM0I4MkQxMUU1OTJEMThGMTAwOEM0MUMyOCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PhXVNWsAAAIZSURBVHjatJdLSwJhFIZHLXNnwaTLIOi2SoQWtS7dt+jyE7Sw/kHgLygp/QlZC/dp+4KuuwyhfV5Id2oIvSfOwCTzXWbSF542znceczzHM777u0dDI5MgCbbAGpgH0/xaG3yAB1ABN+BbVdCnEFPxY5AGs4ZemuACnPKbcoxfUmAPVMGJCynF5DNVrqEtDoA8uARRw3uiXCPPNaViuuAapIzRJcU1AzIx3ZdtY/Shmmci8S44NMaXA3b8EdO3Nyc48Am6LgRdPuOUnNWGlphaJiKQzoF1bhNVWmCDzzjJI+z6FQe5T0V9vAJeeXg0FdJN8AKWbQNmOOQKkjgh6dMpnkYxhdyS0jWr4JbPOoVcCUusGggyuZPUVNRMkjiuOY2c5F6klPgE/iy6GIUVllpyw4OUskDisMs5bJcb/ClUXEgpYb+HQeAbxTQhccfF9fZ7GtP4tovSIXHNo7Si2WpOqZH4yaPU1Gg1UZ5JXPYo1e1zp5QtcUtwQU8hlcl7gpoN2stI3AfngotoZ3rTbBmTeznGZ0T7Fm0kfWvZo4H+LvmFol4PufhZ7AjWpgYPrLbf9p9lJLtTyEWrhCS7Wsb6JOwD5IrX0nGFahdFO9cRKI1BWuLawmVvAHZAYYTSAtccqPbqAW8J+6D+D2Gda6SHpaonCbofSyAr6XPRwMny2aLXZ6fhh7aE7aFthl/7sj20lZm+quCPAAMAOfSaKctRp80AAAAASUVORK5CYII=" alt=""/>
    </div>
    <div id="back">取消</div>
  </div>
  <div id="content"></div>
  <script>
    var apppath = "<%=request.getContextPath()%>";
  </script>
  <script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
  <script>
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/search'+compresspath+'.js?v=1"></scr'+'ipt>');
  </script>
</body>
</html>
