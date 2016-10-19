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
  <title>Title</title>
</head>
<body>
<%--低栏--%>
<footer>
  <div class="ding"><em></em><p>钉一下</p></div>
  <div class="tel"><em></em><p>电话</p></div>
  <div class="signin"><em></em><p>签到</p></div>
  <div class="record"><em></em><p>记录</p></div>
</footer>
<style type="text/css">
  footer{
    width: 100%;
    height: 55px;
    background: #FFFFFF;
    position: fixed;
    bottom: 0px;
    border-top: 1px solid #c7c7cc;
  }
  footer div{
    width: 25%;
    float: left;
  }
  footer em{
    display: block;
    width: 42px;
    height: 42px;
  }
  footer p{
    text-align: center;
    color: #8899a6;
    font-size: 11px;
    padding-top: 5.5px;
  }
  footer .ding em{
    background: url("../../../../images/wx/2x/footer/dingyixia.png") center no-repeat;
    background-size: 24px;
  }
  footer .tel em{
    background: url("../../../../images/wx/2x/footer/dianhua.png") center no-repeat;
    background-size: 24px;
  }
  footer .signin em{
    background: url("../../../../images/wx/2x/footer/qiandao.png") center no-repeat;
    background-size: 24px;
  }
  footer .record em{
    background: url("../../../../images/wx/2x/footer/jilu.png") center no-repeat;
    background-size: 24px;
  }
</style>
</body>
</html>
