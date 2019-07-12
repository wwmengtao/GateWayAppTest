package com.homecare.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    public static long getTodayStartTime() {
        return getTodayTime(0);
    }

    public static long getTodayTime(int hour) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, hour);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTimeInMillis();
    }

    public static String displayByDateAndHourAndMinuteAndSecond(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp)) + "(" + timestamp + ")";
    }

}
