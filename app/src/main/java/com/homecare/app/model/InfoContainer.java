package com.homecare.app.model;

public class InfoContainer {
    public static String elderlyId;
    public static SGInfo sgInfo;

    public static boolean infoInvalid() {
        return elderlyId == null || sgInfo == null;
    }
}
