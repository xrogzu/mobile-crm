package com.rkhd.ienterprise.apps.ingage.dingtalk.listeners;

import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 

public class ConfigInitializer implements ServletContextListener {
	
	protected final  Logger LOG = LoggerFactory.getLogger(getClass());

	public void contextDestroyed(ServletContextEvent event) {

		   
	}

	public void contextInitialized(ServletContextEvent event) {
		/**
		 * 初始化配置文件
		 */
		ServletContext context = event.getServletContext();
		String configFile = context.getInitParameter("SystemGlobalsProperties");
		if (StringUtils.isNotBlank(configFile)) {
		    configFile = configFile.trim();
		    SystemGlobals.loadConfig(configFile);
		}

		SetSystemProp.loadConfig("test.properties");

		LOG.info("WxRuntime.suiteTicketKey="+SetSystemProp.getKeyValue(WxRuntime.suiteTicketKey));


	}

}
