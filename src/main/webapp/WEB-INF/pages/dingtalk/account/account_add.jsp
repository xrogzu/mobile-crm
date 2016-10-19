<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2015/12/30
  Time: 10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name = "format-detection" content = "telephone=no">
  <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
  <title>新建公司</title>
  <jsp:include page="../../wx/ddcommon/AppJSApiConfig.jsp?v=1" flush="true" />
  <script>
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/common'+compresspath+'.css"/>');
    document.write('<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wx/add'+compresspath+'.css?v=1"/>');
  </script>

</head>
<body>
  <div id="basic_info" class="boxSizing">
    <div class="title boxSizing"></div>
    <div id="accountName" class="item boxSizing">
      <div class="bor">
        <span class="tag">公司名称<span class="red">*</span></span>
        <input type="text" placeholder="必填" maxlength="50"/>
      </div>
    </div>
    <div id="level" class="item boxSizing">
      <div class="bor">
        <span class="tag">公司等级</span>
        <button>普通</button>
      </div>
    </div>
    <div id="status" class="item boxSizing">
      <div class="bor">
        <span class="tag">公司状态</span>
        <button>潜在</button>
      </div>
    </div>
    <div id="industryId" class="item boxSizing">
      <span class="tag">行业</span>
      <button>点击选择</button>
    </div>
  </div>
  <div id="contact_info" class="block boxSizing">
    <div id="phone" class="item boxSizing">
      <div class="bor">
        <span class="tag">电话</span>
        <input type="tel" placeholder="点击输入" maxlength="30"/>
      </div>
    </div>
    <div id="address" class="item boxSizing">
      <div class="bor">
        <span class="tag">地址</span>
        <input type="text" placeholder="点击输入" maxlength="250"/>
      </div>
    </div>
    <div id="fax" class="item boxSizing">
      <span class="tag">传真</span>
      <input type="tel" placeholder="点击输入" maxlength="30"/>
    </div>
  </div>
  <div id="member_info" class="block boxSizing">
    <div id="ownerPost" class="item boxSizing">
      <span class="tag">所属部门<span class="red">*</span></span>
      <button></button>
    </div>
  </div>
  <div id="remark" class="boxSizing">
    <textarea name="" id="textarea" placeholder="请填写备注内容…" maxlength="1000" ></textarea>
    <span id="num">0/1000</span>
  </div>
  <button id="confirm" class="confirm">保存</button>
  <button id="add_contact" class="confirm">保存并添加联系人</button>
  <script>
    var apppath = "<%=request.getContextPath()%>";
  </script>
  <script src="https://g.alicdn.com/ilw/cdnjs/jquery/1.8.3/jquery.min.js" type="application/x-javascript"></script>
  <script>
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/common'+compresspath+'.js?v=1"></scr'+'ipt>');
    document.write('<scr'+'ipt src="<%=request.getContextPath()%>/js/wx/account/account_add'+compresspath+'.js?v=1"></scr'+'ipt>');
  </script>

</body>
</html>
