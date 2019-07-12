package com.homecare.app.model;

import com.homecare.app.model.ble.ElderlyInfo;

public class SGInfo {
    private WearableInfo wearableInfo;
    private ElderlyInfo elderlyInfo;
    private ElderlyConfig elderlyConfig;
    private String token = "";
    private String bondToken = "0000000000";
    private String appVersion;
    private volatile long lastWallClockTs;
    private volatile int phoneBattery = 100;
    private boolean dfuFailed = false;

    public WearableInfo getWearableInfo() {
        return wearableInfo;
    }

    public void setWearableInfo(WearableInfo wearableInfo) {
        this.wearableInfo = wearableInfo;
    }

    public ElderlyInfo getElderlyInfo() {
        return elderlyInfo;
    }

    public void setElderlyInfo(ElderlyInfo elderlyInfo) {
        this.elderlyInfo = elderlyInfo;
    }

    public ElderlyConfig getElderlyConfig() {
        return elderlyConfig;
    }

    public void setElderlyConfig(ElderlyConfig elderlyConfig) {
        this.elderlyConfig = elderlyConfig;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public long getLastWallClockTs() {
        return lastWallClockTs;
    }

    public void setLastWallClockTs(long lastWallClockTs) {
        this.lastWallClockTs = lastWallClockTs;
    }

    public int getPhoneBattery() {
        return phoneBattery;
    }

    public void setPhoneBattery(int phoneBattery) {
        this.phoneBattery = phoneBattery;
    }

    public boolean isDfuFailed() {
        return dfuFailed;
    }

    public void setDfuFailed(boolean dfuFailed) {
        this.dfuFailed = dfuFailed;
    }

    public int getMonitorInterval() {
        if (elderlyConfig != null) {
            return elderlyConfig.getHrCollectInterval();
        } else {
            return 0;
        }
    }

    public String getBondToken() {
        return bondToken;
    }

    public void setBondToken(String bondToken) {
        this.bondToken = bondToken;
    }

    public void updateWithExistInfo(SGInfo sgInfo) {
        this.wearableInfo.updateWithExistInfo(sgInfo.getWearableInfo());
        this.lastWallClockTs = sgInfo.lastWallClockTs;
        this.phoneBattery = sgInfo.phoneBattery;
        this.dfuFailed = sgInfo.dfuFailed;
    }
}
