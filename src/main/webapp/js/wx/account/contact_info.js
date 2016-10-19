/**
 * Created by dell on 2016/1/25.
 */
$(function() {
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
    change_owner();
    linkMore();
    more();
    set_cir_color($('header .head_pic'));
    top_nav();
    nav();
    get_record($('#recordList > .main'), 5)
    get_opportunity($('#opportunityList > .main'), 3);
    get_info($('#infoList > .main'));
    footer_phone();
    footer_signin();
    footer_create_record();
    var tab = $('<ul id="info_tab" class="tab details"><div class="main boxSizing"></div></ul>');
    $('body').append(tab);
    get_info($('#info_tab > .main'));
    $('#info_tab').fadeIn(300);
})
var reminderHtml = '<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>';

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

function toContactList() {
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
            location.href = apppath + '/wx/contact/list.action?' + ddcommparams;
        }
    }
}

function ctRemove() {
    $('body').css('height', '');
    $('body').css('overflow', '');
    top_nav();
    $('.ct').remove();
    set_title('客户信息');
    set_right('更多', more);
}

function more() {
    if ($('#area').length > 0) {
        $('#area').remove();
    } else {
        $('#new_business').on('click', function() {
            console.log('跳转到新建商机页面');
            location.href = apppath + '/wx/opportunity/add.action?' + ddcommparams + '&from=con_info&type=add&accountId=' + accountId + '&contactId=' + GetQueryString('contactId');
        });
        $('#set_contact').on('click', function() {
            location.href = apppath + '/wx/contact/add.action?' + ddcommparams + '&type=set&from=con_info&contactId=' + GetQueryString('contactId');
        });
        $('#delete_contact').on('click', function() {
            delete_dialog();
        });
    }
}

function delete_dialog() {
    var dia = $('<div class="delShadow"><div class="dia"><p class="boxSizing">确定删除吗?</p><div class="confirm"><div class="no">取消</div><div class="yes boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({ 'top': '200px', 'left': ($(window).width() - $('.dia').width()) / 2 });
    $('.yes').on('click', function() {
        $('.delShadow').remove();
        delete_contact();
        $('.delShadow').remove();
    })
    $('.no').on('click', function() {
        $('.delShadow').remove();
    })
}

function delete_contact() {
    $.ajax({
        url: apppath + '/wx/contact/dodel.action',
        data: { contactId: GetQueryString('contactId') },
        async: false,
        dataType: 'json',
        success: function(oData) {
            if (oData.success) {
                $('.shadow').remove();
                myDialog('删除成功');
                location.href = apppath + '/wx/contact/list.action?' + ddcommparams;
            } else {
                myAlert('删除失败')
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
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
        $('.common').css('marginBottom', nav_height + 20 + 'px');
        $('#topNav').css({ 'position': 'fixed', 'top': 0 });
    } else {
        $('.common').css('marginBottom', 0);
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

function tab(type) {
    $('html, body').animate({ scrollTop: 0 }, 300);
    $('#topNav').css({ 'position': '', 'top': '', 'bottom': '' });
    $('.common').css({ 'paddingBottom': '', 'position': 'relative' });
    $('.info_tab').fadeOut(300);
    if (type == '记录') {
        if ($('#record_tab').length == 0) {
            if ($('.tab').length > 0) {
                $('.tab').remove();
            }
            var tab = $('<ul id="record_tab" class="tab record"><div class="main boxSizing"></div></ul>');
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
            var tab = $('<ul id="opportunity_tab" class="tab opportunity"><div class="main boxSizing"></div></ul>');
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
            var tab = $('<ul id="info_tab" class="tab details"><div class="main boxSizing"></div></ul>');
            $('body').append(tab);
            get_info($('#info_tab > .main'));
            // $('#info_tab').append($('<div class="staff boxSizing"> ' +
            //     '<div class="owner boxSizing"> ' +
            //     '<span class="label">负责人:</span> ' +
            //     '<span class="value"></span> </div></div>'));
            $('#info_tab').fadeIn(300);
        }
    }
}
//====================================录入客户下活动记录=====================================
var recordPageNo = 0;

function get_record($parent_obj, pagesize, type) {
    $.ajax({
        url: apppath + '/wx/activityrecord/dolist.action',
        data: {
            belongId: 2,
            objectId: GetQueryString('contactId'),
            pagesize: pagesize,
            pageNo: recordPageNo
        },
        dataType: 'json',
        success: function(oData) {
            // $('.load-shadow').hide();
            //测试校验
            console.log('跟进记录：' + oData)
            if (oData.success == true) {
                if (oData.entity.records.length == 0) {
                    $parent_obj.css('border', 0);
                    $parent_obj.append(reminderHtml);
                    console.log('该对象暂无跟进记录');
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
            myAlert('网络不给力，请重新加载')
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
        var type = item[i].recordType.type;
        if (i == 0 || item[i].startTime.substr(0, 11) != item[i - 1].startTime.substr(0, 11)) {
            var day = $('<div class="day"><em></em><p class="startDate">' + startDate + '</p><div class="ban"></div></div>');
            $parent_obj.append(day);
        }
        var oList = $('<li class="boxSizing"><div class="info"><span class="userName">' + userName + '</span><span class="typeName">' + typeName + ':</span><span class="objectName">' + objectName + '</span><span class="startTime">' + startTime + '</span></div></li>');
        oList.find('.objectName').attr({ 'belongId': belongId, 'objectId': objectId });
        if (objectId != GetQueryString('contactId')) {
            oList.find('.objectName').on('click', function() {
                var url;
                if ($(this).attr('belongId') == 1) {
                    url = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + $(this).attr('objectId') + '&from=con_info&rcontactId=' + GetQueryString('contactId');
                } else if ($(this).attr('belongId') == 2) {
                    url = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + $(this).attr('objectId') + '&from=con_info&rcontactId=' + GetQueryString('contactId');
                } else if ($(this).attr('belongId') == 3) {
                    url = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('objectId') + '&from=con_info&rcontactId=' + GetQueryString('contactId');
                }
                location.href = url;
            });
        }
        //DO_DING(1,"钉一下"),DO_PHONE(108184,"打电话"),DO_VISIT(108185,"拜访签到"),DO_RECORD(-11,"活动记录"),DO_TASK(-10,"做任务"),DO_SMS(-12,"发短信");
        if (type == "TEL") {
            oList.addClass('phone_bg');
        } else if (type == "SIGN_IN") {
            oList.addClass('sign_in_bg');
        } else if (type == "RECORD") {
            oList.addClass('record_bg');
        }
        //if(item[i].)位置显示
        if (item[i].content) {
            var contentText = item[i].content;
            var content_span = $('<span class="content_span">' + contentText + '</span>');
            var content = $('<div class="content"></div>');
            var triangle = $('<em class="triangle"></em>')
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
        if (item[i].location) {
            var locat = $('<div class="location">' + item[i].location + '</div>');
            oList.append(locat);
        }
        var bottom_border = $('<div class="bottom_border"></div>');
        oList.append(bottom_border);
        $parent_obj.append(oList);
    }
}
//==================================录入商机==================================================
var accountId;
var opportunityPageNo = 0;

function get_opportunity($parent_obj, pagesize, type) {
    var timer = setInterval(function() {
        if (accountId != null) {
            $.ajax({
                url: apppath + '/wx/opportunity/dolist.action',
                data: {
                    accountId: accountId,
                    pageNo: opportunityPageNo,
                    pagesize: pagesize
                },
                dataType: 'json',
                success: function(oData) {
                    // $('.load-shadow').hide();
                    console.log('商机：' + oData)
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
                            if ($('#opportunity_tab > .top').length > 0) {
                                $('#opportunity_tab > .top').text('商机(' + totalSize + ')');
                            }
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
                    myAlert('网络不给力，请重新加载')
                        //var s = [];
                        //for(key in err){
                        //    s.push(key +":"+err[key ]);
                        //}
                        //myDialog(s.join(","));

                }
            })
            clearInterval(timer);
        }
    }, 30)
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
            location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('value') + '&from=con_info&rcontactId=' + GetQueryString('contactId');
        })
        $parent_obj.append(li);
    }
}

//==================================录入联系人详细信息(包括顶部联系人名)===========================
function icon_word(word) {
    //不是汉字，显示前两个字符
    if (!(/^[\u4e00-\u9fa5]+$/).test(word.substr(0, 1))) {
        return word.substr(0, 2);
    }
    //汉字显示后两字符
    else {
        return word.substr(-2);
    }
}
var ownerId;
var dataPermission;
var ownerName;

function get_info($parent_obj) {
    $.ajax({
        url: apppath + '/wx/contact/getDetail.action',
        data: {
            contactId: GetQueryString('contactId'),
            scene: 'DETAIL'
        },
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            console.log(oData);
            if (oData.success == true) {
                dataPermission = oData.entity.expandPro.dataPermission;
                build_info($parent_obj, oData);
                ownerName = oData.entity.data.contactName;
                accountName = oData.entity.expandPro.accountIdText;
                post = oData.entity.data.post;
                $('.common .head_pic').html(icon_word(ownerName));
                $('.common .contactName').html(ownerName);
                $('.common .accountName').html(accountName);
                $('.common .post').text(post);
                accountId = oData.entity.data.accountId;
                if (dataPermission.transfer) {
                    change_owner();
                } else {
                    $('.owner').css('background', 'none');
                }
                if (dataPermission.update) {
                    // change_member();
                } else {
                    $('.member').css('background', 'none');
                }
                if(dataPermission.delete == "false"){
                    $('#set_contact').parent().hide();
                    $('#delete_contact').parent().hide();
                }
                // get_member();
                // $('.common .title').text(oData.entity.data.accountName);
                //跳转到新建联系人时需要的客户名全局变量
                accountName = oData.entity.data.accountName;
                if (oData.entity.data.phone != null) {
                    $('footer > .tel').attr('href', 'tel:' + oData.entity.data.phone);
                } else {
                    $('footer > .tel').attr('href', '');
                }
            } else {
                if (oData.errorCode == '300032') {
                    if (GetQueryString('from') == 'opp_info') {
                        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId') + '&err=con_deleted';
                    } else if (GetQueryString('from') == 'con_info') {
                        location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId') + '&err=con_deleted';
                    } else if (GetQueryString('from') == 'acc_info') {
                        location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId') + '&err=con_deleted';
                    } else if (GetQueryString('from') == 'index') {
                        if (GetQueryString('page')) {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page') + '&err=con_deleted';
                        } else {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams;
                        }
                    }
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000004') {
                    if (GetQueryString('from') == 'opp_info') {
                        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('ropportunityId') + '&err=con_noright';
                    } else if (GetQueryString('from') == 'con_info') {
                        location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('rcontactId') + '&err=con_noright';
                    } else if (GetQueryString('from') == 'acc_info') {
                        location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('raccountId') + '&err=con_noright';
                    } else if (GetQueryString('from') == 'index') {
                        if (GetQueryString('page')) {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&page=' + GetQueryString('page') + '&err=con_noright';
                        } else {
                            location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&err=con_noright';
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
    $parent_obj.children().remove();
    var value;
    var valuenull = '<span style="color:#8fa1b2">未填写</span>';
    var structure = data.entity.structure.components;
    for (var i = 0, len = structure.length; i < len; i++) {
        value = set_infoSup(structure[i].propertyName, data);
        var show, rValue;
        if (structure[i].propertyName == "ownerId" || structure[i].propertyName == "createdBy" || structure[i].propertyName == "updatedBy") {
               show = value.text;
                rValue = value.value;
                if (rValue == "") {
                    info_atom_build($parent_obj, structure[i].label, valuenull, structure[i].propertyName, structure[i].type);
                } else {
                    info_atom_build($parent_obj, structure[i].label, show, structure[i].propertyName, structure[i].type);
                }
        } else if (typeof value === "object") {
            show = value.text;
            rValue = value.value;
            if (rValue == "") {
                info_atom_build($parent_obj, structure[i].label, valuenull, structure[i].propertyName, structure[i].type);
            } else {
                if (structure[i].propertyName == 'gender') {
                    if (rValue == 1) {
                        info_atom_build($parent_obj, structure[i].label, '男', structure[i].propertyName, structure[i].type);
                    }
                    if (rValue == 2) {
                        info_atom_build($parent_obj, structure[i].label, '女', structure[i].propertyName, structure[i].type);
                    }
                } else {
                    info_atom_build($parent_obj, structure[i].label, show, structure[i].propertyName, structure[i].type);
                }
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
    if (value != '') {
        // var oAtom = $('<li class="atom commentAtom boxSizing"><span class="label">备注:</span><span class="value"></span></li>');
        var contentText = value;
        // var content_span = $('<span class="content_span">' + contentText + '</span>');
        // var content = $('<div class="content boxSizing"></div>');
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
    if (data != '') {
        if (data == 'mobile') {
            if (text == des) {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue"><a href="tel:"> ' + text + '</a></span></li>');
            } else {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue"><a href="tel:' + text + '"> ' + text + '</a></span></li>');
            }
            oAtom.on('click', function() {
                location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + GetQueryString('contactId') + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=con_info&contactId=' + GetQueryString('contactId');
            })
        } else if (data == 'phone') {
            if (text == des) {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue"><a href="tel:"> ' + text + '</a></span></li>');
            } else {
                var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue"><a href="tel:' + text + '"> ' + text + '</a></span></li>');
            }
            oAtom.on('click', function() {
                location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + GetQueryString('contactId') + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=con_info&contactId=' + GetQueryString('contactId');
            })
        } else if (data == 'accountId') {
            console.log(accountId);
            var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value blue">' + text + '</span></li>');
            oAtom.find('.value').on('click', function() {
                location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + accountId + '&from=con_info&rcontactId=' + GetQueryString('contactId');
            })
        } else {
            var oAtom = $('<li class="atom boxSizing" id=' + data + '><span class="label">' + tagName + '：</span><span class="value">' + text + '</span></li>');
        }
        $parent_obj.append(oAtom);
    }
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
                myAlert('数据加载出错');
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
            var value = data.entity.records[$(this).index()].id;
            var label = data.entity.records[$(this).index()].name;
            doChangeOwner(label, sub);

            function sub() {
                $.ajax({
                    url: apppath + '/wx/contact/trans.action',
                    data: {
                        contactId: GetQueryString('contactId'),
                        ownerid: value
                    },
                    beforeSend: function() {
                        loader();
                    },
                    dataType: 'json',
                    success: function(oData) {
                        $('.loaderArea').remove();
                        setTimeout(function() {
                            ctRemove();
                        }, 2000);
                        if (oData.success == true) {
                            $('.owner > .value').text(label);
                            myDialog('负责人更改成功');
                            ownerId = value;
                        } else {
                            myAlert('负责人更改失败');
                        }
                    },
                    error: function() {
                        $('.loaderArea').remove();
                        myAlert('请检查网络')
                    }
                })
            }
        })
    })
}

var memberChange = false;

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
                businessId: GetQueryString('contactId'),
                belongs: 2,
                setOwnerIds: arr_value.join(",")
            },
            async: true,
            dataType: 'json',
            success: function(oData) {
                setTimeout(function() {
                    $('.ct').remove();
                    $('body').css({ 'height': '', 'overflow': '' });
                    set_title('客户信息');
                }, 2000);
                if (oData.success == true) {
                    console.log('团队成员提交成功：' + oData)
                    myDialog('更改成功');
                    // get_member();
                    //$('.member > .value').text(arr_label.join(','));
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
    $('footer > .tel').on('click', function() {

        $.ajax({
            url: apppath + '/wx/contact/getDetail.action',
            data: {
                contactId: GetQueryString('contactId'),
                scene: 'DETAIL'
            },
            dataType: 'json',
            success: function(oData) {
                if (oData.entity.data.mobile == "" && oData.entity.data.phone == "") {
                    var no_contact = $('<div class="no_contact">联系人无联系方式，请添加联系方式</div>');
                    $('body').append(no_contact);
                    setTimeout(function() {
                        $('.no_contact').remove();
                        $('.tel_bg').remove();
                    }, 2000)
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
                    add_atom(ul, oData.entity.data.mobile, '手机');
                    add_atom(ul, oData.entity.data.phone, '家庭电话');
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
            }
        })
    })
}

function add_atom(ul, obj, label_name) {
    if (obj && obj != '') {
        var li = $('<a href="tel:' + obj + '" class="tel_atom"><span class="label">' + label_name + '</span>' +
            '<span class="value">' + obj + '</span></a>');
        li.on('click', function() {
            //$('.tel_bg').remove();
            //$('#tel_list').remove();
            //console.log('记录打电话动作');
            //alert(1)
            location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + GetQueryString('contactId') + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=con_info&contactId=' + GetQueryString('contactId');
        })
        ul.append(li);
    }
}

function footer_signin() {
    $('footer > .signin').on('click', function() {
        location.href = apppath + '/wx/activityrecord/qiandao.action?' + ddcommparams + '&belongId=2&objectId=' + GetQueryString('contactId') + '&activityTypeId=' + activeRecordTypeOje["SIGN_IN"] + '&from=con_info&contactId=' + GetQueryString('contactId');
    })
}

function footer_create_record() {
    $('footer > .record').on('click', function() {
        location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + GetQueryString('contactId') + '&activityTypeId=' + activeRecordTypeOje["RECORD"] + '&from=con_info&contactId=' + GetQueryString('contactId');
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
                $(this).unbind('scroll');
                top_nav();
            }
        }
    })
}
