$(document).bind('ready', function() {
    selectors();
    recordSelect();
    get_record($('#recordList > .main'), 20);
})

function selectors() {
    $('.selector').bind('click', function(e) {
        if ($(this).find('.options').css('display') == 'block') {
            $(this).find('.options').hide();
        } else {
            $('.options').hide();
            $(this).find('.options').show();
            if (($(this).offset().top - $(window).scrollTop() - 60) > $(this).find('.options').height()) {
                $(this).find('.options').addClass('upper');
                $(this).find('.options').removeClass('lower');
            } else {
                $(this).find('.options').addClass('lower');
                $(this).find('.options').removeClass('upper');
            }
        }
        e.stopPropagation();
    })
    $('.options li').each(function() {
        $(this).bind('click', function(e) {
            $(this).parent().parent().find('span').text($(this).text());
            $(this).parent().hide();
            e.stopPropagation();
        })
    })
  
    $(document).on('touchstart', function(e) {
        if (!$(e.target).closest(".selector").size()) {
            $(".options").hide();
        }
    });
    $(document).on('click', function(e) {
        if (!$(e.target).closest(".selector").size()) {
            $(".options").hide();
        }
    });
}
//跟进记录
var recordDateType = 1;

function recordSelect() {
    $(".load-shadow").show();
    $('#recordList .selector .options li').each(function() {
        $(this).bind('click', function() {
            recordDateType = $(this).attr('value');
            recordPageNo = 0;
            $('#recordList > .main').children().remove();
            get_record($('#recordList > .main'), 20);
        })
    })
}
var recordPageNo = 0;

function get_record($parent_obj) {
    //先加载缓存数据
    //if(localStorage.getItem(xsyUser.id)){
    //    var ls = JSON.parse(localStorage.getItem(xsyUser.id));
    //    if(ls.index){
    //        if(ls.index.recordData){
    //            //alert('加载opp缓存数据')
    //            recordByData(ls.index.recordData,$parent_obj);
    //        }
    //    }
    //}
    $.ajax({
        url: apppath + '/wx/statics/activerecordlist.action',
        data: {
            pagesize: 20,
            pageNo: recordPageNo,
            searchDateType: recordDateType
        },
        dataType: 'json',
        success: function(oData) {
            $("#behaviorPage .load-shadow").hide();
            //测试校验
            console.log('跟进记录：' + oData)
            if (oData.success == true) {
                //if(recordDateType == 4){
                //    var ls;
                //    if(localStorage.getItem(xsyUser.id)){
                //        ls = JSON.parse(localStorage.getItem(xsyUser.id));
                //        ls.index = {'recordData':oData}
                //    }else{
                //        ls = {'index':{'recordData':oData}}
                //    }
                //    localStorage.setItem(xsyUser.id,JSON.stringify(ls));
                //}
                //alert('加载record新数据')
                recordByData(oData, $parent_obj);
            } else {
                 $("#behaviorPage .load-shadow").hide();
                if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.href = apppath + '/wx/authorized/no.action?' + ddcommparams;
                } else {
                    myAlert('网络不给力，请重新加载')
                    console.log('跟进记录：' + oData)
                }
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}

function recordByData(oData, $parent_obj) {
    var totalSize = oData.entity.totalSize;
    $('#recordList > .top > .title > span').text('跟进记录(' + totalSize + ')');
    if (oData.entity.records.length == 0) {
        $parent_obj.append($('<div class="noInfo">该范围内没有跟进记录</div>'));
        console.log('该对象暂无跟进记录');
    } else {
        build_record_list($parent_obj, oData);
        if ($parent_obj.find('li').length < totalSize) {
            $('#loading').remove();
            build_end($parent_obj, '加载更多', 'load_more');
            recordPageNo++;
            scroll_load();
        } else {
            $('#loading').remove();
        };
    }
}

function build_end($parent_obj, info, name) {
    var end = $('<div id="' + name + '" class="end_info">' + info + '</div>');
    $parent_obj.append(end);
}

function scroll_load() {
    var windowHeight = document.body.clientHeight;
    $(window).bind('scroll', function() {
        if ($('#load_more').length > 0) {
            if ($('#load_more').offset().top - $(document).scrollTop() <= windowHeight - $('#load_more').height() - 50) {
                $('#load_more').remove();
                build_end($('#recordList > .main'), '加载中…', 'loading');
                get_record($('#recordList > .main'), 20);
                $(this).unbind('scroll');
            }
        }
    })
}

function build_record_list($parent_obj, data) {
    var item = data.entity.records;
    for (var i = 0; i < item.length; i++) {
        var userName = item[i].user.userName;
        var typeName = item[i].typeName;
        var objectName = item[i].objectName;
        var belongId = item[i].belongId;
        var objectId = item[i].objectId;
        var startTime = item[i].startTime.substr(-5);
        var startDate = item[i].startTime.substr(0, 11);
        var type = item[i].recordType.type;
        if (i != 0 && item[i].startTime.substr(0, 11) != item[i - 1].startTime.substr(0, 11)) {
            if (i == 0 && $parent_obj.find('.day').last().text == item[i].startTime.substr(0, 11)) {} else {
                var day = $('<div class="day"><em></em><p class="startDate">' + startDate + '</p><div class="ban"></div></div>');
                $parent_obj.append(day);
            }
        } else if (i == 0 && recordPageNo == 0) {
            var day = $('<div class="day"><em></em><p class="startDate">' + startDate + '</p><div class="ban"></div></div>');
            $parent_obj.append(day);
        }
        var oList = $('<li class="boxSizing"><div class="info"><span class="userName">' + userName + '</span><span class="typeName">' + typeName + ':</span><span class="objectName">' + objectName + '</span><span class="startTime">' + startTime + '</span></div></li>');
        oList.find('.objectName').attr({ 'belongId': belongId, 'objectId': objectId });
        oList.find('.objectName').bind('click', function() {
            var url;
            if ($(this).attr('belongId') == 1) {
                url = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + $(this).attr('objectId') + '&from=index&page=behavior';
            } else if ($(this).attr('belongId') == 2) {
                url = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + $(this).attr('objectId') + '&from=index&page=behavior';
            } else if ($(this).attr('belongId') == 3) {
                url = apppath + '/wx/opportunity/detail.action?' + ddcommparams + '&opportunityId=' + $(this).attr('objectId') + '&from=index&page=behavior';
            }
            location.href = url;
        });
        if (type == "TEL") {
            oList.addClass('phone_bg');
        } else if (type == "SIGN_IN") {
            oList.addClass('sign_in_bg');
        } else if (type == "RECORD") {
            oList.addClass('record_bg');
        }
        if (item[i].content) {
            var contentText = item[i].content;
            var content_span = $('<span class="content_span">' + contentText + '</span>');
            var content = $('<div class="content"><em class="triangle"></em></div>');
            content.append(content_span);
            if (contentText.length > 32) {
                var shortContentText = contentText.substr(0, 30) + '...';
                var short_content_span = $('<span class="short_content_span">' + shortContentText + '</span>');
                content.append(short_content_span);
                content_span.css('display', 'none');
                var index = 0;
                var more_btn = $('<em class="more"></em>');
                more_btn.bind('click', function() {
                    index++;
                    $(this).parent().find('.short_content_span').slideToggle();
                    $(this).parent().find('.content_span').slideToggle();
                    $(this).css("transform", "rotate(" + (90 + 180 * index) + "deg)");

                })
                content.append(more_btn);
            }
            oList.append(content);
        }
        if (item[i].location) {
            var locat = $('<div class="location">' + item[i].location + '</div>');
            oList.append(locat);
        }
        var bottom_border = $('<div class="bottom_border"></div>');
        oList.append(bottom_border);
        $parent_obj.append(oList);
    }
    removeBorder();
}

function removeBorder() {
    $('#record_tab li').each(function() {
        if ($(this).next().hasClass('day')) {
            $(this).find('.bottom_border').remove();
        }
    })
}