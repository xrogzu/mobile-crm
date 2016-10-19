$(function() {
    contactSearchDateTypelist();
    loaded();
});
var pageNo = 0;
var pageSize = 20;
var arr = [];
//创建联系人列表
function contactSearchDateTypelist(createDateType) {
    var accSearchdatatype = GetQueryString('searchDateType');
    $.ajax({
        type: "get",
        url: apppath + "/wx/contact/dolist.action?d=" + (+new Date),
        data: { pageNo: pageNo, pagesize: pageSize, searchDateType: accSearchdatatype },
        async: true,
        dataType: 'json',
        success: function(oData) {
            $(".load-shadow").hide();
            if (oData.success == false) {
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('网络不给力，请重新加载');
                }
            } else {
                for (var i = 0, len = oData.entity.records.length; i < len; i++) {
                    arr.push(oData.entity.records[i]);
                }
                if (oData.entity.records.length == 0) {
                    var empty = $('<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>');
                    $('#list').append(empty);
                } else {
                    build_list(arr);
                }
                if ($('#list').find('.con').length < oData.entity.totalSize) {
                    $('#loading').remove();
                    build_end('加载更多', 'load_more');
                    pageNo += 1;
                } else {
                    $('#loading').remove();
                };
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    });
}
function build_list(arr) {
    var topWord;
    for (var i = pageNo * pageSize; i < arr.length; i++) {
        if (/(?!^(\d+|[~!@#$%^&*?]+)$)^[\w~!@#$%\^&*?]+$/.test(arr[i].contactName_py.substr(0, 1))) {
            topWord = arr[i].contactName_py.substr(0, 1);
        } else {
            topWord = '#';
        }
        //icon文字
        var botWord = icon_word(arr, i);
        var topli;
        var topTitle = $('#list').find('div[id="' + topWord.toUpperCase() + '"]');
        if (topTitle.size()) {
            topli = topTitle.parent();
        }

        //第一个块或与前一个不同的新块，生成top
        else {
            if (i == 0) {
                var li = $('<li></li>');
                var top = $('<div class="top boxSizing" id="' + topWord.toUpperCase() + '">' + topWord.toUpperCase() + '</div>');
                li.append(top);
            } else if (topWord != arr[i - 1].contactName_py.substr(0, 1) && !/^\d$/.test(arr[i].contactName_py.substr(0, 1))) {
                var li = $('<li></li>');
                var top = $('<div class="top boxSizing" id="' + topWord.toUpperCase() + '">' + topWord.toUpperCase() + '</div>');
                li.append(top);
            }
            $('#list').append(li);
            topli = $('#list > li:last');
        }
        //生成con内容
        var contactId = arr[i].id;
        var con = document.createElement('div');
        $(con).attr('value', contactId);
        con.className = 'con boxSizing';
        var contactName = arr[i].contactName;
        var post = arr[i].post;
        var accountName = arr[i].accountName;
        var mobile = arr[i].mobile;
        var phone = arr[i].phone;
        console.log(contactName)
        $(con).append($('<div class="pic boxSizing"><i class="cir">' + botWord + '</i></div><div class="info boxSizing" mobile = "' + mobile + '" phone = "' + phone + '"><div class="info_detail"><span class="name">' + contactName + '</span> ' + '<span class="job">' + post + '</span><span class="company boxSizing">' + accountName + '</span></div><a class="tel_click"><em class="ico"></em></a></div>'));
        $('#list > li:last').append(con);
        var shadow = $('<div class="tel_bg"></div>');
        // $(con).find('.tel').off('tap').on('tap', function(e) {
        //     $(window).scrollTop(0);
        //     $('body').append(shadow);
        //     $('.tel_bg').css('top', $(document).scrollTop());
        //     $('.tel_bg').fadeIn(200);
        // });
        topli.append(con);
        set_cir_color($(con).find('.cir'));

    }
    //添加点击事件
    con_click();
    contacttelClick();

}

function contacttelClick() {
    var tellist = $('#list li .con');
    var tellength = tellist.length;
    // if (tellength > 20 ) {
    tellist.find('.tel_click').off('tap').on('tap', function(e) {
        e.preventDefault();
        var objectId = $(this).closest(".con").attr('value');
        var mymobile = $(this).closest(".info").attr('mobile');
        var myphone = $(this).closest(".info").attr('phone');
        if (mymobile == "" && myphone == "") {
            var no_contact = $('<div class="no_contact">暂无联系方式，请填写联系方式</div>');
            $('body').append(no_contact);
            setTimeout(function() {
                $('.no_contact').remove();
            }, 1000);
        } else {
            $(window).scrollTop(0);
            var shadow = $('<div class="tel_bg"></div>'),
                bdy = $('body');
            // shadow.on('tap', function() {
            //     $('.tel_bg').remove();
            //     $('#tel_list').remove();
            // })
            bdy.append(shadow);
            shadow.css('top', $(document).scrollTop());
            shadow.fadeIn(200);
            var ul = $('<div id="tel_list" style="bottom:-288px;z-index:1000"></div>');
            // alert('加载rank缓存数据');
            var list = $('<div id="tel_list_warp"></div>');
            ul.append(list);
            add_atom(list, mymobile, '家庭电话', objectId);
            add_atom(list, myphone, '手机', objectId);
            var rtn_btn = $('<div id="rtn_btn">取消</div>');
            rtn_btn.on('click', function() {
                bdy.css({ 'overflow': '', 'height': '' });
                shadow.remove();
                ul.animate({ 'bottom': -ul.height(), 'z-index': '999' }, 300, function() {
                    ul.remove();
                });
            });
            ul.append(rtn_btn);
            bdy.css({ 'overflow': 'hidden', 'height': '100%' });
            // ul.on('touchstart', function(e) {
            //     e.stopPropagation();
            // });
            // ul.css({ 'bottom': -$('#tel_list').height(), 'z-index': '1000' });
            bdy.append(ul);
            //TODO:这里有个问题，不实用延时，会导致某某bug. 详情问邓芳.
            setTimeout(function() {
                ul.animate({ 'bottom': 0 }, 300);
            }, 250);
            telAtomClick(objectId);
        }
    });

}

function add_atom(ul, obj, label_name, id) {
    if (obj && obj != '') {
        var li = $('<a href=' + 'tel:' + obj + ' class="tel_atom" ><span class="label">' + label_name + '</span>' +
            '<span class="value" >' + obj + '</span></a>');
        ul.append(li);
        //  li.bind('click', function() {
        //     //$('.tel_bg').remove();
        //     //$('#tel_list').remove();
        //     //console.log('记录打电话动作');
        //     location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + id + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=con_list&contactId=' + id;
        // });
    }
}

function telAtomClick(id) {
    $(".tel_atom").on('click', function(e) {
        location.href = apppath + '/wx/activityrecord/create.action?' + ddcommparams + '&belongId=2&objectId=' + id + '&activityTypeId=' + activeRecordTypeOje["TEL"] + '&from=con_list&contactId=' + id;
    });
    // alert('加载rank缓存数据');
}

function icon_word(arr, i) {
    //不是汉字，显示前两个字符
    if (!(/^[\u4e00-\u9fa5]+$/).test(arr[i].contactName.substr(0, 1))) {
        return arr[i].contactName.substr(0, 2);
    }
    //汉字显示后两字符
    else {
        return arr[i].contactName.substr(-2);
    }
}

function con_click() {
    $('#list .info_detail').each(function(index) {
        $(this).click(function() {
            var id = $(this).parent().parent().attr('value');
            var name = $(this).find('span.name').text();
            location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + id;
            //本地存储
            local_set(id, name, 2);
        })
    })
}
var userId = xsyUser.id;
//本地存储
function local_set(id, name, kindle) {
    if (kindle == 1 || kindle == 2) {
        var val = { "id": id, "name": name, "kindle": kindle };
        if (localStorage.getItem(userId)) {
            var ls = JSON.parse(localStorage.getItem(userId));
            //去重，后点排在前
            if (ls.accountHistory) {
                for (var j = 0; j < ls.accountHistory.length; j++) {
                    if (ls.accountHistory[j].id == id) {
                        ls.accountHistory.remove(j);
                    }
                }
                ls.accountHistory.push(val);
            } else {
                var ls = { "accountHistory": [val] }
            }
        } else {
            var ls = { "accountHistory": [val] }
        }
    } else if (kindle == 3) {
        var val = { "id": id, "name": name };
        if (localStorage.getItem(userId) != 'undefined') {
            var ls = JSON.parse(localStorage.getItem(userId));
            //去重，后点排在前
            if (ls.opportunityHistory) {
                for (var j = 0; j < ls.opportunityHistory.length; j++) {
                    if (ls.opportunityHistory[j].id == id) {
                        ls.opportunityHistory.remove(j);
                    }
                }
                ls.opportunityHistory.push(val);
            } else {
                var ls = { "opportunityHistory": [val] }
            }
        } else {
            var ls = { "opportunityHistory": [val] }
        }
    }
    localStorage.setItem(userId, JSON.stringify(ls));
};
//创建底部提示
function build_end(info, name) {
    var end = $('<div id="' + name + '" class="end_info">' + info + '</div>');
    $('#list').append(end);
}
// 滚动加载
var myScroll;

function iScrollClick() {
    if (/iPhone|iPad|iPod|Macintosh/i.test(navigator.userAgent)) return false;
    if (/Chrome/i.test(navigator.userAgent)) return (/Android/i.test(navigator.userAgent));
    if (/Silk/i.test(navigator.userAgent)) return false;
    if (/Android/i.test(navigator.userAgent)) {
        var s = navigator.userAgent.substr(navigator.userAgent.indexOf('Android') + 8, 3);
        return parseFloat(s[0] + s[3]) < 44 ? false : true
    }
}

function loaded() {
    myScroll = new IScroll('#content', {
        click: iScrollClick(),
        probeType: 1,
        mouseWheel: true,
        preventDefault: false,
        scrollbars: true,
        momentum: true,
        useTransform: false,
        click: true,
        tap: true,
        bounceTime: 400,
        mouseWheelSpeed: 400,
    });
    myScroll.on("scrollStart", function() {
        if ($('#load_more').length > 0) {
            $('#load_more').remove();
            build_end('加载中…', 'loading');
            contactSearchDateTypelist();

        }
    });
    myScroll.on("scrollEnd", function() {
        $('#loading').remove();
    });

}
