/**
 * Created by dell on 2016/2/29.
 */
$(document).bind('ready',function(){
    //contactUs();
    $('#xymj').bind('click',function(){
        location.href = apppath+'/dingtalk/serveice/miji.action?'+ddcommparams;
    })
    $('#yjfk').bind('click',function(){
        location.href = apppath+'/dingtalk/serveice/yjfk.action?'+ddcommparams;
    })
    $('#aboutXSY').bind('click',function(){
        location.href = apppath+'/dingtalk/serveice/about.action?'+ddcommparams;
    })
})
dd.ready(function(){
    remove_rTop();
    control_back(back);
})
//function aboutXSY(){
    //$('.list').eq(3).bind('click',function(){
    //    $('#aboutShadow').fadeIn();
    //})
    //$('#aboutShadow .bot').bind('click',function(){
    //    $('#aboutShadow').fadeOut();
    //})
//}
//function contactUs(){
//    $('.list').eq(2).bind('click',function(){
//        $('#contactUs').show();
//        set_title('联系我们');
//    })
//}
function back(){
    if($('#contactUs').css('display')=='block'){
        $('#contactUs').hide();
        set_title('服务');
    }else if($('#aboutShadow').css('display')=='block'){
        $('#aboutShadow').hide();
    }else{
        location.href = apppath+'/dingtalk/statics/index.action?'+ddcommparams;
    }
}