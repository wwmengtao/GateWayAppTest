package com.homecare.app.model.ble;

public class HeartRateRequest extends AbstractRequest{
    @Override
    public String toBinaryString() {
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST + getSequence() + TRANSPORT_HEAD_LENGTH_NO_PAYLOAD_REQUEST + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_HEART_RATE_REPORT;
    }
}
