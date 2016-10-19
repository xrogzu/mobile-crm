$(document).bind('ready',function(){
    get_post();
    input_adjust();
    add_page_title();
    input_color_adjust();
    btn_touch_style();
    change_post();
    remove_rTop();
    $('textarea').autoHeight();
    textarea_num();
    //联系人姓名全为空格则清空
    contactNameReg();
    //在添加公司下跳转来的情况
    if(GetQueryString('from')=='acc_add'){
        $('#accountName button').html(GetQueryString('accountName'));
        $('#add_contact').html('保存并添加更多');
        btn_color_adjust();
        $('#accountName').attr('value',GetQueryString('accountId'));
        set_back(apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+GetQueryString('accountId'));
    }else if(GetQueryString('from')=='acc_info'){
        $('#accountName button').html(GetQueryString('accountName'));
        $('#add_contact').html('确定并添加联系人');
        btn_color_adjust();
        $('#accountName').attr('value',GetQueryString('accountId'));
        set_back(apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+GetQueryString('accountId'));
    }else{
        //从联系人列表添加联系人或从联系人详情编辑联系人（可修改公司）
        account_choose();
    }
    if(GetQueryString('from')=='con_list'){
        $('#add_contact').remove();
    }
    if(GetQueryString('from')=='con_info'){
        set_back(apppath+'dingtalk/contact/info.action?'+ddcommparams+'&contactId='+GetQueryString('contactId'));
    }
    if(GetQueryString('type')=='set'){
        set_info();
        $('#add_contact').remove();
        $('#confirm').html('保存');
    }else{
        //设置默认部门
        setPost = true;
        btn_color_adjust();
    }
    confirm();
    if(GetQueryString('type')=='from_acc_info'){
        $('#add_contact').remove();
        $('#confirm').html('保存');
    }
})
dd.ready(function(){
    control_back(backRemind);
})
function getStartValue(){
    setTimeout(function(){
        contactNameValue = $('#contactName input').val();
        phoneValue = $('#phone input').val();
        accountNameValue = $('#accountName button').html();
        departmentValue = $('#department input').val();
        postValue = $('#post input').val();
        telValue = $('#tel input').val();
        emailValue = $('#email input').val();
        addressValue = $('#address input').val();
        textareaValue = $('textarea').val();
        ownerPostValue = $('#ownerPost  button').html();
    },800);
}
var contactNameValue;
var phoneValue;
var accountNameValue;
var departmentValue;
var postValue;
var telValue;
var emailValue;
var addressValue;
var textareaValue;
var ownerPostValue;
function backRemind(){
    var isChanged = false;
    if(contactNameValue != $('#contactName input').val()||
        phoneValue != $('#phone input').val()||
        accountNameValue != $('#accountName button').html()||
        departmentValue != $('#department input').val()||
        postValue != $('#post input').val()||
        telValue != $('#tel input').val()||
        emailValue != $('#email input').val()||
        addressValue != $('#address input').val()||
        textareaValue != $('textarea').val()||
        ownerPostValue != $('#ownerPost  button').html()){
        isChanged = true;
    }
    var url;
    if(GetQueryString('from')=='acc_add'||GetQueryString('from')=='acc_info'){
        url = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+GetQueryString('accountId');
    }
    if(GetQueryString('from')=='con_list'){
        url = apppath+'/dingtalk/contact/list.action?'+ddcommparams;
    }
    if(GetQueryString('from')=='con_info'){
        url = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+GetQueryString('contactId');
    }
    if($('.ct').length>0){
        $('body').css('height','');
        $('.ct').remove();
        remove_rTop();
        add_page_title();
        $('body').css('overflow','');
    }else{
        if(isChanged){
            if($('.chooseShadow').length==0) {
                myChoose(url);
            }else{
                $('.chooseShadow').remove();
            }
        }else{
            location.href = url;
        }
    }
}

function contactNameReg(){
    $('#contactName input').bind('blur',function(){
        var str = $('#contactName input').val();
        while(str.lastIndexOf(" ")>=0){
            str = str.replace(" ","");
        }
        $('#contactName input').val(str);
    })
}
//编辑、新建title区分
function add_page_title(){
    if(GetQueryString('type')!=null&&GetQueryString('type')=='set'){
        set_title('编辑联系人');
    }else{
        set_title('新建联系人');
    }
}
function account_choose(){
    $('#accountName button').bind('click',function(){
        checkTable('选择公司');
        $.ajax({
            type:"get",
            url:apppath+"/dingtalk/account/dolist.action",
            async:true,
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
                        create_account_list(oData);
                    }
                }
            }
        })
    })
}
function create_account_list(data){
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
            $('#accountName button').html(label);
            btn_color_adjust();
            $('#accountName').attr('value',value);
            $('.ct').remove();
            remove_rTop();
            add_page_title();
            $('body').css({'overflow':'scroll','height':'auto'});
        })
    })
}
var reg_contactName = /^.{1,50}$/;
var reg_phone = /^\d+|[-#*]{1,30}$/;
var reg_department = /^.{0,20}$/;
var reg_post =/^.{0,50}$/;
var reg_tel =/^\d+|[-#*]{0,30}$/;
var reg_email =/^(([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})){0,100}$/;
var reg_address =/^.{0,250}$/;
function confirm(){
    $('#confirm').bind('click',function(){
        if(rtn_reg($('#contactName input'),reg_contactName)&&rtn_reg($('#phone input'),reg_phone)
            &&rtn_reg($('#department input'),reg_department)&&rtn_reg($('#post input'),reg_post)
            &&rtn_reg($('#tel input'),reg_tel)&&rtn_reg($('#email input'),reg_email)
            &&rtn_reg($('#address input'),reg_address)&&$('#accountName').attr('value')){
            info_submit();
        }else{
            if(!$('#accountName').attr('value')){
                myAlert('请选择公司')
            }
            reging();
        }
    })
    $('#add_contact').bind('click',function(){
        if(rtn_reg($('#contactName input'),reg_contactName)&&rtn_reg($('#phone input'),reg_phone)
            &&rtn_reg($('#department input'),reg_department)&&rtn_reg($('#post input'),reg_post)
            &&rtn_reg($('#tel input'),reg_tel)&&rtn_reg($('#email input'),reg_email)
            &&rtn_reg($('#address input'),reg_address)&&$('#accountName').attr('value')){
            info_submit('add_contact');
        }else{
            if(!$('#accountName').attr('value')){
                myDialog('请选择公司')
            }
            reging();
        }
    })
}
function reging(){
    if($('#contactName input').val()==''){
        confirm_reg($('#contactName input'),reg_contactName,'联系人名称不能为空');
    }else{
        confirm_reg($('#contactName input'),reg_contactName,'联系人名称格式错误');
    }
    if($('#phone input').val()==''){
        confirm_reg($('#phone input'),reg_phone,'联系人手机不能为空');
    }else{
        confirm_reg($('#phone input'),reg_phone,'联系人手机格式错误');
    }
    confirm_reg($('#department input'),reg_department,'部门格式错误');
    confirm_reg($('#post input'),reg_post,'职务格式错误');
    confirm_reg($('#tel input'),reg_tel,'座机格式错误');
    confirm_reg($('#email input'),reg_email,'邮箱格式错误');
    confirm_reg($('#address input'),reg_address,'地址格式错误');
}
//===================部门相关================
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
function info_submit(action){
    var url = '';
    if(GetQueryString('type')=='set'){
        url = apppath+"/dingtalk/contact/doupdate.action";
        var contactTemp ={
            contactName:$('#contactName input').val(),
            mobile:$('#phone input').val(),
            accountId:$('#accountName').attr('value'),
            depart:$('#department > input').val(),
            post:$('#post input').val(),
            phone:$('#tel input').val(),
            email:$('#email input').val(),
            address:$('#address input').val(),
            comment:$('#remark textarea').val(),
            dimDepart:$('#ownerPost').attr('value'),
            gender:1,
            ownerId:ownerId,
            id:GetQueryString('contactId')
        };
    }else{
        url = apppath+"/dingtalk/contact/docreate.action";
        var contactTemp ={
            contactName:$('#contactName input').val(),
            mobile:$('#phone input').val(),
            accountId:$('#accountName').attr('value'),
            depart:$('#department input').val(),
            post:$('#post input').val(),
            phone:$('#tel input').val(),
            email:$('#email input').val(),
            address:$('#address input').val(),
            comment:$('#remark textarea').val(),
            dimDepart:$('#ownerPost').attr('value'),
            gender:1,
            ownerId:ownerId
        };
    }
    console.log(contactTemp)
    $.ajax({
        type:"post",
        url:url,
        data:{
            contact:JSON.stringify(contactTemp)
        },
        async:true,
        beforeSend:function(){
            $('.confirm').attr('disabled',true);
            loader();
        },
        success:function(data){
            $('.loaderArea').remove();
            var oData = eval('('+data+')');
            if(oData.success == true){
                //新建/修改联系人埋点
                if(GetQueryString('type')=='set'){
                    var buriedPointType = 'contactUpdate';
                    buriedPoint(buriedPointType);
                }else{
                    var buriedPointType = 'contactAdd';
                    buriedPoint(buriedPointType);
                }
                myDialog('保存成功');
                setTimeout(function(){
                    if(action == 'add_contact'){
                        location.href = apppath+'/dingtalk/contact/add.action?'+ddcommparams+'&from=acc_add&accountId='+$('#accountName').attr('value')+'&accountName='+$('#accountName button').html();
                    }else{
                        if(GetQueryString('from')=='acc_add') {
                            location.href = apppath + '/dingtalk/account/info.action?'+ddcommparams+'&accountId=' + $('#accountName').attr('value');
                        }else if(GetQueryString('from')=='con_list'){
                            location.href = apppath + '/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+oData.entity.id;
                        }else if(GetQueryString('from')=='con_info'){
                            location.href = apppath + '/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+GetQueryString('contactId');
                        }else if(GetQueryString('from')=='acc_info'){
                            location.href = apppath + '/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+oData.entity.id+'&from=acc_info&accountId='+GetQueryString('accountId');
                        }
                    }
                },2000)
            }else if(oData.success == false){
                $('.confirm').removeAttr('disabled');
                if(oData.entity&&oData.entity.key&&oData.entity.key == 'mobile'){
                    myAlert('保存失败，手机号码重复');
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
var ownerId = xsyUser.id;
function set_info(){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/contact/get.action",
        data:{
            contactId:GetQueryString('contactId')
        },
        async:true,
        success:function(data){
            var oData = eval('('+data+')');
            console.log(oData)
            ownerId = oData.entity.ownerId;
            $('#contactName input').val(oData.entity.contactName);
            $('#phone input').val(oData.entity.mobile);
            $('#accountName').attr('value',oData.entity.accountId);
            $('#accountName button').html(oData.entity.accountName);
            btn_color_adjust();
            $('#post input').val(oData.entity.post);
            $('#tel input').val(oData.entity.phone);
            $('#email input').val(oData.entity.email);
            $('#address input').val(oData.entity.address);
            $('#remark textarea').val(oData.entity.comment);
            $('#ownerPost').attr('value',oData.entity.dimDepart);
            $('#ownerPost  button').html(oData.entity.dimDepartText);
            btn_color_adjust();
            getStartValue();
            $('textarea').autoHeight();
            textarea_num();
        }
    })
}
