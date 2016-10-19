$(function(){	
	 build_common_list(apppath + '/wx/opportunity/dolist.action?d='+(+new Date), $('#list .main'), empty, build_list_unit, loaded);
});
var empty = '<div class="content-reminder"><div class="ico"><p>暂无数据</p></div></div>';
var saleStageId = 0;
var closeType = 0;
var min = 0;
var max = 0;
var pageNo = 0;
var pageSize = 20;
var orderField = 'saleStageId, updatedAt desc';
var searchDateType = GetQueryString('searchDateType');
var obj = {
    pageNo: pageNo,
    pagesize: pageSize,
    orderField: orderField,
    searchDateType:searchDateType,
};
//搜索单元创建
function build_word_unit(oData, $container, i) {
    console.log('build_word_unit')
    var opportunityId = oData.entity.records[i].id;
    var opportunityName = oData.entity.records[i].opportunityName;
    var list = $('<div class="boxSizing opportunityList">' + opportunityName + '</div>');
    list.attr('value', opportunityId);
    list.bind('click', function() {
        local_set(opportunityId, opportunityName, 3);
        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + opportunityId;
    })
    $container.append(list);
}
//搜索下拉加载
function wordScrollFunc() {
    $('#search_content').bind('scroll', function() {
        if ($('#load_more').length > 0) {
            if ($('#search_content .main').offset().top <= $(window).height() - $('#search_content .main').height() + 3) {
                //加载更多替换为加载中
                $('#load_more.end_info').remove();
                $('#search_content .main').append($('<div id="loading" class="end_info">加载中…</div>'));
                //加载后续列表
                build_common_list(apppath + '/wx/opportunity/dolist.action', $('#search_content .main'), '无相关商机', build_word_unit, wordScrollFunc);
            }
        }
    })
}
//列表页单元创建
function build_list_unit(oData, $container, i) {
    var item = oData.entity.records[i];
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
    li.attr('id', id);
    li.attr('name', opportunityName);
    li.bind('click', function() {
        local_set($(this).attr('id'), $(this).attr('name'), 3);
        location.href = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('id') + '&from=opp_list';
    })
    $container.append(li);
}
//列表页下拉加载
// function loaded() {
//     $('#content').bind('scroll', function() {
//         if ($('#load_more').length > 0) {
//             if ($('#load_more').offset().top <= $(window).height() - $('#load_more').height() - 53) {
//                 $('#load_more.end_info').remove();
//                 $('#list .main').append($('<div id="loading" class="end_info">加载中…</div>'));
//                 build_common_list(apppath + '/wx/opportunity/dolist.action', $('#list .main'), empty, build_list_unit, loaded)
//             }
//         }
//     })
// }
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

// 滚动加载
var myScroll;

function loaded(){
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
        if ($('#load_more.end_info').length > 0) {
             $('#load_more.end_info').remove();
                $('#list .main').append($('<div id="loading" class="end_info">加载中…</div>'));
                build_common_list(apppath + '/wx/opportunity/dolist.action', $('#list .main'), empty, build_list_unit, loaded)

        }
    });
    myScroll.on("scrollEnd", function() {
        $('#loading').remove();
    });

}

//生成带下拉刷新功能的列表
//url=apppath+'/wx/opportunity/dolist.action'
//全局obj,pageNo,pageSize
//nullInfo = '无相关商机'
//$container = $('#search_content .main')
//build_unit(oData,$container)局部函数
//scrollFunc()下拉加载动作
function build_common_list(url, $container, nullInfo, build_unit, scrollFunc) {
    $(window).scrollTop(0);
    $.ajax({
        url: url,
        data: obj,
        //async:false,
        dataType: 'json',
        success: function(oData) {
            $(".load-shadow").hide();
            // console.log(oData)
            if (oData.success == true) {
                //获取总条数
                var len = oData.entity.totalSize;
                var sEnd;
                if (len == 0) {
                    //无结果情况
                    $container.children().remove();
                    var $list = $('<div class="list">' + nullInfo + '</div>');
                    $list.css({ 'width': '100%', 'lineHeight': '90px', 'fontSize': '30px', 'textAlign': 'center' });
                    $container.append($list);
                } else {
                    //有结果情况
                    if (len - pageNo * pageSize > pageSize) {
                        //下拉有加载
                        sEnd = pageSize;
                    } else {
                        //下拉无加载
                        sEnd = len - pageNo * pageSize;
                    }
                    //生成列表
                    for (var i = 0; i < sEnd; i++) {
                        //创建单元
                        build_unit(oData, $container, i);
                    }
                    //创建加载更多栏
                    if ($container.children().length < len) {
                        $container.append($('<div id="load_more" class="end_info">加载更多</div>'));
                    }
                    //pageNo+1
                    obj.pageNo++;
                    pageNo++;
                    //去除加载中栏（如果存在）
                    $('#loading').remove();
                    //判断是否滚动到底部
                    scrollFunc();
                }
            } else {
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
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