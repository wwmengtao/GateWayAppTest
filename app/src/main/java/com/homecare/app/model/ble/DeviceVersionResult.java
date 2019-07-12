package com.homecare.app.model.ble;

public class DeviceVersionResult extends WearableData {
    private String firmwareVersion;
    private String hardwareVersion;
    private String amsVersion;
    @Override
    public String toCloudMessage() {
        return null;
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
}
