/**
 * Created by dell on 2016/2/29.
 */
$(document).bind('ready',function(){

    textarea_num();
    confirm();
})
dd.ready(function(){
    remove_rTop();
    control_back(back);
})
function back(){
    if($('#textarea').val()==''&&$('input').val()==''){
        location.href = apppath+'/dingtalk/serveice/index.action?'+ddcommparams;
    }else{
        if($('.chooseShadow').length==0) {
            myChoose(apppath+'/dingtalk/serveice/index.action?'+ddcommparams);
        }else{
            $('.chooseShadow').remove();
        }
    }
}
var reg_opinion = /^.{1,300}$/;
var reg_tel = /^[a-zA-Z0-9@.]{0,200}$/;
var device = 3;
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
if(isAndroid){
    device = 2;
}else if(isiOS){
    device = 1;
}
function confirm(){
    $('#send').bind('touchstart',function(){
        $(this).addClass('touching');
    })
    $('#send').bind('touchend',function(){
        $(this).removeClass('touching');
    })
    $('#send').bind('click',function(){
        if(rtn_reg($('#textarea'),reg_opinion)&&rtn_reg($('.contactWay input'),reg_tel)){
            $.ajax({
                url:apppath+'/dingtalk/feedback/add.action',
                data:{
                    content:$('#textarea').val(),
                    contactWay:$('.contactWay input').val(),
                    device:device
                },
                beforeSend:function(){
                    $(this).attr('disabled',true);
                    loader();
                },
                success:function(data){
                    $('.loaderArea').remove();
                    var oData = eval('('+data+')');
                    if(oData.success == true){
                        //意见反馈埋点
                        var buriedPointType = 'Suggest';
                        buriedPoint(buriedPointType);
                        myDialog('提交成功');
                        time_out_location(apppath+'/dingtalk/serveice/index.action?'+ddcommparams);
                    }else if(oData.entity.status == '0000002'){
                        myAlert('提交失败，不能包含表情符');
                        $button.removeAttr('disabled');
                    }else if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                        location.href = apppath+'/dingtalk/authorized/no.action?'+ddcommparams;
                    }else{
                        myAlert('提交失败');
                        $(this).removeAttr('disabled');
                    }
                },
                error:function(){
                    $('.loaderArea').remove();
                    myAlert('暂时无法获取数据，请检查您的网络','without');
                    $(this).removeAttr('disabled');
                }
            })
        }else{
            if($('#textarea').val()==''){
                myAlert('请填写反馈内容');
            }
            confirm_reg($('.contactWay input'),reg_tel,'联系方式格式错误');
        }
    })
}
function confirm_reg($obj,reg,warning_words){
    if(!reg.test($obj.val())){
        add_warn($obj,warning_words);
    }
}
function add_warn($obj,warning_words){
    $obj.addClass('warn');
    var warning = $('<div class="warning"><span>'+warning_words+'</span></div>');
    $obj.parent().css({'marginBottom':'74px'});
    $obj.parent().append(warning);
    $obj.bind('click',function(){
        remove_warn($obj);
    })
}
function remove_warn($obj){
    $obj.removeClass('warn');
    $obj.parent().css({'marginBottom':'0px'});
    $obj.parent().find('.warning').remove();
}
//textarea字数统计
function textarea_num(){
    $('#num').text($('#textarea').val().length+'/300');
    $('#textarea').bind('input',function(){
        if($(this).val().length>300){
            $(this).val($(this).val().substr(0,300));
        }
        $('#num').text($(this).val().length+'/300');
    })
}