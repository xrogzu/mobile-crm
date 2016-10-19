package com.rkhd.ienterprise.apps.ingage.dingtalk.helper;


import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;

public class Env {
	
	public static final String OAPI_HOST = "https://oapi.dingtalk.com";
	public static final String OA_BACKGROUND_URL = ""; 
	public static final String CORP_ID = "dingd880a86ac51bdc25";//"dingbc2edcbb3b994e1f";
	public static final String SECRET = "gNytlfuHlGQ3Ua8cVSFyw7jnr2i3s2fHcqLTite8MtvBdr5lN0e--CyKhf-V8e_Z";//"x2VvFdEhrjAsjPizzoq3wh8b2I6ZtQV5m6qELazPGPu02jTG9gWiQM_a3hmTU-pa";
	
	public static final String SSO_Secret = "B1RpV5ulx_YkyWfjucClFfLTXRX3mGIDtvFnN5TsRFC-y_Z-zSe_GaCgvg_rbWB5";// "NRfe9EgkZfBepmZzwXt7p8f2ew_wJmAQtd7RjRsmF1xK7YGgcwJ4OSf8Eedvf7sk";

	//这些参数,在钉钉服务器每20分钟进行Ticket推送时进行设置
	public static String suiteTicket; 
	//public static String authCode;
	public static String suite_access_token;



	/**
	 * 参数在ConfigInitializer类里初始化话
	 */
	public static final String CREATE_SUITE_KEY ="suite4xxxxxxxxxxxxxxx";// "";
	public static  String SUITE_KEY = "";
	public static  String SUITE_SECRET = "";
	public static  String TOKEN = "";
	public static  String ENCODING_AES_KEY = "";



	
}
