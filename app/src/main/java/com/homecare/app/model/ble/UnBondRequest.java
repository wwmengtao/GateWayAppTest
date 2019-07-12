package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;

public class UnBondRequest extends AbstractRequest {
    private String token;
    public UnBondRequest(String token){
        this.token = token;
    }
    @Override
    public String toBinaryString() {
        int bondToken = Integer.parseInt(token);
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST + getSequence() + TRANSPORT_HEAD_LENGTH_DEVICE_UNBONDING_REQ+ TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_DEVICE_UNBONDING + ParserUtil.intTotoBinaryString(bondToken, 32);
    }
}
