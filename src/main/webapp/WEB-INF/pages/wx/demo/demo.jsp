<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/5/4
  Time: 11:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.min.js"  ></script>
    <script type="text/javascript" src="https://cdn.bootcss.com/json3/3.3.2/json3.min.js"  ></script>

    <title>自定义demo</title>
</head>
<body>
<input type="button" onclick="getAddAcctountDesc()" value="获取公司描述-->增加操作"/><br/>
<input type="button" onclick="getupdateAcctountDesc()" value="获取公司描述-->修改操作"/><br/>
<input type="button" onclick="getAddContactDesc()" value="获取联系人描述-->增加操作"/><br/>
<input type="button" onclick="getupdateContactDesc()" value="获取联系人描述-->修改操作"/><br/>

<input type="button" onclick="getAddOpportunityDesc()" value="获取商机描述-->增加操作"/><br/>

<input type="button" onclick="getAddOpportunityDesc(194809)" value="获取商机描述-->修改操作"/><br/>
租户ID：<input type="text" name="tenantId" id="tenantId" value="71189">
<input type="button" onclick="doSync()" value="同步企业"/><br/>

<form id="demoForm">


</form>
<hr>
<h1>模仿微信端登录</h1>
<form id="mofangForm" action="${pageContext.request.contextPath}/wx/pcadmin/test.action">
    corpid:<input type="text" name="corpid" value="wx7409b34a733c7e82"><br/>
    userid:<input type="text" name="userid" value="xubaoyong"><br/>
    <input type="submit" value="提交"><br/>
</form>
</body>
<script type="text/javascript">
    <!--
function getAddAcctountDesc(){
    var url = "${pageContext.request.contextPath}/wx/account/getDetail.action";
    $.ajax({
        url: url,    //请求的url地址
        dataType: "json",   //返回格式为json
       // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
        data: { d:new Date().getTime(),scene:'ADD' },    //参数值
        type: "GET",   //请求方式
        beforeSend: function() {
            //请求前的处理
            $('#demoForm').html("");
        },
        success: function(data) {
            //请求成功时处理v
           // alert(data);
          //  var data = eval("("+data+")");

            var dataString  =JSON.stringify(data);
            console.log(dataString);

            var structureList2 = data.entity.structure.components;
            $('#demoForm').empty();
            for(var i=0;i<structureList2.length;i++){
                var obj = structureList2[i];
                var propertyname  = obj['propertyName'];
                var createable = obj['createable'];
                var label = obj['label'];
                if(!createable){

                    continue;
                }

                var type  = obj['type'];

                if(type == 'ITEM_TYPE_LINE'){
                    $('#demoForm').append("<b>"+label +'</b><br/>')
                    continue;
                }
                var value = "";
                if("ITEM_TYPE_SELECT" == type){
                    var   selectitem = obj['selectitem'];
                    value = selectitem[0]['value'];
                }
                if("ownerId" == propertyname){
                    value = "78539";
                }
                if("dimDepart" == propertyname){
                    var xobj = data.entity.expandPro.dimDepartJSONObject.departs;
                    value = xobj[0].id;
                }


                $('#demoForm').append(label +':'+'<input save="true" class="easyui-textbox" name="'+propertyname+'" value='+value+'></input>'+type+'---'+propertyname+'<br/>')

               //
            }



            //$("body").eq(0).append( "<input type='button' id='saveBtn' value='保存' onclick='inserActionTestFn()'></input>" );
            $('#demoForm').append( "<input type='button'  value='保存' onclick='inserActionTestFn()'></input>" );


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
    function  inserActionTestFn(){
        var saveobj = {};
        $("#demoForm > [save='true']").each(function(index, el){
                var name = $(this).attr("name");
                var v = $(this).val();
            saveobj[name]= v;
        }) ;

        var url = "${pageContext.request.contextPath}/wx/account/docreate.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { account:JSON.stringify(saveobj) },    //参数值
            type: "GET",   //请求方式
            beforeSend: function() {
                //请求前的处理
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString)



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
function getupdateAcctountDesc(){
    var  accountId = 332728;
    var url = "${pageContext.request.contextPath}/wx/account/getDetail.action";
    $.ajax({
        url: url,    //请求的url地址
        dataType: "json",   //返回格式为json
        // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
        data: { d:new Date().getTime(),accountId:accountId,scene:'UPDATE' },    //参数值
        type: "GET",   //请求方式
        beforeSend: function() {
            //请求前的处理
            $('#demoForm').html("");
        },
        success: function(data) {
            //请求成功时处理v
            // alert(data);
            //  var data = eval("("+data+")");
            var dataString  =JSON.stringify(data);
            alert(dataString);

            var dataString  =JSON.stringify(data);

            var structureList2 = data.entity.structure.components;
            var dbData = data.entity.data;

            for(var i=0;i<structureList2.length;i++){
                var obj = structureList2[i];
                var propertyname  = obj['propertyName'];
                var label = obj['label'];
                var type  = obj['type'];
                if("ITEM_TYPE_LINE" == type){
                    $('#demoForm').append("<b>"+label +'</b><br/>')
                    continue;
                }
                var updateable = obj['updateable'];
                if(!updateable){
                    continue;
                }
                var value = "";

                if("ownerId" == propertyname){
                    value = "78539";
                }
                if("dimDepart" == propertyname){
                    var xobj = data.entity.expandPro.dimDepartJSONObject.departs;
                    value = xobj[0].id;
                }
                value = dbData[propertyname];
                if("select" == type && value == ""){
                    var   selectitem = obj['selectitem'];
                    value = selectitem[0]['value'];
                }
               // if("客户名称" == label){
                    $('#demoForm').append(label +':')
                    $('#demoForm').append( '<input save="true" atype="'+type+'" updateable="'+updateable+'"  name="'+propertyname+'" value="'+value+'"/>'+propertyname+"--");
                    $('#demoForm').append(type+'<br/>')
               // }




                //
            }
            $('#demoForm').append('id:'+'<input save="true" name="id" value='+dbData['id']+'></input><br/>')
            $("#demoForm").append( "<input type='button' value='保存' onclick='updateSaveTestFn()'></input>" );


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
    function updateSaveTestFn(){
        var saveobj = {};
        $("#demoForm > [save='true']").each(function(index, el){
            var name = $(this).attr("name");
            var updateable = $(this).attr("updateable");
            var atype = $(this).attr("atype");
            var v = $(this).val();
            //过了掉不能修改的字段。id除外
            if( updateable != 'true' && name != 'id' ) {
                return true;
            }
            //数字类型空置是默认为0
            if(( atype =='ITEM_TYPE_REAL' || atype == 'ITEM_TYPE_INTEGER') && v.length ==0 ){
                v= 0;
            }
            saveobj[name]= v;
        }) ;
        saveobj['custCheckbox1'] = [1,2];

        var url = "${pageContext.request.contextPath}/wx/account/doupdate.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { account:JSON.stringify(saveobj) },    //参数值
            type: "post",   //请求方式
            beforeSend: function() {
                //请求前的处理
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString)



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

    function getAddContactDesc(){
        var url = "${pageContext.request.contextPath}/wx/contact/getDetail.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { d:new Date().getTime(),scene:'ADD' },    //参数值
            type: "GET",   //请求方式
            beforeSend: function() {
                //请求前的处理
                $('#demoForm').html("");
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString);

                var structureList2 = data.entity.structure.components;

                for(var i=0;i<structureList2.length;i++){
                    var obj = structureList2[i];
                    var propertyname  = obj['propertyName'];
                    var createable = obj['createable'];
                    var updateable = obj['updateable'];
                    var label = obj['label'];
                    var type  = obj['type'];
                    if("ITEM_TYPE_LINE" == type){
                        $('#demoForm').append("<b>"+label +'</b><br/>')
                        continue;
                    }
                    var value = "";
                    if("ITEM_TYPE_SELECT" == type){
                        var   selectitem = obj['selectitem'];
                        value = selectitem[0]['value'];
                    }
                    if("ownerId" == propertyname){
                        value = "78539";
                    }
                    if("accountId"== propertyname){
                        value = "324357";
                    }

                    if("dimDepart" == propertyname){
                        var xobj = data.entity.expandPro.dimDepartJSONObject.departs;
                        value = xobj[0].id;
                    }
                    if("ITEM_TYPE_LINE" == type){
                        $('#demoForm').append("<b>"+label +'</b><br/>')
                    }else{
                        $('#demoForm').append(label +':')
                        $('#demoForm').append( '<input save="true" atype="'+type+'" createable="'+createable+'"  name="'+propertyname+'" value="'+value+'"/>'+propertyname +"------");
                        $('#demoForm').append(type+'<br/>')
                    }
                    //
                }

                $('#demoForm').append( "<input type='button' value='保存' onclick='inserContactTestFn()'></input>" );


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

    function  inserContactTestFn(){
        var saveobj = {};
        $("#demoForm > [save='true']").each(function(index, el){
            var name = $(this).attr("name");
            var v = $(this).val();
            saveobj[name]= v;
        }) ;

        var url = "${pageContext.request.contextPath}/wx/contact/docreate.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { contact:JSON.stringify(saveobj) },    //参数值
            type: "GET",   //请求方式
            beforeSend: function() {
                //请求前的处理
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString)
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
    function getupdateContactDesc(){
        var  contactId = 162203;
        var url = "${pageContext.request.contextPath}/wx/contact/getDetail.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { d:new Date().getTime(),contactId:contactId ,scene:'UPDATE'},    //参数值
            type: "GET",   //请求方式
            beforeSend: function() {
                //请求前的处理
                $('#demoForm').html("");
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");
                var dataString  =JSON.stringify(data);
               alert(dataString);


                var dataString  =JSON.stringify(data);

                var structureList2 =    data.entity.structure.components;
                var dbData = data.entity.data;

                for(var i=0;i<structureList2.length;i++){
                    var obj = structureList2[i];
                    var updateable =obj['updateable'];
                    if(!updateable){
                        continue;
                    }
                    var createable = obj['createable'];
                    var propertyname  = obj['propertyName'];

                    var label = obj['label'];
                    var type  = obj['type'];

                    var  value = dbData[propertyname];

                    if("ITEM_TYPE_LINE" == type){
                        $('#demoForm').append("<b>"+label +'</b><br/>')
                        continue;
                    }
                    if("ITEM_TYPE_SELECT" == type && value == ""){
                        var   selectitem = obj['selectitem'];
                        value = selectitem[0]['value'];
                    }

                    $('#demoForm').append(label +':');
                    $('#demoForm').append( '<input save="true" atype="'+type+'" updateable="'+updateable+'"  name="'+propertyname+'" value="'+value+'"/>');
                    $('#demoForm').append(propertyname+"------"+type+'<br/>');

                    //$('#demoForm').append(label +':'+'<input save="true" atype="'+type+'" updateable="'+updateable+'" class="easyui-textbox" name="'+propertyname+'" value='+value+'></input>'+type+'<br/>')

                }
                $('#demoForm').append('id:'+'<input save="true" name="id" value='+dbData['id']+'></input><br/>')
                $('#demoForm').append( "<input type='button' value='保存' onclick='updateContactSaveTestFn()'></input>" );


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
    function updateContactSaveTestFn(){
        var saveobj = {};
        $("#demoForm > [save='true']").each(function(index, el){
            var name = $(this).attr("name");
            var updateable = $(this).attr("updateable");
            var atype = $(this).attr("atype");
            var v = $(this).val();
            //过了掉不能修改的字段。id除外
            if( updateable != 'true' && name != 'id' ) {
                return true;
            }
            //数字类型空置是默认为0
            if((atype == 'int' ||atype == 'float') && v.length ==0 ){
                v= 0;
            }
            saveobj[name]= v;
        }) ;

        var url = "${pageContext.request.contextPath}/wx/contact/doupdate.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { contact:JSON.stringify(saveobj) },    //参数值
            type: "post",   //请求方式
            beforeSend: function() {
                //请求前的处理
                //$('#demoForm').html("");
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString)



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


    function getAddOpportunityDesc(opportunityId){
        var param = { d:new Date().getTime(),scene:'ADD' };
        var mtype = 'add';
        if(opportunityId != null){
            param.opportunityId = opportunityId;
            param.scene='UPDATE'
            mtype = 'update';
        }
        var url = "${pageContext.request.contextPath}/wx/opportunity/getDetail.action";
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: param,    //参数值
            type: "GET",   //请求方式
            beforeSend: function() {
                //请求前的处理
                $('#demoForm').html("");
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString);

                var structureList2 = data.entity.structure.components;
                var dbData = data.entity.data;

                for(var i=0;i<structureList2.length;i++){
                    var obj = structureList2[i];
                    var propertyname  = obj['propertyName'];
                    var updateable =obj['updateable'];
                    var createable = obj['createable'];
                    var label = obj['label'];
                    var type  = obj['type'];
                    var value = "";
                    if("ITEM_TYPE_LINE" == type){
                        $('#demoForm').append("<b>"+label +'</b><br/>')
                        continue;
                    }
                    if(mtype == 'add'){
                        if(!createable){
                            continue;
                        }
                        if("ITEM_TYPE_SELECT" == type){
                            var   selectitem = obj['selectitem'];
                            value = selectitem[0]['value'];
                        }else if("ITEM_TYPE_DUMMY" == type && ("saleStageId" == propertyname || "sourceId" == propertyname )){
                            var   selectitem = obj['dummyItemBean'];
                            value = selectitem[0]['value'];
                        }
                        if("ownerId" == propertyname){
                            value = "78539";
                        }
                        if("dimDepart" == propertyname){
                            var xobj = data.entity.expandPro.dimDepartJSONObject.departs;
                            value = xobj[0].id;
                        }
                        if(propertyname == 'closeDate'){
                            var now = new Date();
                            value = now.getFullYear() +"-"+(now.getMonth()<10?"0"+now.getMonth():now.getMonth())+"-"+(now.getDate()>10?now.getDate():"0"+now.getDate());
                        } else if(propertyname == 'accountId'){
                            value = 292551;
                        }
                    }else {
//                        alert(propertyname);
//                        alert(dbData[propertyname]);
                        value = dbData[propertyname];
                        if('sourceId' == propertyname){
                            var   selectitem = obj['dummyItemBean'];
                            value = selectitem[0]['value'];
                        }
                        if( 'dimDepart' == propertyname
                                //|| "product" == propertyname
                        ){
                            //continue;
                        }
                        if(!updateable && propertyname != "id"){
                            continue;
                        }
                    }
                    $('#demoForm').append(label +':'+'<input save="true"   atype="'+type+'" updateable="'+updateable+'"  name="'+propertyname+'" value='+value+'></input>'+propertyname+"----"+type+'<br/>')

                    //
                }

                if(mtype = 'update') {
                    $('#demoForm').append('id:' + '<input save="true" name="id" value=' + dbData['id'] + '></input><br/>')
                }
                $('#demoForm').append( "<input type='button' value='保存' onclick='saveOpportunity(\""+mtype+"\")'></input>" );


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

    function saveOpportunity(mtype){

        var saveobj = {};
        $("#demoForm > [save='true']").each(function(index, el){
            var name = $(this).attr("name");
            var v = $(this).val();
            var updateable = $(this).attr("updateable");
            var atype = $(this).attr("atype");
            var v = $(this).val();
            if(mtype == 'update') {
                //过了掉不能修改的字段。id除外
                if (updateable != 'true' && name != 'id') {
                    return true;
                }


            }
            //数字类型空置是默认为0
            if ((atype == 'ITEM_TYPE_REAL' ) && v.length == 0) {
                v = 0;
            }
            if(name == 'source'){
                return true;
            }
            saveobj[name]= v;
        }) ;



        var url = "${pageContext.request.contextPath}/wx/opportunity/docreate.action";
        if(mtype == 'update'){
            url = "${pageContext.request.contextPath}/wx/opportunity/doupdate.action";
        }
        $.ajax({
            url: url,    //请求的url地址
            dataType: "json",   //返回格式为json
            // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
            data: { opportunity:JSON.stringify(saveobj) },    //参数值
            type: "GET",   //请求方式
            beforeSend: function() {
                //请求前的处理
            },
            success: function(data) {
                //请求成功时处理v
                // alert(data);
                //  var data = eval("("+data+")");

                var dataString  =JSON.stringify(data);
                alert(dataString)



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

function doSync(){
    var tenantId = $("#tenantId").val();
    var url = "${pageContext.request.contextPath}/wx/expose/doSync.action";

    $.ajax({
        url: url,    //请求的url地址
        dataType: "json",   //返回格式为json
        // async: true, //请求是否异步，默认为异步，这也是ajax重要特性
        data: { tenantId:tenantId },    //参数值
        type: "GET",   //请求方式
        beforeSend: function() {
            //请求前的处理
        },
        success: function(data) {
            //请求成功时处理v
            // alert(data);
            //  var data = eval("("+data+")");

            var dataString  =JSON.stringify(data);
            alert(dataString)



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
   // 188561

    //-->
</script>

</html>
