
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>微信授权页面</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/wx/pc1.css"/>
</head>
<body>
<div class="container">
    <!--header-->
    <div class="header">
        <div class="header-con">
            <a href="http://www.xiaoshouyi.com"><img src="${pageContext.request.contextPath}/images/wx/2x/common/header_img.jpg" /></a>
            <a href="javascript:doYinDao()"  ><img src="${pageContext.request.contextPath}/images/wx/2x/common/wx_ico.png"  width="139" height="29" style="margin-left: 42px;"></a>
            <a class="register" href="https://crm.xiaoshouyi.com/global/register.action" >注&nbsp;册</a>
        </div>
    </div>
    <!--banner-->
    <div class="banner">
        <h3 class="bt1">销售易，让销售更容易</h3>
        <div class="banner-btn">
            <span class="des">移动CRM领导者</span>
            <a href="javascript:doYinDao()">立即开通</a></div>
    </div>
    <!--choice-->
    <div class="choice-title">
        为什么选择销售易
    </div>
    <div class="choice-item">
        <dl>
            <dt class="img1"></dt>
            <dd class="img-font font1">专业的产品</dd>
            <dd class="img-desc">销售易通过专业的功能、灵活的配置能力和扩展能力，充分满足了各行业、不同规模企业的深度要求。</dd>
        </dl>
        <dl class="item-center">
            <dt class="img2"></dt>
            <dd class="img-font font2">创新的技术</dd>
            <dd class="img-desc">销售易的专业度和创造力获得了Gartner的充分肯定，荣登2015年最酷创新供应商榜单。</dd>
        </dl>
        <dl>
            <dt class="img3"></dt>
            <dd class="img-font font3">世界级团队</dd>
            <dd class="img-desc">来自美国硅谷行业顶级大牛，IBM、微软等跨国公司企业级IT专家，及来自新浪、搜狐的互联网精英组成三强合一的世界级团队。</dd>
        </dl>
    </div>
    <!--about-->
    <div class="about">
        <div class="about-title">
            关于销售易
        </div>
        <div class="about-msg">
            <p>销售易是一家移动互联时代创新型销售管理服务商，帮助企业提升销售管理执行力和业绩;成立5年来赢得了包括分众传媒、滴滴出行、米其林、易华录、科锐配电等知名企业在内的十几万名客户的信任，并获得顶级风投红杉资本、经纬中国的鼎力支持，一举成为移动互联时代的销售管理领军企业，致力成为世界级最专业、最创新的销售管理服务商。</p>
            <p>销售易创始人及CEO史彦泽先生，拥有18年中美销售管理经验，管理软件巨头SAP原中国商业用户部总经理。核心团队由来自美国硅谷行业顶级大牛、IBM、微软等跨国公司企业家IT专家,以及来自新浪、搜狐的互联网精英组成,打造三强合一的世界级团队。</p>
        </div>
    </div>
    <!--customer-->
    <div class="customer-title">
        他们正在使用销售易实现业绩增长
    </div>
    <ul class="customer-img">
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
    </ul>
</div>
</body>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/wx/jquery-1.11.3.js"  ></script>
<script type="text/javascript">
    <!--
    function doYinDao(){
        var url = "${pageContext.request.contextPath}/wx/auth/doyinDao.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: {d: new Date().getTime()},    //参数值
            type: "GET",   //请求方式
            beforeSend: function () {

            },
            success: function (data) {
                var url = data.entity;
                var success = data.success;
                if(success){
                    window.location = url;
                }else {
                    console.log( dir(data));
                }


            },
            complete: function() {
                //请求完成的处理
            },
            error: function(error) {
                //请求出错处理
                //alert(error);
            }
        });
    }
    //-->
</script>
</html>

