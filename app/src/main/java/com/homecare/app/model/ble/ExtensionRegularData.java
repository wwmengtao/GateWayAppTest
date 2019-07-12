package com.homecare.app.model.ble;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeng on 2015/8/5.
 */
public class ExtensionRegularData extends WearableData{
    private long timestamp;
    private int energyActivityType;
    private int motionActivityType;
    private int postureType;
    private int postureDuration;
    private List<Integer> motionDataList = new ArrayList<>(12);

    @Override
    public String toCloudMessage() {
        if (motionDataList.size() > 0) {
            StringBuilder message = new StringBuilder();
            message.append(timestamp).append(",");
            for (Integer motionData : motionDataList) {
                message.append(motionData).append(",");
            }
            message.deleteCharAt(message.length() - 1);
            return message.toString();
        } else {
            return null;
        }
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

    public int getPostureType() {
        return postureType;
    }

    public void setPostureType(int postureType) {
        this.postureType = postureType;
    }

    public int getPostureDuration() {
        return postureDuration;
    }

    public void setPostureDuration(int postureDuration) {
        this.postureDuration = postureDuration;
    }

    public void addMotionData(int motionData) {
        this.motionDataList.add(motionData);
    }
}
