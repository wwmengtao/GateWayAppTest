package com.homecare.app.model;

public interface Constants {
    public static final int ALERT_FALL = 1;
    public static final int ALERT_SOS = 2;
    public static final int ALERT_HR_TOO_FAST = 3;
    public static final int ALERT_HR_TOO_SLOW = 4;
    public static final int ALERT_WEARABLE_DISCONNECT = 5;
    public static final int ALERT_PHONE_DISCONNECT = 6;
    public static final int ALERT_PHONE_LOW_POWER = 7;
    public static final int ALERT_WEARABLE_ABNORMAL = 8;
    public static final int ALERT_WEARABLE_LOW_POWER = 9;
    public static final int ALERT_NOTE_DELIMITER = 100;
    public static final int NOTE_WEARABLE_LOW_POWER = 101;
    public static final int NOTE_PHONE_LOW_POWER = 102;
    public static final int NOTE_LYING_TOO_LONG = 103;
    public static final int NOTE_SIT_TOO_LONG = 104;
    public static final int NOTE_GATEWAYAPP_CONNECTION_RECOVERED = 105;
    public static final int NOTE_WEARABLE_NORMAL = 106;
    public static final int NOTE_SLEEP = 107;
    public static final int NOTE_CHECK = 108;
    public static final int NOTE_JOB_DELIMITER = 1000;
    public static final int JOB_CAREWORKER = 1001;
    public static final int JOB_DRUG = 1002;

    public static final int PUSH_TYPE_CONFIG = 1;
    public static final int PUSH_TYPE_NOTE = 2;
    public static final int PUSH_TYPE_ALERT = 3;
    public static final int CONFIG_ELDERLY_CHANGED = 1;

    public static final String SG_ATTR_HEIGHT = "height";
    public static final String SG_ATTR_WEIGHT = "weight";
    public static final String SG_ATTR_HRFAST = "hrFast";
    public static final String SG_ATTR_HRSLOW = "hrSlow";
    public static final String SG_ATTR_MONITORINTERVAL = "monitorInterval";
    public static final String SG_ATTR_GENDER = "gender";
    public static final String SG_ATTR_BIRTHDAY = "birthday";

    public static final String TRANSPORT_HEAD_LENGTH_NO_PAYLOAD_REQUEST = "0000";
    public static final String TRANSPORT_HEAD_DELIMITER= "101011";
    //public static final String TRANSPORT_HEAD_VERSION= "00";
    public static final String TRANSPORT_HEAD_DEFAULT_CRC= "0000000000000000";
    public static final String TRANSPORT_HEAD_NONE_ACKED_LINK_REQUEST = TRANSPORT_HEAD_DELIMITER + "000";
    public static final String TRANSPORT_HEAD_ACKED_LINK_REQUEST = TRANSPORT_HEAD_DELIMITER + "001";
    public static final String TRANSPORT_HEAD_ACKED_LINK_ACK_RESPONSE = TRANSPORT_HEAD_DELIMITER + "011";
    public static final String TRANSPORT_HEAD_ACKED_LINK_ACK_CRC_ERROR = TRANSPORT_HEAD_DELIMITER + "111";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_BONDING_RESP = "0101";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_BONDING_REQ = "0100";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DEVICE_BONDING = "00000000";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_UNBONDING_REQ = "0100";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_UNBONDING_RESP = "0101";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DEVICE_UNBONDING = "00000001";
    public static final String TRANSPORT_HEAD_LENGTH_DFU_OTA = "0001";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DFU_OTA = "00000010";
    public static final String TRANSPORT_HEAD_LENGTH_USERINFO_SETTING = "0011";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_USERINFO_SETTING = "00010000";
    public static final String TRANSPORT_HEAD_LENGTH_WALLCLOCK_SETTING = "0100";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_WALLCLOCK_SETTING = "00010001";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_REPORT_THRESHOLD = "0010";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DEVICE_REPORT_THRESHOLD = "00010010";
    public static final String TRANSPORT_HEAD_LENGTH_FEATURE_CONFIGURATION = "0001";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_FEATURE_CONFIGURATION = "00010011";
    public static final String TRANSPORT_HEAD_LENGTH_VIBRATION_COMMAND = "0001";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_VIBRATION_COMMAND = "00010100";
    public static final String TRANSPORT_HEAD_LENGTH_SYSTEM_SOFTWARE_RESET = "0001";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_SYSTEM_SOFTWARE_RESET = "00010101";
    public static final String TRANSPORT_HEAD_LENGTH_SYSTEM_SHUTDOWN = "0001";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_SYSTEM_SHUTDOWN = "00010110";
    public static final String TRANSPORT_HEAD_LENGTH_FIRMWARE_HARDWARE_VERSION = "1000";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_FIRMWARE_HARDWARE_VERSION = "00100000";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_ADDRESS = "0110";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DEVICE_ADDRESS = "00100001";
    public static final String TRANSPORT_HEAD_LENGTH_BATTERY_STATUS = "0100";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_BATTERY_STATUS = "00100010";
    public static final String TRANSPORT_HEAD_LENGTH_RSSI_LEVEL = "0010";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_RSSI_LEVEL = "00100011";
    public static final String TRANSPORT_HEAD_LENGTH_TX_POWER_LEVEL = "0010";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_TX_POWER_LEVEL = "00100100";
    public static final String TRANSPORT_HEAD_LENGTH_HEALTH_THRESHOLD_CONFIGURATION = "0011";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_HEALTH_THRESHOLD_CONFIGURATION = "00110000";
    public static final String TRANSPORT_HEAD_LENGTH_HEART_RATE_REPORT = "0101";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_HEART_RATE_REPORT = "00110001";
    public static final String TRANSPORT_HEAD_LENGTH_SLEEP_REPORT = "1111";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_SLEEP_REPORT = "00110010";
    public static final String TRANSPORT_HEAD_LENGTH_ACTIVITY_REPORT = "0101";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_ACTIVITY_REPORT = "00110011";
    public static final String TRANSPORT_HEAD_LENGTH_PEDOMETER_REPORT = "0111";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_PEDOMETER_REPORT = "00110101";
    public static final String TRANSPORT_HEAD_LENGTH_POSTURE_DETECTION = "0110";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_POSTURE_DETECTION = "00110110";
    public static final String TRANSPORT_HEAD_LENGTH_REGULAR_REPORT_CONFIGURATION = "0110";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_REGULAR_REPORT_CONFIGURATION = "01000000";
    public static final String TRANSPORT_HEAD_LENGTH_BASIC_REGULAR_REPORT = "1101";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_BASIC_REGULAR_REPORT = "01000001";
    public static final String TRANSPORT_HEAD_LENGTH_EXTENSION_REGULAR_REPORT = "1000";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_EXTENSION_REGULAR_REPORT = "01000010";
    public static final String TRANSPORT_HEAD_LENGTH_HEALTH_EVENT_CONFIGURATION = "0010";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_HEALTH_EVENT_CONFIGURATION = "01010000";
    public static final String TRANSPORT_HEAD_LENGTH_HEALTH_EVENT_NOTIFICATION = "0101";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_HEALTH_EVENT_NOTIFICATION = "01010001";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_EVENT_CONFIGURATION = "0010";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DEVICE_EVENT_CONFIGURATION = "01010010";
    public static final String TRANSPORT_HEAD_LENGTH_DEVICE_EVENT_NOTIFICATION = "0101";
    public static final String TRANSPORT_PAYLOAD_APP_SUB_DEVICE_EVENT_NOTIFICATION = "01010011";

    //public static final String WEARABLE_TOKEN = "ABC1";

    public static final long ONE_MINUTE_IN_MS = 60 * 1000;
    public static final long ONE_DAY_IN_MS = 24 * 3600 * 1000;

    public static final String APK_FILE_NAME = "gatewayapp.apk";
    public static final String FIRMWARE_FILE_NAME = "kiwi-firmware.hex";

    public static final int PHONE_LOWEST_BATTERY = 5;
    public static final int WEARABLE_LOWEST_BATTERY = 5;
}
