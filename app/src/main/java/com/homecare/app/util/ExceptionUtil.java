package com.homecare.app.util;

import android.util.Log;

import com.homecare.app.service.SdService;

public class ExceptionUtil {

    public static void trackUncaughtException(final String tag) {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    StringBuilder errorLog = new StringBuilder();
                    String topError = "Uncaught Exception detected in thread: " + t.getName() + ", " + e.getMessage();
                    Log.e(tag, topError);
                    errorLog.append(topError).append("\n");
                    for (StackTraceElement s : e.getStackTrace()) {
                        Log.e(tag, s.toString());
                        errorLog.append(s.toString()).append("\n");
                    }
                    SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(System.currentTimeMillis()) + ": " + errorLog.toString(), SdService.APP_ERROR_LOG);
                }
            });
        } catch (SecurityException e) {
            Log.e(tag, "Could not set the Default Uncaught Exception Handler: " + e.getLocalizedMessage());
        }
    }

}
