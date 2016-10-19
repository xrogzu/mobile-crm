/**
 * Created by dell on 2016/1/15.
 */
$(document).on('ready', function() {
    if (GetQueryString('type') == 'add') {
        getAddAcctountDesc('ADD');
    } else if (GetQueryString('type') == 'set') {
        document.title = '修改客户';
        getAddAcctountDesc('UPDATE');
    }
    $('textarea').autoHeight();
    if (!xsyUser) {
        alert('获取当前用户失败')
    }
    //取消报错样式
    $('input').each(function() {
        $(this).on('click', function() {
            if ($(this).hasClass('warn')) {
                remove_warn($(this));
            }
        })
    })
    
})
var reg_accountName = /^.{1,50}$/;
var reg_tel = /^\d+|[-#*]{0,30}$/;
var reg_address = /^.{0,250}$/;
var reg_fax = /^.{0,30}$/;
var selectitmeObj = {};
var userDepartId = xsyUser.departId;
var userDepartName = xsyUser.departmentName;
// 渲染客户列表
function getAddAcctountDesc(scene) {
    $.ajax({
        url: apppath + "/wx/account/getDetail.action",
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

                var dimDepart = $('#dimDepart').find('button');
                dimDepart.html(userDepartName);
                dimDepart.attr("value", userDepartId);
                var footer = $('<div class="footer"></div>');
                footer.append('<button id="confirm" class="confirm">保存</button>');
                footer.append('<button id="add_contact" class="confirm">保存并添加联系人</button>');
                $('body').append(footer);
                confirm();
                SelectItemPost();
                textarea_num();
                DummyItemPost();
                checkboxItemPost();
                dimdepart_choose();
                parentAccountChoose();
                ownerChoose();
                $('textarea').autoHeight();
                region();
                btn_color_adjust();
                btn_touch_style();
                input_color_adjust();
                input_adjust();
                if (GetQueryString('type') == 'set') {
                    set_info();
                    var accountName = $('#accountName').find('input')
                    $('#add_contact').remove();
                    $('#confirm').html('保存');
                } else {
                    //设置默认部门
                    setPost = true;
                }
                businessInfoQuery();
                enterpriseinfoBack();
                enterpriseinfoWrite();
                enterpriseBack();
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
// ===============================省市区==================================
function region() {
    $("#region1").cityPicker({
        title: "省市区"
    });
}
//===========================================改变所属部门==================================
var postData;
var setPost = false;
//  上级客户选择
var accountData;
var accountPost = false;

function parentAccountChoose() {
    $('#parentAccountId').on('click', function() {
        checkTable('选择上级客户');
        $.ajax({
            type: "get",
            url: apppath + "/wx/account/dolist.action",
            async: true,
            dataType: 'json',
            success: function(oData) {
                console.log('上级客户' + oData)
                if (oData.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    accountData = oData;
                    create_parentAccount_list(oData);
                    $('#parentAccountId').attr('value', accountData.entity.records[0].id);
                    $('#parentAccountId  button').html(accountData.entity.records[0].accountName);


                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    });
}

function create_parentAccount_list(data) {
    var container = $('<div class="container"></div>');
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data.entity.records[i].accountName + '</span></div>');
        list.attr('value', data.entity.records[i].id);
        container.append(list);
    }
    $('.ct').append(container);
    for (var i = 0, len = $('.ct .item').length; i < len; i++) {
        if ($('#parentAccountId button').html() == $('.ct .item').eq(i).find('span').text()) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    $('.ct .item').each(function(index) {
        $(this).on('click', function() {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity.records[$(this).index()].id;
            var index = $(this).index();
            var label = data.entity.records[$(this).index()].accountName;
            $('#parentAccountId button').html(label);
            btn_color_adjust();
            $('#parentAccountId button').attr('value', value);
            $('.ct').remove();

            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}

//  客户所有人选择
var ownerData;
var ownerPost = false;

function ownerChoose() {
    $('#ownerId').on('click', function() {
        checkTable('选择客户所有人');
        $.ajax({
            type: "get",
            url: apppath + "/wx/xsyuser/pager.action",
            async: true,
            dataType: 'json',
            success: function(oData) {
                console.log('客户所有人' + oData)
                if (oData.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    ownerData = oData;
                    create_owner_list(oData);
                    if (ownerPost) {
                        $('#parentAccountId').attr('value', accountData.entity.records[0].id);
                        $('#parentAccountId  button').html(accountData.entity.records[0].name);

                    }
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
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
        $(this).on('click', function() {
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
// 确认框
function confirm() {
    $('#confirm').on('click', function() {
        confirm_action();
    })
    $('#add_contact').on('click', function() {
        confirm_action('add_contact');
    })

    function confirm_action(action) {
        regingData() && info_submit(action);
    }
}

//提交信息
function info_submit(action) {
    var url = '';
    if (GetQueryString('type') == 'set') {
        url = apppath + "/wx/account/doupdate.action";
        console.log('更新接口');
        var accountTemp = {
            id: GetQueryString('accountId')
        };
        $('#basic_info .item').each(function() {
            var that = $(this);
            var value;
            if (that.find('input').size()) {
                if (that.attr('id') == 'region') {
                    var parts = that.find('input').val();
                    var partsArray = parts.split(" ");
                    accountTemp['state'] = partsArray[0];
                    accountTemp['city'] = partsArray[1];
                    accountTemp['region'] = partsArray[2];
                    return;
                }
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
                    var arrayList = that.find('button').attr('value').split(",");
                    if (arrayList.length == 0) {
                        value = [];
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
            accountTemp[that.attr('id')] = value;
        });
    } else {
        url = apppath + "/wx/account/docreate.action";
        console.log('创建接口')
        var accountTemp = {};
        $('#basic_info .item').each(function() {
            var value;
            var that = $(this);
            if (that.find('input').size()) {
                if (that.attr('id') == 'region') {
                    var parts = that.find('input').val();
                    var partsArray = parts.split(" ");
                    accountTemp['state'] = partsArray[0];
                    accountTemp['city'] = partsArray[1];
                    accountTemp['region'] = partsArray[2];
                    return;
                }
                value = that.find('input').val();
            }
            if (that.find('button').size()) {
                var btn = that.find('button');
                if (btn.html() == "" || btn.html() == "点击选择") {
                    value = null;
                } else if (that.attr('btype') == "ITEM_TYPE_CHECKBOX") {
                    var arrayList = that.find('button').attr('value').split(",");
                    if (arrayList.length == 0) {
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
            accountTemp[that.attr('id')] = value;
        });
    }
    $.ajax({
        type: "post",
        url: url,
        data: {
            account: JSON.stringify(accountTemp)
        },
        dataType: 'json',
        async: true,
        beforeSend: function() {
            $('.confirm').attr('disabled', 'true');
            loader();
        },
        success: function(oData) {
            $('.loaderArea').remove();
            if (oData.success == true) {
                myDialog('保存成功');
                setTimeout(function() {
                    if (action == 'add_contact') {
                        var name = accountTemp.accountName;
                        var accName = encodeURIComponent(name);
                        location.replace(apppath + '/wx/contact/add.action?' + ddcommparams + '&from=acc_add&type=add&accountId=' + oData.entity.id + '&accountName=' + accName);
                    } else {
                        if (GetQueryString('type') == 'set') {
                            //编辑
                            var accountId = GetQueryString('accountId');
                        } else {
                            //新建
                            var accountId = oData.entity.id
                        }
                        location.replace(apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + accountId);
                    }
                }, 2000);
            } else if (oData.success == false) {
                $('.confirm').removeAttr('disabled');
                if (oData.entity && oData.entity.status && oData.entity.status == '0000001') {
                    myAlert('保存失败，客户名称已存在');
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
var ownerId = xsyUser.id;

function set_info() {
    $.ajax({
        url: apppath + "/wx/account/getDetail.action",
        data: {
            accountId: GetQueryString('accountId'),
            scene: 'UPDATE'
        },
        dataType: 'json',
        success: function(oData) {
            // console.log(oData)
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
                    if (that.attr('id') == 'region') {
                        var state = oData.entity.data.state;
                        var city = oData.entity.data.city;
                        var region = oData.entity.data.region;
                        var regionValue = state + " " + city + " " + region;
                        if (regionValue.length == 2) {
                            that.find('input').attr('placeholder', '点击输入');
                        } else {
                            that.find('input').val(regionValue);
                        }
                        return;
                    }
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
    })
}


//工商信息查询
var searchAccountName;

function businessInfoQuery() {
    $('.entity-search').on('click', function() {
        var searchAccountName = $('#accountName').find('input').val();
        if (searchAccountName == "") {
            myAlert("请输入查询客户名称");
        } else {
            $('.business-query-wrap').show();
            $('html').css('overflow','hidden');
            $('#search').find('input').val(searchAccountName);
            create_word_list(searchAccountName);

        }

    })
}
//工商信息返回
function businessInfoBack() {
    $('.back').on('click', function() {
        $('.business-query-wrap').hide();
        $('html').css('overflow','auto');
        $('#search').find('input').val("");
        $('.business-query-wrap .business-query-content').children().remove();
    });
}

function create_word_list(val) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/enterpriseinfo/dolist.action",
        async: true,
        data: { enterpriseName: val },
        dataType: 'json',
        success: function(oData) {
            console.log(oData);
            // oData = eval('('+data+')');
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                $('.dialog').remove();
                build_word_list(oData, val);
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}
var accLength;

function build_word_list(obj, val) {
    $('.business-query-content>.list').remove();
    accLength = obj.entity.length;
    //客户部分
    if (accLength > 0) {
        var acc_area = $('<div id="acc_area" class="boxSizing"></div>');
    }
    if ($('#acc_area').length > 0) {
        $('#acc_area').remove();
    }
    $('.business-query-content').append(acc_area);
    create_acc_list(val);
    if (accLength == 0) {
        $('.business-query-content>.list').remove();
        var oList = document.createElement('div');
        oList.style.cssText = "width:100%;padding-left:10px;line-height:88px;font-size:30px;text-align:center;";
        oList.className = 'list';
        oList.innerHTML = "无相关客户";
        $('.business-query-content').append(oList);
    };
};

function create_acc_list(val) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/enterpriseinfo/dolist.action",
        async: true,
        data: { enterpriseName: val },
        dataType: 'json',
        success: function(oData) {
            // var new_data = eval('('+data1+')');
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                $('.dialog').remove();
                build_acc_list(oData, val);
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    });
}

function build_acc_list(data, val) {
    for (var i = 0, len = data.entity.length; i < len; i++) {
        var oList = document.createElement('div');
        var name = data.entity[i].name;
        var reg = new RegExp(val, "g");
        var id = data.entity[i].id;
        oList.href = id;
        oList.name = name;
        name = name.replace(reg, "<span class='blue'>" + val + "</span><span class=" + data.entity[i].type + "></span>");
        oList.innerHTML = name;
        oList.className = 'boxSizing word_list';
        //arrId.push(id);arrName.push(name);
        oList.addEventListener('click', function() {
            $('.business-querydetail-wrap').show();
            getBusinessInfo(this.name);
            $('.business-query-wrap').hide();
            $('#search').find('input').val("");
            $('.business-query-wrap .business-query-content').children().remove();

            $('#basic_info').hide();
            $('#confirm').hide();
            $('add_contact').hide();
            $('html,body').css('overflow', 'hidden');
        });
        $('#acc_area').append(oList);
    }

}


//客户信息详情页
function getBusinessInfo(val) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/enterpriseinfo/get.action",
        async: true,
        data: { enterpriseName: val },
        dataType: 'json',
        success: function(oData) {
            // var new_data = eval('('+data1+')');
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                $('.dialog').remove();

                bulidEnterpriseinfo(oData);
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    });
}
var enterpriseaccountName;
var detailAddress;
var phone;
var website;

function bulidEnterpriseinfo(data) {
    console.log(data);
    if (data.entity.orgNo) {
        enterpriseaccountName = data.entity.accountName;
        $(".business-search-name").html(enterpriseaccountName);
        $("#search-fddbr").html(data.entity.legalUser);
        $("#search-zczb").html(data.entity.registerFunds);
        detailAddress = data.entity.detailAddress;
        detailAddress == "" ? $('#detail_bgdz').html('暂无信息') : $('#detail_bgdz').html(detailAddress);
        phone = data.entity.phone;
        phone == "" ? $('#detail_tel').html('暂无信息') : $('#detail_tel').html(phone);
        $('#detail_fax').html('暂无信息');
        var email = data.entity.email;
        email == "" ? $('#detail_email').html('暂无信息') : $('#detail_email').html(email);
        website = data.entity.website;
        website == "" ? $('#detail_website').html('暂无信息') : $('#detail_website').html(website);
        $('#detail_weibo').html('暂无信息');
        var regNo = data.entity.regNo;
        regNo == "" ? $('#detail_regNo').html('暂无信息') : $('#detail_regNo').html(regNo);
        var orgNo = data.entity.orgNo;
        orgNo == "" ? $('#detail_orgNo').html('暂无信息') : $('#detail_orgNo').html(orgNo);
        var creditNo = data.entity.creditNo;
        creditNo == "" ? $('#detail_creditNo').html('暂无信息') : $('#detail_creditNo').html(creditNo);
        var regiAuth = data.entity.regiAuth;
        regiAuth == "" ? $('#detail_regiAuth').html('暂无信息') : $('#detail_regiAuth').html(regiAuth);
        var regiStatus = data.entity.regiStatus;
        regiStatus == "" ? $('#detail_regiStatus').html('暂无信息') : $('#detail_regiStatus').html(regiStatus);
        var busiStartDate = data.entity.busiStartDate;
        busiStartDate == "" ? $('#detail_busiStartDate').html('暂无信息') : $('#detail_busiStartDate').html(busiStartDate);
        var busiEndDate = data.entity.busiEndDate;
        busiEndDate == "" ? $('#detail_busiEndDate').html('暂无信息') : $('#detail_busiEndDate').html(busiEndDate);
        var detail_busiBuildDate = data.entity.busiStartDate;
        detail_busiBuildDate == "" ? $('#detail_busiBuildDate').html('暂无信息') : $('#detail_busiBuildDate').html(detail_busiBuildDate);
        var checkDate = data.entity.checkDate;
        checkDate == "" ? $('#detail_checkDate').html('暂无信息') : $('#detail_checkDate').html(checkDate);
        $("#detail_postcode").html('暂无信息');

        var employees = data.entity.employees;
          $('.querydetail-parter-body').empty();
        for (var i = 0, len = employees.length; i < len; i++) {
            var item = $('<div class="querydetail-parter-item"></div>')
            var contactName = $('<span>' + employees[i].contactName + '</span>');
            var contactPost = $('<span>' + employees[i].contactPost + '</span>');
            item.append(contactPost);
            item.append(contactName);
            $('.querydetail-parter-body').append(item);
        }

        var changeRecords = data.entity.changeRecords;
         $('.querydetail-changeRecords-body').empty();
        for (var i = 0, len = changeRecords.length; i < len; i++) {
            var item = $('<div class="querydetail-changeRecords-item"></div>');
            var changeDate = changeRecords[i].changeDate;
            var changeItem = changeRecords[i].changeItem;
            var beforeContent = changeRecords[i].beforeContent;
            var afterContent = changeRecords[i].afterContent;
            var dchangeDate = $('<span>' + changeDate + '</span>');
            var dchangeItem = $('<span>' + changeItem + '</span><span>变更</span>');
            var dbeforeContent = $('<span>' + beforeContent + '</span>');
            var dafterContent = $('<span>' + afterContent + '</span>');
            item.append(dchangeDate);
            item.append(dchangeItem);
            item.append(dbeforeContent);
            item.append(dafterContent);
            $('.querydetail-changeRecords-body').append(item);
        }
    }

    else{
        $(".business-querydetail-wrap").children().remove();
        var empty = $('<div class="content-reminder"><div class="ico"><p>暂无信息</p></div></div>');
        $('.business-querydetail-wrap').append(empty);
    }
}

function enterpriseinfoBack() {
    $('.querydetail-back').on('click', function() {
        $('.business-querydetail-wrap').hide();
        $('.business-query-wrap').hide();
        $('#search').find('input').val("");

        $('#basic_info').show();
        $('#confirm').show();
        $('add_contact').show();
        $('html,body').css('overflow', '');
    });
}

function enterpriseinfoWrite() {
    $('.querydetail-write').on('click', function() {
        $('.business-querydetail-wrap').hide();
        $('.business-query-wrap').hide();
        $('#search').find('input').val("");
        $('#basic_info').show();
        $('#confirm').show();
        $('add_contact').show();
        $('html,body').css('overflow', '');
        $("#accountName").find('input').val(enterpriseaccountName);
        $('#address').find('input').val(detailAddress);
        $('#phone').find('input').val(phone);
        $('#url').find('input').val(website);
        var region = detailAddress.substring(0, 10);
        $('#region').find('input').val(region);
    });
}


//工商信息搜索返回
function enterpriseBack(){
    $("#back").on('click',function(){
        $(".business-query-wrap").hide();
        $('html').css('overflow','');
    });
    $(".querydetail-back").on('click',function(){
        $(".business-querydetail-wrap").hide();
        $("#basic_info").show();
    });
}