/**
 * Created by dell on 2016/1/5.
 */
$(document).bind('ready',function(){
    earlyLoad();
    $('#txt').bind('click',function(){
        location.href = apppath+'/dingtalk/account/search.action?'+ddcommparams;
    });
    create_account_list(0);
    filter();
    $('#linkman').bind('click',function(){
        location.href = apppath+'/dingtalk/contact/list.action?'+ddcommparams;
    });
    $('#search').bind('click',function(){
        location.href = apppath+'/dingtalk/account/search.action?'+ddcommparams;
    });
    set_right('新建',add_account);
});
dd.ready(function() {
    var buriedPointType = 'accountList';
    buriedPoint(buriedPointType);
    control_back(acc_list_back);
});
function add_account(){
    location.href = apppath+'/dingtalk/account/add.action?'+ddcommparams+'&from=acc_list';
}
function acc_list_back(){
    if($('#filter_area').css('display')=='block'){
        $('#filter_area').hide();
        $('.filter').removeClass('active');
        $('.filter').addClass('normal');
    }else{
        location.href = apppath+'/dingtalk/statics/index.action?'+ddcommparams;
    }
}
function filter(){
    stopScroll($('#filter_area'));
    var filter_type = 0;
    $('#filter').bind('click',function(){
        if($('#filter_area .content').find('.filter_btn').length<=1){
            create_filter();
        }
        if(filter_type == 0){
            $('#filter').addClass('active');
            $('#filter').removeClass('normal');
            filter_type = 1;
        }else{
            $('#filter').addClass('normal');
            $('#filter').removeClass('active');
            filter_type = 0;
        }
        $('#filter_area').toggle();
    })
}
function create_filter(){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/account/getLevelInfo.action",
        async:true,
        success:function(data) {
            var oData = eval('('+data+')');
            if(oData.success == false){
                myAlert('暂时无法获取数据，请稍后再试');
            }else {
                var arrValue = [];
                for(var i=0;i<oData.entity.length;i++){
                    var fbtn = $('<div class="filter_btn boxSizing">'+oData.entity[i].label+'</div>');
                    arrValue.push(oData.entity[i].value);
                    $('#filter_area .content').append(fbtn);
                }
                $('#filter_area .filter_btn').each(function(index){
                    $(this).bind('click',function(){
                        pageNo = 0;
                        $(this).addClass('active');
                        $(this).siblings().removeClass('active');
                        $('#filter_area').hide();
                        $('#list').children().remove();
                        if($(this).index()>0){
                            var level = arrValue[$(this).index()-1];
                            arr = [];
                            create_account_list(level);
                        }else{
                            arr = [];
                            create_account_list(0);
                        }
                    })
                })
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络');
        }
    })
}
var pageNo = 0;
var pageSize = 20;
var arr = [];
function earlyLoad(){
    //先加载缓存数据
    if(xsyUser){
        if(localStorage.getItem(xsyUser.id)){
            var ls = JSON.parse(localStorage.getItem(xsyUser.id));
            if(ls.accountList){
                if(ls.accountList.accListData){
                    accListByData(ls.accountList.accListData);
                    pageNo = 0;
                }
            }
        }
    }
}
//创建公司列表
function create_account_list(level){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/account/dolist.action",
        data:{pageNo:pageNo,pagesize:pageSize,level:level},
        async:true,
        success:function(data){
            var oData = eval('('+data+')');
            if(oData.success == false){
                if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/dingtalk/authorized/no.action?'+ddcommparams;
                }else{
                    myAlert('暂时无法获取数据，请稍后再试');
                }
            }else {
                if(level==0){
                    if(xsyUser){
                        var ls;
                        if(localStorage.getItem(xsyUser.id)){
                            ls = JSON.parse(localStorage.getItem(xsyUser.id));
                            ls.accountList = {'accListData':oData}
                        }else{
                            ls = {'accountList':{'accListData':oData}}
                        }
                        localStorage.setItem(xsyUser.id,JSON.stringify(ls));
                    }
                }
                if(level==0){
                    $('#list').children().remove();
                }
                accListByData(oData);
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络');
        }
    });
};
function accListByData(oData){
    arr = [];
    for (var i = 0; i < oData.entity.records.length; i++) {
        arr.push(oData.entity.records[i]);
    }
    if(oData.entity.records.length == 0){
        var empty = $('<div class="empty">暂无相关数据</div>');
        $('#list').append(empty);
    }else{
        build_list(arr);
    }
    if ($('#list').find('.con').length < oData.entity.totalSize) {
        $('#loading').remove();
        build_end('加载更多', 'load_more');
        pageNo += 1;
        scroll_load();
    } else {
        $('#loading').remove();
    }
}
//上拉加载更多
function scroll_load(){
    $('#content').bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#load_more').offset().top<=$(window).height()-$('#load_more').height()-55){
                $('#load_more').remove();
                build_end('加载中…','loading');
                create_account_list();
            }
        }
    })
}
function build_list(arr){
    var topWord;
    for(var i = 0;i<arr.length;i++){
        if(/(?!^(\d+|[~!@#$%^&*?]+)$)^[\w~!@#$%\^&*?]+$/.test(arr[i].accountName_py.substr(0,1))){
            topWord = arr[i].accountName_py.substr(0,1);
        }else{
            topWord = '#';
        }
        //第一个块或与前一个不同的新块，生成top
        if(i==0){
            var li = $('<li></li>');
            var top = $('<div class="top boxSizing" id="'+topWord.toUpperCase()+'">'+topWord.toUpperCase()+'</div>');
            li.append(top);
        }else if(topWord!=arr[i-1].accountName_py.substr(0,1)&&!/^\d$/.test(arr[i].accountName_py.substr(0,1))){
            var li = $('<li></li>');
            var top = $('<div class="top boxSizing" id="'+topWord.toUpperCase()+'">'+topWord.toUpperCase()+'</div>');
            li.append(top);
        }
        $('#list').append(li);
        //生成con内容
        var con = document.createElement('div');
        $(con).attr('value', arr[i].id);
        con.className = 'con boxSizing';
        $(con).append($('<div class="info"> <span class="accountName">'+arr[i].accountName+'</span><em class="sign_in"></em></div>'));
        $(con).find('em').bind('click',function(e){
            location.href = apppath+'/dingtalk/activityrecord/qiandao.action?'+ddcommparams+'&belongId=1&objectId='+$(this).parent().parent().attr('value')+'&activityTypeId='+activeRecordTypeOje["SIGN_IN"]+'&from=acc_info&accountId='+$(this).parent().parent().attr('value');
            e.stopPropagation();
        })
        $('#list > li:last').append(con);
    }
    //添加点击事件
    con_click(arr);
}
function con_click(arr){
    $('#list .con').each(function(index){
        $(this).click(function(){
            //返回目标ID
            console.log(this.href);
            //本地存储
            local_set(arr[index].id,arr[index].accountName,1);
            location.href = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+$(this).attr('value');
        })
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

//创建底部提示
function build_end(info,name){
    var end = $('<div id="'+name+'" class="end_info">'+info+'</div>');
    $('#list').append(end);
}
