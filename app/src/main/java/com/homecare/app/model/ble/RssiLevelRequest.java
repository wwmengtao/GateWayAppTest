package com.homecare.app.model.ble;

/**
 * Created by zeng on 2015/8/5.
 */
public class RssiLevelRequest extends AbstractRequest{
    @Override
    public String toBinaryString() {
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST + getSequence() + TRANSPORT_HEAD_LENGTH_NO_PAYLOAD_REQUEST + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_RSSI_LEVEL;
    }
}
