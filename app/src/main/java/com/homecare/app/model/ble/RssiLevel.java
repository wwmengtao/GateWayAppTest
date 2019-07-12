package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class RssiLevel extends WearableData{
    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toCloudMessage() {
        return null;
    }
}
