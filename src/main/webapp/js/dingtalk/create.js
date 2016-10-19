/**
 * Created by dell on 2016/1/29.
 */
$(document).bind('ready',function(){
    getObjectName();
    $('textarea').autoHeight();
    remove_rTop();
    textarea_num();
    confirm();
    btn_touch_style();
})
dd.ready(function(){
    control_back(backRemind);
})
function backRemind(){
    if($('.chooseShadow').length==0){
        if(activityTypeId == activeRecordTypeOje["TEL"]&&$('textarea').val()!='拨打了一个电话'){
            myChoose();
        }else if(activityTypeId == activeRecordTypeOje["RECORD"]&&$('textarea').val()!=''){
            myChoose();
        }else{
            history.back()
        }
    }
}
function myChoose(){
    var dia = $('<div class="chooseShadow"><div class="dia"><p class="boxSizing">确定放弃操作并退出吗？</p><div class="chooseBtn"><div class="n">取消</div><div class="y boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({'top':'200px','left':($(window).width()-$('.dia').width())/2});
    $('.y').bind('click',function(){
        $('.chooseShadow').remove();
        history.go(-1);
    })
    $('.n').bind('click',function(){
        $('.chooseShadow').remove();
    })
}

var belongId = Number(GetQueryString('belongId'));
var objectId = GetQueryString('objectId');
var activityTypeId = GetQueryString('activityTypeId');
var source = 0;
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
if(isAndroid){
    source = 1;
}else if(isiOS){
    source = 2;
}
//提交记录信息
function confirm(){
    $('#confirm').bind('click',function(){
        var val = $('#textarea').val();
        if(val == ''){
            myAlert('请填写备注');
        }else{
            var saveData = {
                content:val,
                belongId:belongId,
                activityTypeId:activityTypeId,
                objectId:objectId
            }
            create_record({activityRecordDtoString:JSON.stringify(saveData)},$('#confirm'));
        }
    })
}

function set_relevance(objectName){
    var arr_belong = ['','公司','联系人','销售机会','','','市场活动','','','','','销售线索'];
    var belongName = arr_belong[belongId];
    $('#relevance > .value').text(belongName+'-'+objectName);
    if(activityTypeId == activeRecordTypeOje["TEL"]){
        $('textarea').val('拨打了一个电话');
        $('#num').text($('textarea').val().length+'/1000');
    }
}
function getObjectName() {
    switch (belongId) {
        case 1:
            $.ajax({
                url: apppath + '/dingtalk/account/get.action',
                data: {accountId: objectId},
                success: function (data) {
                    var oData = eval('(' + data + ')');
                    var objectName = oData.entity.accountName;
                    set_relevance(objectName);
                }
            })
            break;
        case 2:
            $.ajax({
                url: apppath + '/dingtalk/contact/get.action',
                data: {contactId: objectId},
                success: function (data) {
                    console.log(data)
                    var oData = eval('(' + data + ')');
                    var objectName = oData.entity.contactName;
                    set_relevance(objectName);
                }
            })
            break;
        case 3:
            $.ajax({
                url: apppath + '/dingtalk/opportunity/get.action',
                data: {opportunityId: objectId},
                success: function (data) {
                    var oData = eval('(' + data + ')');
                    var objectName = oData.entity.opportunityName;
                    set_relevance(objectName);
                }
            })
            break;
    }
}


