package com.homecare.app.model.ble;

public class SleepData extends WearableData {
    private long timestamp;
    private long offBedTime;
    private long onBedTime;
    private int interruptionTimes;
    private int deepSleepTime;
    private int lightSleepTime;
    private int restingHearRate;
    private String qualityDesc;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getOffBedTime() {
        return offBedTime;
    }

    public void setOffBedTime(long offBedTime) {
        this.offBedTime = offBedTime;
    }

    public long getOnBedTime() {
        return onBedTime;
    }

    public void setOnBedTime(long onBedTime) {
        this.onBedTime = onBedTime;
    }

    public int getInterruptionTimes() {
        return interruptionTimes;
    }

    public void setInterruptionTimes(int interruptionTimes) {
        this.interruptionTimes = interruptionTimes;
    }

    public int getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(int deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public int getLightSleepTime() {
        return lightSleepTime;
    }

    public void setLightSleepTime(int lightSleepTime) {
        this.lightSleepTime = lightSleepTime;
    }

    public int getRestingHearRate() {
        return restingHearRate;
    }

    public void setRestingHearRate(int restingHearRate) {
        this.restingHearRate = restingHearRate;
    }

    public String getQualityDesc() {
        return qualityDesc;
    }

    public void setQualityDesc(String qualityDesc) {
        this.qualityDesc = qualityDesc;
    }

    @Override
    public String toCloudMessage() {
        return timestamp + "," + onBedTime + "," + offBedTime + "," + interruptionTimes + "," + deepSleepTime + "," + lightSleepTime + "," + restingHearRate;
    }

}
