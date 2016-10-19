$(document).on('ready', function() {
    if (GetQueryString('type') == 'add') {
        getAddContactDesc("ADD");
    } else if (GetQueryString('type') == 'set') {
        document.title = '修改联系人';
        getAddContactDesc("UPDATE");
    }

    if (!xsyUser) {
        alert('获取当前用户失败')
    }
})

function afterinitfn() {
    get_post();
    input_color_adjust();

    btn_touch_style();

    //联系人姓名全为空格则清空
    contactNameReg();
}

var dataString;
var selectitmeObj = {};
var userName = xsyUser.name;
var userDepartId = xsyUser.departId;
var userDepartName = xsyUser.departmentName;
// 渲染联系人列表

function getAddContactDesc(scene) {
    $.ajax({
        url: apppath + "/wx/contact/getDetail.action",
        dataType: "json",
        data: { scene: scene },
        type: "GET",
        beforeSend: function() {
            //请求前的处理
        },
        success: function(data) {
            if (data.success == false) {
                myAlert('加载数据失败，请稍后再试');
                $('.load-shadow').hide();
            }
            if (data.success == true) {
                $('.load-shadow').hide();
                dataString = JSON.stringify(data);
                console.log(dataString);
                var structureList = data.entity.structure.components;
                $.each(structureList, function(i, o) {
                    customizItem($('#basic_info'), o, selectitmeObj);
                });
                var ownerid = $('#ownerId').find('button');
                ownerid.html(userName);
                ownerid.attr("value", ownerId);
                ownerid.attr("readonly", true);
                ownerid.css("color", '#ccc');
                var dimDepart = $('#dimDepart').find('button');
                dimDepart.html(userDepartName);
                dimDepart.attr("value", userDepartId);
                var footer = $('<div class="footer"></div>');
                footer.append('<button id="confirm" class="confirm">保存</button>');
                footer.append('<button id="add_contact" class="confirm">保存并添加新联系人</button>');
                $('body').append(footer);
                confirm();
                selectDate();
                dimdepart_choose();
                textarea_num();
                SelectItemPost();
                DummyItemPost();
                $('textarea').autoHeight();
                initData();

                afterinitfn();

            }
        },
        complete: function() {
            //请求完成的处理
        },
        error: function(error) {
            //请求出错处理
            alert(error);
        }
    });
}

function initData() {
    if (GetQueryString('from') == 'acc_add') {
        var name = GetQueryString('accountName');
        var accName;
        if (name.indexOf('%23') >= 0) {
            var accName = decodeURIComponent(name);
        } else {
            accName = name;
        }
        var accountId = $('#accountId').find('button');
        accountId.html(accName);
        accountId.attr('value', GetQueryString('accountId'));
        accountId.attr('disabled', 'disabled');
        accountId.css('color', '#ccc');
        accountId.css('background', 'none');
        $('#add_contact').html('保存并添加更多');

        btn_color_adjust();
        set_back(apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('accountId'));
    } else if (GetQueryString('from') == 'acc_info') {
        var name = GetQueryString('accountName');
        var accName;
        if (name.indexOf('%23') >= 0) {
            var accName = decodeURIComponent(name);
        } else {
            accName = name;
        }
        var accountId = $('#accountId').find('button');
        accountId.html(accName);
        accountId.attr('value', GetQueryString('accountId'));
        accountId.attr('disabled', 'disabled');
        accountId.css('color', '#ccc');
        accountId.css('background', 'none');
        $('#add_contact').html('确定并添加联系人');
        set_back(apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('accountId'));
    } else {
        //从联系人列表添加联系人或从联系人详情编辑联系人（可修改公司）
        accountPost();
    }
    if (GetQueryString('from') == 'con_list') {
        // $('#add_contact').remove();
    }
    if (GetQueryString('from') == 'con_info') {
        set_back(apppath + 'wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('contactId'));
    }
    if (GetQueryString('type') == 'set') {
        set_info();
        $('#add_contact').remove();
        $('#confirm').html('保存');
    } else {
        //设置默认部门
        setPost = true;
        // btn_color_adjust();
    }
    if (GetQueryString('type') == 'from_acc_info') {
        $('#add_contact').remove();
        $('#confirm').html('保存');
    }
}

function contactNameReg() {
    $('#contactName input').on('blur', function() {
        var str = $('#contactName input').val();
        while (str.lastIndexOf(" ") >= 0) {
            str = str.replace(" ", "");
        }
        $('#contactName input').val(str);
    })
}
var reg_contactName = /^.{1,50}$/;
var reg_phone = /^\d+|[-#*]{1,30}$/;
var reg_department = /^.{0,20}$/;
var reg_post = /^.{0,50}$/;
var reg_tel = /^\d+|[-#*]{0,30}$/;
var reg_email = /^(([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})){0,100}$/;
var reg_address = /^.{0,250}$/;

function confirm() {
    $('#confirm').on('click', function() {
        confirm_action();
    })
    $('#add_contact').on('click', function() {
        confirm_action('add_contact');
    })

    function confirm_action(action) {
        regingData() && info_submit(action);
    }

}
//===================部门相关================
var postData;
var setPost = false;

function get_post() {
    $.ajax({
        type: "get",
        url: apppath + "/wx/department/mydepartment.action",
        async: true,
        dataType: 'json',
        success: function(oData) {
            console.log('部门' + oData)
            if (oData.success == false) {
                myAlert('网络不给力，请重新加载');
            } else {
                postData = oData;
                if (setPost) {
                    $('#dimDepart button').attr('value', postData.entity.departs[0].id);
                    $('#dimDepart button').html(postData.entity.departs[0].text);

                }
            }
        },
        error: function() {
            myAlert('网络不给力，请重新加载')
        }
    });
}

function info_submit(action) {
    var url = '';
    if (GetQueryString('type') == 'set') {
        url = apppath + "/wx/contact/doupdate.action";
        console.log('更新接口');
        var contactTemp = {
            id: GetQueryString('contactId')
        };
        $('#basic_info .item').each(function() {
            var that = $(this);
            var value;
            if (that.find('input').size()) {
                var btype = that.attr('btype');
                if (btype == "ITEM_TYPE_INTEGER" && that.find('input').val() == "" || btype == "ITEM_TYPE_REAL" && that.find('input').val() == "") {
                    value == 0;
                    return;
                }
                value = that.find('input').val();
            }
            if (that.find('button').size()) {
                var btn = that.find('button');
                if (btn.html() == "" || btn.html() == "点击选择") {
                    value = null;
                } else if (that.attr('btype') == "ITEM_TYPE_CHECKBOX") {
                    var arrayList = [];
                    arrayList = that.find('button').attr('value').split(",");
                    if (arrayList.length = 0) {
                        value = []
                    } else {
                        value = arrayList;
                    }
                    console.log(arrayList);
                } else {
                    value = that.find('button').attr('value');
                }
            }
            if (that.find('textarea').size()) {
                value = that.find('textarea').val();
            }
            contactTemp[that.attr('id')] = value;
        });
    } else {
        url = apppath + "/wx/contact/docreate.action";
        console.log('创建接口')
        var contactTemp = {};
        $('#basic_info .item').each(function() {
            var value;
            var that = $(this);
            if (that.find('input').size()) {
                var btype = that.attr('btype');
                if (btype == "ITEM_TYPE_INTEGER" && that.find('input').val() == "" || btype == "ITEM_TYPE_REAL" && that.find('input').val() == "") {
                    value == 0;
                    return;
                }
                value = that.find('input').val();
            } else if (that.find('button').size()) {
                var btn = that.find('button');
                if (btn.html() == "" || btn.html() == "点击选择") {
                    value = null;
                }
                if (that.attr('btype') == "ITEM_TYPE_CHECKBOX") {
                    var arrayList = [];
                    arrayList = that.find('button').attr('value').split(",");
                    if (arrayList.length = 0) {
                        value = []
                    } else {
                        value = arrayList;
                    }
                    console.log(arrayList);
                } else {
                    value = that.find('button').attr('value');
                }
            }
            if (that.find('textarea').size()) {
                value = that.find('textarea').val();
            }
            contactTemp[that.attr('id')] = value;
        });
    }
    console.log(contactTemp)
    $.ajax({
        type: "post",
        url: url,
        data: {
            contact: JSON.stringify(contactTemp)
        },
        async: true,
        beforeSend: function() {
            $('.confirm').attr('disabled', true);
            loader();
        },
        dataType: 'json',
        success: function(oData) {
            $('.loaderArea').remove();
            if (oData.success == true) {
                myDialog('保存成功');
                setTimeout(function() {
                    if (action == 'add_contact') {
                        location.replace(apppath + '/wx/contact/add.action?' + ddcommparams + '&from=acc_add&type=add&accountId='+$('#accountId button').attr('value')+'&accountName='+$('#accountId button').html());
                    } else {
                        if (GetQueryString('from') == 'acc_add') {
                            location.replace(apppath + '/wx/account/info.action?' + ddcommparams + '&accountId=' + GetQueryString('accountId'));
                        } else if (GetQueryString('from') == 'con_list') {
                            location.replace(apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + oData.entity.id);
                        } else if (GetQueryString('from') == 'con_info') {
                            location.replace(apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + GetQueryString('contactId'));
                        } else if (GetQueryString('from') == 'acc_info') {
                            location.replace(apppath + '/wx/contact/info.action?' + ddcommparams + '&contactId=' + oData.entity.id + '&from=acc_info&accountId=' + GetQueryString('accountId'));
                        }
                    }
                }, 2000)
            } else if (oData.success == false) {
                $('.confirm').removeAttr('disabled');
                if (oData.entity && oData.entity.key && oData.entity.key == 'mobile') {
                    myAlert('保存失败，手机号码重复');
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000002') {
                    myAlert('保存失败，不能包含表情符');
                } else if (oData.entity && oData.entity.status && oData.entity.status == '0000003') {
                    location.replace(apppath + '/wx/authorized/no.action?' + ddcommparams);
                } else {
                    myAlert('保存失败，请检查输入信息');
                }
            }
        },
        error: function() {
            $('.loaderArea').remove();
            $('.confirm').removeAttr('disabled');
            myAlert('网络不给力，请重新加载')
        }
    })
}
var ownerId = xsyUser.id;

function set_info() {
    $.ajax({
        type: "get",
        url: apppath + "/wx/contact/getDetail.action",
        data: {
            contactId: GetQueryString('contactId'),
            scene: 'UPDATE'
        },
        async: true,
        dataType: 'json',
        success: function(oData) {
            console.log(oData)
                // ownerId = oData.entity.ownerId;
            $('#basic_info .item').each(function() {
                var that = $(this);
                var value = set_infoSup(that.attr('id'), oData);
                var show, rValue;
                if (typeof value === "object") {
                    show = value.text;
                    rValue = value.value;
                } else {
                    show = rValue = value;
                }
                if (that.find('input').size()) {
                    that.find('input').val(rValue);
                }
                if (that.find('button').size()) {
                    that.find('button').attr('value', rValue);
                    if (show === "") {
                        that.find('button').html("点击选择");
                    } else {
                        that.find('button').html(show);
                    }
                }
                if (that.find('textarea').size()) {
                    that.find('textarea').val(rValue);
                }
            });
            btn_color_adjust();

            $('textarea').autoHeight();
            textarea_num();
        }
    })
}


