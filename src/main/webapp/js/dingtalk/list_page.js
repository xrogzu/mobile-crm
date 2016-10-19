/**
 * Created by dell on 2016/1/18.
 */
$(window).bind('load',function(){
    if(GetQueryString('type')=='level_choose'){
        level_choose();
    }else if(GetQueryString('type')=='industry_choose'){
        industry_choose();
    }else if(GetQueryString('type')=='member'){
        member();
    }else if(GetQueryString('type')=='owner'){
        owner();
    }else if(GetQueryString('type')=='account_choose'){
        account_choose();
    }
})


//========================================负责人页===========================================
function owner(){
    $('title').text('更改负责人');
    create_list();
}

var pageNo = 0;
var pageSize = 30;
var arr = [];
function member(){
    $('title').text('添加团队成员');
    create_list();
    create_confirm();
}
function set_active(){
    var mb_index = JSON.parse(localStorage.getItem('member_index'));
    for(var i=0;i<mb_index.length;i++){
        $('.item').eq(mb_index[i]).find('em').addClass('active');
        $('.item').eq(mb_index[i]).find('em').removeClass('radio');
    }
}
function create_list(){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/xsyuser/pager.action",
        data:{pageNo:pageNo,pagesize:pageSize},
        async:true,
        success:function(data){
            var oData = eval('('+data+')');
            if(oData.success == false){
                myAlert('数据加载出错');
            }else{
                var container = $('<div id="container" class="container"></div>');
                $('body').append(container);
                for(var i = 0;i<oData.entity.records.length;i++){
                    arr.push(oData.entity.records[i]);
                }
                if(oData.entity.records.length == 0){
                    var empty = $('<div class="empty">暂无相关数据</div>');
                    container.append(empty);
                }else{
                    build_list(container,arr);
                }
                if($('#container').find('.item').length<oData.entity.totalSize){
                    $('#loading').remove();
                    build_end('加载更多','load_more');
                    pageNo += 1;
                    scroll_load();
                }else{
                    $('#loading').remove();
                };
            }
        }
    });
}
function build_list(container,arr){

    //团队成员模式（多选）
    if(GetQueryString('type')=='member'){
        for(var i = pageNo*pageSize;i<arr.length;i++){
            //生成con内容
            var con = $('<div class="item"><em class="checkbox"></em><span>'+arr[i].label+'</span></div>');
            container.append(con);
        }
        checkbox_click();
        set_active();
    }
    //负责人模式（单选）
    if(GetQueryString('type')=='owner'){
        for(var i = pageNo*pageSize;i<arr.length;i++){
            //生成con内容
            var con = $('<div class="item"><em class="radio"></em><span>'+arr[i].label+'</span></div>');
            container.append(con);
        }
        radio_click(arr);
        if(GetQueryString('index')!=''){
            $('.item').eq(GetQueryString('index')).find('em').addClass('active');
            $('.item').eq(GetQueryString('index')).find('em').removeClass('radio');
        }
    }
}
function checkbox_click(){
    $('.item').each(function(index){
        $(this).bind('click',function(){
            if($(this).find('em').hasClass('active')){
                $(this).find('em').removeClass('active');
                $(this).find('em').addClass('checkbox');
            }else{
                $(this).find('em').addClass('active');
                $(this).find('em').removeClass('checkbox');
            }
        })
    })
}

//上拉加载更多
function scroll_load(){
    $('#container').bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#load_more').offset().top<=$(window).height()-$('#load_more').height()){
                $('#load_more').remove();
                build_end('加载中…','loading');
                create_list();
            }
        }
    })
}
function build_end(info,name){
    var end = $('<div id="'+name+'" class="end_info">'+info+'</div>');
    $('#container').append(end);
}

//==========================选择公司（from添加联系人）===============================
