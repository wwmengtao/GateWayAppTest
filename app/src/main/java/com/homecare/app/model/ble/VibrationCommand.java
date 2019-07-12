package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.Constants;

public class VibrationCommand extends AbstractRequest {
    public static final int REMINDER_ID_START = 1;
    public static final int REMINDER_ID_STOP = 0;
    public static final int REMINDER_MODE_LIGHT = 1;
    public static final int REMINDER_MODE_INTENSE = 2;
    public static final int REMINDER_MODE_REPEATED = 3;
    public int reminderId;
    public int reminderMode;
    public VibrationCommand(int reminderId, int reminderMode){
        this.reminderId = reminderId;
        this.reminderMode = reminderMode;
    }
    @Override
    public String toBinaryString() {
        int vibrationCommand = reminderId << 7 | reminderMode;
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST +getSequence() + TRANSPORT_HEAD_LENGTH_VIBRATION_COMMAND + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_VIBRATION_COMMAND + ParserUtil.intTotoBinaryString(vibrationCommand, 8);
    }
}
