$(document).on('ready', function() {
        $(".shcedule-detail-header-right").on('click', function() {
            $(".options").toggle();
        });
        $("#schedule_edit").on('click', function() {
            location.href = apppath + '/wx/schedule/add.action?' + ddcommparams + '&type=set&from=shcedule_info&id=' + GetQueryString('id');
        });
        getShceduleInfo();
        scheduleExit();
        schduleDelete();

    })
    // 点击页面任何地方 新建弹窗消失
$(document).on('touchstart', function(e) {
    if (!$(e.target).closest(".shcedule-detail-header-right").size()) {
        $(".options").hide();
    }
});
$(document).on('click', function(e) {
    if (!$(e.target).closest(".shcedule-detail-header-right").size()) {
        $(".options").hide();
    }
});

function scheduleExit() {
    $('#schedule_exit').on('click', function() {
        myConfirm('确认是否退出，退出之后将无法看到日程');
        $('.y').on('click', function() {
            exitReceipt();
        })
        $('.n').on('click', function() {
            $('.chooseShadow').remove();
        })
    });
}

function exitReceipt() {
    var id = GetQueryString('id');
    $.ajax({
        url: apppath + '/wx/schedule/doreject.action',
        data: { id: id },
        dataType: 'json',
        success: function(Data) {
            if (Data.success == true) {
                myAlert('退出成功');
                if (GetQueryString('from') == 'schdule_list') {
                    setTimeout(function() {
                        location.replace(apppath + '/wx/schedule/list.action?' + ddcommparams);
                    }, 2000)
                }
            }
        }
    });
}

function schduleDelete() {
    $('#schedule_delete').on('click', function() {
        myConfirm('删除后无法恢复，确认是否删除');
        $('.y').on('click', function() {
            deleteReceipt();
        })
        $('.n').on('click', function() {
            $('.chooseShadow').remove();
        })
    });
}

function deleteReceipt() {
    var id = GetQueryString('id');
    $.ajax({
        url: apppath + '/wx/schedule/dodelete.action',
        data: { id: id },
        dataType: 'json',
        success: function(Data) {
            if (Data.success == true) {
                myAlert('删除成功');
                if (GetQueryString('from') == 'schdule_list') {
                    setTimeout(function() {
                        location.replace(apppath + '/wx/schedule/list.action?' + ddcommparams);
                    }, 2000)
                }
            }
        }
    });
}

//接受
function schduleAccept() {
    $(".shcedule-accept").on('click', function(e) {
        var id = GetQueryString('id');
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
                window.location.reload();
            }
        }
    });
}

// 拒绝日程
function scheduleReject() {
    $(".shcedule-reject").on('click', function(e) {
        var id = GetQueryString('id');
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
                var fromw = GetQueryString('from');
                if (fromw == 'index_schdule_list') {
                    location.href = apppath + '/wx/statics/index.action?' + ddcommparams + '&listform=index_schedule_info' + '&id='+id +'&reject=reject';
                } else {
                    location.href = apppath + '/wx/schedule/list.action?' + ddcommparams + '&listform=schedule_info' + '&id='+id+'&reject=reject';
                }
                // location.href = document.referrer
                    // history.go(-1);
                    // location.reload();
            }
        }
    });
}

function getShceduleInfo() {
    $.ajax({
        url: apppath + '/wx/schedule/getDetail.action',
        data: {
            id: GetQueryString('id'),
            scene: 'DETAIL'
        },
        dataType: "json",
        success: function(oData) {
            if (oData.success == true) {
                console.log(oData);
                $(".load-shadow").hide();
                if (oData.entity.expandPro.dataPermission.delete == false && oData.entity.expandPro.canDoQuitOperate == false) {
                    $(".shcedule-detail-header-right").hide();
                } else if (oData.entity.expandPro.canDoQuitOperate == true && oData.entity.expandPro.dataPermission.delete == false) {
                    $("#schedule_delete").parent().hide();
                    $("#schedule_edit").parent().hide();
                } else if (oData.entity.expandPro.canDoQuitOperate == false) {
                    $("#schedule_exit").parent().hide();
                }
                var nullText = "<span class='cell-null'>未填写</span>"
                var name = oData.entity.data.name;
                name == "" ? $(".shcedule-detail-title").html(nullText) : $(".shcedule-detail-title").html(name);
                var typeText = oData.entity.expandPro.typeText;
                typeText == "" ? $('.shcedule-detail-style').html(nullText) : $('.shcedule-detail-style').html(typeText);
                var startDate = oData.entity.data.startDate;
                startDate == "" ? $(".shcedule-detail-time-start").html(nullText) : $(".shcedule-detail-time-start").html(startDate);
                var endDate = oData.entity.data.endDate;
                endDate == "" ? $(".shcedule-detail-time-end").html(nullText) : $(".shcedule-detail-time-end").html(endDate);
                var objectId = oData.entity.data.objectId;
                var objectIdText = oData.entity.expandPro.objectIdText;
                objectId == "" ? $('#objectId').html(nullText) : $('#objectId').html(objectIdText);
                var belongId = oData.entity.data.belongId;
                var belongIdText = oData.entity.expandPro.belongIdText;
                belongId == "" ? $('#belongId').html('关联业务') : $('#belongId').html(belongIdText);
                var isPrivate = oData.entity.data.isPrivate;
                isPrivate == false ? $('#isPrivate').html('公开') : $('#isPrivate').html('私密');
                var description = oData.entity.data.description;
                description == "" ? $('#description').html(nullText) : $('#description').html(description);
                if (oData.entity.data.status == '1') {
                    $(".shcedule-operation-area").show();
                    schduleAccept();
                    scheduleReject();
                }
                var frequency = oData.entity.data.frequency;
                var frequencyText = oData.entity.expandPro.frequencyText;
                var recurStopCondition = oData.entity.data.recurStopCondition;
                var recurStopConditionText = oData.entity.expandPro.recurStopConditionText;
                var recurStopValue = oData.entity.data.recurStopValue;
                if (recurStopValue != 0) {
                    var recurStopValueTime = recurStopValue.substring(0, 10);
                }
                if (frequency == '1' || frequency == '2' || frequency == '3') {
                    $('#recurStopCondition_cell').show();
                    $("#recurStopCondition").html(recurStopConditionText);
                }
                if (recurStopCondition == '2') {
                    $('#recurStopValue_cell').show();
                    $("#recurStopValue").html(recurStopValueTime);
                }
                $('#frequency').html(frequencyText);
                var memberslist = oData.entity.data.acceptMember;
                var members = oData.entity.expandPro.members;
                if (memberslist.length == 0) {
                    $('#members').html(nullText);
                } else {
                    $.each(memberslist, function(i, o) {
                        var icon = members[o].icon;
                        if (icon == "") {
                            icon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFQAAABUCAIAAACTCYeWAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMDY3IDc5LjE1Nzc0NywgMjAxNS8wMy8zMC0yMzo0MDo0MiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QTYwMkRDMDM1MTY5MTFFNjkxMzRDM0QxMDRBNDg0MDQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QTYwMkRDMDQ1MTY5MTFFNjkxMzRDM0QxMDRBNDg0MDQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpBNjAyREMwMTUxNjkxMUU2OTEzNEMzRDEwNEE0ODQwNCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpBNjAyREMwMjUxNjkxMUU2OTEzNEMzRDEwNEE0ODQwNCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pgcxnv0AAAL0SURBVHja7NpJc9owFABg7OKADRhhGwgQE5jOhB7aU///T+j00E7XYRIYIITFYMwSsIG+9txOheUFkffOSOjTYj9JFpzVJvFSQ0y84EA84hGPeMQjHvGIRzziEY94xCMe8YhHPC+RjP4vLXsxsx3bWa43W2+/l5LJjJImuWxRIxlFjrIlQpQHmJOZ3R+OAf+vH5T0wl3DlKTkpeG/tjvD8fS/P7uSpHet12pWuZA1fzweP31v08ghdq774fO3xXJ9IXgYc5jwJxWBztq5Hvf4x9H0aWKdWgrG/8d9l3v8YDTxV3BszRfLFcd4GHYWAOVj4kzx07nNUhyGnlc8PORh6rLUsNo8w+LnEg9NZ69kGWYaEiJ+87xlr2S743PkD4dDAJUcD1ziRTGAykVB5BIvp1IBVJJOcYmHjSp7JaHucELEC4JgFPIsNWh5NZC1E0+SoxGVpTjJZznO8K6LOlNxQ+cY/0oUqyXDX9myoaWuJL53dfVq2V9Bs1LifksL76pq+eTBr5SMXEbhHg/RatZPkihy+q5pRtCwiM7tT8LcXBdFQbgcvJrN1OgmPyyTSlGPplXR3djoBULzs+ZNJdTEJh48UakyFoOujzjDU2fFiQvEU55kzmznAvGUqt5wdIxskkVwVzeZ2YOn8XS+oN/MwavB0Ajf+IfeI8idlZ+Lt4wiGyTfMCvhvfPDwj/0H2Gsl2vWyiHbK2oE3n9CCF0QPL7TH46sWbBHztAFJY00zKpwtvjO7y8P5v4mOdVCgFmgF2AWnBe+Cw80axbNpXpWkYs6adQq8ePnC6fdHYR9nfqXLsjIreYt4/EmEx7kH7/8jDEbfP/2DYufKcm57w3jTYXb3X48Gd7O9WDk48VDA1iucf3jXc9LnEG43j4GvBLmRRJl5HPw4EvHgIeUy8fJZLDRNJleeEzfOraa9ZQkWX++JY3SLIoiyWXrtXJBzZ37ru5sAz85RzziEY94xCMe8YhHPOIRj3jEIx7xiEc8B/FLgAEAUoMR//OC7WYAAAAASUVORK5CYII=";
                        }

                        var memberwrap = $('<div class="member-wrap" id=' + members[o].id + '></div>')
                        var membername = $('<span class="member-name">' + members[o].name + '</span>');
                        var memberimg = $('<img src=' + icon + '>')

                        memberwrap.append(memberimg);
                        memberwrap.append(membername);
                        $('#members').append(memberwrap);
                    });
                }
                var waitmemberslist = oData.entity.data.waitingMember;
                if (waitmemberslist.length == 0) {
                    $("#waitmemberArray").hide();
                } else {
                    $.each(waitmemberslist, function(i, o) {
                        var icon = members[o].icon;
                        if (icon == "") {
                            icon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFQAAABUCAIAAACTCYeWAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMDY3IDc5LjE1Nzc0NywgMjAxNS8wMy8zMC0yMzo0MDo0MiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QTYwMkRDMDM1MTY5MTFFNjkxMzRDM0QxMDRBNDg0MDQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QTYwMkRDMDQ1MTY5MTFFNjkxMzRDM0QxMDRBNDg0MDQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpBNjAyREMwMTUxNjkxMUU2OTEzNEMzRDEwNEE0ODQwNCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpBNjAyREMwMjUxNjkxMUU2OTEzNEMzRDEwNEE0ODQwNCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pgcxnv0AAAL0SURBVHja7NpJc9owFABg7OKADRhhGwgQE5jOhB7aU///T+j00E7XYRIYIITFYMwSsIG+9txOheUFkffOSOjTYj9JFpzVJvFSQ0y84EA84hGPeMQjHvGIRzziEY94xCMe8YhHPC+RjP4vLXsxsx3bWa43W2+/l5LJjJImuWxRIxlFjrIlQpQHmJOZ3R+OAf+vH5T0wl3DlKTkpeG/tjvD8fS/P7uSpHet12pWuZA1fzweP31v08ghdq774fO3xXJ9IXgYc5jwJxWBztq5Hvf4x9H0aWKdWgrG/8d9l3v8YDTxV3BszRfLFcd4GHYWAOVj4kzx07nNUhyGnlc8PORh6rLUsNo8w+LnEg9NZ69kGWYaEiJ+87xlr2S743PkD4dDAJUcD1ziRTGAykVB5BIvp1IBVJJOcYmHjSp7JaHucELEC4JgFPIsNWh5NZC1E0+SoxGVpTjJZznO8K6LOlNxQ+cY/0oUqyXDX9myoaWuJL53dfVq2V9Bs1LifksL76pq+eTBr5SMXEbhHg/RatZPkihy+q5pRtCwiM7tT8LcXBdFQbgcvJrN1OgmPyyTSlGPplXR3djoBULzs+ZNJdTEJh48UakyFoOujzjDU2fFiQvEU55kzmznAvGUqt5wdIxskkVwVzeZ2YOn8XS+oN/MwavB0Ajf+IfeI8idlZ+Lt4wiGyTfMCvhvfPDwj/0H2Gsl2vWyiHbK2oE3n9CCF0QPL7TH46sWbBHztAFJY00zKpwtvjO7y8P5v4mOdVCgFmgF2AWnBe+Cw80axbNpXpWkYs6adQq8ePnC6fdHYR9nfqXLsjIreYt4/EmEx7kH7/8jDEbfP/2DYufKcm57w3jTYXb3X48Gd7O9WDk48VDA1iucf3jXc9LnEG43j4GvBLmRRJl5HPw4EvHgIeUy8fJZLDRNJleeEzfOraa9ZQkWX++JY3SLIoiyWXrtXJBzZ37ru5sAz85RzziEY94xCMe8YhHPOIRj3jEIx7xiEc8B/FLgAEAUoMR//OC7WYAAAAASUVORK5CYII=";
                        }
                        var memberwrap = $('<div class="member-wrap" id=' + members[o].id + '></div>')
                        var membername = $('<span class="member-name">' + members[o].name + '</span>');
                        var memberimg = $('<img src=' + icon + '>')
                        memberwrap.append(memberimg);
                        memberwrap.append(membername);
                        $('#waitmembers').append(memberwrap);
                    });
                }

            } else {
                myAlert('网络不给力，请重新加载')
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    })
}
