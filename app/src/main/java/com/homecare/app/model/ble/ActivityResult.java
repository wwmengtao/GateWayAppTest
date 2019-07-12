package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class ActivityResult extends WearableData{
    public final static int ENERGY_ACTIVITY_TYPE_UNDECIDED = 0;
    public final static int ENERGY_ACTIVITY_TYPE_STATIC = 1;
    public final static int ENERGY_ACTIVITY_TYPE_LIGHT = 2;
    public final static int ENERGY_ACTIVITY_TYPE_INTENSE = 3;
    public final static int MOTION_ACTIVITY_TYPE_UNDECIDED = 0;
    public final static int MOTION_ACTIVITY_TYPE_WALK = 1;
    public final static int MOTION_ACTIVITY_TYPE_RUN = 2;
    public final static int MOTION_ACTIVITY_TYPE_WHEELCHAIR = 3;
    public final static int MOTION_ACTIVITY_TYPE_DRIVEINVEHICLE = 4;
    public final static int MOTION_ACTIVITY_TYPE_STATIC = 5;
    public final static int MOTION_ACTIVITY_TYPE_SLEEP = 6;
    private long timestamp;
    private int energyActivityType;
    private int motionActivityType;

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

    public int getEnergyActivityType() {
        return energyActivityType;
    }

    public void setEnergyActivityType(int energyActivityType) {
        this.energyActivityType = energyActivityType;
    }

    public int getMotionActivityType() {
        return motionActivityType;
    }

    public void setMotionActivityType(int motionActivityType) {
        this.motionActivityType = motionActivityType;
    }
}
