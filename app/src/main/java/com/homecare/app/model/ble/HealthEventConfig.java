package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.Constants;

public class HealthEventConfig extends AbstractRequest {
    private boolean enableLongTimeSitEvent;
    private boolean enableHighHeartrateEvent;

    public HealthEventConfig(boolean enableHighHeartrateEvent,boolean enableLongTimeSitEvent){
        this.enableHighHeartrateEvent = enableHighHeartrateEvent;
        this.enableLongTimeSitEvent = enableLongTimeSitEvent;
    }
    @Override
    public String toBinaryString() {
        int sit = enableLongTimeSitEvent ? 1 : 0;
        int hr = enableHighHeartrateEvent ? 1 : 0;
        int config = hr << 1 | sit;
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_HEALTH_EVENT_CONFIGURATION + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_HEALTH_EVENT_CONFIGURATION + ParserUtil.intTotoBinaryString(config, 16);
    }
}
