package com.homecare.app.model.ble;

public class HeartRateResult extends WearableData{
    private long timestamp;
    private int hearRate;
    private int sq;

    @Override
    public String toCloudMessage() {
        return timestamp + "," + hearRate + "," + sq;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getHearRate() {
        return hearRate;
    }

    public void setHearRate(int hearRate) {
        this.hearRate = hearRate;
    }

    public int getSq() {
        return sq;
    }

    public void setSq(int sq) {
        this.sq = sq;
    }
}
