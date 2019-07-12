package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class PedometerResult extends WearableData{
    private long timestamp;
    private int stepCount;
    private int walkDistance;
    @Override
    public String toCloudMessage() {
        return null;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }
}
