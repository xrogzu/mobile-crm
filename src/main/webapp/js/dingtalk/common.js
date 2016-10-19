 $(document).bind('ready',function(){
     //底栏跳转
     footerSkip();
     //主体被删除返回的提示
     errInfo();
 })
function footerSkip(){
    $('footer .account').bind('click',function(){
        location.href = apppath+'/dingtalk/account/list.action?'+ddcommparams;
    })
    $('footer .opportunity').bind('click',function(){
        location.href = apppath+'/dingtalk/opportunity/list.action?'+ddcommparams;
    })
    $('footer .service').bind('click',function(){
        location.href = apppath+'/dingtalk/serveice/index.action?'+ddcommparams;
    })
    $('footer .index').bind('click',function(){
        location.href = apppath+'/dingtalk/statics/index.action?'+ddcommparams;
    })
}
 function errInfo(){
     if(GetQueryString('err')){
         if(GetQueryString('err')=='acc_deleted'){
             myDialog('该公司已被删除','without');
         }else if(GetQueryString('err')=='con_deleted'){
             myDialog('该联系人已被删除','without');
         }else if(GetQueryString('err')=='opp_deleted'){
             myDialog('该销售机会已被删除','without');
         }else if(GetQueryString('err')=='acc_noright'){
             myDialog('您无权查看该公司','without');
         }else if(GetQueryString('err')=='con_noright'){
             myDialog('您无权查看该联系人','without');
         }else if(GetQueryString('err')=='opp_noright'){
             myDialog('您无权查看该销售机会','without');
         }
     }
 }
//禁止ios弹性滑动
dd.ready(function(){
    dd.ui.webViewBounce.disable();
    //======================同步钉钉头部title======================
    var head = document.getElementsByTagName('head')[0];
    var title = head.getElementsByTagName('title')[0].innerHTML;
    dd.biz.navigation.setTitle({
        title : title,//控制标题文本，空字符串表示显示默认文本
        onSuccess : function(result) {},
        onFail : function(err) {}
    });
})



//禁止滑动操作
function stopScroll($obj){
     function stopScrolling( touchEvent ) {
         touchEvent.preventDefault();
     }
     $obj.bind( 'touchmove' , stopScrolling , false );
 }

//控制返回动作（点击返回按钮，执行func）
function control_back(func){
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    if(isAndroid){
        function backAction(e){
            e.preventDefault();
            func();
        }
        document.addEventListener('backbutton', function(e){backAction(e)}, false);
    }else if(isiOS){
        dd.biz.navigation.setLeft({
            control: true,//是否控制点击事件，true 控制，false 不控制， 默认false
            onSuccess : function(result) {
                func();
            },
            onFail : function(err) {}
        });
    }
}

//设置返回路径
function set_back(url){
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    if(isAndroid){
        document.addEventListener('backbutton', function(e) {
            e.preventDefault();
            location.href = url;
        }, false);
    }else if(isiOS){
        dd.biz.navigation.setLeft({
            show: true,//控制按钮显示， true 显示， false 隐藏， 默认true
            control: true,//是否控制点击事件，true 控制，false 不控制， 默认false
            showIcon: true,//是否显示icon，true 显示， false 不显示，默认true； 注：具体UI以客户端为准
            onSuccess : function(result) {
                location.href = url;
            },
            onFail : function(err) {}
        });
    }
}

//头像颜色库
var arr_color = ['#78c06e','#f65e8d','#f6bf26','#3bc2b5','#5c6bc0','#f65e5e','#5e97f6','#9a89b9','#bd84cd','#6bb5ce','#a1887f','#ff943e','#5ec9f6','#c5cb63','#ff8e6b','#78919d'];
function set_cir_color(obj){
    if(obj.color == null){
        obj.color = arr_color[Math.floor(Math.random()*arr_color.length)];
    };
    obj.css('backgroundColor',obj.color);
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
     if( index >= 0   &&  index <length ){
         this.splice(index, 1);
     }
 };

function set_title(title){
    dd.ready(function(){
        dd.biz.navigation.setTitle({
            title : title,
            onSuccess : function(result) {},
            onFail : function(err) {}
        });
    })
}
function remove_rTop(){
    dd.ready(function() {
        dd.biz.navigation.setRight({
            control: true,
            show:false
        })
    })
}
function set_right(text,func){
    dd.ready(function() {
        dd.biz.navigation.setRight({
            control: true,
            text:text,
            onSuccess:function(){
                func();
            }
        })
    })
}
function checkTable(title){
    $('.ct').remove();
    var ct = $('<div class="ct"></div>');
    $('body').append(ct);
    $(window).scrollTop(0);
    $('body').css({'height':'100%','overflow':'hidden'});
    set_title(title);
    remove_rTop();
}

//textarea高度自适应
jQuery.fn.extend({
    autoHeight: function(){
        return this.each(function(){
            var $this = jQuery(this);
            if( !$this.attr('_initAdjustHeight') ){
                $this.attr('_initAdjustHeight', $this.outerHeight());
            }
            _adjustH(this);
            _adjustH(this).on('input', function(){
                _adjustH(this);
            });
        });
        //重置高度
        function _adjustH(elem){
            var $obj = jQuery(elem);
            return $obj.css({height: $obj.attr('_initAdjustHeight'), 'overflow-y': 'hidden'})
                .height( elem.scrollHeight );
        }
    }
});
//textarea字数统计
function textarea_num(){
    $('#num').text($('#textarea').val().length+'/1000');
    $('#textarea').bind('input',function(){
        if($(this).val().length>1000){
            $(this).val($(this).val().substr(0,1000));
        }
        $('#num').text($(this).val().length+'/1000');
    })
}
//字符串去空格
String.prototype.trim=function(){
    return this.replace(/(^\s*)|(\s*$)/g,'');
}
//正则验证及报错
function reg_exp($obj,reg,warning_words){
    $obj.bind('click',function(){
        $(this).css('background','');
    })
    $obj.bind('blur',function(){
        confirm_reg($obj,reg,warning_words);
    })
}
//提交出错时验证+报错
function confirm_reg($obj,reg,warning_words){
    if(!reg.test($obj.val())){
        add_warn($obj,warning_words);
    }
}
function rtn_reg($obj,reg){
    if(!reg.test($obj.val())){
        return false;
    }else{
        return true;
    }
}
function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  decodeURI(r[2]); return null;
}

//button元素文字颜色调整
function btn_color_adjust(){
    $('button').each(function(){
        if($(this).html()!='点击选择'){
            $(this).addClass('active');
        }else{
            $(this).removeClass('active');
        }
    })
}
//input文字颜色调整
function input_color_adjust(){
    $('input').each(function(){
        $(this).bind('input',function(){
            if($(this).val()!=null){
                $(this).addClass('active')
            }else{
                $(this).removeClass('active')
            }
        })
    })
    $('textarea').each(function(){
        $(this).bind('input',function(){
            console.log('1')
            if($(this).val()!=null){
                $(this).addClass('active')
            }else{
                $(this).removeClass('active')
            }
        })
    })
}

//表单弹出错误提示
function add_warn($obj,warning_words){
    $obj.addClass('warn');
    var warning = $('<div class="warning"><span>'+warning_words+'</span></div>');
    $obj.parent().parent().css({'marginBottom':'70px'});
    $obj.parent().append(warning);
    $obj.bind('click',function(){
        remove_warn($obj);
    })
}
function remove_warn($obj){
    $obj.removeClass('warn');
    $obj.parent().parent().css({'marginBottom':'0px'});
    $obj.parent().find('.warning').remove();
}
//保存按钮点击效果
function btn_touch_style(){
    $('.confirm').bind('touchstart',function(){
        $(this).addClass('touching');
    })
    $('.confirm').bind('touchend',function(){
        $(this).removeClass('touching');
    })
}
//创建活动记录
//content(记录类型中文)，belongId(来源业务对象),activityTypeId(活动记录类型),objectId(来源对象的ID),isBack(是否返回上一页1:是)
function create_record(data,$button){
    $.ajax({
        url:apppath+'/dingtalk/activityrecord/docreate.action',
        contentType:'application/x-www-form-urlencoded',
        data:data,
        beforeSend:function(){
            $button.attr('disabled',true);
            loader();
        },
        success:function(str){
            $('.loaderArea').remove();
            var oData = eval('('+str+')');
            if(oData.success==true){
                if(data.activityTypeId == activeRecordTypeOje["SIGN_IN"]){
                    var buriedPointType = 'Sign';
                    buriedPoint(buriedPointType);
                }else{
                    var buriedPointType = 'activityRecordAdd';
                    buriedPoint(buriedPointType);
                }
                myDialog('创建成功');
                setTimeout(function(){
                    doWindowLocationChange()
                },2000)
            }else if(oData.entity.status == '0000002'){
                myAlert('创建失败，不能包含表情符');
                $button.removeAttr('disabled');
            }else if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                location.href = apppath+'/dingtalk/authorized/no.action?'+ddcommparams;
            }else{
                $button.removeAttr('disabled');
                myAlert('创建失败');
            }
        },
        error:function( jqXHR,  textStatus,  errorThrown){
            $('.loaderArea').remove();
            $button.removeAttr('disabled');
            myAlert('暂时无法获取数据，请检查您的网络');
        }
    })
    function doWindowLocationChange(){
        var from = GetQueryString('from');
        if(from=='acc_info'){
            location.href = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+GetQueryString('accountId');
        }else if(from == 'con_info'){
            location.href = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+GetQueryString('contactId');
        }else if(from == 'opp_info'){
            location.href = apppath+'/dingtalk/opportunity/detail.action?'+ddcommparams+'&opportunityId='+GetQueryString('opportunityId');
        }
    }
}
function myAlert(info){
    $('.UPS').remove();
    var shadow = $('<div class="commonShadow UPS"></div>');
    var oAlert = $('<div class="alert"><span>'+info+'</span><div>确定</div></div>');
    oAlert.find('div').bind('click',function(){
        $(this).parent().parent().remove();
    })
    shadow.append(oAlert);
    $('body').append(shadow);
    stopScroll($('.commonShadow'));
    stopScroll($('.alert'));
}
//默认为带对号的提示框，如果pic为without，则无对号
function myDialog(info,pic){
    $('.UPS').remove();
    if(pic == 'without'){
        var oDialog = $('<div class="dialog_without_pic UPS">'+info+'</div>');
    }else{
        var oDialog = $('<div class="dialog UPS">'+info+'</div>');
    }
    $('body').append(oDialog);
    setTimeout(function(){
        $('.dialog').remove();
        $('.dialog_without_pic').remove();
    },2000);
}
//延迟跳转
function time_out_location(url){
    setTimeout(function(){
        location.href = url;
    },2000)
}

//选择是否退出，是则跳转至相应url
function myChoose(url,func){
    $('.UPS').remove();
    var dia = $('<div class="chooseShadow UPS"><div class="dia"><p class="boxSizing">确定放弃操作并退出吗？</p><div class="chooseBtn"><div class="n">取消</div><div class="y boxSizing">确认</div></div></div></div>');
    $('body').append(dia);
    $('.dia').css({'top':'200px','left':($(window).width()-$('.dia').width())/2});
    $('.y').bind('click',function(){
        $('.chooseShadow').remove();
        if(url!=''){
            location.href = url;
        }else if(func){
            func();
        }
    })
    $('.n').bind('click',function(){
        $('.chooseShadow').remove();
    })
}
//input格式自动校正（去除首尾空格）
function input_adjust(){
    $('input').bind('blur',function(){
        var val = $(this).val();
        $(this).val(val.trim());
    })
}
//生成带下拉刷新功能的列表
//url=apppath+'/dingtalk/opportunity/dolist.action'
//全局obj,pageNo,pageSize
//nullInfo = '无相关商机'
//$container = $('#search_content .main')
//build_unit(oData,$container)局部函数
//scrollFunc()下拉加载动作
function build_common_list(url,$container,nullInfo,build_unit,scrollFunc){
    $(window).scrollTop(0);
    $.ajax({
        url:url,
        data:obj,
        //async:false,
        success:function(data){
            console.log(data)
            var oData = eval('('+data+')');
            if(oData.success == true){
                //获取总条数
                var len = oData.entity.totalSize;
                var sEnd;
                if(len == 0){
                    //无结果情况
                    $container.children().remove();
                    var $list = $('<div class="list">'+nullInfo+'</div>');
                    $list.css({'width':'100%','lineHeight':'90px','fontSize':'30px','textAlign':'center'});
                    $container.append($list);
                }else{
                    //有结果情况
                    if(len-pageNo*pageSize>pageSize){
                        //下拉有加载
                        sEnd = pageSize;
                    }else{
                        //下拉无加载
                        sEnd = len-pageNo*pageSize;
                    }
                    //生成列表
                    for(var i=0;i<sEnd;i++){
                        //创建单元
                        build_unit(oData,$container,i);
                    }
                    //创建加载更多栏
                    if ($container.children().length < len) {
                        $container.append($('<div id="load_more" class="end_info">加载更多</div>'));
                    }
                    //pageNo+1
                    obj.pageNo ++;
                    pageNo++;
                    //去除加载中栏（如果存在）
                    $('#loading').remove();
                    //判断是否滚动到底部
                    scrollFunc();
                }
            }else {
                 if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                     location.href = apppath+'/dingtalk/authorized/no.action?'+ddcommparams;
                 }else {
                     myAlert('暂时无法获取数据，请稍后再试');
                 }
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络');
        }
    })
}
function loader(){
    var loader = $('<div class="loaderArea"><div class="loader"></div><span>加载中…</span></div>');
    $('body').append(loader);
}

function buriedPoint(buriedPointType){
    dd.biz.util.ut({
        key: 'open_micro_general_operat',
        value: {'corpId':_config.corpId,'agentId':_config.agentId,'type':buriedPointType},
        onSuccess : function() {
            console.log('埋点成功:'+buriedPointType);
        },
        onFail : function(err) {
            var oErr = eval('('+err+')');
            console.log('埋点失败：')
            console.log(oErr)
        }
    })
}
function doChangeOwner(name,func){
    var cOwner = $('<div class="cOwnerShadow"><div class="cOwner boxSizing"><div class="topInfo boxSizing">' +
    '<p><span class="word">确认将联系人变更为:"</span><span class="ownerName">'+name+'</span><span class="word">",</span></p><p>变更后将失去相关权限</p></div>' +
    '<div class="botBtn"><div class="no boxSizing">取消</div><div class="yes boxSizing">确认</div></div></div></div>');
    cOwner.find('.yes').bind('click',function(){
        cOwner.remove();
        func();
    })
    cOwner.find('.no').bind('click',function(){
        cOwner.remove();
    })
    $('body').append(cOwner);
}
