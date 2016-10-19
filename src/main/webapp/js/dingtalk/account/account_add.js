/**
 * Created by dell on 2016/1/15.
 */
$(document).bind('ready', function() {
    get_post();
    add_page_title();
    remove_rTop();
    $('#level').attr('value', 3); //默认等级普通
    if (GetQueryString('type') == 'set') {
        set_info();
        $('#add_contact').remove();
        $('#confirm').html('保存');
    } else {
        //设置默认部门
        setPost = true;
    }
    $('textarea').autoHeight();
    textarea_num();
    level_choose();
    status_choose();
    industry_choose();
    if (!xsyUser) {
        alert('获取当前用户失败')
    }
    btn_color_adjust();
    btn_touch_style();
    change_post();
    confirm();
    input_color_adjust();
    //取消报错样式
    $('input').each(function() {
        $(this).bind('click', function() {
            if ($(this).hasClass('warn')) {
                remove_warn($(this));
            }
        })
    })
    input_adjust();
})
dd.ready(function() {
    control_back(backRemind);
})

function getStartValue() {
    accountNameValue = $('#accountName input').val();
    levelValue = $('#level').attr('value');
    industryIdValue = $('#industryId button').html();
    telValue = $('#phone input').val();
    addressValue = $('#address input').val();
    faxValue = $('#fax input').val();
    textareaValue = $('textarea').val();
    statusValue = $('#status button').html();
    ownerPostValue = $('#ownerPost  button').html();

}
var accountNameValue;
var levelValue;
var industryIdValue;
var telValue;
var addressValue;
var faxValue;
var textareaValue;
var statusValue;
var ownerPostValue;

function backRemind() {
    var isChanged = false;
    if (accountNameValue != $('#accountName input').val() ||
        levelValue != $('#level').attr('value') ||
        industryIdValue != $('#industryId button').html() ||
        telValue != $('#phone input').val() ||
        addressValue != $('#address input').val() ||
        faxValue != $('#fax input').val() ||
        textareaValue != $('textarea').val() ||
        statusValue != $('#status button').html() ||
        ownerPostValue != $('#ownerPost  button').html()) {
        isChanged = true;
    }
    var url;
    if (GetQueryString('from') == 'acc_list') {
        url = apppath + '/dingtalk/account/list.action?' + ddcommparams;
    } else if (GetQueryString('from') == 'acc_info') {
        url = apppath + '/dingtalk/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('accountId');

    }
    if ($('.ct').length > 0) {
        $('body').css('height', '');
        $('.ct').remove();
        remove_rTop();
        add_page_title();
        $('body').css('overflow', '');
    } else {
        if (isChanged) {
            if ($('.chooseShadow').length == 0) {
                myChoose(url);
            } else {
                $('.chooseShadow').remove();
            }
        } else {
            location.href = url;
        }
    }
}

var reg_accountName = /^.{1,50}$/;
var reg_tel = /^\d+|[-#*]{0,30}$/;
var reg_address = /^.{0,250}$/;
var reg_fax = /^.{0,30}$/;

//新建、编辑title区分
function add_page_title() {
    if (GetQueryString('type') != null && GetQueryString('type') == 'set') {
        set_title('编辑公司');
    } else {
        set_title('新建公司');
    }
}

//================================客户等级页==============================
function level_choose() {
    $('#level').bind('click', function() {
        checkTable('选择公司等级');
        $.ajax({
            type: "get",
            url: apppath + "/dingtalk/account/getLevelInfo.action",
            async: true,
            success: function(data) {
                var oData = eval('(' + data + ')');
                create_level_list(oData);
            }
        })
    })
}

function create_level_list(data) {
    var container = $('<div class="container"></div>');
    for (var i = 0; i < data.entity.length; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data.entity[i].label + '</span></div>');
        container.append(list);
    }
    $('.ct').append(container);
    for (var i = 0; i < $('.ct .item').length; i++) {
        if ($('#level button').html() == $('.ct .item').eq(i).find('span').text()) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    $('.ct .item').each(function(index) {
        $(this).bind('click', function() {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity[$(this).index()].value;
            var index = $(this).index();
            var label = data.entity[$(this).index()].label;
            $('#level button').html(label);
            btn_color_adjust();
            $('#level').attr('value', value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}

function status_choose() {
    $('#status').attr('value', 1);
    $('#status').bind('click', function() {
        checkTable('选择公司状态');
        $.ajax({
            type: "get",
            url: apppath + "/dingtalk/account/getstates.action",
            async: true,
            success: function(data) {
                var oData = eval('(' + data + ')');
                if (oData.success) {
                    console.log('公司状态' + data)
                    create_status_list(oData);
                } else {
                    myAlert('暂时无法获取数据，请稍后再试');
                }
            },
            error: function() {
                myAlert('暂时无法获取数据，请检查您的网络')
            }
        })
    })
}

function create_status_list(data) {
    var container = $('<div class="container"></div>');
    for (var i = 0; i < data.entity.length; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data.entity[i].label + '</span></div>');
        container.append(list);
    }
    $('.ct').append(container);
    for (var i = 0; i < $('.ct .item').length; i++) {
        if ($('#status button').html() == $('.ct .item').eq(i).find('span').text()) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    $('.ct .item').each(function(index) {
        $(this).bind('click', function() {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity[$(this).index()].value;
            var label = data.entity[$(this).index()].label;
            $('#status button').html(label);
            btn_color_adjust();
            $('#status').attr('value', value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}
//==================================行业选择页====================================
function industry_choose() {
    $('#industryId').bind('click', function() {
        checkTable('选择行业');
        $.ajax({
                type: "get",
                url: apppath + "/dingtalk/account/industryIds.action",
                async: true,
                success: function(data) {
                    var oData = eval('(' + data + ')');
                    create_industry_list(oData);
                }
            })
            //window.open(apppath+'/dingtalk/common/listpage.action?type=industry_choose&industryId='+$('#industryId').attr('index'));
    })
}

function create_industry_list(data) {
    var container = $('<div class="container"></div>');
    for (var i = 0; i < data.entity.length; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data.entity[i].label + '</span></div>');
        container.append(list);
    }
    $('.ct').append(container);
    for (var i = 0; i < $('.ct .item').length; i++) {
        if ($('#industryId > button').html() == $('.ct .item').eq(i).find('span').text()) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    $('.ct .item').each(function(index) {
        $(this).bind('click', function() {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity[$(this).index()].value;
            var index = $(this).index();
            var label = data.entity[$(this).index()].label;
            $('#industryId button').html(label);
            btn_color_adjust();
            $('#industryId').attr('value', value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}
//===========================================改变所属部门==================================
var postData;
var setPost = false;

function get_post() {
    $.ajax({
        type: "get",
        url: apppath + "/dingtalk/department/mydepartment.action",
        async: true,
        success: function(data) {
            console.log('部门' + data)
            var oData = eval('(' + data + ')');
            if (oData.success == false) {
                myAlert('暂时无法获取数据，请稍后再试');
            } else {
                postData = oData;
                if (setPost) {
                    $('#ownerPost').attr('value', postData.entity.departs[0].id);
                    $('#ownerPost  button').html(postData.entity.departs[0].text);
                    getStartValue();
                }
            }
        },
        error: function() {
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    });
}

function change_post() {
    $('#ownerPost button').bind('click', function() {
        checkTable('选择所属部门');
        var container = $('<div id="container" class="container"></div>');
        $('.ct').append(container);
        build_list(container, postData);
    })
}

function build_list(container, data) {
    for (var i = 0; i < data.entity.departs.length; i++) {
        var con = $('<div class="item"><em class="radio"><i></i></em><span>' + data.entity.departs[i].text + '</span></div>');
        con.attr('value', data.entity.departs[i].id);
        container.append(con);
    }
    for (var i = 0; i < $('.ct .item').length; i++) {
        if ($('#ownerPost button').html() == $('.ct .item').eq(i).find('span').text()) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    radio_click(data);
}

function radio_click(data) {
    $('.ct .item').each(function(index) {
        $(this).bind('click', function() {
            if ($(this).find('em').hasClass('active')) {} else {
                $(this).find('em').addClass('active');
                $(this).siblings().find('em').removeClass('active');
            }
            var value = $(this).attr('value');
            var label = $(this).find('span').text();
            $('#ownerPost > button').html(label);
            btn_color_adjust();
            $('#ownerPost').attr('value', value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}

function confirm() {
    $('#confirm').bind('click', function() {
        confirm_action();
    })
    $('#add_contact').bind('click', function() {
        confirm_action('add_contact');
    })

    function confirm_action(action) {
        if (rtn_reg($('#accountName input'), reg_accountName) && rtn_reg($('#phone input'), reg_tel) && rtn_reg($('#address input'), reg_address) && rtn_reg($('#fax input'), reg_fax)) {
            info_submit(action);
        } else {
            if ($('#accountName input').val() == '') {
                confirm_reg($('#accountName input'), reg_accountName, '公司名称不能为空');
                $(window).scrollTop(0);
            } else {
                confirm_reg($('#accountName input'), reg_accountName, '公司名称格式错误');
            }
            confirm_reg($('#phone input'), reg_tel, '电话格式错误');
            confirm_reg($('#address input'), reg_address, '地址格式错误');
            confirm_reg($('#fax input'), reg_fax, '传真格式错误');
        }
    }
}

//提交信息
function info_submit(action) {
    var url = '';
    if (GetQueryString('type') == 'set') {
        url = apppath + "/dingtalk/account/doupdate.action";
        console.log('更新接口')
        var accountTemp = {
            accountName: $('#accountName input').val(),
            level: $('#level').attr('value'),
            industryId: $('#industryId').attr('value'),
            phone: $('#phone input').val(),
            address: $('#address input').val(),
            fax: $('#fax input').val(),
            ownerId: ownerId,
            dbcSelect1: $('#status').attr('value'),
            dimDepart: $('#ownerPost').attr('value'),
            comment: $('#remark textarea').val(),
            id: GetQueryString('accountId')
        };
    } else {
        url = apppath + "/dingtalk/account/docreate.action";
        console.log('创建接口')
        var accountTemp = {
            accountName: $('#accountName input').val(),
            level: $('#level').attr('value'),
            industryId: $('#industryId').attr('value'),
            phone: $('#phone input').val(),
            address: $('#address input').val(),
            fax: $('#fax input').val(),
            ownerId: ownerId,
            dbcSelect1: $('#status').attr('value'),
            dimDepart: $('#ownerPost').attr('value'),
            comment: $('#remark textarea').val()
        };
    }

    $.ajax({
        type: "post",
        url: url,
        data: {
            account: JSON.stringify(accountTemp)
        },
        async: true,
        beforeSend: function() {
            $('.confirm').attr('disabled', 'true');
            loader();
        },
        success: function(data) {
            $('.loaderArea').remove();
            var oData = eval('(' + data + ')');
            if (oData.success == true) {
                //新建/修改客户埋点
                if (GetQueryString('type') == 'set') {
                    var buriedPointType = 'accountUpdate';
                    buriedPoint(buriedPointType);
                } else {
                    var buriedPointType = 'accountAdd';
                    buriedPoint(buriedPointType);
                }
                myDialog('保存成功');
                setTimeout(function() {
                    if (action == 'add_contact') {
                        location.href = apppath + '/dingtalk/contact/add.action?' + ddcommparams + '&from=acc_add&accountId=' + oData.entity.id + '&accountName=' + $('#accountName input').val();
                    } else {
                        if (GetQueryString('type') == 'set') {
                            //编辑
                            var accountId = GetQueryString('accountId');
                        } else {
                            //新建
                            var accountId = oData.entity.id
                        }
                        location.href = apppath + '/dingtalk/account/info.action?' + ddcommparams + '&accountId=' + accountId;
                    }
                }, 2000);
            } else if (oData.success == false) {
                $('.confirm').removeAttr('disabled');
                if (oData.entity && oData.entity.status && oData.entity.status == '0000001') {
                    myAlert('保存失败，公司名称已存在');
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000002') {
                    myAlert('保存失败，不能包含表情符');
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/dingtalk/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('保存失败，请检查输入信息');
                }
            }
        },
        error: function() {
            $('.loaderArea').remove();
            $('.confirm').removeAttr('disabled');
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
var ownerId = xsyUser.id;

function set_info() {
    $.ajax({
        url: apppath + "/dingtalk/account/get.action",
        data: {
            accountId: GetQueryString('accountId')
        },
        success: function(data) {
            var oData = eval('(' + data + ')');
            console.log(oData)
            ownerId = oData.entity.ownerId;
            $('#accountName input').val(oData.entity.accountName);
            $('#level button').html(oData.entity.levelText);
            btn_color_adjust();
            $('#level').attr('value', oData.entity.level);
            $('#industryId button').html(oData.entity.industryIdText);
            btn_color_adjust();
            $('#industryId').attr('value', oData.entity.industryId);
            $('#phone input').val(oData.entity.phone);
            $('#address input').val(oData.entity.address);
            $('#fax input').val(oData.entity.fax);
            $('#owner').attr('value', oData.entity.ownerId);
            $('#owner button').html(oData.entity.ownerText);
            $('#status button').html(oData.entity.dbcSelect1Text);
            $('#status').attr('value', oData.entity.dbcSelect1);
            $('#ownerPost').attr('value', oData.entity.dimDepart);
            $('#ownerPost  button').html(oData.entity.dimDepartText);
            btn_color_adjust();
            $('#remark textarea').val(oData.entity.comment);
            getStartValue();
            $('textarea').autoHeight();
            textarea_num();
        }
    })
}
