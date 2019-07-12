package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class PostureDetectionResult extends WearableData{
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_SIT = 1;
    public static final int TYPE_LIE = 2;
    private long timestamp;
    private int type;
    private int duration;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toCloudMessage() {
        return null;
    }
}
