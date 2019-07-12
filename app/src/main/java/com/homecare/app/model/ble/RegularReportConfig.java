package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;

public class RegularReportConfig  extends AbstractRequest {
    private int regularInterval;
    private boolean enableExtensionReport;
    private boolean validHeartrate;
    private boolean validTemperature;
    private boolean validHumidity;
    private boolean validPressure;
    private boolean validStaticTime;
    private boolean validWalkTime;
    private boolean validRunTime;
    private boolean validWheelTime;
    private boolean validEnergyActivityType;
    private boolean validMotionActivityType;
    private boolean validPostureType;
    private boolean validPostureDuration;

    public RegularReportConfig(int regularInterval,boolean enableExtensionReport,boolean validHeartrate,boolean validTemperature,
                               boolean validHumidity,boolean validPressure,boolean validStaticTime,boolean validWalkTime,boolean validRunTime,
                               boolean validWheelTime,boolean validEnergyActivityType,boolean validMotionActivityType,
                               boolean validPostureType, boolean validPostureDuration) {
        this.regularInterval = regularInterval;
        this.enableExtensionReport = enableExtensionReport;
        this.validEnergyActivityType = validEnergyActivityType;
        this.validHeartrate = validHeartrate;
        this.validHumidity = validHumidity;
        this.validMotionActivityType = validMotionActivityType;
        this.validPostureDuration = validPostureDuration;
        this.validPostureType = validPostureType;
        this.validPressure = validPressure;
        this.validTemperature = validTemperature;
        this.validRunTime = validRunTime;
        this.validWalkTime = validWalkTime;
        this.validStaticTime = validStaticTime;
        this.validWheelTime = validWheelTime;
    }
    public int getRegularInterval() {
        return regularInterval;
    }

    public void setRegularInterval(int regularInterval) {
        this.regularInterval = regularInterval;
    }

    public boolean isEnableExtensionReport() {
        return enableExtensionReport;
    }

    public void setEnableExtensionReport(boolean enableExtensionReport) {
        this.enableExtensionReport = enableExtensionReport;
    }

    public boolean isValidHeartrate() {
        return validHeartrate;
    }

    public void setValidHeartrate(boolean validHeartrate) {
        this.validHeartrate = validHeartrate;
    }

    public boolean isValidTemperature() {
        return validTemperature;
    }

    public void setValidTemperature(boolean validTemperature) {
        this.validTemperature = validTemperature;
    }

    public boolean isValidHumidity() {
        return validHumidity;
    }

    public void setValidHumidity(boolean validHumidity) {
        this.validHumidity = validHumidity;
    }

    public boolean isValidPressure() {
        return validPressure;
    }

    public void setValidPressure(boolean validPressure) {
        this.validPressure = validPressure;
    }

    public boolean isValidWalkTime() {
        return validWalkTime;
    }

    public void setValidWalkTime(boolean validWalkTime) {
        this.validWalkTime = validWalkTime;
    }

    public boolean isValidStaticTime() {
        return validStaticTime;
    }

    public void setValidStaticTime(boolean validStaticTime) {
        this.validStaticTime = validStaticTime;
    }

    public boolean isValidRunTime() {
        return validRunTime;
    }

    public void setValidRunTime(boolean validRunTime) {
        this.validRunTime = validRunTime;
    }

    public boolean isValidWheelTime() {
        return validWheelTime;
    }

    public void setValidWheelTime(boolean validWheelTime) {
        this.validWheelTime = validWheelTime;
    }

    public boolean isValidEnergyActivityType() {
        return validEnergyActivityType;
    }

    public void setValidEnergyActivityType(boolean validEnergyActivityType) {
        this.validEnergyActivityType = validEnergyActivityType;
    }

    public boolean isValidMotionActivityType() {
        return validMotionActivityType;
    }

    public void setValidMotionActivityType(boolean validMotionActivityType) {
        this.validMotionActivityType = validMotionActivityType;
    }

    public boolean isValidPostureType() {
        return validPostureType;
    }

    public void setValidPostureType(boolean validPostureType) {
        this.validPostureType = validPostureType;
    }

    public boolean isValidPostureDuration() {
        return validPostureDuration;
    }

    public void setValidPostureDuration(boolean validPostureDuration) {
        this.validPostureDuration = validPostureDuration;
    }

    @Override
    public String toBinaryString() {
        int hr = validHeartrate ? 1 : 0;
        int temp = validTemperature ? 1 : 0;
        int hum = validHumidity ? 1 : 0;
        int pre = validPressure ? 1 : 0;
        int st = validStaticTime ? 1 : 0;
        int wt = validWalkTime ? 1:0;
        int rt = validRunTime ? 1:0;
        int wht = validWheelTime ? 1:0;
        int eat = validEnergyActivityType ? 1:0;
        int mat = validMotionActivityType ? 1:0;
        int pt = validPostureType ? 1:0;
        int pd = validPostureDuration ? 1:0;
        int config = hr | temp << 1 | hum << 2 | pre << 3 | st << 4 | wt << 5  | rt << 6 | wht << 7 | eat << 8 | mat << 9 | pt << 10 | pd << 11;
        String bits = ParserUtil.intTotoBinaryString(config,32);
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_REGULAR_REPORT_CONFIGURATION + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_REGULAR_REPORT_CONFIGURATION + ParserUtil.intTotoBinaryString(regularInterval,8)
                + ParserUtil.intTotoBinaryString(enableExtensionReport ? 1 : 0,8) +  bits.substring(24,32) + bits.substring(16,24)
                + bits.substring(8,16) + bits.substring(0,8);
    }
}
