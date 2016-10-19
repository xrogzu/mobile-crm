<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/1/20
  Time: 20:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>销售易CRM</title>
    <script>
        var apppath = "<%=request.getContextPath()%>";
    </script>

    <jsp:include page="../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
    <script>
        document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css?v=1"/>');
        document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/login'+compresspath+'.css?v=1"/>');
    </script>
</head>
<body>
<div class="loadArea"><div class="loader"></div></div><span>欢迎登录销售易系统<br/>请稍等,正在努力加载数据中...</span>
</body>
<script>
    function callBackFn(){
        window.location =  "<%=request.getContextPath()%>/wx/statics/index.action?"+ddcommparams;
    }
    dd.ready(function() {

//        alert('ding ready');
        try{
            if(!_config){
                alert("获取用户信息失败,请重新登录");
            }else{
                getUserInfo(_config.corpId,callBackFn);
            }

        } catch(exception){
            //此处可以打印异常
            myAlert('exception='+exception);
        }
    })

    function getUserInfo(corpid,callBackFn){

        if(window.user != null){
            //调用回调函数
            if(callBackFn){
                callBackFn(window.user);
            }
            return;
        }
        $.ajax({
            url: apppath+'/wx/user/getuser.action',
            type: 'GET',
            data:{
                d:new Date().getTime(),
                corpid:corpid
            },
            success: function (data, status, xhr) {

                var ret = JSON.parse(data);
                if(ret.success){
                    var user= ret.entity;
                    if(!user){
                        doLogin(corpid,callBackFn);
                    }else{
                        window.user = user;
                        if(callBackFn){
                            callBackFn(user);
                        }
                    }

                   // logger.i('user : ' + ret.entity);
                }else
                {
                    myAlert("未授权用户");
                }

            },
            error: function (xhr, errorType, error) {
                myAlert("查询用户信息失败");
                //logger.e(errorType + ', ' + error);
            }
        });
    }
    function doLogin(thirdcorpid,callBackFn){

        dd.runtime.permission.requestAuthCode({
            corpId: thirdcorpid,
            onSuccess: function(result) {
                /*{
                 code: 'hYLK98jkf0m' //string authCode
                 }*/
                var code = result.code;
                $.ajax({
                    url: apppath+'/wx/dologin.action?corpid=' + thirdcorpid+'&code='+code,
                    type: 'GET',
                    success: function (data, status, xhr) {

                        var ret = JSON.parse(data);

                        if(ret.success){
                            var user= ret.entity;
                            //alert("222"+user);
//                            logger.info('user : ' + ret.entity);
                            window.user = user;
                            if(callBackFn){

                                callBackFn(user);
                            }

                        }else
                        {
                            myAlert("登录失败");
                        }

                    },
                    error: function (xhr, errorType, error) {
                        //alert("yinyien");
                        //logger.e(errorType + ', ' + error);
                    }
                });
            },
            onFail : function(err) {}

        })
    }

    dd.error(function(err) {
        //logger.e('dd error: ' + JSON.stringify(err));
        myAlert(JSON.stringify(err));
        myAlert('ding error'+JSON.stringify(_config));
    });
    function myAlert(info){
        $('.UPS').remove();
        var shadow = $('<div class="commonShadow UPS"></div>');
        var oAlert = $('<div class="alert"><span>'+info+'</span><div>确定</div></div>');
        oAlert.find('div').bind('click',function(){
            $('.UPS').remove();
            location.href = apppath+'/wx/authorized/no.action?'+ddcommparams;
        })
        shadow.append(oAlert);
        $('body').append(shadow);
    }
</script>
</html>
