package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class SystemShutdownResult extends WearableData{
    private boolean success;

    @Override
    public String toCloudMessage() {
        return null;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
