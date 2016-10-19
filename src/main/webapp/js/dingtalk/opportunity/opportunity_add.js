/**
 * Created by dell on 2016/2/18.
 */
$(document).bind('ready',function(){
    input_adjust();
    remove_rTop();
    //owner_choose();
    $('textarea').autoHeight();
    textarea_num();
    get_accountName();
    change_post();
    get_post();
    confirm();
    select_date();
    if(GetQueryString('type') == 'set'){
        set_info();
        $('#accountName .value').css('background','none');
    }else{
        account_choose();
        //设置默认部门
        setPost = true;
    }
})

dd.ready(function(){
    add_page_title();
    control_back(backRemind);
})
function getStartValue(){
    opportunityNameValue = $('#opportunityName input').val();
    accountIdValue = $('#accountName .value').attr('id');
    moneyValue = $('#money input').val();
    dateValue = $('#date input').val();
    textareaValue = $('textarea').val();
    ownerPostValue = $('#ownerPost  button').html();
}
var opportunityNameValue;
var accountIdValue;
var moneyValue;
var dateValue;
var textareaValue;
var ownerPostValue;
function backRemind(){
    var isChanged = false;
    if(opportunityNameValue != $('#opportunityName input').val()||
    accountIdValue != $('#accountName .value').attr('id')||
    moneyValue != $('#money input').val()||
    dateValue != $('#date input').val()||
    ownerPostValue != $('#ownerPost  button').html()||
    textareaValue != $('textarea').val()){
        isChanged = true;
    }
    var url;
    if(GetQueryString('from')=='opp_list'){
        url = apppath+'/dingtalk/opportunity/list.action?'+ddcommparams;
    }else if(GetQueryString('from')=='opp_info'){
        url = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&opportunityId='+GetQueryString('opportunityId');
    }else if(GetQueryString('from')=='acc_info'){
        url = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+GetQueryString('accountId');
    }else if(GetQueryString('from')=='con_info'){
        url = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+GetQueryString('contactId');
    }
    if($('.ct').length>0){
        $('body').css('height','');
        $('.ct').remove();
        remove_rTop();
        add_page_title();
        $('body').css('overflow','');
    }else {
        if (isChanged) {
            if ($('.chooseShadow').length == 0) {
                myChoose(url);
            } else {
                $('.chooseShadow').remove();
            }
        } else {
            location.href = url;
        }
    }
}
//编辑、新建title区分
function add_page_title(){
    if(GetQueryString('type')!=null&&GetQueryString('type')=='set'){
        set_title('编辑销售机会');
    }else{
        set_title('新建销售机会');
    }
}
function get_accountName(){
    if(GetQueryString('from')=='acc_info'||GetQueryString('from')=='con_info'){
        $.ajax({
            url:apppath+'/dingtalk/account/get.action',
            data:{
                accountId:GetQueryString('accountId')
            },
            success:function(data){
                var oData = eval('('+data+')');
                $('#accountName .value').text(oData.entity.accountName);
                $('#accountName .value').css('color','#222');
                $('#accountName').attr('value',GetQueryString('accountId'));
                getStartValue();
            }
        })
    }
}
function select_date(){
    if(GetQueryString('type') != 'set'){
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth()+1;
        var day = date.getDate();
        if(month<10){
            month = '0'+String(month);
        }
        if(day<10){
            day = '0'+String(day);
        }
        var dateStr = year+'-'+month+'-'+day;
        $('#date .value').text(dateStr);
    }
    dd.ready(function(){
        $('#date .value').bind('click',function(){
            dd.biz.util.datepicker({
                format: 'yyyy-MM-dd',
                value: dateStr, //默认显示日期
                onSuccess : function(result) {
                    $('#date .value').text(result.value);
                },
                onFail : function() {}
            })
        })
    })
}
var url = apppath+'/dingtalk/opportunity/docreate.action';
if(GetQueryString('type')=='set'){
    url = apppath+'/dingtalk/opportunity/doupdate.action';
}
var reg_opportunityName = /^.{1,50}$/;
var reg_money = /^\d{1,10}$/;
var reg_accountName =/\S/;
function confirm(){
    $('#confirm').bind('touchstart',function(){
        $(this).addClass('touching');
    })
    $('#confirm').bind('touchend',function(){
        $(this).removeClass('touching');
    })
    $('#confirm').bind('click',function(){
        var accountId = '';
        if($('#accountName').attr('value')){
            accountId = $('#accountName').attr('value');
        }
        if(rtn_reg($('#opportunityName .value'),reg_opportunityName)&&accountId!=''&&rtn_reg($('#money .value'),reg_money)){
            submit(url);
        }else{
            confirm_reg($('#opportunityName .value'),reg_opportunityName,'销售机会名称格式错误');
            if(accountId==''){
                confirm_reg($('#accountName .value'),reg_accountName,'请选择公司');
            }
            confirm_reg($('#money .value'),reg_money,'预计金额格式错误');
        }
    })
}
function submit(url){
    var opportunityName = $('#opportunityName .value').val();
    var accountId = Number($('#accountName').attr('value'));
    var closeDate = $('#date .value').text();
    var money = Number($('#money .value').val());
    var comment = $('#textarea').val();
    var subData = {
        ownerId:ownerId,
        opportunityName:opportunityName,
        accountId:accountId,
        closeDate:closeDate,
        money:money,
        comment:comment,
        opportunityType:1,
        dimDepart:$('#ownerPost').attr('value')
    }
    if(GetQueryString('type') == 'set'){
        subData.id = GetQueryString('opportunityId');
    }
    console.log(JSON.stringify(subData))
    $.ajax({
        url:url,
        type:'post',
        data:{opportunity:JSON.stringify(subData)},
        beforeSend:function(){
            $('#confirm').attr('disabled',true);
            loader();
        },
        success:function(data){
            $('.loaderArea').remove();
            var oData = eval('('+data+')');
            if(oData.success == true){
                //新建/修改商机埋点
                if(GetQueryString('type')=='set'){
                    var buriedPointType = 'oppUpdate';
                    buriedPoint(buriedPointType);
                }else{
                    var buriedPointType = 'oppAdd';
                    buriedPoint(buriedPointType);
                }
                var opportunityId = oData.entity.id;
                if(GetQueryString('type') == 'set'){
                    opportunityId = GetQueryString('opportunityId');
                }
                myDialog('保存成功');
                var url;
                if(GetQueryString('from')=='opp_list'){
                    url = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&&opportunityId='+opportunityId+'&from=opp_list';
                }else if(GetQueryString('from')=='opp_info'){
                    url = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&&opportunityId='+opportunityId+'&from=opp_list';
                }else if(GetQueryString('from')=='acc_info'){
                    url = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&&opportunityId='+opportunityId+'&from=acc_info&accountId='+GetQueryString('accountId');
                }else if(GetQueryString('from')=='con_info'){
                    url = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&&opportunityId='+opportunityId+'&from=con_info&contactId='+GetQueryString('contactId');
                }
                time_out_location(url);
            }else if(oData.success == false) {
                console.log(oData);
                $('#confirm').removeAttr('disabled');
                if(oData.entity&&oData.entity.key&&oData.entity.key == 'opportunityName'){
                    myAlert('保存失败，销售机会名称重复');
                }else if(oData.entity&&oData.entity.status&&oData.entity.status == '0000002'){
                    myAlert('保存失败，不能包含表情符');
                }else if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/dingtalk/authorized/no.action?'+ddcommparams;
                }else{
                    myAlert('保存失败，请检查输入信息');
                }
            }
        },
        error:function(){
            $('.loaderArea').remove();
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
function account_choose(){
    $('#accountName .value').bind('click',function(){
        checkTable('选择公司');
        $.ajax({
            url:apppath+"/dingtalk/account/dolist.action",
            success:function(data) {
                var oData = eval('('+data+')');
                if(oData.success == false){
                    myAlert('暂时无法获取数据，请稍后再试');
                }else{
                    if(oData.entity.records.length==0){
                        var no_contact = $('<div class="no_contact">暂无公司，请新建公司</div>');
                        $('body').append(no_contact);
                        setTimeout(function(){
                            $('.no_contact').remove();
                        },2000);
                    }else {
                        create_list(oData);
                    }
                }
            }
        })
    })
}
function create_list(data){
    console.log(data)
    var container = $('<div class="container"></div>');
    for(var i=0;i<data.entity.records.length;i++){
        var list = $('<div class="item boxSizing" href="'+data.entity.records[i].id+'"><em class="radio"><i></i></em><span>'+data.entity.records[i].accountName+'</span></div>');
        container.append(list);
    }
    $('.ct').append(container);
    for(var i=0;i<$('.ct .item').length;i++){
        if($('#accountName button').html()==$('.ct .item').eq(i).find('span').text()){
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    $('.ct .item').each(function(index){
        $(this).bind('click',function(){
            $(this).find('em').addClass('active');
            $(this).siblings().find('em').removeClass('active');
            var value = data.entity.records[$(this).index()].id;
            var index = $(this).index();
            var label = data.entity.records[$(this).index()].accountName;
            $('#accountName .value').html(label);
            $('#accountName .value').css('color','#222');
            $('#accountName').attr('value',value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({'overflow':'scroll','height':'auto'});
        })
    })
}
//===========================================改变所属部门==================================
var postData;
var setPost = false;
function get_post(){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/department/mydepartment.action",
        async:true,
        success:function(data){
            console.log('部门'+data)
            var oData = eval('('+data+')');
            if(oData.success == false){
                myAlert('暂时无法获取数据，请稍后再试');
            }else{
                postData = oData;
                if(setPost){
                    $('#ownerPost').attr('value',postData.entity.departs[0].id);
                    $('#ownerPost  button').html(postData.entity.departs[0].text);
                    btn_color_adjust();
                    getStartValue();
                }
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    });
}
function change_post(){
    $('#ownerPost button').bind('click',function(){
        checkTable('选择所属部门');
        var container = $('<div id="container" class="container"></div>');
        $('.ct').append(container);
        build_list(container,postData);
    })
}
function build_list(container,data){
    for(var i=0;i<data.entity.departs.length;i++){
        var con = $('<div class="item"><em class="radio"><i></i></em><span>'+data.entity.departs[i].text+'</span></div>');
        con.attr('value',data.entity.departs[i].id);
        container.append(con);
    }
    for(var i=0;i<$('.ct .item').length;i++){
        if($('#ownerPost button').html()==$('.ct .item').eq(i).find('span').text()){
            $('.ct .item').eq(i).find('em').addClass('active');
        }
    }
    radio_click(data);
}
function radio_click(data){
    $('.ct .item').each(function(index){
        $(this).bind('click',function(){
            if($(this).find('em').hasClass('active')){}
            else{
                $(this).find('em').addClass('active');
                $(this).siblings().find('em').removeClass('active');
            }
            var value = $(this).attr('value');
            var label = $(this).find('span').text();
            $('#ownerPost > button').html(label);
            btn_color_adjust();
            $('#ownerPost').attr('value',value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({'overflow':'scroll','height':'auto'});
        })
    })
}
var ownerId = xsyUser.id;
function set_info(){
    $.ajax({
        url: apppath + '/dingtalk/opportunity/get.action',
        data: {opportunityId: GetQueryString('opportunityId')},
        success: function (data) {
            console.log(data)
            var oData = eval('(' + data + ')');
            if (oData.success == true) {
                ownerId = oData.entity.ownerId;
                var opportunityName = oData.entity.opportunityName;
                var accountName = oData.entity.accountName;
                var accountId = oData.entity.accountId;
                var money = oData.entity.money;
                var closeDate = oData.entity.closeDate.substr(0,10);
                var comment = oData.entity.comment;
                $('#opportunityName .value').val(opportunityName);
                $('#accountName .value').text(accountName);
                $('#accountName').attr('value',accountId);
                $('#accountName .value').css('color','black');
                $('#money .value').val(money);
                $('#date .value').text(closeDate);
                $('#ownerPost').attr('value',oData.entity.dimDepart);
                $('#ownerPost  button').html(oData.entity.dimDepartText);
                btn_color_adjust();
                $('#textarea').val(comment);
                $('#num').text($('#textarea').val().length+'/1000');
                $('textarea').autoHeight();
                getStartValue();
            }
        }
    });
}
















