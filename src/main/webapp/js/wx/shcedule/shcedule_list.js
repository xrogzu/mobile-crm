var haveOpen = false;
var index_ = 1;
var scheduleData;
function execRn() {
    var currentyear = $(".picker-calendar-year-picker .current-year-value").text();
    var currentmonth = $(".picker-calendar-month-picker .current-month-value").text();
    $(".shcedule-year").text(currentyear);
    $(".shcedule-month").text(currentmonth);
    //alert(i++);
    // alert("" + index_++);
    initpicker();
    // var span = $('<i class="circle"></i>');
    // $("[data-date='2016-6-9']").find('span').append(span);
}
var selectDate;
var selectFlag;
$(document).ready(function() {
    $("#inline-calendar").calendar({
        container: "#inline-calendar",
        input: "#date3",
        onOpen: function(p) {
            var currentyear = $(".picker-calendar-year-picker .current-year-value").text();
            var currentmonth = $(".picker-calendar-month-picker .current-month-value").text();
            $(".shcedule-year").text(currentyear);
            $(".shcedule-month").text(currentmonth);
            haveOpen = true;

        },
        onMonthAdd: function() {
            if (haveOpen)
                execRn();
        },
        onDayClick: function(p, dayContainer, year, month, day) {
            var firstyear = year;
            var firstmouth = parseInt(month) + 1;
            var firstday = day;
            selectDate = firstyear + '-' + Appendzero(firstmouth) + '-' + Appendzero(firstday);
            $(".shcedule-filter").removeClass('active');
            getScduleinfo(selectDate,selectDate);
            selectFlag = true;
        }


    });
    screen();
    $('.shcedule-add').on('click', function() {
        console.log(selectDate);
        if (selectFlag) {
            location.href = apppath + '/wx/schedule/add.action?' + ddcommparams + '&from=schedule_list&type=add&data=' + selectDate;
        } else {
            location.href = apppath + '/wx/schedule/add.action?' + ddcommparams + '&from=schedule_list&type=add';
        }
    });
    $('.shcedule-return').on('click', function() {
        window.location.reload();
    });
    // var startTime = GetQueryString('startDate');
    // var endTime = GetQueryString('endDate');
    // var currentTime = $('#date3').val();
    // if (GetQueryString('type') == 'add') {
    //     getScduleinfo(startTime, endTime);
    // } else {

    // }
    initpicker();
    window.addEventListener('popstate', function(event) {
        console.log(event.state);
        if (event.state == "1" || event.state == null) {
            $("#shcedule_screen_area").hide();
            $(".shcedule-filter").removeClass('active');
        }
        var area = $("#shcedule_screen_area");
        if (event.state == null && area.length == 0 || event.state == "1" && area.length == 0) {
            // location.href = apppath + '/wx/serveice/index.action?' + ddcommparams;
        }
    });

    if (event.state == null) {
        history.pushState('1', 'ceshi title', window.location.href);
    } else {
        history.pushState('2', 'ceshi title', window.location.href);

    }
});

function getScduleinfo(startTime, endTime, type, from) {
    $.ajax({
        type: "get",
        url: apppath + "/wx/schedule/dolist.action?d=" + (+new Date),
        data: { startTime: startTime, endTime: endTime, type: type },
        async: true,
        dataType: 'json',
        success: function(data) {
            if (data.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                console.log(data);
                var records = data.entity.records;
                $.each(records, function(i, o) {
                    if (o.count == 0) {
                        if (from == "screen") {
                            $('.schedule-body').children().remove();
                            var schedulenull = $('<div class="schedule-list-null"> <p>无相关内容</p></div>')
                            $('.schedule-body').append(schedulenull);
                            addschdule();
                        } else {
                            $('.schedule-body').children().remove();
                            var schedulenull = $('<div class="schedule-list-null"> <p>今天没有日程哦</p><a href="javascript:;" class="index-add-schdule">马上添加</a></div>')
                            $('.schedule-body').append(schedulenull);
                            addschdule();
                        }

                    } else {
                        // var time = i;
                        // var timestring = time.split('-');
                        // var targetSpan = $('.shcedule-datepicker-body').find('[data-year="' + timestring[0] + '"][data-month="' + (Number(timestring[1]) - 1) + '"][data-day="' + Number(timestring[2]) + '"]');
                        // var span = $('<i class="circle"></i>');
                        // targetSpan.find('span').append(span);
                        createScduleList(o.list);
                    }
                });
            }

        }
    });
}



function initpicker() {
    var firsttime = $(".picker-calendar-month-current").find(".picker-calendar-row").first().find(".picker-calendar-day").first();
    var lasttime = $(".picker-calendar-month-current").find(".picker-calendar-row").last().find(".picker-calendar-day").last();
    var firstyear = firsttime.attr('data-year');
    var firstmouth = Number(firsttime.attr('data-month')) + 1;
    var firstday = firsttime.attr('data-day');
    var firsttimestring = firstyear + '-' + Appendzero(firstmouth) + '-' + Appendzero(firstday);
    var lastyear = lasttime.attr('data-year');
    var lastmouth = Number(lasttime.attr('data-month')) + 1;
    var lastday = lasttime.attr('data-day');
    var lasttimestring = lastyear + '-' + Appendzero(lastmouth) + '-' + Appendzero(lastday);
    $.ajax({
        type: "get",
        url: apppath + "/wx/schedule/dolist.action",
        data: { startTime: firsttimestring, endTime: lasttimestring },
        async: true,
        dataType: 'json',
        success: function(data) {
            if (data.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                console.log(data);
                scheduleData = data;
                var records = data.entity.records;
                $.each(records, function(i, o) {
                    if (o.count == '0') {
                        // return;
                    } else {
                        var time = i;
                        var timestring = time.split('-');
                        var targetSpan = $('.shcedule-datepicker-body').find('[data-year="' + timestring[0] + '"][data-month="' + (Number(timestring[1]) - 1) + '"][data-day="' + Number(timestring[2]) + '"]');
                        var span = $('<i class="circle"></i>');
                        targetSpan.find('span').append(span);
                    }

                });
            }
              var currentTime = $('#date3').val();
               getlistSchduleInfo(currentTime);
        }
    });

}

function getlistSchduleInfo(datetime, from) {
    console.log(scheduleData);
    var records = scheduleData.entity.records;
    $.each(records, function(i, o) {
        if (i == datetime) {
            if (o.count == 0) {
                if (from == "screen") {
                    $('.schedule-body').children().remove();
                    var schedulenull = $('<div class="schedule-list-null"> <p>无相关内容</p></div>')
                    $('.schedule-body').append(schedulenull);
                    addschdule();
                } else {
                    $('.schedule-body').children().remove();
                    var schedulenull = $('<div class="schedule-list-null"> <p>今天没有日程哦</p><a href="javascript:;" class="index-add-schdule">马上添加</a></div>')
                    $('.schedule-body').append(schedulenull);
                    addschdule();
                }

            } else {
                // var timestring = datetime.split('-');
                // var targetSpan = $('.shcedule-datepicker-body').find('[data-year="' + timestring[0] + '"][data-month="' + (Number(timestring[1]) - 1) + '"][data-day="' + Number(timestring[2]) + '"]');
                // var span = $('<i class="circle"></i>');
                // targetSpan.find('span').append(span);
                createScduleList(o.list);
            }
        }

    });
}

function pickerSelect() {
    // $(".picker-calendar-row").find(".picker-calendar-day").on('click', function() {
    //     var firstyear = $(this).attr('data-year');
    //     var firstmouth = Number($(this).attr('data-month')) + 1;
    //     var firstday = $(this).attr('data-day');
    //     var firsttimestring = firstyear + '-' + Appendzero(firstmouth) + '-' + Appendzero(firstday);
    //     $(".shcedule-filter").removeClass('active');
    //     getlistSchduleInfo(firsttimestring);
    // });

}

function createScduleList(data) {
    $('.schedule-body').children().remove();
    var scheduleitem = $('<div class="schedule-item"></div>');
    for (var i = 0, len = data.length; i < len; i++) {
        var startDate = data[i].startDate.substring(11, 16);
        var endDate = data[i].endDate.substring(11, 16);
        var status = data[i].status;
        var schedulecell = $('<div class="schedule-cell" id=' + data[i].id + '></div>');
        var scheduleitemtime = $('<div class="schedule-item-time"><span class="schedule-time-start">' + startDate + '</span><span class="schedule-time-line">-</span><span class="schedule-time-end">' + endDate + '</span></div>');
        var scheduleitemtitle = $('<div class="schedule-item-title">' + data[i].name + '</div>');
        var scheduleitemoperate = $('<div class="schedule-item-operate"><a href="javascript:;" class="schedule-item-accept">接受</a><a href="javascript:;" class="schedule-item-refuse">拒绝</a></div>')
        schedulecell.append(scheduleitemtime);
        schedulecell.append(scheduleitemtitle);
        if (status == "1") {
            schedulecell.append(scheduleitemoperate);
        }
        if (status == "2") {
            schedulecell.append(scheduleitemoperate);
        }
        scheduleitem.append(schedulecell);
    }
    $('.schedule-body').append(scheduleitem);
    $(".schedule-item .schedule-cell").on('click', function() {
        var id = $(this).attr("id");
        location.href = apppath + '/wx/schedule/info.action?' + ddcommparams + '&from=schdule_list' + '&id=' + id;
    });
    if(GetQueryString('reject') == 'reject'){
        var id = GetQueryString('id');
        $('.schedule-item #'+id+'').remove();
    }
    schduleAccept();
    scheduleReject();
}
var shceduleTypelist = {};
var shceduleTypeId;

function screen() {
    $(".load-shadow").show();
    $('.shcedule-filter').on('click', function() {
        if ($(this).hasClass('active')) {
            // $(this).removeClass('active');
            $('#shcedule_screen_area').show();
        } else {
            $(this).addClass('active');
            if ($('#shcedule_screen_area').length > 0) {
                $('#shcedule_screen_area').show();
            } else {
                var screen = $('<div id="shcedule_screen_area"  class="boxSizing"></div>');
                var stage_screen = $('<div class="stage_screen"><div class="title boxSizing">日程类型</div><div class="con"></div></div>');
                var confirm = $('<div class="footer"><a href="javascript:;" class="confirm">确定</a> <a href="javascript:;" class="reset">重置</a></div>');
                screen.append(stage_screen);
                screen.append(confirm);
                $('body').append(screen);
                $.ajax({
                    url: apppath + '/wx/schedule/getDetail.action',
                    dataType: 'json',
                    data: { scene: 'DETAIL' },
                    success: function(oData) {
                        $(".load-shadow").hide();
                        if (oData.success == true) {
                            var components = oData.entity.structure.components;
                            var len = components.length;
                            for (var i = 0; i < len; i++) {
                                if (components[i].propertyname == 'type') {
                                    shceduleTypelist = components[i].selectItem;
                                }
                            }
                            for (var i = 0, length = shceduleTypelist.length; i < length; i++) {
                                var stage_item = $('<div class="stage_item item boxSizing"><span>' + shceduleTypelist[i].label + '</span></div>');
                                stage_item.attr('id', shceduleTypelist[i].value);
                                stage_item.bind('click', function() {
                                    if ($(this).hasClass('active')) {
                                        $(this).removeClass('active');
                                    } else {
                                        $(this).addClass('active');
                                        $(this).siblings().removeClass('active');
                                        shceduleTypeId = Number($(this).attr('id'));
                                    }
                                });
                                $('.stage_screen .con').append(stage_item);
                            }

                        }
                    }
                })
            }
        }
        $('.reset').bind('click', function() {
            $('.item').removeClass('active');
            // $('.shcedule-filter').removeClass('active');
            // $('#shcedule_screen_area').hide();
            var time = $("#date3").val();
            getlistSchduleInfo(time);

        })
         $('.confirm').unbind('click');
        $('.confirm').bind('click', function() {
            // $('shcedule-filter').removeClass('active');
            // $('#list .main').children().remove();
            var activeitem = $("#shcedule_screen_area .con").find(".active").length;
            if (activeitem <= 0) {
                $('.shcedule-filter').removeClass('active');
            }
            var type = $(".stage_item.active").attr('id');
            var time = $("#date3").val();
            $('#shcedule_screen_area').hide();
            var elem = "screen";
            getScduleinfo(time, time, type, elem);

        })
    })
}


// 添加日程
function addschdule() {
    $('.index-add-schdule').on('click', function() {
         if (selectFlag) {
            location.href = apppath + '/wx/schedule/add.action?' + ddcommparams + '&from=schedule_list&type=add&data=' + selectDate;
        } else {
            location.href = apppath + '/wx/schedule/add.action?' + ddcommparams + '&from=schedule_list&type=add';
        }
    });
}

//接受
function schduleAccept() {
    $(".schedule-item-accept").on('click', function(e) {
        var id = $(this).closest('.schedule-cell').attr("id");
        e.stopPropagation();
        acceptReceipt(id, $(this));
    });
}

function acceptReceipt(id, elem) {
    $.ajax({
        url: apppath + '/wx/schedule/doaccept.action',
        data: { id: id },
        dataType: 'json',
        success: function(Data) {
            if (Data.success == true) {
                elem.hide();
                elem.siblings().hide();
            }
        }
    });
}

// 拒绝日程
function scheduleReject() {
    $(".schedule-item-refuse").on('click', function(e) {
        var id = $(this).closest('.schedule-cell').attr("id");
        e.stopPropagation();
        rejectReceipt(id, $(this));
    });
}

function rejectReceipt(id, elem) {
    $.ajax({
        url: apppath + '/wx/schedule/doreject.action',
        data: { id: id },
        dataType: 'json',
        success: function(Data) {
            if (Data.success == true) {
                elem.hide();
                elem.siblings().hide();
                elem.closest('.schedule-cell').remove();
            }
        }
    });
}
