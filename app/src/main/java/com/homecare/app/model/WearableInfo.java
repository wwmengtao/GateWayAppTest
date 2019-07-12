package com.homecare.app.model;

public class WearableInfo {
    private String deviceAddress;
    private String deviceName;
    private int lowWearablePowerThreshold;
    private int lowPhonePowerThreshold;
    private String firmwareVersion = "";
    private String hardwareVersion = "";
    private String amsVersion = "";
    private volatile int lastHr = -1;
    private volatile int lastSq = -1;
    private volatile long lastHrTs;
    private volatile int lastBattery = -1;
    private volatile boolean disconnect = false;

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getLowWearablePowerThreshold() {
        return lowWearablePowerThreshold;
    }

    public void setLowWearablePowerThreshold(int lowWearablePowerThreshold) {
        this.lowWearablePowerThreshold = lowWearablePowerThreshold;
    }

    public int getLowPhonePowerThreshold() {
        return lowPhonePowerThreshold;
    }

    public void setLowPhonePowerThreshold(int lowPhonePowerThreshold) {
        this.lowPhonePowerThreshold = lowPhonePowerThreshold;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getAmsVersion() {
        return amsVersion;
    }

    public void setAmsVersion(String amsVersion) {
        this.amsVersion = amsVersion;
    }

    public int getLastHr() {
        return lastHr;
    }

    public void setLastHr(int lastHr) {
        this.lastHr = lastHr;
    }

    public int getLastSq() {
        return lastSq;
    }

    public void setLastSq(int lastSq) {
        this.lastSq = lastSq;
    }

    public long getLastHrTs() {
        return lastHrTs;
    }

    public void setLastHrTs(long lastHrTs) {
        this.lastHrTs = lastHrTs;
    }

    public int getLastBattery() {
        return lastBattery;
    }

    public void setLastBattery(int lastBattery) {
        this.lastBattery = lastBattery;
    }

    public boolean isDisconnect() {
        return disconnect;
    }

    public void setDisconnect(boolean disconnect) {
        this.disconnect = disconnect;
    }

    public void updateWithExistInfo(WearableInfo wearableInfo) {
        this.lastHr = wearableInfo.lastHr;
        this.lastSq = wearableInfo.lastSq;
        this.lastHrTs = wearableInfo.lastHrTs;
        this.lastBattery = wearableInfo.lastBattery;
        this.disconnect = wearableInfo.disconnect;
    }
}
