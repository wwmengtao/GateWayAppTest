package com.homecare.app;

import android.app.Application;

import android.content.SharedPreferences;

public class HomeCareApp extends Application {

    public static SharedPreferences sp;
    public static final String SP_NAME = "loginInfo";
    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
