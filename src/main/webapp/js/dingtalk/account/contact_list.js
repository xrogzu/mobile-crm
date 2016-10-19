/**
 * Created by dell on 2016/1/5.
 */
$(document).bind('ready',function(){
    $('#txt').bind('click',function(){
        location.href = apppath+'/dingtalk/account/search.action?'+ddcommparams;
    });
    create_contact_list();
    filter();
    set_right('新建',add_contact);
    $('#company').bind('click',function(){
        location.href = apppath+'/dingtalk/account/list.action?'+ddcommparams+'';
    });
    $('#search').bind('click',function(){
        location.href = apppath+'/dingtalk/account/search.action?'+ddcommparams+'';
    })
});
dd.ready(function() {
    control_back(con_list_back);
    var buriedPointType = 'contactList';
    buriedPoint(buriedPointType);
});
function add_contact(){
    location.href = apppath+'/dingtalk/contact/add.action?'+ddcommparams+'&from=con_list';
}
function con_list_back(){
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
        url:apppath+"/dingtalk/contact/getCreateDateTypeInfo.action",
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
                $('#filter_area .filter_btn').eq(0).addClass('active');
                $('#filter_area .filter_btn').each(function(index){
                    $(this).bind('click',function(){
                        pageNo = 0;
                        $(this).addClass('active');
                        $(this).siblings().removeClass('active');
                        $('#filter_area').hide();
                        $('#list').children().remove();
                        var createDateType = arrValue[$(this).index()];
                        if($(this).index()>0){
                            arr = [];
                            create_contact_list(createDateType);
                        }else{
                            arr = [];
                            create_contact_list();
                        }
                    })
                })
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
var pageNo = 0;
var pageSize = 20;
var arr = [];
//创建联系人列表
function create_contact_list(createDateType){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/contact/dolist.action",
        data:{pageNo:pageNo,pagesize:pageSize,createDateType:createDateType},
        async:true,
        success:function(data){
            var oData = eval('('+data+')');
            if(oData.success == false){
                if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/dingtalk/authorized/no.action?'+ddcommparams;
                }else {
                    myAlert('暂时无法获取数据，请稍后再试');
                }
            }else{
                for(var i = 0;i<oData.entity.records.length;i++){
                    arr.push(oData.entity.records[i]);
                }
                if(oData.entity.records.length == 0){
                    var empty = $('<div class="empty">暂无相关数据</div>');
                    $('#list').append(empty);
                }else{
                    build_list(arr);
                }
                if($('#list').find('.con').length<oData.entity.totalSize){
                    $('#loading').remove();
                    build_end('加载更多','load_more');
                    pageNo += 1;
                    scroll_load();
                }else{
                    $('#loading').remove();
                };
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    });
}
//上拉加载更多
function scroll_load(){
    $('#content').bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#load_more').offset().top<=$(window).height()-$('#load_more').height()){
                $('#load_more').remove();
                build_end('加载中…','loading');
                create_contact_list();
            }
        }
    })
}
function build_list(arr){
    var topWord;
    for(var i = pageNo*pageSize;i<arr.length;i++){
        if(/(?!^(\d+|[~!@#$%^&*?]+)$)^[\w~!@#$%\^&*?]+$/.test(arr[i].contactName_py.substr(0,1))){
            topWord = arr[i].contactName_py.substr(0,1);
        }else{
            topWord = '#';
        }
        //icon文字
        var botWord = icon_word(arr,i);
        //第一个块或与前一个不同的新块，生成top
        if(i==0){
            var li = $('<li></li>');
            var top = $('<div class="top boxSizing" id="'+topWord.toUpperCase()+'">'+topWord.toUpperCase()+'</div>');
            li.append(top);
        }else if(topWord!=arr[i-1].contactName_py.substr(0,1)&&!/^\d$/.test(arr[i].contactName_py.substr(0,1))){
            var li = $('<li></li>');
            var top = $('<div class="top boxSizing" id="'+topWord.toUpperCase()+'">'+topWord.toUpperCase()+'</div>');
            li.append(top);
        }
        $('#list').append(li);
        //生成con内容
        var con = document.createElement('div');
        $(con).attr('value',arr[i].id);
        con.className = 'con boxSizing';
        var contactName = arr[i].contactName;
        var post = arr[i].post;
        var accountName = arr[i].accountName;
        var mobile = arr[i].mobile;
        console.log(contactName)
        $(con).append($('<div class="pic boxSizing"><i class="cir">'+botWord+'</i></div><div class="info boxSizing"> <span class="name">'+contactName+'</span> '
        +'<span class="job">'+post+'</span><span class="company boxSizing">'+accountName+'</span><a class="tel" href="tel:'+mobile+'"></a></div>'));
        $('#list > li:last').append(con);
        $(con).find('.tel').bind('click',function(e){
            var objectId = $(this).parent().parent().attr('value');
            location.href = apppath+'/dingtalk/activityrecord/create.action?'+ddcommparams+'&belongId=2&objectId='+objectId+'&activityTypeId='+activeRecordTypeOje["TEL"]+'&from=con_info&contactId='+objectId;
            e.stopPropagation();
        })
        set_cir_color($(con).find('.cir'));
    }
    //添加点击事件
    con_click(arr);
}
function icon_word(arr,i){
    //不是汉字，显示前两个字符
    if(!( /^[\u4e00-\u9fa5]+$/).test(arr[i].contactName.substr(0,1))){
        return arr[i].contactName.substr(0,2);
    }
    //汉字显示后两字符
    else{
        return arr[i].contactName.substr(-2);
    }
}
function con_click(arr){
    $('#list .con').each(function(index){
        $(this).click(function(){
            location.href = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+$(this).attr('value');
            //本地存储
            local_set(arr[index].id,arr[index].contactName,2);
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