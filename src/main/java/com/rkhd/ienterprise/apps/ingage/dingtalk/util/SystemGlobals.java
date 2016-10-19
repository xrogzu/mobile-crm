package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description 系统变量加载类
 * @ClassName SystemGlobals
 */
public class SystemGlobals {

    /** The preferences. */
    private static Properties preferences = new Properties();

    /** The queries. */
    private static Properties queries = new Properties();

    private static Map<String, Object> objectProperties = new HashMap<String, Object>();

    /**
     * Gets the preference.
     *
     * @param key
     *            the key
     * @return the preference
     */
    public static String getPreference(String key) {
	String s = preferences.getProperty(key);
	if (s != null)
	    s = s.trim();
	return s;
    }

    /**
     * Gets the preference.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the preference
     */
    public static String getPreference(String key, String defaultValue) {
	String s = preferences.getProperty(key);
	if (s == null)
	    return defaultValue;
	else
	    return s;
    }

    /**
     * Gets the preference.
     *
     * @param key
     *            the key
     * @param params
     *            the params
     * @return the preference
     */
    public static String getPreference(String key, Object... params) {
	String message = preferences.getProperty(key);
	if (message != null)
	    message = message.trim();

	if (params == null || params.length == 0)
	    return message;

	String[] ss = new String[params.length];
	Object o = null;
	for (int i = 0; i < params.length; i++) {
	    o = params[i];
	    if (o == null) {
		ss[i] = "";
	    } else {
		ss[i] = o.toString();
	    }
	}

	return replacePlaceHolder(message, ss);
    }

    /**
     * Sets the preference.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setPreference(String key, String value) {
	if (value != null) {
	    value = value.trim();
	    preferences.setProperty(key, value);
	} else {
	    preferences.remove(key);
	}
    }

    /**
     * Gets the int preference.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the int preference
     */
    public static int getIntPreference(String key, int defaultValue) {
	String s = getPreference(key);
	if (StringUtils.isBlank(s))
	    return defaultValue;
	else
	    return Integer.parseInt(s);
    }

    /**
     * Sets the int preference.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setIntPreference(String key, int value) {
	setPreference(key, String.valueOf(value));
    }

    /**
     * Gets the long preference.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the long preference
     */
    public static long getLongPreference(String key, long defaultValue) {
	String s = getPreference(key);
	if (StringUtils.isBlank(s))
	    return defaultValue;
	else
	    return Long.parseLong(s);
    }

    /**
     * Sets the long preference.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setLongPreference(String key, long value) {
	setPreference(key, String.valueOf(value));
    }

    /**
     * Gets the boolean preference.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the boolean preference
     */
    public static boolean getBooleanPreference(String key, boolean defaultValue) {
	String s = getPreference(key);
	if (StringUtils.isBlank(s))
	    return defaultValue;
	else
	    return Boolean.parseBoolean(s);
    }

    /**
     * Sets the boolean preference.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setBooleanPreference(String key, boolean value) {
	setPreference(key, String.valueOf(value));
    }

    /**
     * Gets the sql.
     *
     * @param key
     *            the key
     * @return the sql
     */
    public static String getSql(String key) {
	return queries.getProperty(key);
    }

    /**
     * Load sql.
     *
     * @param file
     *            the file
     */
    public static void loadSql(String file) {
	try {
	    InputStream is = SystemGlobals.class.getClassLoader()
		    .getResourceAsStream(file);
	    queries.load(is);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Load config.
     *
     * @param file
     *            the file
     */
    public static void loadConfig(String file) {
	try {
	    InputStream is = SystemGlobals.class.getClassLoader()
		    .getResourceAsStream(file);
	    preferences.load(is);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Replace place holder.
     *
     * @param message
     *            the message
     * @param params
     *            the params
     * @return the string
     */
    private static String replacePlaceHolder(String message, String[] params) {
	if (StringUtils.isBlank(message))
	    return message;
	if (params == null || params.length == 0)
	    return message;

	Map<String, String> map = new HashMap<String, String>();
	int index = -1;

	Pattern p = Pattern.compile("\\{(\\d+)\\}");
	Matcher m = p.matcher(message);

	while (m.find()) {
	    if (m.groupCount() < 1)
		continue;
	    index = Integer.parseInt(m.group(1));
	    if (index < 0 || index >= params.length)
		continue;

	    map.put(m.group(0), params[index]);
	}

	if (map.isEmpty())
	    return message;

	for (Entry<String, String> entry : map.entrySet()) {
	    message = message.replace(entry.getKey(), entry.getValue());
	}

	return message;
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
	String s = "thia is a {4} or a {1} {0} hahah";
	String[] params = { "AA", "BB", "CC" };

	Map<String, String> map = new HashMap<String, String>();
	int index = -1;

	Pattern p = Pattern.compile("\\{(\\d+)\\}");
	Matcher m = p.matcher(s);

	while (m.find()) {
	    if (m.groupCount() < 1)
		continue;
	    index = Integer.parseInt(m.group(1));
	    if (index < 0 || index >= params.length)
		continue;

	    map.put(m.group(0), params[index]);
	}

	for (Entry<String, String> entry : map.entrySet()) {
	    s = s.replace(entry.getKey(), entry.getValue());
	}

	System.out.println(s);
    }

    public static void setObjectValue(String field, Object value) {
	objectProperties.put(field, value);
    }

    public static Object getObjectValue(String field) {
	return objectProperties.get(field);
    }

    /** The Constant DB_JNDI. */
    public static final String DB_JNDI = "db_jndi";

    /** The Constant DB_USERNAME. */
    public static final String DB_USERNAME = "db_userName";

    /** The Constant DB_PASSWD. */
    public static final String DB_PASSWD = "db_password";

    /** The Constant DB_URL. */
    public static final String DB_URL = "db_url";

    /** The Constant SPRING_CONFIG. */
    public static final String SPRING_CONFIG = "spring_config";

}