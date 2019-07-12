package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.Constants;

public class DeviceReportThresholdConfig extends AbstractRequest {
    private int lowPowerThreshold;
    private int lowRssiThreshold;

    public DeviceReportThresholdConfig(int lowPowerThreshold, int lowRssiThreshold) {
        this.lowPowerThreshold = lowPowerThreshold;
        this.lowRssiThreshold = lowRssiThreshold;
    }

    @Override
    public String toBinaryString() {
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_DEVICE_REPORT_THRESHOLD
                + TRANSPORT_HEAD_DEFAULT_CRC + TRANSPORT_PAYLOAD_APP_SUB_DEVICE_REPORT_THRESHOLD
                + ParserUtil.intTotoBinaryString(lowPowerThreshold, 8) + ParserUtil.intTotoBinaryString(lowRssiThreshold,8);
    }
}
