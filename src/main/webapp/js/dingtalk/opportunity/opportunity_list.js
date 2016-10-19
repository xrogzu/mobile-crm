/**
 * Created by dell on 2016/2/17.
 */
$(document).bind('ready',function(){
    sort();
    screen();
    build_common_list(apppath+'/dingtalk/opportunity/dolist.action',$('#list .main'),'暂无销售机会',build_list_unit,listScrollFunc);
    set_right('新建',add_opportunity);
    search_show();
})
dd.ready(function(){
    control_back(opp_list_back);
    var buriedPointType = 'oppList';
    buriedPoint(buriedPointType);
})
//右上新建按钮
function add_opportunity(){
    location.href = apppath+'/dingtalk/opportunity/add.action?'+ddcommparams+'&from=opp_list';
}
//返回键
function opp_list_back(){
    if($('#screen_area').length>0&&$('#screen_area').css('display')=='block'){
        $('#screen_area').hide();
        $('.screen').removeClass('active');
    }else if($('#search_ground').length>0){
        $('#search_ground').remove();
    }else{
        location.href = apppath+'/dingtalk/statics/index.action?'+ddcommparams;
    }
}
var saleStageId = 0;
var closeType = 0;
var min = 0;
var max = 0;
var pageNo = 0;
var pageSize = 20;
var orderField = 'saleStageId, updatedAt desc';
var obj = {
    pageNo:pageNo,
    pagesize:pageSize,
    orderField:orderField
};
function sort(){
    $('.sort').bind('click',function(){
        $(this).find('span').addClass('active');
        $(this).siblings().find('span').removeClass('active');
        if($(this).index()==0){
            orderField = 'saleStageId';//按阶段  升序
        }else if($(this).index()==1){
            orderField = 'money desc';//按金额   降序
        }else if($(this).index()==2){
            orderField = 'recentActivityRecordTime desc';//按更新  降序
        }
        $('#list .main').children().remove();
        pageNo = 0;
        obj = {
            pageNo:pageNo,
            pagesize:pageSize,
            saleStageId:saleStageId,
            closeType:closeType,
            money_down:min,
            money_up:max,
            desc:false,
            orderField:orderField
        };
        build_common_list(apppath+'/dingtalk/opportunity/dolist.action',$('#list .main'),'暂无销售机会',build_list_unit,listScrollFunc);
    })
}
function screen(){
    $('.screen').bind('click',function(){
        if($(this).hasClass('active')){
            $(this).removeClass('active');
            $('#screen_area').hide();
        }else{
            $(this).addClass('active');
            if($('#screen_area').length>0){
                $('#screen_area').show();
            }else{
                var screen = $('<div id="screen_area" class="boxSizing"></div>');
                var stage_screen = $('<div class="stage_screen"><div class="title boxSizing">按阶段</div><div class="con"></div></div>');
                var time_screen = $('<div class="time_screen"><div class="title boxSizing">结单时段</div><div class="con"></div></div>');
                var money_screen = $('<div class="money_screen"><div class="title boxSizing">按金额</div><div class="con">' +
                '<span>金额范围（元）</span><input type="tel" maxlength="18" class="max fr"><span class="fr">-</span><input type="tel" maxlength="18" class="min fr"></div></div>');
                var reset = $('<div class="reset boxSizing">重置</div>');
                var confirm = $('<div class="confirm">确定</div>');
                screen.append(stage_screen);
                screen.append(time_screen);
                screen.append(money_screen);
                screen.append(reset);
                screen.append(confirm);
                $('body').append(screen);
                $.ajax({
                    url:apppath+'/dingtalk/opportunity/getSaleStage.action',
                    success:function(data){
                        var oData = eval('('+data+')');
                        if(oData.success==true){
                            for(var i=0;i<oData.entity.length;i++){
                                var stage_item = $('<div class="stage_item item boxSizing"><em></em><span>'+oData.entity[i].label+'</span></div>');
                                stage_item.attr('id',oData.entity[i].value);
                                stage_item.bind('click',function(){
                                    if($(this).hasClass('active')){
                                        $(this).removeClass('active');
                                        saleStageId = 0;
                                    }else {
                                        $(this).addClass('active');
                                        $(this).siblings().removeClass('active');
                                        saleStageId = Number($(this).attr('id'));
                                    }
                                });
                                $('.stage_screen .con').append(stage_item);
                            }
                        }
                    }
                })
                $.ajax({
                    url:apppath+'/dingtalk/opportunity/getCloseDateType.action',
                    success:function(data){
                        var oData = eval('('+data+')');
                        if(oData.success==true){
                            for(var i=0;i<oData.entity.length;i++){
                                var time_item = $('<div class="time_item item"><span>'+oData.entity[i].desc+'</span></div>');
                                time_item.attr('id',oData.entity[i].id);
                                time_item.bind('click',function(){
                                    if($(this).hasClass('active')){
                                        $(this).removeClass('active');
                                        closeType = 0;
                                    }else{
                                        $(this).addClass('active');
                                        closeType = Number($(this).attr('id'));
                                        $(this).siblings().removeClass('active');
                                    }
                                });
                                $('.time_screen .con').append(time_item);
                            }
                        }
                    }
                })
                $('.reset').bind('click',function(){
                    saleStageId = 0;
                    closeType = 0;
                    min = 0;
                    max = 0;
                    $('.item').removeClass('active');
                    $('.money_screen input').val('');
                })
                $('.confirm').bind('click',function(){
                    if($('.min').val()!=''){
                        min = Number($('.min').val());
                    }
                    if($('.max').val()!=''){
                        max = Number($('.max').val());
                    }
                    console.log(min+','+max)
                    if(min<0||max>9999999999){
                        myDialog('金额范围输入错误','without');
                    }else{
                        $('.screen').removeClass('active');
                        $('#list .main').children().remove();
                        pageNo = 0;
                        obj = {
                            pageNo:pageNo,
                            pagesize:pageSize,
                            saleStageId:saleStageId,
                            closeType:closeType,
                            money_down:min,
                            money_up:max,
                            orderField:orderField
                        };
                        build_common_list(apppath+'/dingtalk/opportunity/dolist.action',$('#list .main'),'暂无销售机会',build_list_unit,listScrollFunc);
                        $('#screen_area').hide();
                    }
                })
            };
        }
    })
}


//=========================================搜索======================================
function search_show(){
    $('#search').bind('click',function(){
        //商机搜索埋点
        var buriedPointType = 'oppSearch';
        buriedPoint(buriedPointType);
        var search_ground = $('<div id="search_ground" class="boxSizing"><div class="search_area"><div class="input_area">' +
        '<input id="txt" type="text" placeholder="搜索销售机会"/><div class="delete_icon"></div></div><span id="exit">取消</span></div>' +
        '<div id="search_content"></div></div>');
        $('body').append(search_ground);
        $('#txt').focus();
        search();
        search_exit();
    })
}
//退出搜索页的取消按钮
function search_exit(){
    $('#exit').bind('click',function(){
        $('#search_ground').remove();
    })
}
//关键字
var val = null;
function search(){
    show_history();
    //搜索框键入信息
    $('#txt').bind('change',function(){
        input_delete();
        val = $('#txt').val();
        if(val.substr(0,1)==' '){
            val = val.trim();
        }
        if(val == ''){
            $('.list').remove();
            $('.dialog').remove();
            show_history();
        }else{
            show_word(val);
        }
    });
}
//输入框右侧清空输入按钮
function input_delete(){
    if($('#txt').val() == ''){
        $('.delete_icon').hide();
    }else{
        $('.delete_icon').show();
    };
    $('.delete_icon').bind('click',function(){
        $(this).hide();
        $('#txt').val('');
        $('#content>.list').remove();
        $('.dialog').remove();
        show_history();
    })
}
//展示最近搜索
function show_history(){
    $('#search_content').children().remove();
    console.log(userId)
    if(localStorage.getItem(userId)!='undefined'){
        var ls=JSON.parse(localStorage.getItem(userId));
        console.log(ls)
        if(ls.opportunityHistory){
            if(ls.opportunityHistory.length!=0){
                create_history_list(ls);
            }else{
                create_history_null();
            };
        }else{
            create_history_null();
        };
    }else{
        create_history_null();
    }
}
//创建历史纪录列表
function create_history_list(ls){
    //最多显示20条最近历史搜索记录
    if(ls.opportunityHistory){
        if(ls.opportunityHistory.length<=20){
            for (var i = ls.opportunityHistory.length - 1; i >= 0; i--){
                build_history_list(ls,i);
            }
        }else{
            for (var i = ls.opportunityHistory.length - 1; i >= ls.opportunityHistory.length - 20; i--){
                build_history_list(ls,i);
            }
        }
        create_delete();
    }else{
        create_history_null();
    }
}

function build_history_list(ls,i){
    var oList = document.createElement('div');
    oList.className = 'boxSizing history_list';
    var name = ls.opportunityHistory[i].name;
    var id = ls.opportunityHistory[i].id;
    $(oList).append($('<span class="name">'+name+'</span>'));
    $(oList).attr('id',id);
    $(oList).attr('name',name);
    var oExit = document.createElement('div');
    oExit.innerHTML = '<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDA5MDMwNDRCMkU5MTFFNUE4MDNFMkU5NkI5N0FEOEMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDA5MDMwNDVCMkU5MTFFNUE4MDNFMkU5NkI5N0FEOEMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpCMTRGRjgyMUIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpCMTRGRjgyMkIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Prom4REAAADHSURBVHjanNVNCoMwEAXgydAb9CJ21QvYVb2uK91rwUUPYs9gZ2ACIeTnJQNv48gHCQ91+3ZsROQkb8mP+uYumSUXG/aULLbowVYznIKT5Ct5dKAeG8yYFDwlrwBdQTTG1DjZliE6AGgS0wUHL6FoFotBBC1iKbCEVjGdW+aOPLoEKNWwEphCqYbljhzOBT6DwPjOoEpxQ8+gnnJDz6CecktpEZRbSoug3IhVUe7Aiijbl9ZjI4jl0Nkf+WNYzy/Ao2rQX4ABAORFZTtFQ9/RAAAAAElFTkSuQmCC"/>';
    oExit.className = 'exit';
    $(oExit).bind('click',function(){
        ls.opportunityHistory.remove(i);
        localStorage.setItem(userId,JSON.stringify(ls));
        $(oList).remove();
        if($('#search_content').find('.history_list').length==0){
            $('#search_content > .clear').remove();
            create_history_null();
        }
    });
    oList.appendChild(oExit);
    $('#search_content').append(oList);
    $(oList).bind('click',function(){
        local_set($(this).attr('id'),$(this).attr('name'),3);
        location.href = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&opportunityId='+$(this).attr('id');
    })
}
//清除历史记录
function create_delete(){
    var _delete = document.createElement('div');
    _delete.innerHTML = "清除全部历史记录";
    _delete.className="clear"
    _delete.onclick = function(){
        var ls = JSON.parse(localStorage.getItem(userId));
        ls.opportunityHistory=[];
        localStorage.setItem(userId,JSON.stringify(ls));
        $('#search_content > .history_list').remove();
        $('#search_content > .clear').remove();
        create_history_null();
    };
    $('#search_content').append(_delete);
}
//暂无历史记录
function create_history_null(){
    var history_null = document.createElement('div');
    history_null.innerHTML = '暂无历史记录';
    history_null.className = 'null';
    $('#search_content').append(history_null);
}
function show_word(val){
    pageNo = 0;
    obj = {
        pageNo:pageNo,
        pagesize:pageSize,
        opportunityName:val
    }
    create_word_list();
}
function create_word_list(){
    $('#search_content').children().remove();
    $('#search_content').append($('<div class="boxSizing main"></div>'));
    build_common_list(apppath+'/dingtalk/opportunity/dolist.action',$('#search_content .main'),'无相关销售机会',build_word_unit,wordScrollFunc);
}
//搜索单元创建
function build_word_unit(oData,$container,i){
    console.log('build_word_unit')
    var list = $('<div class="boxSizing opportunityList">'+oData.entity.records[i].opportunityName+'</div>');
    list.attr('value',oData.entity.records[i].id);
    list.bind('click',function(){
        local_set($(this).attr('value'),$(this).html(),3);
        location.href = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&opportunityId='+$(this).attr('value');
    })
    $container.append(list);
}
//搜索下拉加载
function wordScrollFunc(){
    $('#search_content').bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#search_content .main').offset().top<=$(window).height()-$('#search_content .main').height()+3){
                //加载更多替换为加载中
                $('#load_more.end_info').remove();
                $('#search_content .main').append($('<div id="loading" class="end_info">加载中…</div>'));
                //加载后续列表
                build_common_list(apppath+'/dingtalk/opportunity/dolist.action',$('#search_content .main'),'无相关销售机会',build_word_unit,wordScrollFunc);
            }
        }
    })
}
//列表页单元创建
function build_list_unit(oData,$container,i){
    var item = oData.entity.records[i];
    var saleStageText = item.saleStageText;
    var opportunityName = item.opportunityName;
    var Money = item.money;
    var accountName = item.accountName;
    var id = item.id;
    var saleStageOrder = item.saleStageOrder;
    //阶段百分比
    var jieduan_p = item.saleStagePercentage;
    var li = $('<li class="boxSizing"><em class="icon jieduan'+saleStageOrder+'"></em><div class="info"><div class="top">' +
    '<span class="opportunityName">'+opportunityName+'</span><span class="Money">'+Money+'元</span>' +
    '</div><div class="bot"><p class="accountName">'+accountName+'</p><p class="jieduan">阶段：'+saleStageText+'</p>' +
    '<span class="jieduan_p">'+jieduan_p+'%</span></div></div></li>');
    li.attr('id',id);
    li.attr('name',opportunityName);
    li.bind('click',function(){
        local_set($(this).attr('id'),$(this).attr('name'),3);
        location.href = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&opportunityId='+$(this).attr('id')+'&from=opp_list';
    })
    $container.append(li);
}
//列表页下拉加载
function listScrollFunc(){
    $('#content').bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#load_more').offset().top<=$(window).height()-$('#load_more').height()-53){
                $('#load_more.end_info').remove();
                $('#list .main').append($('<div id="loading" class="end_info">加载中…</div>'));
                build_common_list(apppath+'/dingtalk/opportunity/dolist.action',$('#list .main'),'暂无销售机会',build_list_unit,listScrollFunc)
            }
        }
    })
}
var userId = xsyUser.id;
//本地存储
function local_set(id,name,kindle){
    if(kindle == 1||kindle == 2){
        var val = {"id":id,"name":name,"kindle":kindle};
        if(localStorage.getItem(userId)){
            var ls=JSON.parse(localStorage.getItem(userId));
            //去重，后点排在前
            if(ls.accountHistory){
                for(var j=0;j<ls.accountHistory.length;j++){
                    if(ls.accountHistory[j].id==id){
                        ls.accountHistory.remove(j);
                    }
                }
                ls.accountHistory.push(val);
            }else{
                var ls = {"accountHistory":[val]}
            }
        }else{
            var ls = {"accountHistory":[val]}
        }
    }else if(kindle == 3){
        var val = {"id":id,"name":name};
        if(localStorage.getItem(userId)!='undefined'){
            var ls=JSON.parse(localStorage.getItem(userId));
            //去重，后点排在前
            if(ls.opportunityHistory){
                for(var j=0;j<ls.opportunityHistory.length;j++){
                    if(ls.opportunityHistory[j].id==id){
                        ls.opportunityHistory.remove(j);
                    }
                }
                ls.opportunityHistory.push(val);
            }else{
                var ls = {"opportunityHistory":[val]}
            }
        }else{
            var ls = {"opportunityHistory":[val]}
        }
    }
    localStorage.setItem(userId,JSON.stringify(ls));
};