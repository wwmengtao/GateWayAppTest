package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/7/29.
 */
public class UnBondResult extends WearableData {
    private String token;
    private boolean isUnBonded;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUnBonded() {
        return isUnBonded;
    }

    public void setIsUnBonded(boolean isUnBonded) {
        this.isUnBonded = isUnBonded;
    }

    @Override
    public String toCloudMessage() {
        return null;
    }

}
