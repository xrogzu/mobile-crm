/**
 * Created by dell on 2016/2/18.
 */
'use strict';
$(document).bind('ready', function() {
    if (GetQueryString('type') == 'add') {
        getOppList('ADD');
    }
    if (GetQueryString('type') == 'set') {
        document.title = '修改商机';
        getOppList('UPDATE');
    }

    if (!xsyUser) {
        alert('获取当前用户失败')
    }
})
var accountId;
var userName = xsyUser.name;
var userDepartId = xsyUser.departId;
var userDepartName = xsyUser.departmentName;
var ownerId = xsyUser.id;
var selectitmeObj = {};

// 渲染客户列表
function getOppList(scene) {
    $.ajax({
        url: apppath + "/wx/opportunity/getDetail.action",
        dataType: "json",
        data: { scene: scene },
        type: "GET",
        beforeSend: function() {
            //请求前的处理
        },
        success: function(data) {
            if (data.success == false) {
                myAlert('加载数据失败，请稍后再试');
                $('.load-shadow').hide();
            }
            if (data.success == true) {
                $('.load-shadow').hide();
                var dataString = JSON.stringify(data);
                console.log(dataString);
                var structureList = data.entity.structure.components;
                $.each(structureList, function(i, o) {
                    customizItem($('#basic_info'), o, selectitmeObj);
                });
                var myownerId = $('#ownerId').find('button');
                myownerId.html(userName);
                myownerId.attr("value", ownerId);
                myownerId.attr("readonly", true);
                var dimDepart = $('#dimDepart').find('button');
                dimDepart.html(userDepartName);
                dimDepart.attr("value", userDepartId);
                var footer = $('<div class="footer"></div>');
                footer.append('<button id="confirm">保存</button>');
                $('body').append(footer);
                selectDate();
                // accountPost();
                SelectItemPost();
                DummyItemPost();
                dimdepart_choose();
                get_accountName();
                confirm();
                textarea_num();
                input_adjust();
                $('textarea').autoHeight();
                if (GetQueryString('type') == 'set') {
                    set_info();
                } else {
                    accountPost();
                    //设置默认部门
                    setPost = true;
                }
            }

        },
        complete: function() {
            //请求完成的处理
        },
        error: function(error) {
            //请求出错处理
            alert(error);
        }
    });
}

function get_accountName() {
    if (GetQueryString('from') == 'acc_info' || GetQueryString('from') == 'con_info') {
        $.ajax({
            url: apppath + '/wx/account/getDetail.action',
            data: {
                accountId: GetQueryString('accountId'),
                scene: 'DETAIL'
            },
            dataType: 'json',
            success: function(oData) {
                $('#accountId button').html(oData.entity.data.accountName);
                $('#accountId button').css({ 'color': '#ccc', 'background': 'none' });
                $('#accountId button').attr({ 'disabled': 'disabled', 'value': GetQueryString('accountId') });

            }
        });
    }
}
var url = apppath + '/wx/opportunity/docreate.action';
if (GetQueryString('type') == 'set') {
    url = apppath + '/wx/opportunity/doupdate.action';
}
var reg_opportunityName = /^.{1,50}$/;
var reg_money = /^\d{1,10}$/;
var reg_accountName = /\S/;

//  所有人选择
var ownerData;
var ownerPost = false;

function owner_choose() {
    $('#ownerId').bind('click', function() {
        checkTable('选择客户所有人');
        $.ajax({
            type: "get",
            url: apppath + "/wx/xsyuser/pager.action",
            async: true,
            dataType: 'json',
            success: function(oData) {
                console.log('客户所有人' + oData);
                if (oData.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    ownerData = oData;
                    create_owner_list(oData);
                    if (ownerPost) {
                        $('#parentAccountId').attr('value', accountData.entity.records[0].id);
                        $('#parentAccountId  button').html(accountData.entity.records[0].name);
                        getStartValue();
                    }
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载');
            }
        });
    });
}

function create_owner_list(data) {
    var container = $('<div class="container"></div>');
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data.entity.records[i].name + '</span></div>');
        list.attr('value', data.entity.records[i].id);
        container.append(list);
    }
    $('.ct').append(container);
    for (var i = 0, len = $('.ct .item').length; i < len; i++) {
        if ($('#ownerId button').html() == $('.ct .item').eq(i).find('span').text()) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    $('.ct .item').each(function(index) {
        $(this).bind('click', function() {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity.records[$(this).index()].id;
            var index = $(this).index();
            var label = data.entity.records[$(this).index()].name;
            $('#ownerId button').html(label);
            btn_color_adjust();
            $('#ownerId button').attr('value', value);
            $('.ct').remove();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}
//===========================================数据提及验证==================================
function confirm() {
    $('#confirm').bind('click', function() {
        confirm_action();
    })

    function confirm_action(action) {
        regingData() && info_submit(action);
    }
}
function info_submit(action) {
    var url = '';
    if (GetQueryString('type') == 'set') {
        url = apppath + "/wx/opportunity/doupdate.action";
        console.log('更新接口');
        var opportunityTemp = {
            id: GetQueryString('opportunityId')
        };
        $('#basic_info .item').each(function() {
            var that = $(this);
            var value;
            if (that.find('input').size()) {
                var btype = that.attr('btype');
                if (btype == "ITEM_TYPE_INTEGER" && that.find('input').val() == "" || btype == "ITEM_TYPE_REAL" && that.find('input').val() == "") {
                    value == 0;
                    return;
                }
                value = that.find('input').val();
            }
            if (that.find('button').size()) {
                var btn = that.find('button');
                if (btn.html() == "" || btn.html() == "点击选择") {
                    value = null;
                } else if (that.attr('btype') == "ITEM_TYPE_CHECKBOX") {
                    var arrayList = [];
                    arrayList = that.find('button').attr('value').split(",");
                    if (arrayList.length = 0) {
                        value = []
                    } else {
                        value = arrayList;
                    }
                    console.log(arrayList);
                } else {
                    value = that.find('button').attr('value');
                }
            }
            if (that.find('textarea').size()) {
                value = that.find('textarea').val();
            }
            opportunityTemp[that.attr('id')] = value;
        });
    } else {
        url = apppath + "/wx/opportunity/docreate.action";
        console.log('创建接口')
        var opportunityTemp = {};
        $('#basic_info .item').each(function() {
            var value;
            var that = $(this);
            if (that.find('input').size()) {
                var btype = that.attr('btype');
                if (btype == "ITEM_TYPE_INTEGER" && that.find('input').val() == "" || btype == "ITEM_TYPE_REAL" && that.find('input').val() == "") {
                    value == 0;
                    return;
                }
                value = that.find('input').val();
            }
            if (that.find('button').size()) {
                var btn = that.find('button');
                if (btn.html() == "" || btn.html() == "点击选择") {
                    value = null;
                } else if (that.attr('btype') == "ITEM_TYPE_CHECKBOX") {
                    var arrayList = [];
                    arrayList = that.find('button').attr('value').split(",");
                    if (arrayList.length = 0) {
                        value = []
                    } else {
                        value = arrayList;
                    }
                    console.log(arrayList);
                } else {
                    value = that.find('button').attr('value');
                }
            }
            if (that.find('textarea').size()) {
                value = that.find('textarea').val();
            }
            opportunityTemp[that.attr('id')] = value;
        });
    }

    $.ajax({
        type: "post",
        url: url,
        data: {
            opportunity: JSON.stringify(opportunityTemp)
        },
        async: true,
        beforeSend: function() {
            $('.confirm').attr('disabled', 'true');
            loader();
        },
        dataType: 'json',
        success: function(oData) {
            $('.loaderArea').remove();
            if (oData.success == true) {
                myDialog('保存成功');
                var opportunityId = oData.entity.id;
                if (GetQueryString('type') == 'set') {
                    opportunityId = GetQueryString('opportunityId');
                }
                myDialog('保存成功');
                var url;
                if (GetQueryString('from') == 'opp_list') {
                    location.replace(apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&&opportunityId=' + opportunityId + '&from=opp_list');
                } else if (GetQueryString('from') == 'opp_info') {
                    location.replace(apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&&opportunityId=' + opportunityId + '&from=opp_list');
                } else if (GetQueryString('from') == 'acc_info') {
                    location.replace(apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&&opportunityId=' + opportunityId + '&from=acc_info&accountId=' + GetQueryString('accountId'));
                } else if (GetQueryString('from') == 'con_info') {
                    location.replace(apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&&opportunityId=' + opportunityId + '&from=con_info&contactId=' + GetQueryString('contactId'));
                }
                time_out_location(url);
            } else if (oData.success == false) {
                $('.confirm').removeAttr('disabled');
                if (oData.entity && oData.entity.status && oData.entity.status == '0000001') {
                    myAlert('保存失败，公司名称已存在');
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000002') {
                    myAlert('保存失败，不能包含表情符');
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('保存失败，请检查输入信息');
                }
            }
        },
        error: function() {
            $('.loaderArea').remove();
            $('.confirm').removeAttr('disabled');
            myAlert('网络不给力，请重新加载')
        }
    })
}

function set_info() {
    $.ajax({
        url: apppath + "/wx/opportunity/getDetail.action",
        data: {
            opportunityId: GetQueryString('opportunityId'),
            scene: 'UPDATE'
        },
        dataType: 'json',
        success: function(oData) {
            console.log(oData)
                //ownerId = oData.entity.ownerId;
            $('#basic_info .item').each(function() {
                var that = $(this);
                var value = set_infoSup(that.attr('id'), oData);
                var show, rValue;
                if (typeof value === "object") {
                    show = value.text;
                    rValue = value.value;
                } else {
                    show = rValue = value;
                }
                if (that.find('input').size()) {
                    that.find('input').val(rValue);
                }
                if (that.find('button').size()) {
                    that.find('button').attr('value', rValue);
                    if (show === "") {
                        that.find('button').html("点击选择");
                    } else {
                        that.find('button').html(show);
                    }
                }
                if (that.find('textarea').size()) {
                    that.find('textarea').val(rValue);
                }
            });

            btn_color_adjust();
            $('textarea').autoHeight();
            textarea_num();
        }
    });
}
