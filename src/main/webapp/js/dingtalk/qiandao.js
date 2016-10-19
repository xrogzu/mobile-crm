/**
 * Created by dell on 2016/1/28.
 */
$(window).bind('load',function(){
    use_map();
    $('textarea').autoHeight();
    remove_rTop();
    get_date();
    getObjectName();
    textarea_num();
    //confirm();
    btn_touch_style();
})
function use_map(){
    $('#my_position').bind('click',function(){
        $('#container').show();
        $('#panel').show();
    })
}
function closeMap(){
    $('#container').hide();
    $('#panel').hide();
}
function back(){
    if($('#panel').css('display')=='block'){
        closeMap();
    }else{
        if(GetQueryString('from')=='acc_list'){
            location.href = apppath+'/dingtalk/account/list.action?'+ddcommparams;
        }else if(GetQueryString('from')=='acc_info'){
            location.href = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+GetQueryString('accountId');
        }else if(GetQueryString('from')=='con_info'){
            location.href = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+GetQueryString('contactId');
        }else{
            history.back();
        }
    }
}
var lon;
var lat;
dd.ready(function(){
    control_back(back);
    var waiting = $('<div class="waiting">定位中，请稍后...</div>');
    $('body').append(waiting);
    dd.device.geolocation.get({
        targetAccuracy: 10,
        onSuccess: function (result) {
            lon = result.longitude;
            lat = result.latitude;
            $('.waiting').remove();
            //调用高德地图
            var map = new AMap.Map("container", {
                resizeEnable: true
            });
            map.plugin(["AMap.ToolBar"], function() {
                map.addControl(new AMap.ToolBar());
            });
            AMap.service(["AMap.PlaceSearch"], function() {
                var placeSearch = new AMap.PlaceSearch({ //构造地点查询类
                    pageSize: 10,
                    //type: '公司',
                    pageIndex: 1,
                    //city: "010", //城市
                    map: map,
                    panel: "panel"
                });
                var cpoint = [lon,lat]; //中心点坐标
                placeSearch.searchNearBy('', cpoint, 200, function(status, result) {
                    var name = result.poiList.pois[0].address;
                    $('.poi-more').remove();
                    //定位成功后可签到
                    confirm();
                    $('#my_position .value').text(name);
                    poibox_fix();
                    console.log(result)
                });
            });
        },
        onFail: function (err) {
            switch(error.code) {
                case error.PERMISSION_DENIED:
                    myAlert("定位失败,用户拒绝请求地理定位");
                    break;
                case error.POSITION_UNAVAILABLE:
                    myAlert("定位失败,位置信息是不可用");
                    break;
                case error.TIMEOUT:
                    myAlert("定位失败,请求获取用户位置超时");
                    break;
                case error.UNKNOWN_ERROR:
                    myAlert("定位失败,定位系统失效");
                    break;
            }
        }
    });
})
function poibox_fix(){
    $('#panel .poibox').bind('click',function(){
        closeMap();
        $('#my_position .value').text($(this).find('.poi-addr').text().substr(3));
        $(this).css('background','#eee');
        $(this).siblings().css('background','#fff');
    })
}
function get_date(){
    var myDate = new Date();
    var year = myDate.getFullYear();
    var month = myDate.getMonth()+1;
    var date = myDate.getDate();
    var hour = myDate.getHours();
    var minutes = myDate.getMinutes();
    if(month<10){
        month = '0'+String(month);
    }
    if(date<10){
        date = '0'+String(date);
    }
    if(hour<10){
        hour = '0'+String(hour);
    }
    if(minutes<10){
        minutes = '0'+String(minutes);
    }
    $('#current_time > .value').text(year+'年'+month+'月'+date+'日  '+hour+':'+minutes);
}

function getObjectName(){
    switch(belongId)
    {
        case 1:
            $.ajax({
                url:apppath+'/dingtalk/account/get.action',
                data:{accountId:objectId},
                success:function(data){
                    var oData = eval('('+data+')');
                    var objectName = oData.entity.accountName;
                    $('#account .value').text(objectName);
                }
            })
            break;
        case 2:
            $.ajax({
                url:apppath+'/dingtalk/contact/get.action',
                data:{contactId:objectId},
                success:function(data){
                    console.log(data)
                    var oData = eval('('+data+')');
                    var objectName = oData.entity.contactName;
                    $('#account .value').text(objectName);
                }
            })
            break;
        case 3:
            $.ajax({
                url:apppath+'/dingtalk/opportunity/get.action',
                data:{opportunityId:objectId},
                success:function(data){
                    var oData = eval('('+data+')');
                    var objectName = oData.entity.opportunityName;
                    $('#account .value').text(objectName);
                }
            })
            break;
    }

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
    $('#confirm').unbind("click");//修复重复事件多次执行的bug
    $('#confirm').bind('click',function(){
        var val = $('#textarea').val();
        if(val == ''){
            myAlert('请填写备注');
        }else {
            var saveData = {
                content: val,
                belongId: belongId,
                activityTypeId: activityTypeId,
                objectId: objectId
            }
            saveData.position = {
                longitude: lon,
                latitude: lat,
                "location": $('#my_position .value').text(),
                "locationDetail": $('#my_position .value').text()
            }
            create_record({activityRecordDtoString: JSON.stringify(saveData)},$('#confirm'));
        }
    })
}
