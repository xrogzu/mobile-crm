<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
        String  wxqyhHome = "https://qy.weixin.qq.com/";
        request.setAttribute("wxqyhHome",wxqyhHome);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common.css" />
    <title>授权成功页面</title>
    <style type="text/css">
        body {
            font-family: "微软雅黑";
            color: #333;
            background: #fff;
            height: 100%;
            width: 100%;
        }
        .successed p {
            font-size: 14px;
            color: #8fa1b2;
            margin-top: 20px;
        }
        .successed p:first-child {
            font-size: 22px;
            line-height: 2em;
            display: block;
            margin-top: 135px;
            margin-bottom: 10px;
            margin-left: 25px;
            text-align: center;
            color: #475059;
        }

        a {font-size:16px}
        a:link {font-size: 14px;color: #3399cc; text-decoration:none;}
        #a:active {font-size: 14px;color: red; }
        a:visited {font-size: 14px;color:#475059;text-decoration:none;}
        a:hover {font-size: 14px;color: #00aaef; text-decoration:underline;}


    </style>
    </head>

<body>
<div class="successed">
    <div class="ico">
        <p>恭喜您，授权成功了！</p>
        <p class="p2"><span id="seconde_info">3</span>秒后将跳转到您的微信企业号首页，如手动点击【<a href="${wxqyhHome}" target="_self">跳转</a>】</p>
    </div>
</div>
</body>
<script>
    var seconde_info = 3;
    function fomtime()
    {
        seconde_info --;

        if(seconde_info == 0){
           clearInterval(time);
          // window.location="${wxqyhHome}";
        }
        document.getElementById("seconde_info").innerHTML = seconde_info;

    }
    var time = setInterval("fomtime()",1000);

</script>
</html>
