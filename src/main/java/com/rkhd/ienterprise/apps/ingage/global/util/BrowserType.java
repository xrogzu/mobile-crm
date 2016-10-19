package com.rkhd.ienterprise.apps.ingage.global.util;

/**
 * User: teddy zhang
 * Date: 14-5-15
 * Time: 上午11:06
 * Copyright: Ingageapp.com, Inc.
 */
public enum BrowserType {

    /**
     * Standard web-browser
     */
    WEB_BROWSER("Browser"),
    /**
     * Special web-browser for mobile devices
     */
    MOBILE_BROWSER("Browser (mobile)"),
    /**
     * Text only browser like the good old Lynx
     */
    TEXT_BROWSER("Browser (text only)"),
    /**
     * Email client like Thunderbird
     */
    EMAIL_CLIENT("Email Client"),
    /**
     * Search robot, spider, crawler,...
     */
    ROBOT("Robot"),
    /**
     * Downloading tools
     */
    TOOL("Downloading tool"),
    UNKNOWN("unknown");

    private String name;

    private BrowserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}