<%@ page import="com.alibaba.fastjson.JSONObject,com.rkhd.ienterprise.apps.ingage.enums.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta charset="utf-8">
    <meta name = "format-detection" content = "telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
</head>
<body>
<a     href = "/admin/admin-home/index.action"><span id="redirectA"></span></a>
</body>
<script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
<script >
    $(document).ready(function(){
        var hostName = "<%=request.getContextPath()%>";
        var newUrl = location.protocol +"//"+location.hostname  +"/admin/admin-home/index.action";
        newUrl = newUrl.replace(hostName+"/","");
       // alert(newUrl);

       // $("#redirectA").trigger('click');
        window.location=newUrl;
    })
</script>
</html>

