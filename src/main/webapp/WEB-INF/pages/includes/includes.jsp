<%@ page import="com.rkhd.ienterprise.base.user.model.User" 
%><%@ page import="com.rkhd.ienterprise.passport.Identity" 
%><%@ page import="net.sf.cglib.core.Local" 
%><%@ page import="java.util.Locale" 
%><%@ page import="org.springframework.beans.propertyeditors.LocaleEditor" 
%><%@ page import="com.rkhd.ienterprise.web.FirewallHelper" 
%><%@ page import="com.rkhd.ienterprise.constant.CommonConstancts" 
%><%@ page import="cloud.multi.tenant.TenantParam" 
%><%@ page import="com.rkhd.ienterprise.tenant.CurrentTenant" 
%><%@ page import="java.util.HashMap" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" 
%><%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" 
%><%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator"
%><%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page"
%><%@ taglib prefix="ingage" uri="/ingage-base-tags" 
%><%@ taglib prefix="feed" uri="/ingage-feed-tags" 
%><%@ taglib prefix="ingagefn" uri="/ingage-util-tags" 
%><%    
response.setHeader("Pragma", "no-cache");    
response.setHeader("Cache-Control", "no-cache");    
response.setDateHeader("Expires", 0);    
User u = Identity.getInstance().getIdentity(request);    
Locale locale = Locale.CHINA;    
if (u != null) {        
locale = u.getLocale();    
}    
request.setAttribute("locale", locale);    
request.setAttribute("contextPath", FirewallHelper.getContextPath(request));
request.setAttribute("isInternalIngage", FirewallHelper.isInternal(request));
%><%@include file="include_res_define.jsp" 
%><fmt:setLocale value="${locale}"/><fmt:setBundle basename="resource" var="resource" />