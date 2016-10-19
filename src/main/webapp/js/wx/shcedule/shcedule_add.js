$(document).on('ready', function() {
    $(".shcedule-detail-header-right").on('click', function() {
        $(".options").toggle();
    })
    if (GetQueryString('type') == 'add') {
        getShcduleDetail('ADD');
        getCurrentDate();
    } else if (GetQueryString('type') == 'set') {
        document.title = '编辑日程';
        getShcduleDetail('DETAIL');
        scheduleSetInfo();
    }
     var linkDate = GetQueryString('data');
     if(linkDate){
        var startMinutes = $("#startMinutes").text();
        console.log(startMinutes);
        var startInitDate = linkDate + " " +startMinutes;
        $("#datetime-picker-start").val(startInitDate);

        var endMinutes = $("#endMinutes").text();
        var endInitDate = linkDate + " " + endMinutes;
        $("#datetime-picker-end").val(endInitDate);
    }
     $("#datetime-picker-start").datetimePicker();
     $("#datetime-picker-end").datetimePicker();
    shceduleDateSelect();
    confirm();
    searchInputDelete();
    search();
    back();
    selectDate();
    btn_color_adjust();
    remove_rTop();
    $('textarea').autoHeight();
    textarea_num();
})

var xsyuser = xsyUser.name;
var xsyid = xsyUser.id;
var shceduleNameValue;
var shceduleStyleValue;
var shceduleStartTimeValue;
var shceduleEndTimeValue;
var shceduleTypelist = {};
var scheduleFrequencylist = {};
var shceduleBelongidList = {};
var shcedulerecurStopList = {};

function getShcduleDetail(scene) {
    $.ajax({
        url: apppath + "/wx/schedule/getDetail.action",
        dataType: "json",
        data: { scene: scene },
        type: "GET",
        beforeSend: function() {
            //请求前的处理
        },
        success: function(data) {
            //console.log('数据' + data);
            //console.log(data);
            if (data.success == false) {
                myAlert("网络不给力，请重新加载");
            }
            if (data.success == true) {
                var components = data.entity.structure.components;
                var len = components.length;
                $("#shcedule_parter_select").html(xsyuser);
                $('#shcedule_parter_select').attr('value', xsyid);
                $("#shcedule_frequency_select").html("不重复");
                $('#shcedule_frequency_select').attr('value', '0');
                for (var i = 0; i < len; i++) {
                    if (components[i].propertyname == 'type') {
                        shceduleTypelist = components[i].selectItem;
                        $("#shcedule_type_select").html(shceduleTypelist[0].label);
                        $('#shcedule_type_select').attr('value', shceduleTypelist[0].value);
                        getShceduleType(shceduleTypelist);
                    }
                    if (components[i].propertyname == 'frequency') {
                        scheduleFrequencylist = components[i].selectItem;
                        getShceduleFrequency(scheduleFrequencylist);
                    }
                    if (components[i].propertyname == 'belongId') {
                        shceduleBelongidList = components[i].selectItem;
                        getShcedulebelongId(shceduleBelongidList);
                    }
                    if (components[i].propertyname == 'recurStopCondition') {
                        shcedulerecurStopList = components[i].selectItem;
                        getShcedulerecurStop(shcedulerecurStopList);
                    }
                }
                getShceduleMember();
                $('textarea').autoHeight();
                textarea_num();

            }
        }
    });
}
var datestring;
var startData;
var endData;

function getCurrentDate() {
    var date = new Date();
    var vYear = date.getFullYear();
    var vMon = date.getMonth() + 1;
    var vDay = date.getDate();
    var h = date.getHours();
    var m = date.getMinutes();
    var se = date.getSeconds();
    datestring = vYear + String(vMon < 10 ? "0" + vMon : vMon) + String(vDay < 10 ? "0" + vDay : vDay) + String(h < 10 ? "0" + h : h) + String(m < 10 ? "0" + m : m) + String(se < 10 ? "0" + se : se);
    var startyear = datestring.substring(0, 4);
    var startmouth = datestring.substring(4, 6);
    var startday = datestring.substring(6, 8);
    var startDay = startyear + '-' + startmouth + '-' + startday;
    var starthours = datestring.substring(8, 10);
    var startminutes = datestring.substring(10, 12);
    var startTime = starthours + ':' + startminutes;
    var endhours = Number(starthours) + 1;
    var endTime = endhours + ":" + startminutes;
    var selectedDate = GetQueryString('data');
    if (selectedDate == null) {
        $("#startDay").text(startDay);
        $("#startMinutes").text(startTime);
        $("#endDay").text(startDay);
        $("#endMinutes").text(endTime);
        startData = startDay + " " + startTime;
        endData = startDay + " " + endTime;
    }
    else{
        $("#startDay").text(selectedDate);
        $("#startMinutes").text(startTime);
        $("#endDay").text(selectedDate);
        $("#endMinutes").text(endTime);
        startData = startDay + " " + startTime;
        endData = startDay + " " + endTime;
    }
   
}
//是否私密
var isPrivate;

function checkIsPrivate() {
    var bischecked = true;
    var shcedule_is_private = $('input[name="shcedule_is_private"]');
    if (shcedule_is_private.is(':checked') == true) {
        isPrivate = true;
    }
    if (shcedule_is_private.is(':checked') == false) {
        isPrivate = false
    }
    // bischecked ? shcedule_is_private.attr('checked', true) : shcedule_is_private.attr('checked', false);
}

var startdataFirstSelectFlag = true;
var enddataFirstSelectFlag = true;

function shceduleDateSelect() {
    $("#datetime-picker-start").change(function() {
        if (startdataFirstSelectFlag) {
            var selectedDate = GetQueryString('data');
            var value = $("#datetime-picker-start").val();
            var data = value.substring(11, 16);
            var year = value.substring(0, 10);
            if (selectedDate == null) {
                $("#startDay").html(year);
            } else {
                $("#startDay").html(selectedDate);
            }
            $("#startMinutes").html(data);
            startdataFirstSelectFlag = false;
        } else {
            var value = $("#datetime-picker-start").val();
            var data = value.substring(11, 16);
            var year = value.substring(0, 10);
            $("#startDay").html(year);
            $("#startMinutes").html(data);
        }

    });
    $("#datetime-picker-end").change(function() {
        if (enddataFirstSelectFlag) {
            var selectedDate = GetQueryString('data');
            var value = $("#datetime-picker-end").val();
            var year = value.substring(0, 10);
            var data = value.substring(11, 16);
            if (selectedDate == null) {
                $("#endDay").html(year);
            } else {
                $("#endDay").html(selectedDate);
                $("#datetime-picker-end").val(selectedDate);
            }

            $("#endMinutes").html(data);
            enddataFirstSelectFlag = false
        } else {
            var value = $("#datetime-picker-end").val();
            var year = value.substring(0, 10);
            var data = value.substring(11, 16);
            $("#endDay").html(year);
            $("#endMinutes").html(data);
        }

    });

}

function shceduleCheckTime() {
    var startTime = $("#datetime-picker-start").val();
    var endTime = $("#datetime-picker-end").val();
    var end, start;
    if (startTime == "") {
        var startday = $("#startDay").text();
        var startMinutes = $("#startMinutes").text();
        startDayTime = startday + " " + startMinutes;
        start = $.myTime.DateToUnix(startDayTime);
    } else {
        start = $.myTime.DateToUnix(startTime);
    }
    if (endTime == "") {
        var endDay = $("#endDay").text();
        var endMinutes = $("#endMinutes").text();
        var endDayTime = endDay + " " + endMinutes;
        end = $.myTime.DateToUnix(endDayTime);
    } else {
        end = $.myTime.DateToUnix(endTime);
    }
    if (start >= end) {
        return false;
    }
    return true;
}

//类型选择
function getShceduleType(data) {
    $('#shcedule_type_select').on('click', function() {
        $(this).addClass("active");
        var stype = $('#shcedule_type_select');
        checkTable('选择类型');
        createShcduleList(data, stype);
    });
}

// 设置重复
function getShceduleFrequency(data) {
    $('#shcedule_frequency_select').on('click', function() {
        $(this).addClass("active");
        var stype = $('#shcedule_frequency_select');
        checkTable('设置重复');
        createShcduleList(data, stype);
    });
}

//关联业务
function getShcedulebelongId(data) {
    $("#shcedule_belongid_select").on('click', function() {
        $(this).addClass("active");
        var stype = $('#shcedule_belongid_select');
        checkTable('设置重复');
        createBelongidList(data, stype);
    });
}

//结束条件
function getShcedulerecurStop(data) {
    $("#shcedule_end_select").on('click', function() {
        $(this).addClass("active");
        var stype = $('#shcedule_end_select');
        checkTable('结束条件');
        createShcduleList(data, stype);
    });
}

var isRecur = false;

function createShcduleList(data, elem) {
    var container = $('<div class="container"></div>');
    for (var i = 0, len = data.length; i < len; i++) {
        var list = $('<div class="item boxSizing"><em class="radio"><i></i></em><span>' + data[i].label + '</span></div>');
        list.attr('value', data[i].value);
        container.append(list);
    }
    $('.ct').append(container);
    container.find('.item[value="' + $(elem).attr("value") + '"] em').addClass('active');
    $('.ct .item').on('click', function() {

        $(this).find('em').addClass('active');
        $(this).siblings().find('em').removeClass('active');
        var index = $(this).index();
        var label = data[index].label;
        var value = data[index].value;
        var element = $("#shcedule_frequency_select");
        $(elem).html(label);
        $(elem).attr('value', value);
        if ($(elem).attr('id') == 'shcedule_frequency_select') {
            if (value == '1' || value == '2' || value == '3') {
                $("#shcedule_end_item").show();
                var endSelect = $("#shcedule_end_select");
                    endSelect.html("永不结束");
                    endSelect.attr('value','1');
                    endSelect.addClass("active");
                    isRecur = true;
            } else {
                $("#shcedule_end_item").hide();
                $("#shcedule_end_time").hide();
            }
        } else if ($(elem).attr('id') == 'shcedule_end_select') {
            if (value == '2') {
                $("#shcedule_end_time").show();
            } else if (value == '1') {
                $("#shcedule_end_time").hide();
            }
        }

        $('.ct').remove();
        $('body').css({ 'overflow': '', 'height': 'auto' });
    })

}

function createBelongidList(data, elem) {
    var container = $('<div class="container belongid-container"></div>');
    for (var i = 0, len = data.length; i < len; i++) {
        var list = $('<div class="item boxSizing"><span>' + data[i].label + '</span><i class="shcedule-right-arrow"></i></div>');
        list.attr('value', data[i].value);
        container.append(list);
    }
    $('.ct').append(container);
    $('.ct .item').on('click', function() {
        $(".shcedule-search-warp").show();
        var flag = $(this).attr('value');
        $('#shcedule_belongid_select').attr("objectId", flag);
        var text = $('.ipt_area').find('input');
        switch (flag) {
            case '1':
                text.attr('placeholder', '搜索公司');
                text.attr('data-entity', 'account');
                break;
            case '2':
                text.attr('placeholder', '搜索联系人');
                text.attr('data-entity', 'contact');
                break;
            case '3':
                text.attr('placeholder', '搜索商机');
                text.attr('data-entity', 'opp');
                break;
        }
    })
}

function searchInputDelete() {
    if ($('.ipt_area').find('input').val() == '') {
        $('.ipt_area').find('.delete_icon').hide();
    } else {
        $('.ipt_area').find('.delete_icon').show();
    };
    $('.ipt_area').find('.delete_icon').on('click', function() {
        $(this).hide();
        $('.ipt_area').find('input').val('');
        $('#content>.list').remove();
        $('.dialog').remove();
        show_history();
    })
}

function back() {
    $('#back').on('click', function() {
        $('#txt').attr("value", "");
        $('.shcedule-search-warp').find('#content').children().remove();
        $('.shcedule-search-warp').hide();
    });
};

//参与人
function getShceduleMember() {
    $('#shcedule_parter_select').on('click', function() {
        btn_color_adjust();
        getShceduleMemberlist();
    });
}

function getShceduleMemberlist() {
    $.ajax({
        type: "get",
        url: apppath + "/wx/xsyuser/pager.action",
        async: true,
        dataType: 'json',
        success: function(oData) {
            $('.load-shadow').hide();
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                if (oData.entity.records.length == 1) {
                    var no_contact = $('<div class="no_contact">您的公司下暂时没有其他员工</div>');
                    $('body').append(no_contact);
                    setTimeout(function() {
                        $('.no_contact').remove();
                    }, 2000);
                } else {
                    checkTable('添加成员');
                    $('.ct').addClass("multiple");
                    var container = $('<div id="container" class="container"></div>');
                    $('.ct').append(container);
                    var elem = $("#shcedule_parter_select");
                    checkboxDummyList(container, oData, elem);
                }
            }
        }
    });
}

var checkboxMember = {};
var checkboxChange = false;

function checkboxDummyList(container, data, elem) {
    for (var i = 0, len = data.entity.records.length; i < len; i++) {
        var value = data.entity.records[i].id;
        var label = data.entity.records[i].name;
        var con = $('<div class="item boxSizing" id = "' + value + '" atext = "' + label + '"><em class="checkbox"></em><span>' + label + '</span></div>');
        con.attr('value', value);
        container.append(con);
    }
    var itemValue = $(elem).attr('value');
    if (itemValue != "") {
        checkboxMember = itemValue.split(',');
    }
    // $('.ct').append(container);
    for (var j = 0; j < checkboxMember.length; j++) {
        for (var i = 0, len = data.entity.records.length; i < len; i++) {
            var value = $('.ct .item').eq(i).attr('id');
            if ($.trim(checkboxMember[j]) == value) {
                $('.ct .item').eq(i).find('em').addClass('active');
            }
        }
    }

    checkboxClick(data);
    createConfirm(data, elem);
}

function checkboxClick(data) {
    $('.item').each(function(index) {
        $(this).bind('click', function() {
            checkboxChange = true;
            if ($(this).find('em').hasClass('active')) {
                $(this).find('em').removeClass('active');
            } else {
                $(this).find('em').addClass('active');
            }
        })
    })
}

var arr_value = [];
var arr_label = [];

function createConfirm(data, elem) {
    var confirm = $('<button id="confirm" class="confirm">确定</button>');

    $('.ct').append(confirm);
    confirm.bind('touchstart', function() {
        $(this).addClass('touching');
    })
    confirm.bind('touchend', function() {
        $(this).removeClass('touching');
    })
    confirm.bind('click', function() {
        $('#container .item').each(function() {
            if ($(this).find('em').hasClass('active')) {
                if ($.inArray($(this).attr('value'), arr_value) == -1) {
                    arr_value.push($(this).attr('value'));
                    arr_label.push($(this).attr('atext') + ",");
                }
            }
            $(elem).attr('value', arr_value);
            $(elem).html(arr_label);
            $(elem).attr('class', 'active');
            // $(elem).attr('value', value);
            $('.ct').remove();
            $('body').css({ 'overflow': 'auto', 'height': 'auto' })
        })
    })
}

function confirm() {
    $('#shcedule_add_confirm').bind('click', function() {
        confirm_action();
    })

    function confirm_action(action) {
        checkIsPrivate();
        var flag;
        if ($('.shcedule-header-titile input').val() == '') {
            confirm_reg($('.shcedule-header-titile'), '日程主题不能为空');
            $(window).scrollTop(0);
            return;
        }
        if (shceduleCheckTime() == false) {
            confirm_reg($('.shcedule-add-timepicker-right'), '结束时间要晚于开始时间');
            $(window).scrollTop(0);
            return;
        }
        if ($('#shcedule_parter_select').val() == '' || $('#shcedule_parter_select').val() == '点击选择') {
            confirm_reg($('#shcedule_parter_select'), '参与人不能为空');
            $(window).scrollTop(0);
            return;
        }
        var endtime= $("#shcedule_end_select").attr('value');
        var datetime = $("#datetime").val();
        if(endtime == '2'  && datetime == ""){
            confirm_reg($('#datetime'), '结束日期不能为空');
            $(window).scrollTop(0);
            return;
        }
        shceduleAddInfo();

    }
}
//提交信息
function shceduleAddInfo(action) {
    var url = '';
    if (GetQueryString('type') == 'set') {
        url = apppath + "/wx/schedule/doupdate.action";
        //console.log('更新接口');
        var startday = $("#startDay").text();
        var startMinutes = $("#startMinutes").text();
        var modifystartDayTime = startday + " " + startMinutes;
        var endDay = $("#endDay").text();
        var endMinutes = $("#endMinutes").text();
        var modifyendDayTime = endDay + " " + endMinutes;
        var initArray = [];
        var frequencyValue = $("#shcedule_frequency_select").attr('value');
        if (frequencyValue != 0) {
            var setisRecur = true;
        }
        initArray = $("#shcedule_parter_select").attr('value').split(',');
        var recurStopValue = $('#datetime').val();
        var scheduleTemp = {
            name: $('.shcedule-header-titile input').val(),
            type: $('#shcedule_type_select').attr('value'),
            startDate: $('#datetime-picker-start').val() == "" ? modifystartDayTime : $('#datetime-picker-start').val(),
            endDate: $('#datetime-picker-end').val() == "" ? modifyendDayTime : $('#datetime-picker-end').val(),
            isPrivate: isPrivate,
            reminder: -1,
            members: member_value.length == 0 ? [initmember] : initArray,
            description: $('#comment textarea').val(),
            objectId: $("#shcedule_belongid_select").attr('value'),
            belongId: $("#shcedule_belongid_select").attr('objectId'),
            recurStopCondition: $("#shcedule_end_select").attr("value"),
            recurStopValue: recurStopValue,
            frequency: $("#shcedule_frequency_select").attr("value"),
            isRecur: setisRecur,
            id: GetQueryString('id')
        };
    } else {
        url = apppath + "/wx/schedule/docreate.action";
        //console.log('创建接口');
        var startday = $("#startDay").text();
        var startMinutes = $("#startMinutes").text();
        var modifystartDayTime = startday + " " + startMinutes;
        var endDay = $("#endDay").text();
        var endMinutes = $("#endMinutes").text();
        var modifyendDayTime = endDay + " " + endMinutes;
        var initmember = [];
        initmember = $("#shcedule_parter_select").attr('value');
        var recurStopValue = $('#datetime').val();
        var scheduleTemp = {
            name: $('.shcedule-header-titile input').val(),
            type: $('#shcedule_type_select').attr('value'),
            startDate: $('#datetime-picker-start').val() == "" ? modifystartDayTime : $('#datetime-picker-start').val(),
            endDate: $('#datetime-picker-end').val() == "" ? modifyendDayTime : $('#datetime-picker-end').val(),
            isPrivate: isPrivate,
            reminder: -1,
            members: arr_value.length == 0 ? [initmember] : arr_value,
            description: $('#comment textarea').val(),
            objectId: $("#shcedule_belongid_select").attr('value'),
            belongId: $("#shcedule_belongid_select").attr('objectId'),
            recurStopCondition: $("#shcedule_end_select").attr("value"),
            recurStopValue: recurStopValue,
            isRecur: isRecur,
            frequency: $("#shcedule_frequency_select").attr("value"),
        };
    }
    //console.log(scheduleTemp);
    $.ajax({
        type: "post",
        url: url,
        data: {
            scheduleStr: JSON.stringify(scheduleTemp)
        },

        async: true,
        beforeSend: function() {
            $('#shcedule_add_confirm').attr('disabled', 'true');
            loader();
        },
        dataType: 'json',
        success: function(oData) {
            $('.loaderArea').remove();
            if (oData.success == true) {
                //console.log(oData);
                myDialog('保存成功');
                if (GetQueryString('type') == 'set') {
                    var scheduleId = GetQueryString('id');
                } else {
                    var scheduleId = oData.entity.id;
                }
                setTimeout(function() {
                    location.replace(apppath + '/wx/schedule/list.action?' + ddcommparams + '&type=add&id=' + scheduleId + '&startDate=' + scheduleTemp.startDate + '&endDate=' + scheduleTemp.endDate);
                }, 2000);
            } else if (oData.success == false) {
                $('.confirm').removeAttr('disabled');
                myAlert('保存失败，请检查输入信息');
            }
        },
        error: function() {
            $('.loaderArea').remove();
            $('.confirm').removeAttr('disabled');
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}

var ownerId = xsyUser.id;
var member_value = [];
var member_label = [];

function scheduleSetInfo() {
    $.ajax({
        url: apppath + "/wx/schedule/getDetail.action",
        data: {
            id: GetQueryString('id'),
            scene: 'UPDATE',
        },
        dataType: 'json',
        success: function(oData) {
            //console.log(oData);
            $('.shcedule-header-titile input').val(oData.entity.data.name);
            var type = oData.entity.data.type;
            if (type == "") {
                $('#shcedule_type_select').html('选择类型');
            } else {
                $('#shcedule_type_select').html(oData.entity.expandPro.typeText);
                $('#shcedule_type_select').attr('value', oData.entity.data.type);
            }

            btn_color_adjust();
            var startDate = oData.entity.data.startDate;
            var endDate = oData.entity.data.endDate;
            var startYear = startDate.substring(0, 10);
            var startMinutes = startDate.substring(11, 16);
            var endYear = endDate.substring(0, 10);
            var endMinutes = endDate.substring(11, 16);
            $('#startDay').html(startYear);
            $("#startMinutes").html(startMinutes);
            $('#endDay').html(endYear);
            $("#endMinutes").html(endMinutes);
            var membersArray = oData.entity.expandPro.members;
            if (membersArray.length == "0") {
                $('#shcedule_parter_select').html('点击选择');
            } else {
                $.each(membersArray, function(i, o) {
                    member_value.push(o.id);
                    member_label.push(o.name + ",");
                    // var memberwrap = $('<div class="member-wrap" id=' + o.id + '></div>')
                    // var membername = $('<span class="member-name">' + o.name + '</span>');
                    // var memberimg = $('<img src=' + o.icon + '>')
                    // memberwrap.append(memberimg);
                    // memberwrap.append(membername);
                    // $('#members').append(memberwrap);
                });

                $('#shcedule_parter_select').attr('value', member_value);
                $('#shcedule_parter_select').html(member_label);
            }

            var objectId = oData.entity.data.objectId;
            var belongId = oData.entity.data.belongId;
            var objectIdText = oData.entity.expandPro.objectIdText;
            if (belongId == "") {
                $("#shcedule_belongid_select").html('点击选择')
            } else {
                $("#shcedule_belongid_select").addClass("active");
                $("#shcedule_belongid_select").attr('value', objectId);
                $("#shcedule_belongid_select").attr('objectid', belongId);
                $("#shcedule_belongid_select").html(objectIdText);
            }

            var isPrivate = oData.entity.data.isPrivate;
            var shcedule_is_private = $('input[name="shcedule_is_private"]');
            if (isPrivate == true) {
                shcedule_is_private.attr('checked', true);
            } else {
                shcedule_is_private.attr('checked', false);
            }
            var isRecur = oData.entity.data.isRecur;
            var recurStopCondition = oData.entity.data.recurStopCondition;
            var recurStopConditionText = oData.entity.expandPro.recurStopConditionText;
            var recurStopValue =  oData.entity.data.recurStopValue;
            if(isRecur == true && recurStopCondition == '1'){

                 $("#shcedule_end_item").show();
                 $("#shcedule_end_select").addClass('active');
                 $("#shcedule_end_select").attr('value',recurStopCondition);
                 $("#shcedule_end_select").html(recurStopConditionText);
            }
            if(isRecur == true && recurStopCondition == '2'){
                $("#shcedule_end_item").show();
                $("#shcedule_end_time").show();
                $("#shcedule_end_select").addClass('active')
                $("#shcedule_end_select").attr('value',recurStopCondition);
                $("#shcedule_end_select").html(recurStopConditionText);
                var datetime = recurStopValue.substring(0,10);
                $("#datetime").val(datetime);
            }
            var frequency = oData.entity.data.frequency;
            $('#shcedule_frequency_select').attr('value', frequency);
            $('#shcedule_frequency_select').html(oData.entity.expandPro.frequencyText);


            var description = oData.entity.data.description;
            if (description == "") {
                $('#comment textarea').val('描述');
            } else {
                $('#comment textarea').val(oData.entity.data.description);
            }
            //getStartValue();
            $('textarea').autoHeight();
            textarea_num();
        }
    })
}



// 
function list_top() {
    $(window).on('scroll', function() {
        var acc_top = $('#acc_area').offset().top - $(document).scrollTop();
        //吸顶状态
        if (acc_top <= 88) {
            $('#account_top').css({ 'position': 'fixed', 'top': '88px' });
            $('#acc_area').css({ 'paddingTop': $('#account_top').height() + 'px' });
        }
        //流布局状态（原始状态）
        else {
            $('#account_top').css({ 'position': 'static' });
            $('#acc_area').css({ 'paddingTop': '0px' });
        };
        var con_top = $('#con_area').offset().top - $(document).scrollTop();
        //挤压（固定）状态
        if (con_top <= 88 + $('#account_top').height()) {
            $('#account_top').css({ 'position': 'absolute', 'top': $('#con_area').offset().top - $('#account_top').height() - 1 + 'px' });
        } else {
            $('#account_top').css({ 'top': '88px' });
        }
        if (con_top <= 88) {
            $('#contact_top').css({ 'position': 'fixed', 'top': '88px' });
            $('#con_area').css({ 'paddingTop': $('#contact_top').height() + 'px' });
        } else {
            $('#contact_top').css({ 'position': 'static' });
            $('#con_area').css({ 'paddingTop': '0px' });
        };
    })
}
//关键字
var val = null;

function search() {
    // show_history();
    //搜索框键入信息
    $('#txt').on('input', function() {
        input_search();
    });
}

function input_search() {
    searchInputDelete();
    val = $('#txt').val();
    if (val.substr(0, 1) == ' ') {
        val = val.trim();
    }
    if (val == '') {
        $('.list').remove();
        $('.dialog').remove();
        // show_history();
    } else {
        show_word(val);
    }
}
//展示客户最近搜索
function show_history() {
    $('#content').children().remove();
    if (localStorage.getItem(userId) != 'undefined') {
        var ls = JSON.parse(localStorage.getItem(userId));
        //console.log(ls)
        if (ls.accountHistory) {
            if (ls.accountHistory.length != 0) {
                create_history_list(ls);
            } else {
                create_history_null();
            };
        } else {
            create_history_null();
        };
    } else {
        create_history_null();
    }
}

function create_history_list(ls) {
    //最多显示20条最近历史搜索记录
    if (ls.accountHistory.length <= 20) {
        for (var i = ls.accountHistory.length - 1; i >= 0; i--) {
            build_history_list(ls, i);
        }
        create_delete();
    } else {
        for (var i = ls.accountHistory.length - 1; i >= ls.accountHistory.length - 20; i--) {
            build_history_list(ls, i);
        }
        create_delete();
    }
}

function build_history_list(ls, i) {
    var oList = document.createElement('div');
    oList.className = 'boxSizing history_list';
    var name = ls.accountHistory[i].name;
    var id = ls.accountHistory[i].id;
    var kindle = ls.accountHistory[i].kindle;
    if (kindle == 1) {
        $(oList).append($('<em class="account_pic"></em>'));
        $(oList).on('click', function() {
            location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + id;
        })
    } else {
        $(oList).append($('<em class="contact_pic"></em>'));
        $(oList).on('click', function() {
            location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + id;
        })
    }
    $(oList).append($('<span class="name">' + name + '</span>'));
    var oExit = document.createElement('div');
    oExit.innerHTML = '<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDA5MDMwNDRCMkU5MTFFNUE4MDNFMkU5NkI5N0FEOEMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDA5MDMwNDVCMkU5MTFFNUE4MDNFMkU5NkI5N0FEOEMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpCMTRGRjgyMUIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpCMTRGRjgyMkIyRTUxMUU1QTgwM0UyRTk2Qjk3QUQ4QyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Prom4REAAADHSURBVHjanNVNCoMwEAXgydAb9CJ21QvYVb2uK91rwUUPYs9gZ2ACIeTnJQNv48gHCQ91+3ZsROQkb8mP+uYumSUXG/aULLbowVYznIKT5Ct5dKAeG8yYFDwlrwBdQTTG1DjZliE6AGgS0wUHL6FoFotBBC1iKbCEVjGdW+aOPLoEKNWwEphCqYbljhzOBT6DwPjOoEpxQ8+gnnJDz6CecktpEZRbSoug3IhVUe7Aiijbl9ZjI4jl0Nkf+WNYzy/Ao2rQX4ABAORFZTtFQ9/RAAAAAElFTkSuQmCC"/>';
    oExit.className = 'exit';
    $(oExit).on('click', function() {
        ls.accountHistory.remove(i);
        localStorage.setItem(userId, JSON.stringify(ls));
        $(oList).remove();
        if ($('#content').find('.history_list').length == 0) {
            $('#content > .clear').remove();
            create_history_null();
        }
    });
    oList.appendChild(oExit);
    oList.href = id;
    $('#content').append(oList);
}
//清除历史记录
function create_delete() {
    var _delete = document.createElement('div');
    _delete.innerHTML = "清除全部历史记录";
    _delete.className = "clear"
    _delete.onclick = function() {
        var ls = JSON.parse(localStorage.getItem(userId));
        ls.accountHistory = [];
        localStorage.setItem(userId, JSON.stringify(ls));
        $('#content > .history_list').remove();
        $('#content > .clear').remove();
        create_history_null();
    };
    $('#content').append(_delete);
}
//暂无历史记录
function create_history_null() {
    var history_null = document.createElement('div');
    history_null.innerHTML = '暂无历史记录';
    history_null.className = 'null';
    $('#content').append(history_null);
}
//展示关键字搜索
function show_word(val) {
    $('#content').children().remove();
    var entity = $('#txt').attr('data-entity');
    if (entity == 'account') {
        create_accword_list(val);
    }
    if (entity == 'contact') {
        create_conword_list(val);
    }
    if (entity == 'opp') {
        create_oppword_list(val);
    }
}
var acc_pageNo = 0;
var con_pageNo = 0;
var opp_pageNo = 0;
var pageSize = 30;
//首次加载的pageSize条数据
var oData;

function create_accword_list(val) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/account/dolist.action",
        async: true,
        data: { searchName: val, pageNo: 0, pagesize: pageSize },
        dataType: 'json',
        success: function(oData) {
            // oData = eval('('+data+')');
            //console.log(oData);
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                $('.dialog').remove();
                build_accword_list(oData);
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}

function create_conword_list(val) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/contact/dolist.action",
        async: true,
        data: { searchName: val, pageNo: 0, pagesize: pageSize },
        dataType: 'json',
        success: function(oData) {
            // oData = eval('('+data+')');
            //console.log(oData);
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                $('.dialog').remove();
                build_conword_list(oData);
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}

function create_oppword_list(val) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/opportunity/dolist.action",
        async: true,
        data: { opportunityName: val, pageNo: 0, pagesize: pageSize },
        dataType: 'json',
        success: function(oData) {
            // oData = eval('('+data+')');
            //console.log(oData);
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                $('.dialog').remove();
                build_oppword_list(oData);
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}
//var arrId = [];
//var arrName = [];
var accLength;

function build_accword_list(obj) {
    $('#content>.list').remove();
    accLength = obj.entity.totalSize;
    var acc_length = obj.entity.records.length;
    //公司部分
    if (accLength > 0) {
        var acc_area = $('<div id="acc_area" class="boxSizing"></div>');
        // var top = $('<div id="account_top" class="boxSizing top"><img src="../../images/wx/2x/common/account.png"/><sapn>公司</sapn></div>');
        // acc_area.append(top);
    }
    if ($('#acc_area').length > 0) {
        $('#acc_area').remove();
    }
    $('#content').append(acc_area);
    create_acc_list(0, accLength, obj);
    //无结果情况
    if (accLength == 0) {
        $('#content>.list').remove();
        var oList = document.createElement('div');
        oList.style.cssText = "width:100%;padding-left:10px;line-height:88px;font-size:30px;text-align:center;";
        oList.className = 'list';
        oList.innerHTML = "无相关客户";
        $('#content').append(oList);
    };
};
var conLength;

function build_conword_list(obj) {
    $('#content>.list').remove();
    conLength = obj.entity.totalSize;
    var con_length = obj.entity.records.length;
    //联系人部分
    if (conLength > 0) {
        var con_area = $('<div id="con_area" class="boxSizing"></div>');
    }
    if ($('#con_area').length > 0) {
        $('#con_area').remove();
    }
    $('#content').append(con_area);
    create_con_list(0, conLength, obj);
    //无结果情况
    if (conLength == 0) {
        $('#content>.list').remove();
        var oList = document.createElement('div');
        oList.style.cssText = "width:100%;padding-left:10px;line-height:88px;font-size:30px;text-align:center;";
        oList.className = 'list';
        oList.innerHTML = "无相关联系人";
        $('#content').append(oList);
    };
};


//
var oppLength;

function build_oppword_list(obj) {
    $('#content>.list').remove();
    conLength = obj.entity.totalSize;
    var oppLength = obj.entity.records.length;
    //联系人部分
    if (conLength > 0) {
        var opp_area = $('<div id="opp_area" class="boxSizing"></div>');
    }
    if ($('#opp_area').length > 0) {
        $('#opp_area').remove();
    }
    $('#content').append(opp_area);
    create_opp_list(0, oppLength, obj);
    //无结果情况
    if (oppLength == 0) {
        $('#content>.list').remove();
        var oList = document.createElement('div');
        oList.style.cssText = "width:100%;padding-left:10px;line-height:88px;font-size:30px;text-align:center;";
        oList.className = 'list';
        oList.innerHTML = "无相关商机";
        $('#content').append(oList);
    };
};

//公司部分加载机制
function create_acc_list(start, end, obj) {
    if (obj != null) {
        var new_data = obj;
        build_acc_list(start, end, new_data);
    } else {
        $.ajax({
            type: "get",
            url: apppath + "/wx/account/dolist.action",
            async: true,
            data: { searchName: val, pageNo: acc_pageNo, pagesize: pageSize },
            dataType: 'json',
            success: function(oData) {
                // var new_data = eval('('+data1+')');
                if (oData.entity.accounts.success == false || oData.entity.contacts.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    $('.dialog').remove();
                    build_acc_list(start, end, oData);
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    }
};

function getBelongIdText(id, label) {
    var belongid = $('#shcedule_belongid_select');
    belongid.attr('value', id);
    belongid.html(label);
    $('#txt').attr("value", "");
    $('.shcedule-search-warp').find('#content').children().remove();
    $('.shcedule-search-warp').hide();
    $('.ct').remove();
    $('body').css({ 'overflow': '', 'height': 'auto' });
}

function build_acc_list(start, end, new_data) {
    if (end > new_data.entity.records.length) {
        end = new_data.entity.records.length;
    }
    for (var i = start; i < end; i++) {
        var oList = document.createElement('div');
        var name = new_data.entity.records[i].accountName;
        var accountName = new_data.entity.records[i].accountName;
        var reg = new RegExp(val, "g");
        var id = new_data.entity.records[i].id;
        oList.href = id;
        oList.name = name;
        name = name.replace(reg, "<span class='blue'>" + val + "</span>");
        oList.innerHTML = name;
        oList.className = 'boxSizing word_list';
        oList.accountName = accountName;
        //arrId.push(id);arrName.push(name);
        oList.addEventListener('click', function() {
            local_set(this.href, this.name, 1);
            getBelongIdText(this.href, this.accountName);
            // location.href = apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + this.href;
        });
        $('#acc_area').append(oList);
    };
    // if ($('#acc_area > .word_list').length < accLength) {
    //     if ($('#acc_area > .word_list').length == 5) {
    //         acc_first_more(oData);
    //     } else {
    //         create_acc_more();
    //     }
    // } else if (accLength > 5) {
    //     create_acc_less();
    // }
}

// function acc_first_more(data) {
//     var first_more = $('<div class="show_more">查看更多</div>');
//     $('#acc_area').append(first_more);
//     first_more.on('click', function() {
//         $(this).remove();
//         create_acc_list(5, 30, data);
//     });
// }

// function create_acc_more() {
//     var bot_more = $('<div class="show_more">查看更多</div>');
//     $('#acc_area').append(bot_more);
//     bot_more.on('click', function() {
//         $(this).remove();
//         acc_pageNo += 1;
//         create_acc_list(0, 30);
//     });
// };

// function create_acc_less() {
//     var bot_less = $('<div class="show_less">收起</div>');
//     $('#acc_area').append(bot_less);
//     bot_less.on('click', function() {
//         $(this).remove();
//         $('#acc_area>.word_list').each(function(index) {
//             if ($(this).index() > 5) {
//                 $(this).remove();
//             }
//         });
//         acc_pageNo = 0;
//         acc_first_more(oData);
//     });
// };
//联系人部分加载机制
function create_con_list(start, end, obj) {
    if (obj != null) {
        var new_data = obj;
        build_con_list(start, end, new_data);
    } else {
        $.ajax({
            type: "get",
            url: apppath + "/wx/contact/dolist.action",
            async: true,
            data: { searchName: val, pageNo: con_pageNo, pagesize: pageSize },
            dataType: 'json',
            success: function(oData) {
                // var new_data = eval('('+data1+')');
                if (oData.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    $('.dialog').remove();
                    build_con_list(start, end, oData);
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    }
};

function build_con_list(start, end, new_data) {
    if (end > new_data.entity.records.length) {
        end = new_data.entity.records.length;
    }
    for (var i = start; i < end; i++) {
        var oList = document.createElement('div');
        var name = new_data.entity.records[i].contactName;
        var contactName = new_data.entity.records[i].contactName;
        var reg = new RegExp(val, "g");
        name = name.replace(reg, "<span class='blue'>" + val + "</span>");
        var id = new_data.entity.records[i].id;
        oList.innerHTML = name;
        oList.className = 'boxSizing word_list';
        oList.href = id;
        oList.name = name;
        oList.accountName = contactName;
        //arrId.push(id);arrName.push(name);
        oList.addEventListener('click', function() {
            //console.log(this.href);
            local_set(this.href, this.name, 2);
            getBelongIdText(this.href, this.accountName);
            // location.href = apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + this.href;
        });
        $('#con_area').append(oList);
    };
    // if ($('#con_area > .word_list').length < conLength) {
    //     if ($('#con_area > .word_list').length == 5) {
    //         con_first_more(oData);
    //     } else {
    //         create_con_more();
    //     }
    // } else if (conLength > 5) {
    //     create_con_less();
    // }
}

// function con_first_more(data) {
//     var first_more = $('<div class="show_more">查看更多</div>');
//     $('#con_area').append(first_more);
//     first_more.on('click', function() {
//         $(this).remove();
//         create_con_list(5, 30, data);
//     });
// }

// function create_con_more() {
//     var bot_more = $('<div class="show_more">查看更多</div>');
//     $('#con_area').append(bot_more);
//     bot_more.on('click', function() {
//         $(this).remove();
//         con_pageNo += 1;
//         create_con_list(0, 30);
//     });
// };

// function create_con_less() {
//     var bot_less = $('<div class="show_less">收起</div>');
//     $('#con_area').append(bot_less);
//     bot_less.on('click', function() {
//         $(this).remove();
//         $('#con_area>.word_list').each(function(index) {
//             if ($(this).index() > 5) {
//                 $(this).remove();
//             }
//         });
//         con_pageNo = 0;
//         con_first_more(oData);
//     });
// };

//商机部分加载机制
function create_opp_list(start, end, obj) {
    if (obj != null) {
        var new_data = obj;
        build_opp_list(start, end, new_data);
    } else {
        $.ajax({
            type: "get",
            url: apppath + "/wx/opportunity/dolist.action",
            async: true,
            data: { opportunityName: val, pageNo: opp_pageNo, pagesize: pageSize },
            dataType: 'json',
            success: function(oData) {
                // var new_data = eval('('+data1+')');
                if (oData.success == false) {
                    myAlert('网络不给力，请重新加载');
                } else {
                    $('.dialog').remove();
                    build_opp_list(start, end, oData);
                }
            },
            error: function() {
                myAlert('网络不给力，请重新加载')
            }
        });
    }
};

function build_opp_list(start, end, new_data) {
    if (end > new_data.entity.records.length) {
        end = new_data.entity.records.length;
    }
    for (var i = start; i < end; i++) {
        var oList = document.createElement('div');
        var name = new_data.entity.records[i].opportunityName;
        var opportunityName = new_data.entity.records[i].opportunityName;
        var reg = new RegExp(val, "g");
        name = name.replace(reg, "<span class='blue'>" + val + "</span>");
        var id = new_data.entity.records[i].id;
        oList.innerHTML = name;
        oList.className = 'boxSizing word_list';
        oList.href = id;
        oList.name = name;
        oList.opportunityName = opportunityName;
        //arrId.push(id);arrName.push(name);
        oList.addEventListener('click', function() {
            //console.log(this.href);
            local_set(this.href, this.name, 3);
            getBelongIdText(this.href, this.opportunityName);
            // location.href = apppath + '/wx/opportunity/info.action?' + ddcommparams + '&opportunityId=' + this.href;
        });
        $('#opp_area').append(oList);
    };
    // if ($('#opp_area > .word_list').length < oppLength) {
    //     if ($('#opp_area > .word_list').length == 5) {
    //         opp_first_more(oData);
    //     } else {
    //         create_opp_more();
    //     }
    // } else if (oppLength > 5) {
    //     create_opp_less();
    // }
}

// function opp_first_more(data) {
//     var first_more = $('<div class="show_more">查看更多</div>');
//     $('#opp_area').append(first_more);
//     first_more.on('click', function() {
//         $(this).remove();
//         create_opp_list(5, 30, data);
//     });
// }

// function create_opp_more() {
//     var bot_more = $('<div class="show_more">查看更多</div>');
//     $('#opp_area').append(bot_more);
//     bot_more.on('click', function() {
//         $(this).remove();
//         opp_pageNo += 1;
//         create_opp_list(0, 30);
//     });
// };

// function create_opp_less() {
//     var bot_less = $('<div class="show_less">收起</div>');
//     $('#opp_area').append(bot_less);
//     bot_less.on('click', function() {
//         $(this).remove();
//         $('#opp_area>.word_list').each(function(index) {
//             if ($(this).index() > 5) {
//                 $(this).remove();
//             }
//         });
//         opp_pageNo = 0;
//         opp_first_more(oData);
//     });
// };
var userId = xsyUser.id;
//本地存储
function local_set(id, name, kindle) {
    if (kindle == 1 || kindle == 2 || kindle == 3) {
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
                ls.accountHistory = [val];
            }
        } else {
            var ls = {
                "accountHistory": [val]
            }
        }
    } else if (kindle == 3) {
        var val = { "id": id, "name": name };
        if (localStorage.getItem(userId)) {
            var ls = JSON.parse(localStorage.getItem(userId));
            //去重，后点排在前
            for (var j = 0; j < ls.accountHistory.length; j++) {
                if (ls.accountHistory[j].id == id) {
                    ls.accountHistory.remove(j);
                }
            }
            ls.accountHistory.push(val);
        } else {
            var ls = {
                "accountHistory": [val]
            }
        }
    }
    localStorage.setItem(userId, JSON.stringify(ls));
};
