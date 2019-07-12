package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class BatteryStatus extends WearableData{
    private long timestamp;
    private int percent;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public String toCloudMessage() {
        return null;
    }
}
