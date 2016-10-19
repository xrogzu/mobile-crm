/**
 * Created by dell on 2016/2/24.
 */
$(document).on('ready', function() {
    top_nav();
    nav();
    linkMore();
    more();
    get_record($('#recordList > .main'), 5);
    get_contacts($('#contactList > .main'), 5);
    get_info($('#infoList > .main'));
    //初始化
    var tab = $('<ul id="record_tab" class="tab record recordArea"><div class="main boxSizing"></div></ul>');
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
    get_member();
    footer_phone();
    footer_signin();
    footer_create_record();
    stopScroll($('.shadow'));
    stopScroll($('#saleStage'));
})

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
$(document).on('click', function(e) {
    if (!$(e.target).closest(".link_more").size()) {
        $(".options").hide();
    }
});
var accountId;
var opportunityName;
var opportunityType;
var closeDate;
var order;
var ownerId;
var money;
var dimDepart;

function more() {
    $('#saleStage').hide();
    $('.shadow').hide();
    if ($('#area').length > 0) {
        $('#area').remove();
    } else {
        $('#relevance_contact').on('click', function() {
            $('#area').remove();
            relevance_contact();
        });
        $('#set_opportunity').on('click', function() {
            $('#area').remove();
            location.href = apppath + '/wx/opportunity/add.action?' + ddcommparams + '&from=opp_info&opportunityId=' + GetQueryString('opportunityId') + '&type=set';
        });
        $('#delete_opportunity').on('click', function() {
            $('#area').remove();
            delete_dialog();
        });
    }
}

// 关联联系人
function relevance_contact() {
    $.ajax({
        url: apppath + '/wx/contact/dolist.action',
        data: {
            pageNo: 0,
            pogesize: 20,
            accountId: accountId
        },
        dataType: 'json',
        success: function(oData) {
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                console.log('关联联系人：' + oData)
                if (oData.entity.records.length == 0) {
                    var no_contact = $('<div class="no_contact">暂无联系人可关联，请添加联系人</div>');
                    $('body').append(no_contact);
                    setTimeout(function() {
                        $('.no_contact').remove();
                    }, 2000);
                } else {
                    checkTable('关联联系人');
                    var container = $('<div id="container" class="container"></div>');
                    $('.ct').addClass('staff-warp');
                    $('.ct').append(container);
                    build_member_list(container, oData, apppath + '/wx/opportunity/setcontacts.action');
                }
            }
        }
    })
}

function delete_dialog() {
    var dia = $('<div class="delShadow"><div class="dia"><p class="boxSizing">确定删除吗?</p><div class="confirm"><div class="no">取消</div><div class="yes boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({ 'top': '200px', 'left': ($(window).width() - $('.dia').width()) / 2 });
    $('.yes').on('click', function() {
        delete_opportunity();
        $('.delShadow').remove();
    })
    $('.no').on('click', function() {
        $('.delShadow').remove();
    })
}

function delete_opportunity() {
    $.ajax({
        url: apppath + '/wx/opportunity/dodel.action',
        data: { opportunityId: GetQueryString('opportunityId') },
        dataType: 'json',
        success: function(oData) {
            if (oData.success == true && oData.entity.status == 0) {
                myDialog('删除成功');
                time_out_location(apppath + '/wx/opportunity/list.action?' + ddcommparams);
            } else {
                myAlert('删除失败');
            }
        }
    })
}

function saleStagePush() {
    $('#opportunityInfo .salesStageSelect').on('click', function() {
        $(window).scrollTop(0);
        $('.shadow').fadeIn();
        $('#saleStage').fadeIn();
        $('#saleStage #exitSaleStage').on('click', function() {
            $('#saleStage').fadeOut();
            $('.shadow').fadeOut();
        })
        if ($('#saleStage .bottom li').length == 0) {
            $.ajax({
                url: apppath + '/wx/opportunity/getSaleStage.action',
                dataType: 'json',
                success: function(oData) {
                    console.log(oData);
                    if (oData.success == true) {
                        $('#saleStage .bottom').children().remove();
                        for (var i = 0, len = oData.entity.length; i < len; i++) {
                            var item = $('<li><em></em><span class="label">' + oData.entity[i].label + '</span>' +
                                '<span class="percent">' + oData.entity[i].percent + '%</span></li>');
                            $('#saleStage .bottom').append(item);
                            item.attr('value', oData.entity[i].value);
                            item.on('click', function() {
                                $(this).addClass('active');
                                $(this).siblings().removeClass('active');
                                var saleStageId = $(this).attr('value');
                                change_stage(saleStageId);
                            })
                        }
                        $('#saleStage .bottom li').eq(order - 1).addClass('active');
                    }
                }
            })
        }
    })
}

function change_stage(saleStageId) {
    var saleStageId = Number(saleStageId);
    var saveData = {};
    saveData.id = Number(GetQueryString('opportunityId'));
    saveData.saleStageId = saleStageId;

    $.ajax({
        url: apppath + '/wx/opportunity/doupdate.action',
        data: {
            opportunity: JSON.stringify(saveData)
        },
        beforeSend: function() {
            loader();
        },
        dataType: 'json',
        success: function(oData) {
            $('.loaderArea').remove();
            if (oData.success == true) {
                myDialog('保存成功');
                get_info($(''));
                setTimeout(function() {
                    $('#saleStage').fadeOut();
                    $('.shadow').fadeOut();
                }, 2000);
            } else {
                myDialog('保存失败', 'without');
                $('#saleStage').fadeOut();
                $('.shadow').fadeOut();
            }
        },
        error: function() {
            $('.loaderArea').remove();
            myAlert('请检查网络');
        }
    })
}

function closeStage() {
    if ($('#saleStage').css('display') == 'block') {
        $('#saleStage').fadeOut();
        $('.shadow').fadeOut();
    } else if ($('.ct').length > 0) {
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
            location.href = apppath + '/wx/opportunity/list.action?' + ddcommparams;
        }
    }
}

function ctRemove() {
    $('body').css('height', '');
    $('body').css('overflow', '');
    ceilingJudge();
    $('.ct').remove();
    set_title('商机详情');
    set_right('更多', more);
}

function drawCircle(percent) {
    var r = $('#picture').width() / 2;
    $('#circle').attr({ 'height': r * 2, 'width': r * 2 });
    var c = document.getElementById('circle').getContext('2d');
    c.beginPath();
    c.arc(r, r, r - 14, Math.PI * 0, Math.PI * 2, false);
    c.lineWidth = 25;
    if (percent == 0) {
        c.strokeStyle = '#ff5747';
    } else {
        c.strokeStyle = '#e1e8ed';
    }
    c.stroke();
    c.closePath();
    var percent = 1.5 + percent / 100 * 2;
    c.beginPath();
    c.arc(r, r, r - 14, Math.PI * 1.5, Math.PI * percent, false);
    c.lineWidth = 25;
    c.strokeStyle = '#22d2a6';
    c.stroke();
    c.closePath();
}
var nav_top = $('#topNav').offset().top;
var nav_height = $('#topNav').height();

function top_nav() {
    $(window).on('scroll', function() {
        ceilingJudge();
    })
}

function ceilingJudge() {
    if ($('body').scrollTop() >= nav_top + 120) {
        $('#opportunityInfo').css('marginBottom', nav_height + 20 + 'px');
        $('#topNav').css({ 'position': 'fixed', 'top': 0 });
    } else {
        $('#opportunityInfo').css('marginBottom', 0);
        $('#topNav').css({ 'position': 'static' });
    }
}

function nav() {
    $('#nav li').each(function() {
        $(this).on('click', function() {
            $(this).addClass('active');
            $(this).siblings().removeClass('active');
            tab($(this).attr('href'));
        })
    })
}
var reminderHtml = '<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>';

function tab(type) {
    $('html, body').animate({ scrollTop: 0 }, 300);
    $('#opportunityInfo').css('marginBottom', 0);
    $('#topNav').css({ 'position': 'static' });
    $('.content').fadeOut(300);
    $('#opportunityInfo').css('marginBottom', 0);
    $('#topNav').css({ 'position': 'static' });
    $('.content').fadeOut(300);

    if (type == '联系人') {
        if ($('#contact_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="contact_tab" class="tab contact contactArea"><div class="main boxSizing"></div></ul>');
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
            var tab = $('<ul id="record_tab" class="tab record recordArea"><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            recordPageNo = 0;
            get_record($('#record_tab > .main'), 20, 'tab');
            $('#record_tab').fadeIn(300);
        }
    } else if (type == '详情') {
        if ($('#info_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="info_tab" class="tab details infoArea"><div class="staff boxSizing"></div><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            get_info($('#info_tab > .main'));
            $('.staff').append($('<div class="owner boxSizing"> ' +
                '<span class="label">负责人:</span> ' +
                '<span class="value"></span> </div> ' +
                '<div class="member boxSizing"> ' +
                '<span class="label">团队成员:</span> ' +
                '<span class="value"></span> </div> </div>'));
            if (xsyUser.id == ownerId) {
                change_owner();
                change_member();
            }
            $('#info_tab').fadeIn(300);
        }
    } else if (type == '全部') {
        $('.tab').remove();
        $('.content').fadeIn(300);
    }
}
//====================================活动记录=====================================
var recordPageNo = 0;

function get_record($parent_obj, pagesize, type) {
    $parent_obj.children().remove();
    $.ajax({
        url: apppath + '/wx/activityrecord/dolist.action?d=' + (+new Date),
        data: {
            belongId: 3,
            objectId: GetQueryString('opportunityId'),
            pagesize: pagesize,
            pageNo: recordPageNo
        },
        dataType: 'json',
        success: function(oData) {
            // $('.load-shadow').hide();
            //测试校验
            console.log('跟进记录：')
            console.log(oData)
            if (oData.success == true) {
                if (oData.entity.records.length == 0) {
                    $parent_obj.css('border', 0);
                    console.log('该对象暂无跟进记录');
                    $parent_obj.append(reminderHtml);
                } else {
                    if (oData.entity.totalSize > 5) {
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
                    var totalSize = oData.entity.totalSize;
                    build_record_list($parent_obj, oData);
                    if ($parent_obj.find('li').length < totalSize && type == 'tab') {
                        $('#loading').remove();
                        build_end($parent_obj, '加载更多', 'load_more');
                        recordPageNo++;
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
    var item = data.entity.records;
    for (var i = 0, len = item.length; i < len; i++) {
        //console.log(item[i])
        var userName = item[i].user.userName;
        var typeName = item[i].typeName;
        var objectName = item[i].objectName;
        var belongId = item[i].belongId;
        var objectId = item[i].objectId;
        var startTime = item[i].startTime.substr(-5);
        var startDate = item[i].startTime.substr(0, 11);
        var type = item[i].type;
        if (i == 0 || item[i].startTime.substr(0, 11) != item[i - 1].startTime.substr(0, 11)) {
            var day = $('<div class="day"><em></em><p class="startDate">' + startDate + '</p><div class="ban"></div></div>');
            $parent_obj.append(day);
        }
        var oList = $('<li class="boxSizing"><div class="info"><span class="userName">' + userName + '</span><span class="typeName">' + typeName + ':</span><span class="objectName">' + objectName + '</span><span class="startTime">' + startTime + '</span></div></li>');
        oList.find('.objectName').attr({ 'belongId': belongId, 'objectId': objectId });
        if (objectId != GetQueryString('opportunityId')) {
            oList.find('.objectName').on('click', function() {
                var url;
                if ($(this).attr('belongId') == 1) {
                    url = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + $(this).attr('objectId') + '&from=con_info&ropportunityId=' + GetQueryString('opportunityId');
                } else if ($(this).attr('belongId') == 2) {
                    url = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + $(this).attr('objectId') + '&from=con_info&ropportunityId=' + GetQueryString('opportunityId');
                } else if ($(this).attr('belongId') == 3) {
                    url = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('objectId') + '&from=con_info&ropportunityId=' + GetQueryString('opportunityId');
                }
                location.href = url;
            });
        }
        if (type == activeRecordTypeOje["DO_DING"]) {
            oList.addClass('ding_bg');
        } else if (type == activeRecordTypeOje["TEL"]) {
            oList.addClass('phone_bg');
        } else if (type == activeRecordTypeOje["SIGN_IN"]) {
            oList.addClass('sign_in_bg');
        } else if (type == activeRecordTypeOje["RECORD"]) {
            oList.addClass('record_bg');
        }
        if (item[i].content) {
            var contentText = item[i].content;
            var content_span = $('<span class="content_span">' + contentText + '</span>');
            var content = $('<div class="content"><em class="triangle"></em></div>');
            content.append(content_span);
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
        if (item[i].location) {
            var locat = $('<div class="location">' + item[i].location + '</div>');
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
//====================================联系人信息=====================================
var contactIdArr = [];
var contactPageNo = 0;

function get_contacts($parent_obj, pagesize, type) {
    contactIdArr = [];
    $.ajax({
        url: apppath + '/wx/opportunity/contacts.action?d=' + (+new Date),
        data: {
            opportunityId: GetQueryString('opportunityId')
        },
        dataType: 'json',
        success: function(oData) {
            //测试校验
            console.log('联系人：')
            console.log(oData)
            if (oData.success == true) {
                var totalSize = oData.entity.totalSize;
                $('#contactList > .top > .title').text('联系人(' + totalSize + ')');
                $('#nav > .contact').text('联系人(' + totalSize + ')');
                if (totalSize == 0) {
                    $parent_obj.css('border', 0);
                    $parent_obj.append(reminderHtml);
                    console.log('该对象暂无联系人信息');
                } else {
                    if (totalSize > 5) {
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

                    for (var i = 0, len = totalSize; i < len; i++) {
                        contactIdArr.push(oData.entity.contacts[i].contactId);
                    }
                    build_contact_list($parent_obj, oData, pagesize);
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

function build_contact_list($parent_obj, data, pagesize) {
    var item = data.entity.contacts;
    var len = item.length;
    if (pagesize == 5) {
        if (len >= 5) {
            len = 5;
        }
    }
    for (var i = 0; i < len; i++) {
        var name = item[i].contactName;
        var post = item[i].post;
        var mobile = item[i].mobile;
        var phone = item[i].phone;
        var id = item[i].contactId;
        var oList = $('<li class="boxSizing" ><div class="contact_info_list" mobile="' + mobile + '" phone="' + phone + '"><span class="name">' + name + '</span><span class="post">' + post + '</span></div><a class="tel"></a></li>');

        oList.find('.contact_info_list').attr('value', id);
        oList.find('.contact_info_list').on('click', function() {
            location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + $(this).attr('value') + '&from=opp_info&ropportunityId=' + GetQueryString('opportunityId');
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
        var li = $('<a href="tel:' + obj + '" class="tel_atom"><span class="label">' + label_name + '</span>' +
            '<span class="value">' + obj + '</span></a>');
        li.on('click', function() {
            // e.stopPropagation();
            //$('.tel_bg').remove();
            //$('#tel_list').remove();
            //console.log('记录打电话动作');
            //alert(1)
            location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + id + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=opp_info&opportunityId=' + GetQueryString('opportunityId');

        })
        ul.append(li);
    }
}
//============================详情==============================
var dataPermission;
var ownerName;
var infoData; //修改时需要的参数
function get_info($parent_obj) {
    var opportunityId = GetQueryString('opportunityId');
    $.ajax({
        url: apppath + '/wx/opportunity/getDetail.action',
        data: {
            opportunityId: opportunityId,
            scene: 'DETAIL'
        },
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            console.log('详情' + oData)
            if (oData.success == true) {
                dataPermission = oData.entity.expandPro.dataPermission;
                infoData = oData.entity.data;
                build_info($parent_obj, oData);
                opportunityName = oData.entity.data.opportunityName;
                ownerName = oData.entity.expandPro.ownerIdText;
                accountId = oData.entity.data.accountId;
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
                if (dataPermission.transfer) {
                    change_owner();
                } else {
                    $('.staff>.owner').css('background', 'none');
                }
                if (dataPermission.update) {
                    change_member();
                    saleStagePush();
                } else {
                    $('.staff>.member').css('background', 'none');
                    $('#opportunityInfo .salesStageSelect').addClass('unable');
                }
                if(dataPermission.delete == false){
                    $('#set_opportunity').parent().hide();
                    $('#delete_opportunity').parent().hide();
                }
                var saleStageText = oData.entity.expandPro.saleStageIdText;
                money = oData.entity.data.money;
                var percent = oData.entity.expandPro.saleStageIdPercentage;
                ownerName = oData.entity.expandPro.ownerText;
                order = oData.entity.expandPro.saleStageIdOrder;
                var orderArr = ["", "一", "二", "三", "四", "五", "六", "七", "八", "九"];
                $('#text .opportunityName').html(opportunityName);
                $('#text .saleStage').html('第' + orderArr[order] + '阶段：' + saleStageText);
                $('#text .money').html(money + '元');
                $('.owner .value').text(ownerName);
                get_member();
                drawCircle(percent);
                $('#picture .percent').html(percent + '%');
                if (percent == 0) {
                    $('#progress .title').hide();
                    $('#progress .percent').hide();
                    var looseProgress = $('<p class="looseProgress">输单</p>');
                    // $('#progress').append(looseProgress);
                    if ($('#progress').find('.looseProgress').length > 0) {
                        return;
                    } else {
                        $('#progress').append(looseProgress);
                    }
                } else {
                    $('#progress .title').show();
                    $('#progress .percent').show();
                    $('#progress .looseProgress').remove();
                }
                build_info($parent_obj, oData);
            } else {
                if (oData.errorCode == '300032') {
                    if (GetQueryString('from') == 'opp_info') {
                        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId') + '&err=opp_deleted';
                    } else if (GetQueryString('from') == 'con_info') {
                        location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId') + '&err=opp_deleted';
                    } else if (GetQueryString('from') == 'acc_info') {
                        location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId') + '&err=opp_deleted';
                    } else if (GetQueryString('from') == 'index') {
                        if (GetQueryString('page')) {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page') + '&err=opp_deleted';
                        } else {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams;
                        }
                    }
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000004') {
                    if (GetQueryString('from') == 'opp_info') {
                        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId') + '&err=opp_noright';
                    } else if (GetQueryString('from') == 'con_info') {
                        location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId') + '&err=opp_noright';
                    } else if (GetQueryString('from') == 'acc_info') {
                        location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId') + '&err=opp_noright';
                    } else if (GetQueryString('from') == 'index') {
                        if (GetQueryString('page')) {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page') + '&err=opp_noright';
                        } else {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&err=opp_noright';
                        }
                    }
                } else {
                    myAlert('网络不给力，请重新加载')
                }
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载');
        }
    })
}

function build_info($parent_obj, data) {
    console.log('详情')
    console.log(data)
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
        } else if (structure[i].propertyName == "campaignId") {
            return;
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
                    info_atom_build($parent_obj, structure[i].label, value, structure[i].propertyName, structure[i].type);
                }
            }
        }
    }
    // if (value != '') {
    //     var oAtom = $('<li class="atom commentAtom boxSizing"style="border-top: 1px solid #e3e3e5"><span class="label">备注:</span><span class="value"></span></li>');
    //     var contentText = value;
    //     var content_span = $('<span class="content_span">' + contentText + '</span>');
    //     var content = $('<div class="content"></div>');
    //     content.append(content_span);
    //     if (contentText.length > 32) {
    //         var shortContentText = contentText.substr(0, 30) + '...';
    //         var short_content_span = $('<span class="short_content_span">' + shortContentText + '</span>');
    //         content.append(short_content_span);
    //         content_span.css('display', 'none');
    //         var index = 0;
    //         var more_btn = $('<em class="more"></em>');
    //         more_btn.on('click', function() {
    //             index++;
    //             $(this).parent().find('.short_content_span').slideToggle();
    //             $(this).parent().find('.content_span').slideToggle();
    //             $(this).css("transform", "rotate(" + (90 + 180 * index) + "deg)");

    //         })
    //         content.append(more_btn);
    //     }
    //     oAtom.find('.value').append(content);
    //     $('.staff .commentAtom').remove();
    //     $('.staff').append(oAtom);
    // }
}

function info_atom_build($parent_obj, tagName, text, data, type) {
    var des = '<span style="color:#8fa1b2">未填写</span>';
    if (text != '') {
        if (data == 'accountId') {
            console.log(accountId);
            var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue">' + text + '</span></li>');
            oAtom.find('.value').on('click', function() {
                location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + accountId + '&from=opp_info&ropportunityId=' + GetQueryString('opportunityId');
            })
        } else if (type == "ITEM_TYPE_LINE") {
            var oAtom = $('<li class="atom boxSizing line-atom" id=' + data + '><span class="line-title">' + tagName + '</span></a></li>');
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
            if (data == "winRate") {
                var num = 0;
                if (text == des) {
                    var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + ':</span><span class="value">' + num + '%</span></li>');
                } else{
                    var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + ':</span><span class="value">' + text + '%</span></li>');

                }
            } else {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + ':</span><span class="value">' + text + '</span></li>');
            }
        }
        $parent_obj.append(oAtom);
    }
}
//获取团队成员信息
function get_member() {
    $.ajax({
        type: "post",
        url: apppath + '/wx/group/relateowner.action',
        data: {
            businessId: GetQueryString('opportunityId'),
            belongs: 3
        },
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            console.log('团队成员：' + oData);
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载')
            } else {
                var arr = [];
                for (var i = 0, len = oData.entity.users.length; i < len; i++) {
                    arr.push(oData.entity.users[i].name)
                }
                $('.staff > .member > .value').text(arr.join(' , '));
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
                        var no_contact = $('<div class="no_contact">您的公司下暂时没有其他员工</div>');
                        $('body').append(no_contact);
                        setTimeout(function() {
                            $('.no_contact').remove();
                        }, 2000);
                    } else {
                        checkTable('转移给他人');
                        var container = $('<div id="container" class="container ownerContainer"></div>');
                        $('.ct').append(container);
                        build_owner_list(container, oData);
                    }
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    })
}

function build_owner_list(container, data) {
    //负责人模式（单选）
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        if (data.entity.records[i].name == $('#infoList .owner > .value').text()) {
            var con = $('<div class="item"><em class="radio active"><i></i></em><span id=' + data.entity.records[i].id + '>' + data.entity.records[i].name + '</span></div>');
        } else {
            var con = $('<div class="item"><em class="radio"><i></i></em><span id=' + data.entity.records[i].id + '>' + data.entity.records[i].name + '</span></div>');
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
            var value = data.entity.records[$(this).index()].id;
            var label = data.entity.records[$(this).index()].name;
            doChangeOwner(label, sub);

            function sub() {
                $.ajax({
                    url: apppath + '/wx/opportunity/trans.action',
                    data: {
                        opportunityId: GetQueryString('opportunityId'),
                        userId: value
                    },
                    beforeSend: function() {
                        loader();
                    },
                    dataType: 'json',
                    success: function(oData) {
                        $('.loaderArea').remove();
                        setTimeout(function() {
                            $('.ct').remove();
                            set_title('商机详情');
                            $('body').css({ 'overflow': '', 'height': '' });
                            $('.common').css('paddingBottom', '0px');
                            set_right('更多', more);
                        }, 2000);
                        if (oData.success == true) {
                            $('.owner > .value').text(label);
                            get_record($('#recordList > .main'), 5);
                            myDialog('负责人更改成功');
                        } else {
                            myAlert('负责人更改失败');
                        }
                    },
                    error: function() {
                        $('.loaderArea').remove();
                        myAlert('网络不给力，请重新加载')
                    }
                })
            }
        })
    })
}
//修改团队成员
function change_member() {
    $('.member').on('click', function() {
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
                        var no_contact = $('<div class="no_contact">您的公司下暂时没有其他员工</div>');
                        $('body').append(no_contact);
                        setTimeout(function() {
                            $('.no_contact').remove();
                        }, 2000);
                    } else {
                        checkTable('修改团队成员');
                        var container = $('<div id="container" class="container"></div>');
                        $('.ct').addClass('staff-warp');
                        $('.ct').append(container);
                        build_member_list(container, oData, apppath + '/wx/group/setrelateowner.action');
                    }
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    });
}

function build_member_list(container, data, memberUrl) {
    //团队成员模式（多选）
    var arr_member = $('#infoList .member > .value').text().split(',');
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var label;
        var value;
        if (memberUrl == apppath + '/wx/group/setrelateowner.action') {
            label = data.entity.records[i].name;
            value = data.entity.records[i].id;
        } else if (memberUrl == apppath + '/wx/opportunity/setcontacts.action') {
            label = data.entity.records[i].contactName;
            value = data.entity.records[i].id;
        }
        if (label != ownerName) {
            var con = $('<div class="item boxSizing"><em class="checkbox"></em><span>' + label + '</span></div>');
            con.attr('value', value);
            container.append(con);
        }
    }
    for (var i = 0, len = contactIdArr.length; i < len; i++) {
        for (var j = 0; j < data.entity.records.length; j++) {
            if (data.entity.records[j].id == contactIdArr[i]) {
                $('.ct .item').eq(j).find('em').addClass('active');
            }
        }
    }
    for (var j = 0; j < arr_member.length; j++) {
        for (var i = 0, len = data.entity.records.length; i < len; i++) {
            var label;
            if (memberUrl == apppath + '/wx/group/setrelateowner.action') {
                label = data.entity.records[i].label;
            } else if (memberUrl == apppath + '/wx/opportunity/setcontacts.action') {
                label = data.entity.records[i].contactName;
            }
            console.log(arr_member[j] + '+' + label)
            if ($.trim(arr_member[j]) == $('.ct .item').eq(i).find('span').text()) {
                $('.ct .item').eq(i).find('em').addClass('active');
            }
        }
    }
    checkbox_click(data);
    create_confirm(data, memberUrl);
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

function create_confirm(data, url) {
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
        chooseData(url, arr_value);
        console.log(relavanceContactData)
        $.ajax({
            type: "post",
            url: url,
            data: relavanceContactData,
            async: true,
            beforeSend: function() {
                loader();
            },
            dataType: 'json',
            success: function(oData) {
                $('.loaderArea').remove();
                setTimeout(function() {
                    $('.ct').remove();
                    $('body').css({ 'height': '', 'overflow': '' });
                    set_title('商机详情');
                    set_right('更多', more);
                }, 2000);
                console.log('关联联系人返回：' + oData)
                if (oData.success == true) {
                    myDialog('更改成功');
                    window.location.reload();
                    if (url == apppath + '/wx/group/setrelateowner.action') {
                        get_record($('#recordList > .main'), 5);
                        get_member();
                    } else if (url == apppath + '/wx/opportunity/setcontacts.action') {
                        $('#contactList > .main').children().remove();
                        get_contacts($('#contactList > .main'), 5);
                    }
                } else {
                    myAlert('更改失败');
                }
            },
            error: function(result) {
                myAlert('网络不给力，请重新加载')
                $('.loaderArea').remove();
            }
        })
    })
}
var relavanceContactData;

function chooseData(url, arr_value) {
    if (url == apppath + '/wx/group/setrelateowner.action') {
        relavanceContactData = {
            businessId: Number(GetQueryString('opportunityId')),
            belongs: 3,
            setOwnerIds: arr_value.join(",")
        };
    } else if (url == apppath + '/wx/opportunity/setcontacts.action') {
        relavanceContactData = {
            opportunityId: Number(GetQueryString('opportunityId')),
            contactIds: arr_value.join(",")
        }
    }
}

function footer_phone() {
    $('footer .tel').on('click', function() {
        $(window).scrollTop(0);
        $.ajax({
            url: apppath + '/wx/opportunity/contacts.action',
            data: { opportunityId: GetQueryString('opportunityId') },
            dataType: 'json',
            success: function(oData) {
                var item = oData.entity.contacts;
                var totalSize = oData.entity.totalSize;
                if (totalSize == 0) {
                    var no_contact = $('<div class="no_contact">暂无联系人可用，请添加联系人</div>');
                    $('body').append(no_contact);
                    setTimeout(function() {
                        $('.no_contact').remove();
                    }, 2000)
                } else {
                    mobileList(totalSize, item);
                    // var shadow = $('<div class="tel_bg"></div>');
                    // shadow.on('click', function() {
                    //     $('.tel_bg').remove();
                    //     $('#mobile_list').remove();
                    // })
                    // $('body').append(shadow);
                    // $('.tel_bg').css('top', $(document).scrollTop());
                    // $('.tel_bg').fadeIn(200);
                    // var ul = $('<div id="mobile_list"></div>');
                    // for (var i = 0, len = totalSize; i < len; i++) {
                    //     console.log(item[i])
                    //     var mobile = item[i].mobile;
                    //     var contactName = item[i].contactName;
                    //     var contactId = item[i].id;
                    //     var post = item[i].post;
                    //     var li = $('<a href="tel:' + mobile + '" class="mobile_atom"><div class="left boxSizing"><span class="name">' + contactName + '</span>' +
                    //         '<span class="post">' + post + '</span></div><span class="mobile boxSizing">' + mobile + '</span></a>');
                    //     li.attr('id', contactId);
                    //     li.on('click', function() {
                    //         $('.tel_bg').remove();
                    //         $('#mobile_list').remove();
                    //         //记录打电话动作
                    //         location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=3&objectId=' + GetQueryString('opportunityId') + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=opp_info&opportunityId=' + GetQueryString('opportunityId');
                    //     })
                    //     ul.append(li);
                    // }
                    // var return_button = $('<li id="rtn_btn">取消</li>');
                    // return_button.on('click', function() {
                    //     $('body').css({ 'overflow': '', 'height': '' });
                    //     $('.tel_bg').remove();
                    //     $('#mobile_list').animate({ 'bottom': -$('#mobile_list').height() }, 300);
                    //     setTimeout(function() {
                    //         $('#mobile_list').remove();
                    //     }, 300);
                    // })
                    // ul.append(return_button);
                    // $('body').append(ul);
                    // $('body').css({ 'overflow': 'hidden', 'height': '100%' });
                    // $('#mobile_list').on('touchmove', function(e) {
                    //     e.stopPropagation();
                    // })
                    // $('#mobile_list').css({ 'bottom': -$('#mobile_list').height() })
                    // $('#mobile_list').animate({ 'bottom': '0' }, 300);
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
        var contactId = data[i].contactId;
        var post = data[i].post;
        var li = $('<div class="mobile_atom"><div class="left boxSizing"><span class="name">' + contactName + '</span>' +
            '</div><a href="tel:' + mobile + '"  class="mobile boxSizing">' + mobile + '</a><a  class="mobile boxSizing" href="tel:' + phone + '">' + phone + '</a></div>');
        li.attr('id', contactId);
            li.find('a').on('click', function() {
                //$('.tel_bg').remove();
                //$('#mobile_list').remove();
                //记录打电话动作
                var id = $(this).parent().attr('id');
                location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + id + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=opp_info&opportunityId=' + GetQueryString('opportunityId');
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
        location.href = apppath + '/wx/activityrecord/qiandao.action?' + ddcommparams + '&belongId=3&objectId=' + GetQueryString('opportunityId') + '&activityTypeId=' + activeRecordTypeOje["SIGN_IN"] + '&from=opp_info&opportunityId=' + GetQueryString('opportunityId');
    })
}

function footer_create_record() {
    $('footer > .record').on('click', function() {
        location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=3&objectId=' + GetQueryString('opportunityId') + '&activityTypeId=' + activeRecordTypeOje["RECORD"] + '&from=opp_info&opportunityId=' + GetQueryString('opportunityId');
    })
}

function build_end($parent_obj, info, name) {
    var end = $('<div id="' + name + '" class="end_info">' + info + '</div>');
    $parent_obj.append(end);
}

function scroll_load($parent_obj, func) {
    var windowHeight = document.body.clientHeight;
    $(window).on('scroll', function() {
        if ($('#load_more').length > 0) {
            if ($('#load_more').offset().top - $(document).scrollTop() <= windowHeight - $('#load_more').height() - 50) {
                $('#load_more').remove();
                build_end($parent_obj, '加载中…', 'loading');
                func($parent_obj, 20);
                $(this).unon('scroll');
                top_nav();
            }
        }
    })
}
