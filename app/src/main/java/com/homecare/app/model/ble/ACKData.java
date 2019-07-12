package com.homecare.app.model.ble;

public class ACKData extends WearableData {
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
