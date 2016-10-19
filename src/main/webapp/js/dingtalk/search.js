/**
 * Created by dell on 2016/1/8.
 */
$(document).bind('ready',function(){
    search();
    input_delete();
    back();
    remove_rTop();
    //keyboard();
})
dd.ready(function(){
    var buriedPointType = 'ACSearch';
    buriedPoint(buriedPointType);
})
//钉钉自定义键盘
function keyboard(){
    dd.ready(function(){
        dd.ui.input.plain({
            placeholder: '请输入公司或联系人名称', //占位符
            text: '', //默认填充文本
            onSuccess: function(data) {
                //onSuccess将在点击发送之后调用
                $('#search').find('input').val(data.text);
                input_search();
            },
            onFail: function() {

            }
        })
    })
}
function input_delete(){
    if($('#search').find('input').val() == ''){
        $('#search').find('.delete_icon').hide();
    }else{
        $('#search').find('.delete_icon').show();
    };
    $('#search').find('.delete_icon').bind('click',function(){
        $(this).hide();
        $('#search').find('input').val('');
        $('#content>.list').remove();
        $('.dialog').remove();
        show_history();
    })
}
function list_top(){
    $(window).bind('scroll',function(){
        var acc_top = $('#acc_area').offset().top- $(document).scrollTop();
        //吸顶状态
        if(acc_top<=88){
            $('#account_top').css({'position':'fixed','top':'88px'});
            $('#acc_area').css({'paddingTop':$('#account_top').height()+'px'});
        }
        //流布局状态（原始状态）
        else{
            $('#account_top').css({'position':'static'});
            $('#acc_area').css({'paddingTop':'0px'});
        };
        var con_top = $('#con_area').offset().top- $(document).scrollTop();
        //挤压（固定）状态
        if(con_top<=88+$('#account_top').height()){
            $('#account_top').css({'position':'absolute','top':$('#con_area').offset().top-$('#account_top').height()-1+'px'});
        }else{
            $('#account_top').css({'top':'88px'});
        }
        if(con_top<=88){
            $('#contact_top').css({'position':'fixed','top':'88px'});
            $('#con_area').css({'paddingTop':$('#contact_top').height()+'px'});
        }else{
            $('#contact_top').css({'position':'static'});
            $('#con_area').css({'paddingTop':'0px'});
        };
    })
}
//关键字
var val = null;
function search(){
    show_history();
    //搜索框键入信息
    $('#txt').bind('input',function(){
        input_search();
    });
}
function input_search(){
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
}
//展示最近搜索
function show_history(){
    $('#content').children().remove();
    if(localStorage.getItem(userId)!='undefined'){
        var ls=JSON.parse(localStorage.getItem(userId));
        console.log(ls)
        if(ls == null){
            create_history_null();
        }else{
            if(ls.accountHistory.length!=0){
                create_history_list(ls);
            }else{
                create_history_null();
            };
        }
    }else{
        create_history_null();
    }
}
function create_history_list(ls){
    //最多显示20条最近历史搜索记录
    if(ls.accountHistory.length<=20){
        for (var i = ls.accountHistory.length - 1; i >= 0; i--){
            build_history_list(ls,i);
        }
        create_delete();
    }else{
        for (var i = ls.accountHistory.length - 1; i >= ls.accountHistory.length - 20; i--){
            build_history_list(ls,i);
        }
        create_delete();
    }
}
function build_history_list(ls,i){
    var oList = document.createElement('div');
    oList.className = 'boxSizing history_list';
    var name = ls.accountHistory[i].name;
    var id = ls.accountHistory[i].id;
    var kindle = ls.accountHistory[i].kindle;
    if(kindle == 1){
        $(oList).append($('<em class="account_pic"></em>'));
        $(oList).bind('click',function(){
            location.href = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+id;
        })
    }else{
        $(oList).append($('<em class="contact_pic"></em>'));
        $(oList).bind('click',function(){
            location.href = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+id;
        })
    }
    $(oList).append($('<span class="name">'+name+'</span>'));
    var oExit = document.createElement('div');
    oExit.innerHTML = '<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDA5MDMwNDRCMkU5MTFFNUE4MDNFMkU5NkI5N0FEOEMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDA5MDMwNDVCMkU5MTFFNUE4MDNFMkU5NkI5N0FEOEMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpCMTRGRjgyMUIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpCMTRGRjgyMkIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Prom4REAAADHSURBVHjanNVNCoMwEAXgydAb9CJ21QvYVb2uK91rwUUPYs9gZ2ACIeTnJQNv48gHCQ91+3ZsROQkb8mP+uYumSUXG/aULLbowVYznIKT5Ct5dKAeG8yYFDwlrwBdQTTG1DjZliE6AGgS0wUHL6FoFotBBC1iKbCEVjGdW+aOPLoEKNWwEphCqYbljhzOBT6DwPjOoEpxQ8+gnnJDz6CecktpEZRbSoug3IhVUe7Aiijbl9ZjI4jl0Nkf+WNYzy/Ao2rQX4ABAORFZTtFQ9/RAAAAAElFTkSuQmCC"/>';
    oExit.className = 'exit';
    $(oExit).bind('click',function(){
        ls.accountHistory.remove(i);
        localStorage.setItem(userId,JSON.stringify(ls));
        $(oList).remove();
        if($('#content').find('.history_list').length==0){
            $('#content > .clear').remove();
            create_history_null();
        }
    });
    oList.appendChild(oExit);
    oList.href = id;
    $('#content').append(oList);
}
//清除历史记录
function create_delete(){
    var _delete = document.createElement('div');
    _delete.innerHTML = "清除全部历史记录";
    _delete.className="clear"
    _delete.onclick = function(){
        var ls = JSON.parse(localStorage.getItem(userId));
        ls.accountHistory=[];
        localStorage.setItem(userId,JSON.stringify(ls));
        $('#content > .history_list').remove();
        $('#content > .clear').remove();
        create_history_null();
    };
    $('#content').append(_delete);
}
//暂无历史记录
function create_history_null(){
    var history_null = document.createElement('div');
    history_null.innerHTML = '暂无历史记录';
    history_null.className = 'null';
    $('#content').append(history_null);
}
//展示关键字搜索
function show_word(val){
    $('#content').children().remove();
    create_word_list(val);
}
var acc_pageNo = 0;
var con_pageNo = 0;
var pageSize = 30;
//首次加载的pageSize条数据
var oData;
function create_word_list(val){
    $.ajax({
        type:"get",
        url:apppath+"/dingtalk/account/dosearch.action",
        async:true,
        data:{searchName:val,pageNo:0,pagesize:pageSize},
        success:function(data){
            oData = eval('('+data+')');
            if(oData.entity.accounts.success == false||oData.entity.contacts.success == false){
                myAlert('暂时无法获取数据，请稍后再试');
            }else {
                $('.dialog').remove();
                build_word_list(oData);
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
//var arrId = [];
//var arrName = [];
var accLength;
var conLength;
function build_word_list(obj){
    $('#content>.list').remove();
    accLength = obj.entity.accounts.entity.totalSize;
    conLength = obj.entity.contacts.entity.totalSize;
    var acc_length = obj.entity.accounts.entity.records.length;
    var con_length = obj.entity.contacts.entity.records.length;
    //公司部分
    if(accLength>0){
        var acc_area = $('<div id="acc_area" class="boxSizing"></div>');
        var top = $('<div id="account_top" class="boxSizing top"><img src="../../images/dingtalk/2x/common/account.png"/><sapn>公司</sapn></div>');
        acc_area.append(top);
    }
    if($('#acc_area').length>0){
        $('#acc_area').remove();
    }
    $('#content').append(acc_area);
    create_acc_list(0,5,obj);
    //联系人部分
    if(conLength>0){
        var con_area = $('<div id="con_area" class="boxSizing"></div>');
        var top = $('<div id="contact_top" class="boxSizing top"><img src="../../images/dingtalk/2x/common/contact.png"/><sapn>联系人</sapn></div>');
        con_area.append(top);
    }
    if($('#con_area').length>0){
        $('#con_area').remove();
    }
    $('#content').append(con_area);
    create_con_list(0,5,obj);
    //无结果情况
    if(conLength==0&&accLength==0){
        $('#content>.list').remove();
        var oList = document.createElement('div');
        oList.style.cssText = "width:100%;padding-left:10px;line-height:88px;font-size:30px;text-align:center;";
        oList.className = 'list';
        oList.innerHTML = "无相关公司或联系人";
        $('#content').append(oList);
    };
};
//公司部分加载机制
function create_acc_list(start,end,obj){
    if(obj != null){
        var new_data = obj;
        build_acc_list(start,end,new_data);
    }else{
        $.ajax({
            type:"get",
            url:apppath+"/dingtalk/account/dosearch.action",
            async:true,
            data:{searchName:val,pageNo:acc_pageNo,pagesize:pageSize},
            success:function(data1){
                var new_data = eval('('+data1+')');
                if(new_data.entity.accounts.success == false||new_data.entity.contacts.success == false){
                    myAlert('暂时无法获取数据，请稍后再试');
                }else {
                    $('.dialog').remove();
                    build_acc_list(start, end, new_data);
                }
            },
            error:function(){
                myAlert('暂时无法获取数据，请检查您的网络')
            }
        });
    }
};
function build_acc_list(start,end,new_data){
    if(end>new_data.entity.accounts.entity.records.length){
        end = new_data.entity.accounts.entity.records.length;
    }
    for(var i=start;i<end;i++){
        var oList = document.createElement('div');
        var name = new_data.entity.accounts.entity.records[i].accountName;
        var reg=new RegExp(val,"g");
        var id = new_data.entity.accounts.entity.records[i].id;
        oList.href = id;oList.name = name;
        name = name.replace(reg,"<span class='blue'>"+val+"</span>");
        oList.innerHTML = name;oList.className = 'boxSizing word_list';
        //arrId.push(id);arrName.push(name);
        oList.addEventListener('click',function(){
            local_set(this.href,this.name,1);
            location.href = apppath+'/dingtalk/account/info.action?'+ddcommparams+'&accountId='+this.href;
        });
        $('#acc_area').append(oList);
    };
    if($('#acc_area > .word_list').length<accLength){
        if($('#acc_area > .word_list').length==5){
            acc_first_more(oData);
        }else{
            create_acc_more();
        }
    }else if(accLength>5){
        create_acc_less();
    }
}
function acc_first_more(data){
    var first_more = $('<div class="show_more">查看更多</div>');
    $('#acc_area').append(first_more);
    first_more.bind('click',function(){
        $(this).remove();
        create_acc_list(5,30,data);
    });
}
function create_acc_more(){
    var bot_more = $('<div class="show_more">查看更多</div>');
    $('#acc_area').append(bot_more);
    bot_more.bind('click',function(){
        $(this).remove();
        acc_pageNo += 1;
        create_acc_list(0,30);
    });
};
function create_acc_less(){
    var bot_less = $('<div class="show_less">收起</div>');
    $('#acc_area').append(bot_less);
    bot_less.bind('click', function () {
        $(this).remove();
        $('#acc_area>.word_list').each(function(index){
            if($(this).index()>5){
                $(this).remove();
            }
        });
        acc_pageNo = 0;
        acc_first_more(oData);
    });
};
//联系人部分加载机制
function create_con_list(start,end,obj){
    if(obj != null){
        var new_data = obj;
        build_con_list(start,end,new_data);
    }else{
        $.ajax({
            type:"get",
            url:apppath+"/dingtalk/account/dosearch.action",
            async:true,
            data:{searchName:val,pageNo:con_pageNo,pagesize:pageSize},
            success:function(data1){
                var new_data = eval('('+data1+')');
                if(new_data.entity.accounts.success == false||new_data.entity.contacts.success == false){
                    myAlert('暂时无法获取数据，请稍后再试');
                }else {
                    $('.dialog').remove();
                    build_con_list(start, end, new_data);
                }
            },
            error:function(){
                myAlert('暂时无法获取数据，请检查您的网络')
            }
        });
    }
};
function build_con_list(start,end,new_data){
    if(end>new_data.entity.contacts.entity.records.length){
        end = new_data.entity.contacts.entity.records.length;
    }
    for(var i=start;i<end;i++){
        var oList = document.createElement('div');
        var name = new_data.entity.contacts.entity.records[i].contactName;
        var reg=new RegExp(val,"g");
        name = name.replace(reg,"<span class='blue'>"+val+"</span>");
        var id = new_data.entity.contacts.entity.records[i].id;
        oList.innerHTML = name;oList.className = 'boxSizing word_list';
        oList.href = id;oList.name = name;
        //arrId.push(id);arrName.push(name);
        oList.addEventListener('click',function(){
            console.log(this.href);
            local_set(this.href,this.name,2);
            location.href = apppath+'/dingtalk/contact/info.action?'+ddcommparams+'&contactId='+this.href;
        });
        $('#con_area').append(oList);
    };
    if($('#con_area > .word_list').length<conLength){
        if($('#con_area > .word_list').length==5){
            con_first_more(oData);
        }else{
            create_con_more();
        }
    }else if(conLength>5){
        create_con_less();
    }
}
function con_first_more(data){
    var first_more = $('<div class="show_more">查看更多</div>');
    $('#con_area').append(first_more);
    first_more.bind('click',function(){
        $(this).remove();
        create_con_list(5,30,data);
    });
}
function create_con_more(){
    var bot_more = $('<div class="show_more">查看更多</div>');
    $('#con_area').append(bot_more);
    bot_more.bind('click',function(){
        $(this).remove();
        con_pageNo += 1;
        create_con_list(0,30);
    });
};
function create_con_less(){
    var bot_less = $('<div class="show_less">收起</div>');
    $('#con_area').append(bot_less);
    bot_less.bind('click', function () {
        $(this).remove();
        $('#con_area>.word_list').each(function(index){
            if($(this).index()>5){
                $(this).remove();
            }
        });
        con_pageNo = 0;
        con_first_more(oData);
    });
};

//返回按钮回退功能
function back(){
    $('#back').bind('click',function(){
        history.go(-1);
    });
};
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
                ls.opportunityHistory = [val];
            }
        }else{
            var ls = {
                "accountHistory":[val]
            }
        }
    }else if(kindle == 3){
        var val = {"id":id,"name":name};
        if(localStorage.getItem(userId)){
            var ls=JSON.parse(localStorage.getItem(userId));
            //去重，后点排在前
            for(var j=0;j<ls.opportunityHistory.length;j++){
                if(ls.opportunityHistory[j].id==id){
                    ls.opportunityHistory.remove(j);
                }
            }
            ls.opportunityHistory.push(val);
        }else{
            var ls = {
                "opportunityHistory":[val]
            }
        }
    }
    localStorage.setItem(userId,JSON.stringify(ls));
};









