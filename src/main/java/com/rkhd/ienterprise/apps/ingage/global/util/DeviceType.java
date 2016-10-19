package com.rkhd.ienterprise.apps.ingage.global.util;

/**
 * User: teddy zhang
 * Date: 14-5-15
 * Time: 上午11:07
 * Copyright: Ingageapp.com, Inc.
 */
public enum DeviceType {

    /**
     * Standard desktop or laptop computer
     */
    COMPUTER("Computer"),
    /**
     * Mobile phone or similar small mobile device
     */
    MOBILE("Mobile"),
    /**
     * Small tablet type computer.
     */
    TABLET("Tablet"),
    /**
     * Game console like the Wii or Playstation.
     */
    GAME_CONSOLE("Game console"),
    /**
     * Digital media receiver like the Apple TV.
     * No device detection implemented yet for this category. Please send provide user-agent strings if you have some.
     */
    DMR("Digital media receiver"),
    /**
     * Other or unknow type of device.
     */
    UNKNOWN("Unknown");

    String name;

    private DeviceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
