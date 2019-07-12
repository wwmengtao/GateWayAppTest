package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class DfuResult extends WearableData{
    public static final int DFU_STATUS_SUCCESS = 0;
    public static final int DFU_STATUS_FAILURE = 1;
    public static final int DFU_ERROR_UNBONDING = 0;
    public static final int DFU_ERROR_LOW_BATTERY = 1;
    private int status;
    private int errorCode;
    @Override
    public String toCloudMessage() {
        return null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
