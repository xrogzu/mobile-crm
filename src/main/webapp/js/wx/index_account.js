$(function() {
    // earlyLoad();
    accountSearchDateTypelist();
});


var pageNo = 0;
var pageSize = 20;
var arr = [];

var reminderHtml = '<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>';
function earlyLoad() {
    //先加载缓存数据
    if (xsyUser) {
        if (localStorage.getItem(xsyUser.id)) {
            var ls = JSON.parse(localStorage.getItem(xsyUser.id));
            if (ls.accountList) {
                if (ls.accountList.accListData) {
                    accListByData(ls.accountList.accListData);
                    pageNo = 0;
                }
            }
        }
        // window.location.href = window.location.href
    }
}
function accountSearchDateTypelist() {
    var accSearchdatatype = GetQueryString('searchDateType');
    $.ajax({
        type: 'post',
        url: apppath + "/wx/account/dolist.action",
        data: {
            pageNo: pageNo,
            pagesize: pageSize,
            searchDateType: accSearchdatatype
        },
        dataType: 'json',
        async: true,
        success: function(oData) {
            console.log(oData);
            $(".load-shadow").hide();
            if (oData.success == false) {
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('网络不给力，请重新加载');
                }
            } else {
                if (oData.entity.totalSize == 0) {
                    $("#content").append(reminderHtml);
                } else {
                    if (xsyUser) {
                        var ls;
                        if (localStorage.getItem(xsyUser.id)) {
                            ls = JSON.parse(localStorage.getItem(xsyUser.id));
                            ls.accountList = { 'accListData': oData }
                        } else {
                            ls = { 'accountList': { 'accListData': oData } }
                        }
                        localStorage.setItem(xsyUser.id, JSON.stringify(ls));
                    }
                    accListByData(oData);
                }

            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载');
        }
    });
}

function earlyLoad() {
    //先加载缓存数据
    if (xsyUser) {
        if (localStorage.getItem(xsyUser.id)) {
            var ls = JSON.parse(localStorage.getItem(xsyUser.id));
            if (ls.accountList) {
                if (ls.accountList.accListData) {
                    accListByData(ls.accountList.accListData);
                    pageNo = 0;
                }
            }
        }
        // window.location.href = window.location.href
    }
}

function accListByData(oData) {
    arr = [];
    for (var i = 0, len = oData.entity.records.length; i < len; i++) {
        arr.push(oData.entity.records[i]);
    }
    if (oData.entity.records.length == 0) {
        var empty = $('<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>');
        $('#list').append(empty);
    } else {
        build_list(arr);
    }
    console.log($('#list').find('.con').length);
    if ($('#list').find('.con').length < oData.entity.totalSize) {
        // if (pageNo < oData.entity.totalPage && oData.entity.totalPage != 0) {
        $('#loading').remove();
        build_end('加载更多', 'load_more');
        pageNo += 1;
        loaded();
    } else {
        $('#loading').remove();
    }
}
//上拉加载更多
function scroll_load() {
    $('#content').on('scroll', function() {
        loaded();
        // if ($('#load_more').length > 0) {
        //     if ($('#load_more').offset().top <= $(window).height() - $('#load_more').height() - 55) {
        //         $('#load_more').remove();
        //         build_end('加载中…', 'loading');
        //         accountSearchDateTypelist();
        //     }
        // }
    })
}
var listAccountId, listAccountName;

function build_list(arr) {
    var topWord;
    for (var i = 0, len = arr.length; i < len; i++) {
        if (/(?!^(\d+|[~!@#$%^&*?]+)$)^[\w~!@#$%\^&*?]+$/.test(arr[i].accountName_py.substr(0, 1))) {
            topWord = arr[i].accountName_py.substr(0, 1);
        } else {
            topWord = '#';
        }
        var topli;
        var topTitle = $('#list').find('div[id="' + topWord.toUpperCase() + '"]');
        if (topTitle.size()) {
            topli = topTitle.parent();
        } else {
            //第一个块或与前一个不同的新块，生成top
            if (i == 0) {
                var li = $('<li></li>');
                var top = $('<div class="top boxSizing" id="' + topWord.toUpperCase() + '">' + topWord.toUpperCase() + '</div>');
                li.append(top);
            } else if (topWord != arr[i - 1].accountName_py.substr(0, 1) && !/^\d$/.test(arr[i].accountName_py.substr(0, 1))) {
                var li = $('<li></li>');
                var top = $('<div class="top boxSizing" id="' + topWord.toUpperCase() + '">' + topWord.toUpperCase() + '</div>');
                // li.on('click',{},liClickFn);
                li.append(top);
            }
            $('#list').append(li);

            topli = $('#list > li:last');
        }
        //生成con内容
        listAccountId = arr[i].id;
        listAccountName = arr[i].accountName;
        var con = document.createElement('div');
        $(con).attr('value', listAccountId);
        con.className = 'con boxSizing';
        $(con).append($('<div class="info" > <span class="accountName">' + listAccountName + '</span><em class="sign_in"></em></div>'));
        $(con).find('em').on('click', function(e) {
                location.href = apppath + '/wx/activityrecord/qiandao.action?' + ddcommparams + '&belongId=1&objectId=' + $(this).parent().parent().attr('value') + '&activityTypeId=' + activeRecordTypeOje["SIGN_IN"] + '&from=acc_info&accountId=' + $(this).parent().parent().attr('value');
                e.stopPropagation();
            })
            // $(con).find('div.info').on('tap', function(e) {
            //     local_set(accountId, accountName, 1);
            //     location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + accountId;
            // })
        topli.append(con);
    }
    //添加点击事件
    con_click();
}

function con_click() {
    $('#list li div.con').each(function(index) {
        $(this).click(function() {
            var id = $(this).attr('value');
            var name = $(this).find('span.accountName').text();
            local_set(id, name, 1);
            location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + id;

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

function loaded() {
    myScroll = new IScroll('#content', {
        probeType: 3,
        mouseWheel: true,
        preventDefault: false,
        scrollbars: true,
        momentum: true,
        useTransform: false,
        click: true,
        tap: true,
        bounceTime: 200,
        mouseWheelSpeed: 200,
    });
    myScroll.on("scrollStart", function() {
        if ($('#load_more').length > 0) {
            $('#load_more').remove();
            build_end('加载中…', 'loading');
            accountSearchDateTypelist();
        }
    });
    myScroll.on("scrollEnd", function() {
        $('#loading').remove();
    });

}
