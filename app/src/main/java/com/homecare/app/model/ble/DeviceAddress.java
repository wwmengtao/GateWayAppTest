package com.homecare.app.model.ble;

public class DeviceAddress extends WearableData{
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toCloudMessage() {
        return address;
    }
}
