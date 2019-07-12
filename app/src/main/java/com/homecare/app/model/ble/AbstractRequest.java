package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.Constants;

public abstract class AbstractRequest implements ExchangeInfo, Constants {
    private String sequence = "000";

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public byte[] toBytes() {
        return ParserUtil.bitsToBytes(toBinaryString());
    }
}
