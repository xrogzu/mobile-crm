<%@ page language="java" pageEncoding="UTF-8" %><%
    String resDomain = FirewallHelper.getContextPath(request);//这个是专门给js，css，img用的，不要乱用！
    String resNewDomain = resDomain;//新版ui对应的！

    if(request.getServerName().equalsIgnoreCase("crm.xiaoshouyi.com") || request.getServerName().equalsIgnoreCase("beta.xiaoshouyi.com")){
        //若是开发环境，需要把resNewDomain设置为线上值
        resNewDomain = "https://rs.ingageapp.com";
    } else  if(request.getServerName().equalsIgnoreCase("dev.xiaoshouyi.com")){
        //若是开发环境，需要把resNewDomain设置为线上值
        resNewDomain = "https://devrs.ingageapp.com";
    } else  if(request.getServerName().equalsIgnoreCase("test.xiaoshouyi.com")){
        //若是开发环境，需要把resNewDomain设置为线上值
        resNewDomain = "https://testrs.ingageapp.com";
    }else  if(request.getServerName().equalsIgnoreCase("ty.xiaoshouyi.com")){
        //若是开发环境，需要把resNewDomain设置为线上值
        resNewDomain = "https://tyrs.ingageapp.com";
    }else if(request.getServerName().equalsIgnoreCase("branch.xiaoshouyi.com")){
        //若是开发环境，需要把resNewDomain设置为线上值
        resNewDomain = "https://branchrs.ingageapp.com";
    }

    //js
    String resJsVer = "";
    String resJsPath = resDomain + "/js" + resJsVer;
    //css
    String resCssVer = "";
    String resCssPath = resDomain + "/css" + resCssVer;
    //img
    String resImagePath = resDomain + "/img";
    //swf
    String resSwfPath = "/swf";

    request.setAttribute("resJsPath",resJsPath);
    request.setAttribute("resCssPath",resCssPath);
    request.setAttribute("resImagePath",resImagePath);
    request.setAttribute("resSwfPath",resSwfPath);
    //fixme 这里强制定制了资源集团的tenantid，为了隐藏资源相关菜单
    request.setAttribute("sickSpecialTenantId",129444 );
    //fixme 这里强制定制了北京云端至尚的tenantid，为了隐藏资源相关菜单
    request.setAttribute("sickSpecialTenantId1",130871 );
    //fixme 这里强制定制了湖南三民重科的tenantid，为了隐藏资源相关菜单
    request.setAttribute("sickSpecialTenantId2",130149 );
    // 盈富信展（北京）资产管理有限公司
    request.setAttribute("sickSpecialTenantId3",149303 );

%>