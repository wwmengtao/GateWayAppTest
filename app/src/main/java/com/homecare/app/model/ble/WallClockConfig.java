package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;

import java.util.Calendar;

public class WallClockConfig extends AbstractRequest {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public WallClockConfig(){
        Calendar now = Calendar.getInstance();
        year = now.get(Calendar.YEAR) - 2000;
        month = now.get(Calendar.MONTH) + 1;
        hour = now.get(Calendar.HOUR_OF_DAY);
        day = now.get(Calendar.DAY_OF_MONTH);
        minute = now.get(Calendar.MINUTE);
        second = now.get(Calendar.SECOND);
    }
    public String toBinaryString(){
        String bits =  ParserUtil.intTotoBinaryString(year,6) + ParserUtil.intTotoBinaryString(month,4)
                + ParserUtil.intTotoBinaryString(day,5) + ParserUtil.intTotoBinaryString(hour,5) + ParserUtil.intTotoBinaryString(minute,6) + ParserUtil.intTotoBinaryString(second,6);
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST + getSequence() + TRANSPORT_HEAD_LENGTH_WALLCLOCK_SETTING + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_WALLCLOCK_SETTING + bits.substring(24,32) + bits.substring(16,24) + bits.substring(8,16) + bits.substring(0,8);
    }
}
