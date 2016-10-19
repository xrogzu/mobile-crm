$(document).bind('ready', function() {
    //底栏跳转
    footerSkip();
    //主体被删除返回的提示
    errInfo();
})

function footerSkip() {
    $('footer .account').bind('click', function() {
        location.href = apppath + '/wx/account/list.action?' + ddcommparams;
    })
    $('footer .opportunity').bind('click', function() {
        location.href = apppath + '/wx/opportunity/list.action?' + ddcommparams;
    })
    $('footer .service').bind('click', function() {
        location.href = apppath + '/wx/serveice/index.action?' + ddcommparams;
    })
    $('footer .index').bind('click', function() {
        location.href = apppath + '/wx/statics/index.action?' + ddcommparams;
    })
}

function errInfo() {
    if (GetQueryString('err')) {
        if (GetQueryString('err') == 'acc_deleted') {
            myDialog('该公司已被删除', 'without');
        } else if (GetQueryString('err') == 'con_deleted') {
            myDialog('该联系人已被删除', 'without');
        } else if (GetQueryString('err') == 'opp_deleted') {
            myDialog('该商机已被删除', 'without');
        } else if (GetQueryString('err') == 'acc_noright') {
            myDialog('您无权查看该公司', 'without');
        } else if (GetQueryString('err') == 'con_noright') {
            myDialog('您无权查看该联系人', 'without');
        } else if (GetQueryString('err') == 'opp_noright') {
            myDialog('您无权查看该商机', 'without');
        }
    }
}

//禁止滑动操作
function stopScroll($obj) {
    function stopScrolling(touchEvent) {
        touchEvent.preventDefault();
    }
    $obj.bind('touchmove', stopScrolling, false);
}

//控制返回动作（点击返回按钮，执行func）
function control_back(func) {}

//设置返回路径
function set_back(url) {}

//头像颜色库
var arr_color = ['#78c06e', '#f65e8d', '#f6bf26', '#3bc2b5', '#5c6bc0', '#f65e5e', '#5e97f6', '#9a89b9', '#bd84cd', '#6bb5ce', '#a1887f', '#ff943e', '#5ec9f6', '#c5cb63', '#ff8e6b', '#78919d'];

function set_cir_color(obj) {
    if (obj.color == null) {
        obj.color = arr_color[Math.floor(Math.random() * arr_color.length)];
    };
    obj.css('backgroundColor', obj.color);
};

//==================删除数组指定元素================
Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
Array.prototype.remove = function(index) {
    var length = this.length;
    if (index >= 0 && index < length) {
        this.splice(index, 1);
    }
};

function set_title(title) {}

function remove_rTop() {}

function set_right(text, func) {}

function checkTable(title) {
    $('.ct').remove();
    var ct = $('<div class="ct" id="ct"></div>');
    var a = $('<a href="javascript:;" class="btn-reset">取消</a>')
    ct.append(a);
    $('body').append(ct);
    $(window).scrollTop(0);
    $('body').css({ 'height': '100%', 'overflow': 'hidden' });
    set_title(title);
    remove_rTop();
    checkTableReset(a);
}

function checkTableReset(elem) {
    $(elem).on('click', function() {
        $('.ct').remove();
        $(".load-shadow").hide();
        $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
    });
}
//textarea高度自适应
jQuery.fn.extend({
    autoHeight: function() {
        return this.each(function() {
            var $this = jQuery(this);
            if (!$this.attr('_initAdjustHeight')) {
                $this.attr('_initAdjustHeight', $this.outerHeight());
            }
            _adjustH(this);
            _adjustH(this).on('input', function() {
                _adjustH(this);
            });
        });
        //重置高度
        function _adjustH(elem) {
            var $obj = jQuery(elem);
            return $obj.css({ height: $obj.attr('_initAdjustHeight'), 'overflow-y': 'hidden' })
                .height(elem.scrollHeight);
        }
    }
});
//textarea字数统计
function textarea_num() {
    var textnum = $('#textarea').val().length;
    $('#num').text(textnum + '/1000');
    $('#textarea').bind('input', function() {
        if ($(this).val().length > 1000) {
            $(this).val($(this).val().substr(0, 1000));
        }
        $('#num').text($(this).val().length + '/1000');
    })
}
//字符串去空格
String.prototype.trim = function() {
        return this.replace(/(^\s*)|(\s*$)/g, '');
    }
    //正则验证及报错
function reg_exp($obj, warning_words) {
    $obj.bind('click', function() {
        $(this).css('background', '');
    })
    $obj.bind('blur', function() {
        confirm_reg($obj, warning_words);
    })
}
//提交出错时验证+报错
function confirm_reg($obj, warning_words) {
    add_warn($obj, warning_words);
}

function rtn_reg($obj, reg) {
    if (!reg.test($obj.val())) {
        return false;
    } else {
        return true;
    }
}

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

//button元素文字颜色调整
function btn_color_adjust() {
    $('button').each(function() {
        if ($(this).html() != '点击选择') {
            $(this).addClass('active');
        } else {
            $(this).removeClass('active');
        }
    })
}
//input文字颜色调整
function input_color_adjust() {
    $('input').each(function() {
        $(this).bind('input', function() {
            if ($(this).val() != null) {
                $(this).addClass('active')
            } else {
                $(this).removeClass('active')
            }
        })
    })
    $('textarea').each(function() {
        $(this).bind('input', function() {
            console.log('1')
            if ($(this).val() != null) {
                $(this).addClass('active')
            } else {
                $(this).removeClass('active')
            }
        })
    })
}

//表单弹出错误提示
function add_warn($obj, warning_words) {
    $obj.addClass('warn');
    var warning = $('<div class="warning"><span>' + warning_words + '</span></div>');
    $obj.parent().parent().css({ 'marginBottom': '70px' });
    $obj.parent().append(warning);
    $obj.bind('click', function() {
        remove_warn($obj);
    });
    $obj.bind('blur', function() {
        remove_warn($obj);
    })
    if ($obj.parent().parent().attr('id') == 'saleStageId') {
        $obj.bind('click', function() {
            $("#winRate").find('.warning').remove();
            $("#winRate").find('input').removeClass('warn');
        })
    }
}

function remove_warn($obj) {
    $obj.removeClass('warn');
    $obj.parent().parent().css({ 'marginBottom': '0px' });
    $obj.parent().find('.warning').remove();
}
//保存按钮点击效果
function btn_touch_style() {
    $('.confirm').bind('touchstart', function() {
        $(this).addClass('touching');
    })
    $('.confirm').bind('touchend', function() {
        $(this).removeClass('touching');
    })
}
//创建活动记录
//content(记录类型中文)，belongId(来源业务对象),activityTypeId(活动记录类型),objectId(来源对象的ID),isBack(是否返回上一页1:是)
function create_record(data, $button) {
    $.ajax({
        url: apppath + '/wx/activityrecord/docreate.action',
        contentType: 'application/x-www-form-urlencoded',
        data: data,
        type: 'post',
        beforeSend: function() {
            $button.attr('disabled', true);
            loader();
        },

        dataType: 'json',
        success: function(oData) {
            $('.loaderArea').remove();
            if (oData.success == true) {
                if (data.activityTypeId == activeRecordTypeOje["SIGN_IN"]) {
                    var buriedPointType = 'Sign';
                    // buriedPoint(buriedPointType);
                } else {
                    var buriedPointType = 'activityRecordAdd';
                    // buriedPoint(buriedPointType);
                }
                myDialog('创建成功');
                setTimeout(function() {
                    doWindowLocationChange()
                }, 2000)
            } else if (oData.entity.status == '0000002') {
                myAlert('创建失败，不能包含表情符');
                $button.removeAttr('disabled');
            } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
            } else {
                $button.removeAttr('disabled');
                myAlert('创建失败');
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $('.loaderArea').remove();
            $button.removeAttr('disabled');
            myAlert('网络不给力，请重新加载');
        }
    })

    function doWindowLocationChange() {
        var from = GetQueryString('from');
        if (from == 'acc_info') {
            location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('accountId');
        } else if (from == 'con_info' || from == 'con_list') {
            location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('contactId');
        } else if (from == 'opp_info') {
            location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + GetQueryString('opportunityId');
        }
    }
}

function myAlert(info) {
    $('.UPS').remove();
    var shadow = $('<div class="commonShadow UPS"></div>');
    var oAlert = $('<div class="alert"><span>' + info + '</span><div>确定</div></div>');
    oAlert.find('div').bind('click', function() {
        $(this).parent().parent().remove();
    })
    shadow.append(oAlert);
    $('body').append(shadow);
    stopScroll($('.commonShadow'));
    stopScroll($('.alert'));
}
//默认为带对号的提示框，如果pic为without，则无对号
function myDialog(info, pic) {
    $('.UPS').remove();
    if (pic == 'without') {
        var oDialog = $('<div class="dialog_without_pic UPS">' + info + '</div>');
    } else {
        var oDialog = $('<div class="dialog UPS">' + info + '</div>');
    }
    $('body').append(oDialog);
    setTimeout(function() {
        $('.dialog').remove();
        $('.dialog_without_pic').remove();
    }, 2000);
}
//延迟跳转
function time_out_location(url) {
    setTimeout(function() {
        location.href = url;
    }, 2000)
}

//选择是否退出，是则跳转至相应url
function myChoose(url, func) {
    $('.UPS').remove();
    var dia = $('<div class="chooseShadow UPS"><div class="dia"><p class="boxSizing">确定放弃操作并退出吗？</p><div class="chooseBtn"><div class="n">取消</div><div class="y boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({ 'top': '200px', 'left': ($(window).width() - $('.dia').width()) / 2 });
    $('.y').bind('click', function() {
        $('.chooseShadow').remove();
        if (url != '') {
            location.href = url;
        } else if (func) {
            func();
        }
    })
    $('.n').bind('click', function() {
        $('.chooseShadow').remove();
    })
}

//选择弹出框
function myConfirm(info) {
    $('.UPS').remove();
    var dia = $('<div class="chooseShadow UPS"><div class="dia"><p class="boxSizing">' + info + '</p><div class="chooseBtn"><div class="n">取消</div><div class="y boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({ 'top': '200px', 'left': ($(window).width() - $('.dia').width()) / 2 });

}
//input格式自动校正（去除首尾空格）
function input_adjust() {
    $('input').bind('blur', function() {
        var val = $(this).val();
        $(this).val(val.trim());
    })
}


function loader() {
    var loader = $('<div class="loaderArea"><div class="loader"></div><span>加载中…</span></div>');
    $('body').append(loader);
}

function buriedPoint(buriedPointType) {}

function doChangeOwner(name, func) {
    var cOwner = $('<div class="cOwnerShadow"><div class="cOwner boxSizing"><div class="topInfo boxSizing">' +
        '<p><span class="word">确认将联系人变更为:"</span><span class="ownerName">' + name + '</span><span class="word">",</span></p><p>变更后将失去相关权限</p></div>' +
        '<div class="botBtn"><div class="no boxSizing">取消</div><div class="yes boxSizing">确认</div></div></div></div>');
    cOwner.find('.yes').bind('click', function() {
        cOwner.remove();
        func();
    })
    cOwner.find('.no').bind('click', function() {
        cOwner.remove();
    })
    $('body').append(cOwner);
}

function getPropertyname(item) {
    var propertyname = item.propertyname;
    if (propertyname == null) {
        propertyname = item.propertyName;
    }
    return propertyname;
}

function customizItem(elem, item, mydata) {
    if (typeof mydata == 'undefined') mydata = {};
    switch (item.type) {
        case 'ITEM_TYPE_LINE':
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing item-title"><div class="line-content"><span class="linetag">' + item.label + '</span></div></div>');
            break;
        case 'ITEM_TYPE_TEXT':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
                if (getPropertyname(item) == "state" || getPropertyname(item) == "city") {
                    return;
                }
                if (getPropertyname(item) == "region") {
                    var label = '省市区';
                    elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + label + '</span></span><input save="needSave" arequired="' + item.required + '" type="text" id="region1" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');
                } else {
                    elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" arequired="' + item.required + '" type="text" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');

                }

            } else {
                if (getPropertyname(item) == "state" || getPropertyname(item) == "city") {
                    return;
                }
                if (getPropertyname(item) == "region") {
                    var label = '省市区';
                    elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + label + '</span></span><input save="needSave" arequired="' + item.required + '" type="text" id="region1" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');

                } else {
                    elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag"><span class="tagtitle">' + item.label + '</span></span><input type="text" save="needSave" arequired="' + item.required + '" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');
                }
            }

            break;
        case 'ITEM_TYPE_SELECT':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            mydata[getPropertyname(item) + 'List'] = item.selectitem;
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><button save="needSave" arequired="' + item.required + '"  atype="button">点击选择</button></div></div>');

            break;
        case 'ITEM_TYPE_INTEGER':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave"  arequired="' + item.required + '" type="number" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');

            break;
        case 'ITEM_TYPE_REAL':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" arequired="' + item.required + '"  type="number" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');
            break;
        case 'ITEM_TYPE_TEXTAREA':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><textarea name="" id="textarea" placeholder="请填写备注内容…" save="needSave" maxlength=' + item.length + ' arequired="' + item.required + '"  atype="textarea"></textarea><span id="num">0/1000</span></div>');
            break;
        case 'ITEM_TYPE_DATE':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" id="datetime" arequired="' + item.required + '" type="text" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');
            break;
        case 'ITEM_TYPE_CHECKBOX':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            mydata[getPropertyname(item) + 'List'] = item.checkitem;
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '"  required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><button save="needSave" arequired="' + item.required + '"  atype="button">点击选择</button></div></div>');
            break;
        case 'ITEM_TYPE_SEQUENCE':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave"  arequired="' + item.required + '" type="text"  maxlength=' + item.length + '  atype="text"/></div></div>');
            break;
        case 'ITEM_TYPE_RELATION':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><button save="needSave" arequired="' + item.required + '"  atype="button">点击选择</button></div></div>');
            break;
        case 'ITEM_TYPE_PHOTO':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><img save="needSave" arequired="' + item.required + '" src="' + item.src + '" alt="" /></div></div>');
            break;
        case 'ITEM_TYPE_DUMMY':
            var requireStr = '';
            if (item.required == true) {
                requireStr = '<span class="red">*</span>';
            }
            mydata[getPropertyname(item) + 'List'] = item.dummyItemBean;
            if (getPropertyname(item) == 'accountName') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" arequired="' + item.required + '" type="text" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/><span class="entity-search"><i></i></span></div></div>');
                return;
            }
            if (getPropertyname(item) == 'industryId') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '"  required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><button save="needSave" arequired="' + item.required + '"  atype="button">点击选择</button></div></div>');
                return;
            }
            if (getPropertyname(item) == 'campaignId') {
                return;
            }
            if (getPropertyname(item) == 'comment') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><textarea name="" id="textarea" placeholder="请填写备注内容…" save="needSave" maxlength=' + item.length + ' arequired="' + item.required + '"  atype="textarea"></textarea><span id="num">0/1000</span></div>');
                return;
            }
            if (getPropertyname(item) == 'dimDepart') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '"  required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><button save="needSave" arequired="' + item.required + '"  atype="button">点击选择</button></div></div>');
                return;
            }
            if (getPropertyname(item) == 'accountId' || getPropertyname(item) == 'sourceId' || getPropertyname(item) == 'saleStageId' || getPropertyname(item) == 'ownerId') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '"  required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><button save="needSave" arequired="' + item.required + '"  atype="button">点击选择</button></div></div>');
                return;
            }
            if (getPropertyname(item) == 'birthday') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" id="datetime" arequired="' + item.required + '" type="text" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');
                return;
            }
            if (getPropertyname(item) == 'phone' || getPropertyname(item) == 'zipCode' || getPropertyname(item) == 'mobile') {
                elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" arequired="' + item.required + '" type="number" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');
                return;
            }
            elem.append('<div id=' + getPropertyname(item) + ' class="item boxSizing" btype="' + item.type + '" required="' + item.required + '"><div class="bor"><span class="tag">' + requireStr + '<span class="tagtitle">' + item.label + '</span></span><input save="needSave" arequired="' + item.required + '" type="text" maxlength=' + item.length + ' placeholder="点击输入" atype="text"/></div></div>');

            break;

    }
}

function setPropertyname(item) {
    var propertyname = item.propertyname;
    if (propertyname == null) {
        propertyname = item.propertyName;
    }
    return propertyname;
}

// 
function set_infoSup(id, data, byid) {
    var re;
    var value = data.entity.data[id];
    var item;
    $.each(data.entity.structure.components, function(i, o) {
        if (byid) {
            if (id == o.id) {
                item = o;
            }
        } else {
            if (id == o.propertyname || id == o.propertyName) {
                item = o;
            }
        }
    });
    switch (item.type) {
        case 'ITEM_TYPE_TEXT':
            re = value;
            break;
        case 'ITEM_TYPE_TEXTAREA':
            re = value;
            break;
        case 'ITEM_TYPE_SEQUENCE':
            re = value;
            break;
        case 'ITEM_TYPE_REAL':
            re = value;
            break;
        case 'ITEM_TYPE_INTEGER':
            re = value;
            break;
        case 'ITEM_TYPE_DATE':
            re = value;
            break;
        case 'ITEM_TYPE_SEQUENCE':
            re = value;
            break;
        case 'ITEM_TYPE_SELECT':
            var text = data.entity.expandPro[setPropertyname(item) + "Text"];
            re = {
                value: value,
                text: text
            }
            break;
        case 'ITEM_TYPE_CHECKBOX':
            var text = data.entity.expandPro[setPropertyname(item) + "Text"];
            re = {
                value: value,
                text: text
            }
            break;
        case 'ITEM_TYPE_RELATION':
            var text = data.entity.expandPro[setPropertyname(item) + "Text"];
            re = {
                value: value,
                text: text
            }
            break;
        case 'ITEM_TYPE_DUMMY':
            if (item.propertyName == 'accountId' || item.propertyName == 'industryId' || item.propertyName == 'parentAccountId' || item.propertyName == 'level' || item.propertyName == 'dimDepart' || item.propertyName == 'createdBy' || item.propertyName == 'updatedBy' || item.propertyName == 'sourceId' || item.propertyName == 'saleStageId' || item.propertyName == 'ownerId') {
                var text = data.entity.expandPro[setPropertyname(item) + "Text"];
                if (text == "") {
                    re = {
                        value: null,
                        text: text
                    }
                } else {
                    re = {
                        value: value,
                        text: text
                    }
                }

            } else {
                re = value;
            }
            break;

    }
    return re;
}

// 提交验证
function regingData() {
    var saveArray = $("*[save='needSave']");
    console.log(saveArray);
    var isvalidate = true;
    $.each(saveArray, function(i, o) {
        var that = saveArray[i];
        var prveName = $(that).prev().find('.tagtitle').text();
        var required = $(that).attr('arequired');
        var maxlength = $(that).attr('maxlength');
        var atype = $(that).attr('atype');
        if (required == 'false') {
            if (atype == "text") {
                if ($.trim(that.value).length > maxlength) {
                    confirm_reg($(that), '' + prveName + '输入内容长度不正确,最多能输入' + maxlength + '个字符');
                    isvalidate = false;
                }
            }
        }
        if (required == 'true') {
            if (atype == "text") {
                if ($.trim(that.value) == "") {
                    confirm_reg($(that), '' + prveName + '不能为空');
                    isvalidate = false;
                }
                if ($.trim(that.value).length > maxlength) {
                    confirm_reg($(that), '' + prveName + '输入内容长度不正确,最多能输入' + maxlength + '个字符');
                    isvalidate = false;
                }
            } else if (atype == "button") {
                if ($.trim(that.innerHTML) == "" || $.trim(that.innerHTML) == "点击选择") {
                    confirm_reg($(that), '' + prveName + '不能为空');
                    isvalidate = false;
                }
            } else if (atype == "textarea") {
                if ($.trim(that.val()) == "") {
                    confirm_reg($(that), '' + prveName + '不能为空');
                    isvalidate = false;
                }
            }

        }
        // if ($.trim(that.value).length > maxlength ) {
        //     confirm_reg($(that), '' + prveName + '输入内容长度不正确');
        //     isvalidate = false;
        // }
    });
    return isvalidate;
}

//电话和邮箱验证
function validateFn(v, type) {
    // var reg_contactName = /^.{1,50}$/;
    var reg_phone = /^\d+|[-#*]{1,30}$/;
    // var reg_department = /^.{0,20}$/;
    // var reg_post =/^.{0,50}$/;
    // var reg_tel =/^\d+|[-#*]{0,30}$/;
    var reg_email = /^(([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})){0,100}$/;
    // var reg_address =/^.{0,250}$/;

    if (type == "phone") {
        return reg_phone.match();
    } else if (type == "email") {
        return reg_email.match();
    }


}

//时间选择
function selectDate() {
    if (GetQueryString('type') != 'set') {
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        if (month < 10) {
            month = '0' + String(month);
        }
        if (day < 10) {
            day = '0' + String(day);
        }
        var dateStr = year + '-' + month + '-' + day;
        // $('#datetime').attr("value", dateStr);
    }
    var _date = document.getElementById('datetime');
    var datePicker = new window.DatePicker({
        confirmCbk: function(data) {
            var dataMouth = data.month;
            var dataDay = data.day;
            if(data.month < 10 ){
                dataMouth = '0' + dataMouth;
            }
            if(dataDay < 10){
                dataDay = '0' + dataDay;
            }
            _date.value = data.year + '-'+ dataMouth + '-' + dataDay;
           
        }
    });
    _date.onfocus = function(e) {
        _date.blur();
        datePicker.open();
    };

}

//选择所属部门
var postData;
var setPost = false;

function dimdepart_choose() {
    $('#dimDepart').bind('click', function() {
        checkTable('选择所属部门');
        $.ajax({
            type: "get",
            url: apppath + "/wx/department/mydepartment.action",
            async: true,
            dataType: 'json',
            success: function(oData) {
                // console.log('部门' + oData)
                if (oData.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    postData = oData;
                    create_dimdepart_list(oData);
                    $('#dimDepart').attr('value', postData.entity.departs[0].id);
                    $('#dimDepart  button').html(postData.entity.departs[0].text);
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    });
}

function create_dimdepart_list(data) {
    var container = $('<div class="container"></div>');
    for (var i = 0, len = data.entity.departs.length; i < len; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data.entity.departs[i].text + '</span></div>');
        list.attr('value', data.entity.departs[i].id);
        container.append(list);
    }
    $('.ct').append(container);
    // for (var i = 0, len = $('.ct .item').length; i < len; i++) {
    //     var value = $('#dimDepart button').attr("value");
    //     var itemvalue = $('.ct .item').eq(i).attr("value");
    //     if (value == itemvalue) {
    //         $('.ct .item').eq(i).find('em').addClass('active');
    //     }
    // }
    container.find('.item[value="' + $('#dimDepart button').attr("value") + '"] em').addClass('active');
    $('.ct .item').each(function(index) {
        $(this).bind('click', function() {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity.departs[$(this).index()].id;
            var index = $(this).index();
            var label = data.entity.departs[$(this).index()].text;
            $('#dimDepart button').html(label);
            btn_color_adjust();
            $('#dimDepart button').attr('value', value);
            $('.ct').remove();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
    })
}

//===========================================选择公司或者客户==================================
//创建底部提示
function build_end(info, name) {
    var end = $('<div id="' + name + '" class="end_info">' + info + '</div>');
    $('.ct').append(end);
}
var accountpageNo = 0;
var accountpageSize = 20;
var accountArr = [];
var scroolpange = 0;

function accountPost() {
    $('#accountId button').bind('click', function() {
        checkTable('选择公司');
        accountpageNo = 0;
        loadaccountList(0, 20, false);

    })
}

function loadaccountList(accountpageNo, accountpageSize, isAppend) {
    var accountId = $('#accountId').find('button');
    $.ajax({
        type: "get",
        url: apppath + "/wx/account/dolist.action",
        async: true,
        data: { pageNo: accountpageNo, pagesize: accountpageSize },
        dataType: 'json',
        success: function(oData) {
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                if (oData.entity.totalSize == 0) {
                    myAlert('暂无公司，请新建公司');
                    $('.ct').remove();
                    // var no_contact = $('<div class="no_contact">暂无公司，请新建公司</div>');
                    // $('body').append(no_contact);
                    // setTimeout(function() {
                    //     $('.no_contact').remove();
                    // }, 200000);
                } else {
                    for (var i = 0, len = oData.entity.records.length; i < len; i++) {
                        accountArr.push(oData.entity.records[i]);
                    }
                    if (oData.entity.records.length == 0) {
                        var empty = $('<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>');
                        $('.ct').append(empty);
                    } else {
                        $('.ct').append(warp);
                        create_account_list(oData, accountId, isAppend);
                    }
                    if ($('.ct').find('.item').length < oData.entity.totalSize) {
                        $('#loading').remove();
                        build_end('加载更多', 'load_more');
                        accountlistLoaded();
                    } else {
                        $('#loading').remove();
                    };

                }
            }
        }
    })
}
var warp = $('<div class="warp"></div>')
    //客户列表
function create_account_list(data, elem, isAppend) {
    console.log(data);
    var container = $('<div class="container account_container"></div>');
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var accountName = data.entity.records[i].accountName;
        var accountId = data.entity.records[i].id
        var list = $('<a  id=' + accountId + ' class="item boxSizing"><em class="radio"><i></i></em><span>' + accountName + '</span></a>');
        container.append(list);
        $('.ct').addClass('account-warp');
        // (function(value, label) {
        //     list.on('click', function(evt) {
        //         $(this).find('em').addClass('active');
        //         $(this).siblings().find('em').removeClass('active');
        //         console.log(label + ":" + value);
        //         $('#accountId button').html(label);
        //         btn_color_adjust();
        //         $('#accountId button').attr('value', value);
        //         $('.ct').remove();
        //         $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        //     })
        // })(accountId, accountName)
    }
    if (!isAppend) {
        $(warp).empty();
    }
    $(warp).append(container);

    for (var i = 0, len = $('.ct .item').length; i < len; i++) {
        var htmlenValue = $('#accountId button').attr("value");
        if (htmlenValue == $('.ct .item').eq(i).attr("id")) {
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    var length = $('.ct .item').length;
    // if (length >= 20) {
    $('.ct .item').off('tap click').on('tap click', function(event) {
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = $(this).attr('id');
            var label = $(this).find('span').text();
            $(elem).html(label);
            btn_color_adjust();
            $(elem).attr('value', value);
            $('.ct').remove();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        })
        // } else {
        //     $('.ct .item').off('click').on('click', function(event) {
        //         $(this).find('em').addClass('active');
        //         $(this).siblings().find('em').removeClass('active');
        //         var value = $(this).attr('id');
        //         var label = $(this).find('span').text();
        //         $(elem).html(label);
        //         btn_color_adjust();
        //         $(elem).attr('value', value);
        //         $('.ct').remove();
        //         $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
        //     })
        // }

}

// 滚动加载
var accountlistScroll;

function accountlistLoaded() {
    accountlistScroll = new IScroll('#ct', {
        probeType: 1,
        mouseWheel: true,
        preventDefault: false,
        scrollbars: false,
        momentum: false,
        useTransform: false,
        click: true,
        tap: true,
        bounceTime: 200,
        mouseWheelSpeed: 200,
    });
    accountlistScroll.on("scrollStart", function() {

        if ($('#load_more').length > 0) {
            $('#load_more').remove();
            build_end('加载中…', 'loading');
            accountpageNo = accountpageNo + 1;
            loadaccountList(accountpageNo, 20, true);
        }
    });
    accountlistScroll.on("scrollEnd", function() {
        $('#loading').remove();
    });

}


// 自定义select下拉选择
function SelectItemPost() {
    $("#basic_info .item").each(function(i) {
        if ($(this).attr("btype") == 'ITEM_TYPE_SELECT') {
            var title = $(this).find('span.tagtitle').text();
            var listName = $(this).attr('id') + 'List';
            $(this).find('button').bind('click', function() {
                checkTable(title);
                createSelectList(selectitmeObj[listName], $(this));
            })
        }
    });
}

function createSelectList(data, elem) {
    var container = $('<div class="container"></div>');
    for (var i = 0, len = data.length; i < len; i++) {
        var list = $('<div class="item 33 boxSizing" value="' + data[i].value + '"  href="' + data[i].value + '"><em class="radio"><i></i></em><span>' + data[i].label + '</span></div>');
        container.append(list);
    }
    $('.ct').append(container);
    // for (var i = 0, len = $('.ct .item').length; i < len; i++) {
    //     var value = $(elem).attr("value");
    //     var itemvalue = $('.ct .item').eq(i).attr("value");
    //     if (value == itemvalue) {
    //         $('.ct .item').eq(i).find('em').addClass('active');
    //     }
    // }
    container.find('.item[value="' + $(elem).attr('value') + '"] em').addClass('active');


    $('.ct .item').on('click', function(event) {
        $(this).find('em').addClass('active');
        $(this).siblings().find('em').removeClass('active');
        var value = data[$(this).index()].value;
        var index = $(this).index();
        var label = data[$(this).index()].label;
        $(elem).html(label);
        btn_color_adjust();
        $(elem).attr('value', value);
        $('.ct').remove();
        $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
    })
}

// 自定义Dummy下拉选择
function DummyItemPost() {
    $("#basic_info .item").each(function(i) {
        if ($(this).attr("btype") == 'ITEM_TYPE_DUMMY') {
            if ($(this).attr('id') == 'accountName' || $(this).attr('id') == 'ownerId' || $(this).attr('id') == 'dimDepart' || $(this).attr('id') == "accountId") {
                return;
            } else {
                if ($(this).find('button').size()) {
                    var title = $(this).find('span.tagtitle').text();
                    var listName = $(this).attr('id') + 'List';
                    $(this).find('button').bind('click', function() {
                        checkTable(title);
                        createDummyList(selectitmeObj[listName], $(this));
                    })
                }
            }
        }
    });
}

function createDummyList(data, elem) {
    var container = $('<div class="container"></div>');
    for (var i = 0, len = data.length; i < len; i++) {
        var list = $('<div class="item boxSizing" value="' + data[i].value + '"><em class="radio"><i></i></em><span>' + data[i].label + '</span></div>');
        container.append(list);
    }
    $('.ct').append(container);
    // for (var i = 0, len = $('.ct .item').length; i < len; i++) {
    //     var value = $(elem).attr("value");
    //     var itemvalue = $('.ct .item').eq(i).attr("value");
    //     if (value == itemvalue) {
    //         $('.ct .item').eq(i).find('em').addClass('active');
    //     }
    // }
    container.find('.item[value="' + $(elem).attr('value') + '"] em').addClass('active');
    $('.ct .item').on('click', function(event) {
        $(this).find('em').addClass('active');
        $(this).siblings().find('em').removeClass('active');
        var value = data[$(this).index()].value;
        var label = data[$(this).index()].label;
        $(elem).html(label);
        if ($(elem).parent().parent().attr('id') == 'saleStageId') {
            var percent = data[$(this).index()].percent;
            $('#winRate input').val(percent);
            $('#winRate input').attr('readonly', true);
        }
        btn_color_adjust();
        $(elem).attr('value', value);
        $('.ct').remove();
        $('body').css({ 'overflow': 'scroll', 'height': 'auto' });
    })
}


// 自定义checkbox下拉多选
function checkboxItemPost() {
    $("#basic_info .item").each(function(i) {
        if ($(this).attr("btype") == 'ITEM_TYPE_CHECKBOX') {
            if ($(this).find('button').size()) {
                var title = $(this).find('span.tagtitle').text();
                var listName = $(this).attr('id') + 'List';
                $(this).find('button').bind('click', function() {
                    checkTable(title);
                    $('.ct').addClass("multiple");
                    var container = $('<div id="container" class="container"></div>');
                    $('.ct').append(container);
                    checkboxDummyList(container, selectitmeObj[listName], $(this));
                })
            }
        }
    });
}
var checkboxMember = {};
var checkboxChange = false;

function checkboxDummyList(container, data, elem) {
    for (var i = 0, len = data.length; i < len; i++) {
        var value = data[i].value;
        var label = data[i].label;
        var con = $('<div class="item boxSizing" id = "' + value + '" atext = "' + label + '"><em class="checkbox"></em><span>' + label + '</span></div>');
        con.attr('value', value);
        container.append(con);
    }
    checkboxMember = $(elem).attr('value').split(',');
    // $('.ct').append(container);
    for (var j = 0; j < checkboxMember.length; j++) {
        for (var i = 0, len = data.length; i < len; i++) {
            var value = $('.ct .item').eq(i).attr('id');
            if ($.trim(checkboxMember[j]) == value) {
                $('.ct .item').eq(i).find('em').addClass('active');
            }
        }
    }

    checkboxClick(data);
    createConfirm(data, elem);
}

function checkboxClick(data) {
    $('.item').each(function(index) {
        $(this).bind('click', function() {
            checkboxChange = true;
            if ($(this).find('em').hasClass('active')) {
                $(this).find('em').removeClass('active');
            } else {
                $(this).find('em').addClass('active');
            }
        })
    })
}

function createConfirm(data, elem) {
    var confirm = $('<button id="confirm" class="confirm">确定</button>');

    $('.ct').append(confirm);
    confirm.bind('touchstart', function() {
        $(this).addClass('touching');
    })
    confirm.bind('touchend', function() {
        $(this).removeClass('touching');
    })
    confirm.bind('click', function() {
        var arr_value = [];
        var arr_label = [];
        $('#container .item').each(function() {
            if ($(this).find('em').hasClass('active')) {
                arr_value.push($(this).attr('value'));
                arr_label.push($(this).attr('atext') + ",");
            }
            $(elem).attr('value', arr_value);
            $(elem).html(arr_label);
            $(elem).attr('class', 'active');
            // $(elem).attr('value', value);
            $('.ct').remove();
            $('body').css({ 'overflow': 'scroll', 'height': 'auto' })
        })
    })
}


// 处理特殊字符
function htmlencode(s) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(s));
    return div.innerHTML;
}

function htmldecode(s) {
    var div = document.createElement('div');
    div.innerHTML = s;
    return div.innerText || div.textContent;
}

//获取当前时间
function curTime() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    if (month < 10) {
        month = '0' + String(month);
    }
    if (day < 10) {
        day = '0' + String(day);
    }
    var dateStr = year + '-' + month + '-' + day;
    return dateStr;
}

//日期时间与时间戳的转换
(function($) {
    $.extend({
        myTime: {
            /**
             * 当前时间戳
             * @return <int>        unix时间戳(秒)  
             */
            CurTime: function(){
                return Date.parse(new Date())/1000;
            },
            /**              
             * 日期 转换为 Unix时间戳
             * @param <string> 2014-01-01 20:20:20  日期格式              
             * @return <int>        unix时间戳(秒)              
             */
            DateToUnix: function(string) {
                var f = string.split(' ', 2);
                var d = (f[0] ? f[0] : '').split('-', 3);
                var t = (f[1] ? f[1] : '').split(':', 3);
                return (new Date(
                        parseInt(d[0], 10) || null,
                        (parseInt(d[1], 10) || 1) - 1,
                        parseInt(d[2], 10) || null,
                        parseInt(t[0], 10) || null,
                        parseInt(t[1], 10) || null,
                        parseInt(t[2], 10) || null
                        )).getTime() / 1000;
            },
            /**              
             * 时间戳转换日期              
             * @param <int> unixTime    待时间戳(秒)              
             * @param <bool> isFull    返回完整时间(Y-m-d 或者 Y-m-d H:i:s)              
             * @param <int>  timeZone   时区              
             */
            UnixToDate: function(unixTime, isFull, timeZone) {
                if (typeof (timeZone) == 'number')
                {
                    unixTime = parseInt(unixTime) + parseInt(timeZone) * 60 * 60;
                }
                var time = new Date(unixTime * 1000);
                var ymdhis = "";
                ymdhis += time.getUTCFullYear() + "-";
                ymdhis += (time.getUTCMonth()+1) + "-";
                ymdhis += time.getUTCDate();
                if (isFull === true)
                {
                    ymdhis += " " + time.getUTCHours() + ":";
                    ymdhis += time.getUTCMinutes() + ":";
                    ymdhis += time.getUTCSeconds();
                }
                return ymdhis;
            }
        }
    });
})(jQuery);

//日期补全0
function Appendzero(obj) {
    if (obj < 10) return "0" + obj;
    else return obj;
}