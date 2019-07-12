package com.homecare.app.model.ble;

public class ACKRequest extends AbstractRequest{

    public ACKRequest(String sequence) {
        setSequence(sequence);
    }

    @Override
    public String toBinaryString() {
        return TRANSPORT_HEAD_ACKED_LINK_ACK_RESPONSE + getSequence() + TRANSPORT_HEAD_LENGTH_NO_PAYLOAD_REQUEST + TRANSPORT_HEAD_DEFAULT_CRC;
    }

}
