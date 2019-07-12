package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.Constants;

public class HealthThresholdConfig extends AbstractRequest {
    public static final int THRESHOLD_ID_LONG_TIME_SIT = 0;
    public static final int THRESHOLD_ID_HIGH_HEART_RATE = 1;
    public static final int THRESHOLD_ID_LOW_HEART_RATE = 2;
    private int thresholdId;
    private int thresholdValue;

    public HealthThresholdConfig(int thresholdId, int thresholdValue){
        this.thresholdId = thresholdId;
        this.thresholdValue = thresholdValue;
    }

    public int getThresholdId() {
        return thresholdId;
    }

    public void setThresholdId(int thresholdId) {
        this.thresholdId = thresholdId;
    }

    public int getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    @Override
    public String toBinaryString() {
        String binStr = ParserUtil.intTotoBinaryString(thresholdValue, 16);
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_HEALTH_THRESHOLD_CONFIGURATION + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_HEALTH_THRESHOLD_CONFIGURATION + ParserUtil.intTotoBinaryString(thresholdId, 8)
                + binStr.substring(8,16) + binStr.substring(0,8);
    }
}
