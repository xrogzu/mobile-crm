/**
 * Created by dell on 2016/1/6.
 */
$(document).on('ready', function() {
    top_nav();
    nav();
    linkMore();
    more();
    get_record($('#recordList > .main'), 5);
    get_opportunity($('#opportunityList > .main'), 3);
    get_contacts($('#contactList > .main'), 5);
    get_info($('#infoList > .main'));
    footer_phone();
    footer_signin();
    footer_create_record();
    filter_touch();
    //初始化
    var tab = $('<ul id="record_tab" class="tab record boxSizing"><div class="main boxSizing"></div></ul>');
    $('body').append(tab);
    recordPageNo = 0;
    get_record($('#record_tab > .main'), 20, 'tab');
    $('#record_tab').fadeIn(300);
    //移除更多菜单
    $('body').on('click', function() {
        if ($('#area').length > 0) {
            $('#area').remove();
        }
    })
    $('body').on('touchmove', function() {
        if ($('#area').length > 0) {
            $('#area').remove();
        }
    })

})
var reminderHtml = '<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>';

// 头部更多按钮
function linkMore() {
    $(".link_more").on('click', function() {
        $(".options").toggle();
    })
}
// 点击页面任何地方 新建弹窗消失
$(document).on('touchstart', function(e) {
    if (!$(e.target).closest(".link_more").size()) {
        $(".options").hide();
    }
});
$(document).on('chick', function(e) {
    if (!$(e.target).closest(".link_more").size()) {
        $(".options").hide();
    }
});

function toAccountList() {
    $('.cOwnerShadow').remove();
    if ($('.ct').length > 0) {
        if (memberChange) {
            myChoose('', ctRemove);
        } else {
            ctRemove();
        }
    } else {
        if (GetQueryString('from') == 'opp_info') {
            location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId');
        } else if (GetQueryString('from') == 'con_info') {
            location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId');
        } else if (GetQueryString('from') == 'acc_info') {
            location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId');
        } else if (GetQueryString('from') == 'index') {
            if (GetQueryString('page')) {
                location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page');
            } else {
                location.href = apppath + '/wx/statics/index.action?' + ddcommparams;
            }
        } else {
            location.href = apppath + '/wx/account/list.action?' + ddcommparams;
        }
    }
}

function ctRemove() {
    $('body').css('height', '');
    $('body').css('overflow', '');
    ceilingJudge();
    $('.ct').remove();
}
//跳转到新建联系人时需要的客户名全局变量
var accountName;

function more() {
    if ($('#area').length > 0) {
        $('#area').remove();
    } else {
        $('#add_contact').on('click', function() {
            $('window').off('beforeunload');
            $('#area').remove();
            var name = accountName;
            var accName = encodeURIComponent(name);
            location.href = apppath + '/wx/contact/add.action?' + ddcommparams + '&from=acc_info&type=add&accountId=' + GetQueryString('accountId') + '&accountName=' + accName;
        });
        $('#new_business').on('click', function() {
            $('#area').remove();
            console.log('跳转到新建商机页面');
            location.href = apppath + '/wx/opportunity/add.action?' + ddcommparams + '&from=acc_info&type=add&accountId=' + GetQueryString('accountId');
        });
        $('#set_account').on('click', function() {
            $('#area').remove();
            location.href = apppath + '/wx/account/add.action?' + ddcommparams + '&type=set&from=acc_info&accountId=' + GetQueryString('accountId');
        });
        $('#delete_account').on('click', function() {
            $('#area').remove();
            delete_dialog();
        });
    }
}

function delete_dialog() {
    var dia = $('<div class="delShadow"><div class="dia"><p class="boxSizing">确定删除吗?</p><div class="confirm"><div class="no">取消</div><div class="yes boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({ 'top': '200px', 'left': ($(window).width() - $('.dia').width()) / 2 });
    $('.yes').on('click', function() {
        delete_account();
        $('.delShadow').remove();
    })
    $('.no').on('click', function() {
        $('.delShadow').remove();
    })
}

function delete_account() {
    $.ajax({
        url: apppath + '/wx/account/dodel.action',
        data: { accountId: GetQueryString('accountId') },
        async: false,
        dataType: 'json',
        success: function(oData) {
            if (oData.success) {
                $('.shadow').remove();
                myDialog('删除成功');
                time_out_location(apppath + '/wx/account/list.action?' + ddcommparams);
            } else {
                myAlert('删除失败');
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载');
        }
    })
}
var nav_top = $('#topNav').offset().top;

function top_nav() {
    $(window).on('scroll', function() {
        ceilingJudge();
    })
}
var nav_top = $('#topNav').offset().top;
var nav_height = $('#topNav').height();

function ceilingJudge() {
    if ($('body').scrollTop() >= nav_top + 120) {
        $('.top_meun').css({ 'position': 'fixed', 'top': 0, 'width': '100%' });
        // $('#topNav').css({ 'position': 'fixed', 'top': 0 });
        // $('.white_ban').css({ 'position': 'fixed' })
        $('.common').css('paddingBottom', '90px');
    } else {
        $('.top_meun').css({ 'position': 'static', 'top': '', 'bottom': '' });
        $('.common').css({ 'paddingBottom': '', 'position': 'relative' });
        // $('.white_ban').css({ 'position': 'absolute', 'bottom': -10 + 'px', 'top': '' });
        // $('.common').append($('.white_ban'));
    }
}

function nav() {
    $('#topNav').on('scroll', function() {
        if ($('#nav').offset().left > -20) {
            $('.left_filter').fadeOut(200);
        } else {
            $('.left_filter').fadeIn(200);
        }
        if ($('#nav').offset().left <= $(window).width() - $('#nav').width() - 30) {
            $('.right_filter').fadeOut(200);
        } else {
            $('.right_filter').fadeIn(200);
        }
    })
    $('#nav li').each(function() {
        $(this).on('click', function() {
            $(this).addClass('active');
            $(this).siblings().removeClass('active');
            tab($(this).attr('href'));
        })
    })
}

function filter_touch() {
    $('.right_filter').on('touchend', function() {
        $('#topNav').scrollLeft(300);
    })
    $('.left_filter').on('touchend', function() {
        $('#topNav').scrollLeft(0);
    })
}

function tab(type) {
    $('html, body').animate({ scrollTop: 0 }, 300);
    $('#topNav').css({ 'position': '', 'top': '', 'bottom': '' });
    $('.common').css({ 'paddingBottom': '', 'position': 'relative' });
    // $('.white_ban').css({ 'position': 'absolute', 'bottom': -5 + 'px', 'top': '' });
    // $('.common').append($('.white_ban'));
    $('.content').fadeOut(300);
    if (type == '联系人') {
        if ($('#contact_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="contact_tab" class="tab contact boxSizing"><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            contactPageNo = 0;
            get_contacts($('#contact_tab > .main'), 20, 'tab');
            $('#contact_tab').fadeIn(300);
        }
    } else if (type == '记录') {
        if ($('#record_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="record_tab" class="tab record boxSizing"><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            recordPageNo = 0;
            get_record($('#record_tab > .main'), 20, 'tab');
            $('#record_tab').fadeIn(300);
        }
    } else if (type == '商机') {
        if ($('#opportunity_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="opportunity_tab" class="tab opportunity boxSizing"><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            opportunityPageNo = 0;
            get_opportunity($('#opportunity_tab > .main'), 20, 'tab');
            $('#opportunity_tab').fadeIn(300);
        }
    } else if (type == '详情') {
        if ($('#info_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="info_tab" class="tab details boxSizing"><div class="staff boxSizing"></div><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            get_info($('#info_tab > .main'));
             $('.staff').append($('<div class="owner boxSizing"> ' +
                '<span class="label">负责人:</span> ' +
                '<span class="value"></span> </div> ' +
                '<div class="member boxSizing"> ' +
                '<span class="label">团队成员:</span>' +
                ' <span class="value"></span> </div>'));
          
            

            $('#info_tab').fadeIn(300);
        }
    } else if (type == '全部') {
        $('.tab').remove();
        $('.content').fadeIn(300);
        get_info($('#infoList > .main'));
    }
}
//====================================录入客户下活动记录=====================================
var recordPageNo = 0;

function get_record($parent_obj, pagesize, type) {
    // $parent_obj.children().remove();
    $.ajax({
        url: apppath + '/wx/feed/list.action',
        data: {
            belongId: 1,
            objectId: GetQueryString('accountId'),
            pagesize: pagesize,
            pageNo: recordPageNo + 1
        },
        dataType: 'json',
        success: function(oData) {
            // $('.load-shadow').hide();
            //测试校验
            // console.log('跟进记录：' + oData)
            if (oData.success == true) {
                if (oData.entity.feeds.length == 0) {
                    $parent_obj.css('border', 0);
                    $parent_obj.append(reminderHtml);
                    console.log('该对象暂无跟进记录');
                } else {
                    //增加更多按钮
                    if (oData.entity.hasMore) {
                        if ($('#recordList .more').length == 0) {
                            var more = $('<a class="more"></a>');
                            $('#recordList > .top').append(more);
                        }
                        $('#recordList .more').on('click', function() {
                            $('#nav > .record').addClass('active');
                            $('#nav > .record').siblings().removeClass('active');
                            tab($('#nav > .record').attr('href'));
                        })
                    }
                    build_record_list($parent_obj, oData);
                    if (oData.entity.hasMore && type == 'tab') {
                        $('#loading').remove();
                        build_end($parent_obj, '加载更多', 'load_more');
                        recordPageNo += 1;
                        scroll_load($parent_obj, get_record);
                    } else {
                        $('#loading').remove();
                    };
                }
            } else {
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('网络不给力，请重新加载')
                }
            }
        },
        error: function() {
            console.log('网络不给力，请重新加载')
        }
    })
}

function build_record_list($parent_obj, data) {
    var item = data.entity.feeds;
    for (var i = 0; i < item.length; i++) {
        //console.log(item[i])
        var userName = item[i].user.name;
        var typeName;
        var contentText;
        var objectName = item[i].from.name;
        var belongId = item[i].from.belongId;
        var relateBelongId = 4;
        if (item[i].relateBelongId) {
            relateBelongId = Number(item[i].relateBelongId)
        }
        var relateObjectId = item[i].relateObjectId;
        var objectId = item[i].from.id;
        if (item[i].activityRecord.typeName) { //传统类型记录
            typeName = item[i].activityRecord.typeName;
        } else { //新类型记录
            objectId = relateObjectId;
            belongId = relateBelongId;
            objectName = item[i].content;
            var action = item[i].stream.action;
            if (action == 1) {
                switch (relateBelongId) {
                    case 1:
                        typeName = '创建了客户';
                        break;
                    case 2:
                        typeName = '创建了联系人';
                        break;
                    case 3:
                        typeName = '创建了商机';
                        break;
                }
            } else if (action == 3) {
                switch (relateBelongId) {
                    case 1:
                        typeName = '删除了客户';
                        break;
                    case 2:
                        typeName = '删除了联系人';
                        break;
                    case 3:
                        typeName = '删除了商机';
                        break;
                }
            } else if (action == 4) {
                typeName = '转移了客户'
            } else if (action == 5) {
                switch (relateBelongId) {
                    case 1:
                        typeName = '添加了客户';
                        break;
                    case 2:
                        typeName = '添加了联系人';
                        break;
                    case 3:
                        typeName = '添加了商机';
                        break;
                    case 4:
                        typeName = '增加了团队成员';
                        break;
                }
            } else if (action == 6) {
                switch (relateBelongId) {
                    case 1:
                        typeName = '移除了客户';
                        break;
                    case 2:
                        typeName = '移除了联系人';
                        break;
                    case 3:
                        typeName = '移除了商机';
                        break;
                    case 4:
                        typeName = '移除了团队成员';
                        break;
                }
            } else if (action == 11) {
                typeName = '改变了商机状态'
            } else if (action == 15) {
                typeName = '增加了团队成员'
            }
        }

        var date = new Date(item[i].createdAt);
        var sYear = date.getFullYear();
        var sMonth = date.getMonth() + 1;
        if (sMonth < 10) {
            sMonth = '0' + sMonth;
        }
        var sDate = date.getDate();
        if (sDate < 10) {
            sDate = '0' + sDate;
        }
        var hour = date.getHours();
        if (hour < 10) {
            hour = '0' + hour;
        }
        var minu = date.getMinutes();
        if (minu < 10) {
            minu = '0' + minu;
        }
        var startDate = sYear + '-' + sMonth + '-' + sDate;
        var startTime = hour + ':' + minu;
        if (i == 0) {
            var day = $('<div class="day"><em></em><p class="startDate">' + startDate + '</p><div class="ban"></div></div>');
            $parent_obj.append(day);
        } else {
            var createdAt = new Date(item[i].createdAt).getFullYear() + '-' + new Date(item[i].createdAt).getMonth() + '-' + new Date(item[i].createdAt).getDate();
            var createdAt1 = new Date(item[i - 1].createdAt).getFullYear() + '-' + new Date(item[i - 1].createdAt).getMonth() + '-' + new Date(item[i - 1].createdAt).getDate();
            if (createdAt != createdAt1) {
                var day = $('<div class="day"><em></em><p class="startDate">' + startDate + '</p><div class="ban"></div></div>');
                $parent_obj.append(day);
            }
        }
        var oList = $('<li class="boxSizing"><div class="info"><span class="userName">' + userName + '</span><span class="typeName">' + typeName + ':</span><a class="objectName">' + objectName + '</a><span class="startTime">' + startTime + '</span></div></li>');
        oList.find('.objectName').attr({ 'belongId': belongId, 'objectId': objectId });
        if (objectId != GetQueryString('accountId')) {
            oList.find('.objectName').on('click', function() {
                var recordUrl;
                if ($(this).attr('belongId') == 1) {
                    recordUrl = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + $(this).attr('objectId') + '&from=acc_info&raccountId=' + GetQueryString('accountId');
                } else if ($(this).attr('belongId') == 2) {
                    recordUrl = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + $(this).attr('objectId') + '&from=acc_info&raccountId=' + GetQueryString('accountId');
                } else if ($(this).attr('belongId') == 3) {
                    recordUrl = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('objectId') + '&from=acc_info&raccountId=' + GetQueryString('accountId');
                } else {
                    return false;
                }
                location.href = recordUrl;
            });
        }
        if (typeName == "电话") {
            oList.addClass('phone_bg');
        } else if (typeName == "拜访签到") {
            oList.addClass('sign_in_bg');
        } else if (typeName == "快速记录") {
            oList.addClass('record_bg');
        } else if (typeName == "创建了客户" || typeName == "删除了客户" || typeName == "添加了客户" || typeName == "移除了客户" || typeName == "转移了客户") {
            oList.addClass('account_bg');
        } else if (typeName == "创建了联系人" || typeName == "删除了联系人" || typeName == "添加了联系人" || typeName == "移除了联系人") {
            oList.addClass('contact_bg');
        } else if (typeName == "创建了商机" || typeName == "删除了商机" || typeName == "添加了商机" || typeName == "移除了商机" || typeName == "改变了商机状态") {
            oList.addClass('opportunity_bg');
        } else if (typeName == "增加了团队成员" || typeName == "转移了团队成员" || typeName == "移除了团队成员") {
            oList.addClass('member_bg');
        }
        if (item[i].activityRecord.typeName) {
            if (item[i].content) {
                contentText = item[i].content;
                var content_span = $('<span class="content_span">' + contentText + '</span>');
                var content = $('<div class="content"></div>');
                var triangle = $('<em class="triangle"></em>');
                content.append(content_span);
                content.append(triangle);
                if (contentText.length > 32) {
                    var shortContentText = contentText.substr(0, 30) + '...';
                    var short_content_span = $('<span class="short_content_span">' + shortContentText + '</span>');
                    content.append(short_content_span);
                    content_span.css('display', 'none');
                    var index = 0;
                    var more_btn = $('<em class="more"></em>');
                    more_btn.on('click', function() {
                        index++;
                        $(this).parent().find('.short_content_span').slideToggle();
                        $(this).parent().find('.content_span').slideToggle();
                        $(this).css("transform", "rotate(" + (90 + 180 * index) + "deg)");

                    })
                    content.append(more_btn);
                }
                oList.append(content);
            }
        }
        if (item[i].location.detail) {
            var locat = $('<div class="location">' + item[i].location.detail + '</div>');
            oList.append(locat);
        }
        var bottom_border = $('<div class="bottom_border"></div>');
        oList.append(bottom_border);
        $parent_obj.append(oList);
    }
    removeBorder();
}

function removeBorder() {
    $('#record_tab li').each(function() {
        if ($(this).next().hasClass('day')) {
            $(this).find('div:last-child').css('borderBottom', 0);
        }
    })
}
//====================================录入客户下联系人信息=====================================
var contactPageNo = 0;

function get_contacts($parent_obj, pagesize, type) {
    $.ajax({
        url: apppath + '/wx/contact/dolist.action',
        data: {
            accountId: GetQueryString('accountId'),
            pagesize: pagesize,
            orderField: 'createdAt',
            pageNo: contactPageNo
        },
        dataType: 'json',
        success: function(oData) {
            // $('.load-shadow').hide();
            //测试校验
            // console.log('联系人：' + oData)
            if (oData.success == true) {
                if (oData.entity.records.length == 0) {
                    $parent_obj.css('border', 0);
                    $parent_obj.append(reminderHtml);
                    console.log('该对象暂无联系人信息');
                } else {
                    if (oData.entity.totalSize > 5) {
                        if ($('#contactList .more').length == 0) {
                            var more = $('<a class="more"></a>');
                            $('#contactList > .top').append(more);
                        }
                        $('#contactList .more').on('click', function() {
                            $('#nav > .contact').addClass('active');
                            $('#nav > .contact').siblings().removeClass('active');
                            tab($('#nav > .contact').attr('href'));
                        })
                    }
                    var totalSize = oData.entity.totalSize;
                    $('#contactList > .top > .title').text('联系人(' + totalSize + ')');
                    $('#nav > .contact').text('联系人(' + totalSize + ')');
                    build_contact_list($parent_obj, oData);
                    if ($parent_obj.find('li').length < totalSize && type == 'tab') {
                        $('#loading').remove();
                        build_end($parent_obj, '加载更多', 'load_more');
                        contactPageNo++;
                        scroll_load($parent_obj, get_contacts);
                    } else {
                        $('#loading').remove();
                    };
                }
            } else {
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('网络不给力，请重新加载')
                }
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}

function build_contact_list($parent_obj, data) {
    var item = data.entity.records;
    for (var i = 0; i < item.length; i++) {
        var name = item[i].contactName;
        var post = item[i].post;
        var mobile = item[i].mobile;
        var phone = item[i].phone;
        var id = item[i].id;
        var oList = $('<li class="boxSizing" ><div class="contact_info_list" mobile="' + mobile + '" phone="' + phone + '"><span class="name">' + name + '</span><span class="post">' + post + '</span></div><a class="tel"></a></li>');

        oList.find('.contact_info_list').attr('value', id);
        oList.find('.contact_info_list').on('click', function() {
            location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + $(this).attr('value') + '&from=acc_info&raccountId=' + GetQueryString('accountId');
        })
        $parent_obj.append(oList);
    }
    contacttelClick();
}

function contacttelClick() {
    var tellist = $('#list li .con');
    $('.main li').each(function(index, el) {
        $(el).find('.tel').on('click', function(e) {
            var contactInfo = $(this).siblings(".contact_info_list");
            var objectId = contactInfo.attr('value');
            var mymobile = contactInfo.attr('mobile');
            var myphone = contactInfo.attr('phone');
            if (mymobile == "" && myphone == "") {
                var no_contact = $('<div class="no_contact">暂无联系方式，请填写联系方式</div>');
                $('body').append(no_contact);
                setTimeout(function() {
                    $('.no_contact').remove();
                }, 1000);
            } else {
                $(window).scrollTop(0);
                var shadow = $('<div class="tel_bg"></div>');
                shadow.on('click', function() {
                    $('.tel_bg').remove();
                    $('#tel_list').remove();
                })
                $('body').append(shadow);
                $('.tel_bg').css('top', $(document).scrollTop());
                $('.tel_bg').fadeIn(200);
                var ul = $('<div id="tel_list"></div>');
                add_atom(ul, mymobile, '家庭电话', objectId);
                add_atom(ul, myphone, '手机', objectId);
                var rtn_btn = $('<div id="rtn_btn">取消</div>');
                rtn_btn.on('click', function() {
                    $('body').css({ 'overflow': '', 'height': '' });
                    $('.tel_bg').remove();
                    $('#tel_list').animate({ 'bottom': -$('#tel_list').height() }, 300);
                    setTimeout(function() {
                        $('#tel_list').remove();
                    }, 300);
                })
                ul.append(rtn_btn);
                $('body').append(ul);
                $('body').css({ 'overflow': 'hidden', 'height': '100%' });
                $('#tel_list').on('touchmove', function(e) {
                    e.stopPropagation();
                })
                $('#tel_list').css({ 'bottom': -$('#tel_list').height() })
                $('#tel_list').animate({ 'bottom': '0' }, 300);
            }
        });
    });
}

function add_atom(ul, obj, label_name, id) {
    if (obj && obj != '') {
        var li = $('<a class="tel_atom" href="tel:' + obj + '"><span class="label">' + label_name + '</span>' +
            '<span class="value">' + obj + '</span></a>');
        li.on('click', function() {
            // e.stopPropagation();
            //$('.tel_bg').remove();
            //$('#tel_list').remove();
            //console.log('记录打电话动作');
            //alert(1)
            location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + id + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=acc_info&accountId=' + GetQueryString('accountId');
        })
        ul.append(li);
    }
}
//==================================获取商机==================================================
var opportunityPageNo = 0;

function get_opportunity($parent_obj, pagesize, type) {
    $.ajax({
        url: apppath + '/wx/opportunity/dolist.action',
        data: {
            accountId: GetQueryString('accountId'),
            pageNo: opportunityPageNo,
            pagesize: pagesize,
            orderField: 'updatedAt'
        },
        dataType: 'json',
        success: function(oData) {
            // $('.load-shadow').hide();
            if (oData.success == true) {
                if (oData.entity.records.length == 0) {
                    $parent_obj.css('border', 0);
                    $parent_obj.append(reminderHtml);
                    console.log('该对象暂无商机信息');
                } else {
                    if (oData.entity.totalSize > 3) {
                        if ($('#opportunityList .more').length == 0) {
                            var more = $('<a class="more"></a>');
                            $('#opportunityList > .top').append(more);
                        }
                        $('#opportunityList .more').on('click', function() {
                            $('#nav > .chance').addClass('active');
                            $('#nav > .chance').siblings().removeClass('active');
                            tab($('#nav > .chance').attr('href'));
                        })
                    }
                    var totalSize = oData.entity.totalSize;
                    $('#opportunityList > .top > .title').text('商机(' + totalSize + ')');
                    $('#nav > .chance').text('商机(' + totalSize + ')');
                    build_opportunity($parent_obj, oData);
                    if ($parent_obj.find('li').length < totalSize && type == 'tab') {
                        $('#loading').remove();
                        build_end($parent_obj, '加载更多', 'load_more');
                        opportunityPageNo++;
                        scroll_load($parent_obj, get_opportunity);
                    } else {
                        $('#loading').remove();
                    };
                }
            } else if (oData.success == false) {
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('网络不给力，请重新加载')
                }
            }
        },
        error: function(xhr, err) {
            myAlert('网络不给力，请重新加载');
        }
    })
}

function build_opportunity($parent_obj, data) {
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var item = data.entity.records[i];
        var saleStageText = item.saleStageText;
        var opportunityName = item.opportunityName;
        var Money = item.money;
        var accountName = item.accountName;
        var id = item.id;
        var saleStageOrder = item.saleStageOrder;
        //阶段百分比
        var jieduan_p = item.saleStagePercentage;
        var li = $('<li class="boxSizing"><em class="icon jieduan' + saleStageOrder + '"></em><div class="info"><div class="top">' +
            '<span class="opportunityName">' + opportunityName + '</span><span class="Money">' + Money + '元</span>' +
            '</div><div class="bot"><p class="accountName">' + accountName + '</p><p class="jieduan">阶段：' + saleStageText + '</p>' +
            '<span class="jieduan_p">' + jieduan_p + '%</span></div></div></li>');
        li.attr('value', id);
        li.on('click', function() {
            location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('value') + '&from=acc_info&raccountId=' + GetQueryString('accountId');
        })
        $parent_obj.append(li);
    }
}
//==================================录入客户详细信息(包括页眉客户名)===========================
var ownerId;
var dataPermission;
var ownerName;

function get_info($parent_obj) {
    $.ajax({
        url: apppath + '/wx/account/getDetail.action',
        data: {
            accountId: GetQueryString('accountId'),
            scene: 'DETAIL'
        },
        dataType: 'json',
        success: function(oData) {
            console.log(oData);
            if (oData.success == true) {
                $('.load-shadow').hide();
                dataPermission = oData.entity.expandPro.dataPermission;
                build_info($parent_obj, oData);
                ownerName = oData.entity.expandPro.ownerIdText;
                $('.owner .value').text(ownerName);
                ownerId = oData.entity.data.ownerId;
                if (dataPermission.transfer) {
                    change_owner();
                } else {
                    $('.owner').css('background', 'none');
                }
                if (dataPermission.update) {
                    change_member();
                } else {
                    $('.member').css('background', 'none');
                }
                if (dataPermission.delete == false) {
                    $("#set_account").parent().hide();
                    $("#delete_account").parent().hide();
                }
                get_member();
                $('.common .title').text(oData.entity.data.accountName);
                //跳转到新建联系人时需要的客户名全局变量
                accountName = oData.entity.data.accountName;
                if (oData.entity.data.phone != null) {
                    $('footer > .tel').attr('href', 'tel:' + oData.entity.data.phone);
                } else {
                    $('footer > .tel').attr('href', '');
                }
                $('#bus_acc_ico').on('click', function() {
                    // location.href = 'http://www.baidu.com';
                    location.href = 'https://info.camcard.com/site/sdk?from_sdk=XiaoShouYi&search=' + accountName + '&user=mobile_18911666889';
                });
            } else {
                if (oData.errorCode == '300032') {
                    if (GetQueryString('from') == 'opp_info') {
                        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId') + '&err=acc_deleted';
                    } else if (GetQueryString('from') == 'con_info') {
                        location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId') + '&err=acc_deleted';
                    } else if (GetQueryString('from') == 'acc_info') {
                        location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId') + '&err=acc_deleted';
                    } else if (GetQueryString('from') == 'index') {
                        if (GetQueryString('page')) {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page') + '&err=acc_deleted';
                        } else {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams;
                        }
                    }
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000004') {
                    if (GetQueryString('from') == 'opp_info') {
                        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId') + '&err=acc_noright';
                    } else if (GetQueryString('from') == 'con_info') {
                        location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId') + '&err=acc_noright';
                    } else if (GetQueryString('from') == 'acc_info') {
                        location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId') + '&err=acc_noright';
                    } else if (GetQueryString('from') == 'index') {
                        if (GetQueryString('page')) {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page') + '&err=acc_noright';
                        } else {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&err=acc_noright';
                        }
                    }
                } else {
                    myAlert('网络不给力，请重新加载');
                }
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载');
        }
    })
}

function build_info($parent_obj, data) {
    $parent_obj.children().remove();
    var value;
    var valuenull = '<span style="color:#8fa1b2">未填写</span>';
    var structure = data.entity.structure.components;
    for (var i = 0, len = structure.length; i < len; i++) {
        value = set_infoSup(structure[i].propertyName, data);
        var datetime = new Date();
        var show, rValue;
        if (structure[i].propertyName == "createdBy" || structure[i].propertyName == "updatedBy") {
                show = value.text;
                rValue = value.value;
                if (rValue == "") {
                    info_atom_build($parent_obj, structure[i].label, valuenull, structure[i].propertyName, structure[i].type);
                } else {
                    info_atom_build($parent_obj, structure[i].label, show, structure[i].propertyName, structure[i].type);
                }
        } else {
            if (typeof value === "object") {
                show = value.text;
                rValue = value.value;
                if (rValue == "") {
                    info_atom_build($parent_obj, structure[i].label, valuenull, structure[i].propertyName, structure[i].type);
                } else {
                    info_atom_build($parent_obj, structure[i].label, show, structure[i].propertyName, structure[i].type);
                }

            } else {
                show = rValue = value;
                if (rValue == "") {
                    info_atom_build($parent_obj, structure[i].label, valuenull, structure[i].propertyName, structure[i].type);
                } else {
                    if (structure[i].propertyName == 'region') {
                        var state = data.entity.data.state;
                        var city = data.entity.data.city;
                        var region = data.entity.data.region;
                        var regionValue = state + " " + city + " " + region;
                        info_atom_build($parent_obj, structure[i].label, regionValue, structure[i].propertyName, structure[i].type);
                    } else {
                        info_atom_build($parent_obj, structure[i].label, value, structure[i].propertyName, structure[i].type);
                    }
                }
            }
        }
    }
    if (value != '') {
        // var oAtom = $('<li class="atom commentAtom boxSizing" style="border-top: 1px solid #e3e3e5"><span class="label">备注:</span><span class="value"></span></li>');
        var contentText = value;
        // var content_span = $('<span class="content_span">' + contentText + '</span>');
        // var content = $('<div class="content"></div>');
        // content.append(content_span);
        if (contentText.length > 32) {
            var shortContentText = contentText.substr(0, 30) + '...';
            var short_content_span = $('<span class="short_content_span">' + shortContentText + '</span>');
            content.append(short_content_span);
            content_span.css('display', 'none');
            var index = 0;
            var more_btn = $('<em class="more"></em>');
            more_btn.on('click', function() {
                index++;
                $(this).parent().find('.short_content_span').slideToggle();
                $(this).parent().find('.content_span').slideToggle();
                $(this).css("transform", "rotate(" + (90 + 180 * index) + "deg)");

            })
            content.append(more_btn);
        }
        // oAtom.find('.value').append(content);
        $('.staff .commentAtom').remove();
        // $('.staff').append(oAtom);
    }
}

function info_atom_build($parent_obj, tagName, text, data, type) {
    var des = '<span style="color:#8fa1b2">未填写</span>';
    if (text != '') {
        if (data == 'phone') {
            if (text == des) {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue"><a href="tel:"> ' + text + '</a></span></li>');
            } else {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue"><a href="tel:' + text + '"> ' + text + '</a></span></li>');
            }
            oAtom.on('click', function() {
                location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=1&objectId=' + GetQueryString('accountId') + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=acc_info&accountId=' + GetQueryString('accountId');
            })
        }
        if (type == "ITEM_TYPE_LINE") {
            var oAtom = $('<li class="atom boxSizing line-atom" id=' + data + '><span class="line-title">' + tagName + '</span></li>');
        } else {
            if (data == 'state') {
                return;
            }
            if (data == "city") {
                return;
            }
            if (data == "region") {
                var label = '省市区';
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + label + ':</span><span class="value">' + text + '</span></li>');

            }
            if (data == 'accountName') {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + ':</span><span class="value">' + text + '<a id="bus_acc_ico"><i></i></a></span></li>');
            } else {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + ':</span><span class="value">' + text + '</span></li>');
            }
        }
        $parent_obj.append(oAtom);
    }
}

//获取团队成员信息
var memberNameArr = [];
var memberIdArr = [];
var memberName;

function get_member() {
    $.ajax({
        type: "post",
        url: apppath + '/wx/group/relateowner.action',
        data: {
            businessId: GetQueryString('accountId'),
            belongs: 1
        },
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            // console.log('团队成员：' + oData);
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载')
            } else {
                memberNameArr = [];
                memberIdArr = [];
                var memberName = oData.entity.users;
                $.each(memberName, function(i, o) {
                    memberNameArr.push(o.name);
                    memberIdArr.push(o.id);
                });
                $('.member > .value').text(memberNameArr.join(' , '));
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载');
        }
    })
}
//改变负责人

function change_owner() {
    $('.staff .owner').on('click', function() {
        get_owner_list();
    })
}

function get_owner_list() {
    $.ajax({
        type: "get",
        url: apppath + "/wx/xsyuser/pager.action",
        async: true,
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                if (oData.entity.records.length == 1) {
                    var no_contact = $('<div class="no_contact">您的客户下暂时没有其他员工</div>');
                    $('body').append(no_contact);
                    setTimeout(function() {
                        $('.no_contact').remove();
                    }, 2000);
                } else {
                    checkTable('转移给他人');
                    var container = $('<ul id="container" class="container ownerContainer"></ul>');
                    $('.ct').append(container);
                    build_owner_list(container, oData);
                }
            }
        }
    });
}

function build_owner_list(container, data) {
    //负责人模式（单选）
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        if (data.entity.records[i].name == $('#infoList .owner > .value').text()) {
            var con = $('<div class="item"><em class="radio active"><i></i></em><span>' + data.entity.records[i].name + '</span></div>');
        } else {
            var con = $('<div class="item"><em class="radio"><i></i></em><span>' + data.entity.records[i].name + '</span></div>');
        }
        container.append(con);
    }
    radio_click(data);
}

function radio_click(data) {
    $('.ct .item').each(function(index) {
        $(this).on('click', function() {
            if ($(this).find('em').hasClass('active')) {} else {
                $(this).find('em').addClass('active');
                $(this).siblings().find('em').removeClass('active');
            }
            var label = data.entity.records[$(this).index()].name;
            var value = data.entity.records[$(this).index()].id;
            doChangeOwner(label, sub);

            function sub() {
                $.ajax({
                    url: apppath + '/wx/account/changeto.action',
                    data: {
                        accountId: GetQueryString('accountId'),
                        ownerid: value
                    },
                    dataType: 'json',
                    beforeSend: function() {
                        loader();
                    },
                    success: function(oData) {
                        $('.loaderArea').remove();
                        setTimeout(function() {
                            ctRemove();
                        }, 2000);
                        if (oData.success == true) {
                            $('.owner > .value').text(label);
                            get_record($('#recordList > .main'), 5);
                            myDialog('负责人更改成功');
                            ownerId = value;
                        } else {
                            myAlert('负责人更改失败');
                        }
                    },
                    error: function() {
                        $('.loaderArea').remove();
                        myAlert('网络不给力，请重新加载');
                    }
                })
            }
        })
    })
}
//修改团队成员
function change_member() {
    $('.member').on('click', function() {
        get_member_list();
    });
}

function get_member_list() {
    $.ajax({
        type: "get",
        url: apppath + "/wx/xsyuser/pager.action",
        async: true,
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                if (oData.entity.records.length == 1) {
                    var no_contact = $('<div class="no_contact">您的客户下暂时没有其他员工</div>');
                    $('body').append(no_contact);
                    setTimeout(function() {
                        $('.no_contact').remove();
                    }, 2000);
                } else {
                    checkTable('修改团队成员');
                    var container = $('<ul id="container" class="container"></ul>');
                    $('.ct').append(container);
                    $('.ct').addClass("staff-warp");
                    build_member_list(container, oData);
                }
            }
        }
    });
}

function build_member_list(container, data) {
    //团队成员模式（多选）
    var arr_member = $('#infoList .member > .value').text().split(',');
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var label = data.entity.records[i].name;
        if (label != ownerName) {
            var con = $('<li class="item"><em class="checkbox"></em><span>' + label + '</span></li>');
            con.attr('value', data.entity.records[i].id);
            container.append(con);
        }
    }
    var dataEntityRecords = data.entity.records;
    for (var j = 0, len = arr_member.length; j < len; j++) {
        for (var i = 0, len = dataEntityRecords.length; i < len; i++) {
            if ($.trim(arr_member[j]) == $('.ct .item').eq(i).find('span').text()) {
                $('.ct .item').eq(i).find('em').addClass('active');
            }
        }
    }
    checkbox_click(data);
    create_confirm(data);
}
var memberChange = false;

function checkbox_click(data) {
    $('.item').each(function(index) {
        $(this).on('click', function() {
            memberChange = true;
            if ($(this).find('em').hasClass('active')) {
                $(this).find('em').removeClass('active');
            } else {
                $(this).find('em').addClass('active');
            }
        })
    })
}

function create_confirm(data) {
    var confirm = $('<button id="confirm" class="confirm">确定</button>');

    $('.ct').append(confirm);
    confirm.on('touchstart', function() {
        $(this).addClass('touching');
    })
    confirm.on('touchend', function() {
        $(this).removeClass('touching');
    })
    confirm.on('click', function() {
        var arr_value = [];
        $('.item').each(function() {
                if ($(this).find('em').hasClass('active')) {
                    arr_value.push($(this).attr('value'));
                }
            })
            //更改member
        console.log('团队成员提交' + arr_value.join(","))
        $.ajax({
            type: "post",
            url: apppath + '/wx/group/setrelateowner.action',
            data: {
                businessId: GetQueryString('accountId'),
                belongs: 1,
                setOwnerIds: arr_value.join(",")
            },
            dataType: 'json',
            async: true,
            success: function(oData) {
                setTimeout(function() {
                    $('.ct').remove();
                    $('body').css({ 'height': '', 'overflow': '' });
                    set_title
                        ('客户信息');
                }, 2000);
                if (oData.success == true) {
                    console.log('团队成员提交成功：' + oData)
                    myDialog('更改成功');
                    get_member();
                    get_record($('#recordList > .main'), 5);
                } else {
                    myAlert('更改失败')
                }
            },
            error: function(result) {
                myAlert('网络不给力，请重新加载')
            }
        })
    })
}


function footer_phone() {
    $('footer .tel').on('click', function() {
        $(window).scrollTop(0);
        $.ajax({
            url: apppath + '/wx/contact/dolist.action',
            data: { accountId: GetQueryString('accountId'), pageNo: 0, pagesize: 30 },
            dataType: 'json',
            success: function(oData) {
                var item = oData.entity.records;
                var totalSize = oData.entity.totalSize;
                if (totalSize == 0) {
                    if ($('.no_contact').length == 0) {
                        var no_contact = $('<div class="no_contact">暂无联系人可用，请添加联系人</div>');
                        $('body').append(no_contact);
                        setTimeout(function() {
                            $('.no_contact').remove();
                        }, 2000)
                    }
                } else {
                    mobileList(totalSize, item);

                }
            }
        })
    })
}


function mobileList(index, data) {
    var shadow = $('<div class="tel_bg"></div>');
    shadow.on('click', function() {
        $('.tel_bg').remove();
        $('#mobile_list').remove();
    })
    $('body').append(shadow);
    $('.tel_bg').css('top', $(document).scrollTop());
    $('.tel_bg').fadeIn(200);
    var ul = $('<div id="mobile_list"></div>');
    for (var i = 0; i < data.length; i++) {
        console.log(data[i]);
        var mobile = data[i].mobile;
        var phone = data[i].phone;
        var contactName = data[i].contactName;
        var objectId = data[i].id;
        var post = data[i].post;
        // 
        // 
        var li = $('<div class="mobile_atom"><div class="left boxSizing"><span class="name">' + contactName + '</span>' +
            '</div><a  class="mobile boxSizing" href="tel:' + mobile + '">' + mobile + '</a><a  class="mobile boxSizing" href="tel:' + phone + '">' + phone + '</a></div>');
        li.attr('id', objectId);
        $(li).find('a').on('click', function() {
            var id = $(this).siblings().attr('id');
            //$('.tel_bg').remove();
            //$('#mobile_list').remove();
            //记录打电话动作
            location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + objectId + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=acc_info&accountId=' + GetQueryString('accountId');
        })
        ul.append(li);
    }
    var return_button = $('<li id="rtn_btn">取消</li>');
    return_button.on('click', function() {
        $('body').css({ 'overflow': '', 'height': '' });
        $('.tel_bg').remove();
        $('#mobile_list').animate({ 'bottom': -$('#mobile_list').height() }, 300);
        setTimeout(function() {
            $('#mobile_list').remove();
        }, 300);
    })
    ul.append(return_button);
    $('body').append(ul);
    $('body').css({ 'overflow': 'hidden', 'height': '100%' });
    $('#mobile_list').on('touchmove', function(e) {
        e.stopPropagation();
    })
    $('#mobile_list').css({ 'bottom': -$('#mobile_list').height() })
    $('#mobile_list').animate({ 'bottom': '0' }, 300);
}

function footer_signin() {
    $('footer > .signin').on('click', function() {
        location.href = apppath + '/wx/activityrecord/qiandao.action?' + ddcommparams + '&belongId=1&objectId=' + GetQueryString('accountId') + '&activityTypeId=' + activeRecordTypeOje["SIGN_IN"] + '&from=acc_info&accountId=' + GetQueryString('accountId');
    })
}

function footer_create_record() {
    $('footer > .record').on('click', function() {
        location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=1&objectId=' + GetQueryString('accountId') + '&activityTypeId=' + activeRecordTypeOje["RECORD"] + '&from=acc_info&accountId=' + GetQueryString('accountId');
    })
}

function build_end($parent_obj, info, name) {
    var end = $('<div id="' + name + '" class="end_info">' + info + '</div>');
    $parent_obj.append(end);
}

function scroll_load($parent_obj, func) {
    var windowHeight = document.body.clientHeight;
    $(window).off('scroll').on('scroll', function() {
        if ($('#load_more').length > 0) {
            if ($('#load_more').offset().top - $(document).scrollTop() <= windowHeight - $('#load_more').height() - 50) {
                $('#load_more').remove();
                build_end($parent_obj, '加载中…', 'loading');
                func($parent_obj, 20, 'tab');
                $(this).unbind('scroll');
                top_nav();
            }
        }
    })
}
