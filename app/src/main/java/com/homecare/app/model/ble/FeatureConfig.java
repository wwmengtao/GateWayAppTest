package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.Constants;

public class FeatureConfig extends AbstractRequest {
    private boolean enablePedometer;
    private boolean enableHRM;
    private boolean enableAmbient;
    private boolean enableActivity;
    private boolean enablePosture;
    private boolean enableSleep;

    public FeatureConfig(boolean enablePedometer,boolean enableHRM, boolean enableAmbient, boolean enableActivity, boolean enablePosture, boolean enableSleep){
        this.enablePedometer = enablePedometer;
        this.enableHRM = enableHRM;
        this.enableAmbient = enableAmbient;
        this.enableActivity = enableActivity;
        this.enablePosture = enablePosture;
        this.enableSleep = enableSleep;
    }
    @Override
    public String toBinaryString() {
        int pedometer = enablePedometer ? 0 : 1;
        int hrm = enableHRM ? 0 : 1;
        int ambient = enableAmbient ? 0 : 1;
        int activity = enableActivity ? 0 : 1;
        int posture = enablePosture ? 0 : 1;
        int sleep = enableSleep ? 0 : 1;
        int setting = (pedometer) | (hrm << 1) | (ambient << 2) | (activity << 3) | (posture << 4) | (sleep << 5);
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_FEATURE_CONFIGURATION + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_FEATURE_CONFIGURATION + ParserUtil.intTotoBinaryString(setting, 8);
    }

}
