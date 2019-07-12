package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;

public class DeviceEventConfig extends AbstractRequest {
    private boolean enableLowPowerReport;
    private boolean enableLinkLossReport;

    public DeviceEventConfig(boolean enableLinkLossReport, boolean enableLowPowerReport){
        this.enableLinkLossReport = enableLinkLossReport;
        this.enableLowPowerReport = enableLowPowerReport;
    }
    @Override
    public String toBinaryString() {
        int lowper = enableLowPowerReport ? 1 : 0;
        int linloss = enableLinkLossReport ? 1 : 0;
        int config = linloss << 1 | lowper;
        String binStr = ParserUtil.intTotoBinaryString(config, 16);
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_DEVICE_EVENT_CONFIGURATION + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_DEVICE_EVENT_CONFIGURATION + binStr.substring(8,16) + binStr.substring(0,8);
    }
}
