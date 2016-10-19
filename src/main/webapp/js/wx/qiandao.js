/**
 * Created by dell on 2016/1/28.
 */
$(window).bind('load', function() {

    use_map();
    $('textarea').autoHeight();
    remove_rTop();
    get_date();
    getObjectName();
    textarea_num();
    //confirm();
    btn_touch_style();
})

function use_map() {
    $('#my_position').bind('click', function() {
      wx.ready(function() {
    wx.getLocation({
        type: 'gcj02',
        success: function(result) {
            lon = result.longitude;
            lat = result.latitude;
            accuracy = result.accuracy;
            //调用高德地图
            var map = new AMap.Map("container", {
                resizeEnable: true,
                center: [116.397428, 39.90923],
                zoom: 15
            }); 
            map.setZoomAndCenter(14, [116.205467, 39.907761]);
            // 在新中心点添加 marker 
            var marker = new AMap.Marker({
                map: map,
                position: [116.205467, 39.907761]
            });
            map.plugin(["AMap.ToolBar"], function() {
                var toolBar = new AMap.ToolBar();
                 map.addControl(toolBar);
            });
            AMap.service(["AMap.PlaceSearch"], function() {
                var placeSearch = new AMap.PlaceSearch({ //构造地点查询类
                    pageSize: 10,
                    //type: '公司',
                    pageIndex: 1,
                    ///city: "010", //城市
                    map: map,
                    panel: "panel"
                });
                var cpoint = [lon, lat]; //中心点坐标
                placeSearch.searchNearBy('', cpoint, 200, function(status, result) {
                    var name = result.poiList.pois[0].address;
                    console.log(name);
                    $('.poi-more').remove();
                    //定位成功后可签到
                    confirm();
                    $('#my_position .value').text(name);
                    poibox_fix();
                    console.log(result)
                });
            });
        },
        cancel: function(result) {
            alert('用户拒绝请求地理定位');
        }
    });
})
        $('#container').show();
        $('#panel').show();
    })
}

function closeMap() {
    $('#container').hide();
    $('#panel').hide();
}

function back() {
    if ($('#panel').css('display') == 'block') {
        closeMap();
    } else {
        if (GetQueryString('from') == 'acc_list') {
            location.replace(apppath + '/wx/account/list.action?' + ddcommparams);
        } else if (GetQueryString('from') == 'acc_info') {
            location.replace(apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('accountId'));
        } else if (GetQueryString('from') == 'con_info') {
            location.replace(apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('contactId'));
        } else {
            history.back();
        }
    }
}
var lon;
var lat;
var accuracy;

    wx.ready(function() {
    control_back(back);
    var waiting = $('<div class="waiting">定位中，请稍后...</div>');
    $('body').append(waiting);
    wx.getLocation({
        type: 'gcj02',
        success: function(result) {
            lon = result.longitude;
            lat = result.latitude;
            accuracy = result.accuracy;
            $('.waiting').remove();
            //调用高德地图
            var map = new AMap.Map("container", {
                resizeEnable: true,
                center: [116.397428, 39.90923],
                zoom: 15
            });
            map.setZoomAndCenter(14, [116.205467, 39.907761]);
            // 在新中心点添加 marker 
            var marker = new AMap.Marker({
                map: map,
                position: [116.205467, 39.907761]
            });
            map.plugin(["AMap.ToolBar"], function() {
                var toolBar = new AMap.ToolBar();
                 map.addControl(toolBar);
            });
            AMap.service(["AMap.PlaceSearch"], function() {
                var placeSearch = new AMap.PlaceSearch({ //构造地点查询类
                    pageSize: 10,
                    //type: '公司',
                    pageIndex: 1,
                    ///city: "010", //城市
                    map: map,
                    panel: "panel"
                });
                var cpoint = [lon, lat]; //中心点坐标
                placeSearch.searchNearBy('', cpoint, 200, function(status, result) {
                    var name = result.poiList.pois[0].address;
                    console.log(name);
                    $('.poi-more').remove();
                    //定位成功后可签到
                    confirm();
                    $('#my_position .value').text(name);
                    poibox_fix();
                    console.log(result)
                });
            });
        },
        cancel: function(result) {
            alert('用户拒绝请求地理定位');
        }
    });
})


function poibox_fix() {
    $('#panel .poibox').bind('click', function() {
        closeMap();
        $('#my_position .value').text($(this).find('.poi-addr').text().substr(3));
        $(this).css('background', '#eee');
        $(this).siblings().css('background', '#fff');
    })
}

function get_date() {
    var myDate = new Date();
    var year = myDate.getFullYear();
    var month = myDate.getMonth() + 1;
    var date = myDate.getDate();
    var hour = myDate.getHours();
    var minutes = myDate.getMinutes();
    if (month < 10) {
        month = '0' + String(month);
    }
    if (date < 10) {
        date = '0' + String(date);
    }
    if (hour < 10) {
        hour = '0' + String(hour);
    }
    if (minutes < 10) {
        minutes = '0' + String(minutes);
    }
    $('#current_time > .value').text(year + '年' + month + '月' + date + '日  ' + hour + ':' + minutes);
}
var belongId = Number(GetQueryString('belongId'));
var objectId = GetQueryString('objectId');
var activityTypeId = GetQueryString('activityTypeId');
function getObjectName() {
    switch (belongId) {
        case 1:
            $.ajax({
                url: apppath + '/wx/account/getDetail.action',
                data: {
                    accountId: objectId,
                    scene: 'DETAIL'
                },
                dataType: 'json',
                success: function(oData) {
                    console.log("详情"+oData);
                    var objectName = oData.entity.data.accountName;
                    $('#account .value').text(objectName);
                }
            })
            break;
        case 2:
            $.ajax({
                url: apppath + '/wx/contact/getDetail.action',
                data: {
                    contactId: objectId,
                    scene: 'DETAIL'
                },
                dataType: 'json',
                success: function(oData) {
                    console.log(oData)
                    var objectName = oData.entity.data.contactName;
                    $('#account .value').text(objectName);
                }
            })
            break;
        case 3:
            $.ajax({
                url: apppath + '/wx/opportunity/getDetail.action',
                data: { opportunityId: objectId, scene: 'DETAIL' },
                dataType: 'json',
                success: function(oData) {
                    var objectName = oData.entity.data.opportunityName;
                    $('#account .value').text(objectName);
                }
            })
            break;
    }

}


var source = 0;
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
if (isAndroid) {
    source = 1;
} else if (isiOS) {
    source = 2;
}

//提交记录信息
function confirm() { //修复重复事件多次执行的bug
    $('#confirm').unbind("click").bind('click', function() {
        var val = $('#textarea').val();
        if (val == '') {
            myAlert('请填写备注');
        } else {
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
            create_record({ activityRecordDtoString: JSON.stringify(saveData) }, $('#confirm'));
        }
    })
}