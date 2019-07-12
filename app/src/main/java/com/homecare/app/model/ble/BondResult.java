package com.homecare.app.model.ble;

public class BondResult extends WearableData{
    private String token;
    private boolean isBonded;
    private boolean needToBond;

    @Override
    public String toCloudMessage() {
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBonded() {
        return isBonded;
    }

    public void setIsBonded(boolean isBonded) {
        this.isBonded = isBonded;
    }

    public boolean isNeedToBond() {
        return needToBond;
    }

    public void setNeedToBond(boolean needToBond) {
        this.needToBond = needToBond;
    }
}
