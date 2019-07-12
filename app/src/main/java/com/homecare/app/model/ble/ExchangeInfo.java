package com.homecare.app.model.ble;

public interface ExchangeInfo {

    public String toBinaryString();

    public byte[] toBytes();

    public String getSequence();

    public void setSequence(String sequence);
}
