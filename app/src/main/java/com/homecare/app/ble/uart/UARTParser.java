package com.homecare.app.ble.uart;

import com.homecare.app.model.*;
import com.homecare.app.model.ble.*;

import java.util.Calendar;

public class UARTParser {

    public static WearableData parseWearableData(byte[] data) {
        WearableData wearableData = null;
        String bits = bytesToBits(data);
        if(bits.length() == 32 && bits.substring(7, 8).equals("1")) {    //ACK
            String prefix = bits.substring(0, 9);
            wearableData = parseAckData(prefix);
            wearableData.setSequence(parseSequence(bits));
            wearableData.setAck(true);
            return wearableData;
        }
        //none ACK messages
        if (bits.length() < 40) {
            return null;
        }
        byte appId = Byte.parseByte(bits.substring(32, 36), 2);
        byte subId = Byte.parseByte(bits.substring(36, 40), 2);
        if(appId == 0x00 && subId == 0x00) {
            wearableData = parseBondResult(bits);

        } else if(appId == 0x00 && subId == 0x01) {
            wearableData = parseUnBondResult(bits);

        }  else if(appId == 0x00 && subId == 0x02) {
            wearableData = parseDfuResult(bits);

        } /*else if(appId == 0x01 && subId == 0x00) {
            wearableData = parseAckData(prefix);
        } else if(appId == 0x01 && subId == 0x01) {
            wearableData = parseAckData(prefix);
        } else if(appId == 0x01 && subId == 0x02) {
            wearableData = parseAckData(prefix);
        } else if(appId == 0x01 && subId == 0x03) {
            wearableData = parseAckData(prefix);
        } else if(appId == 0x01 && subId == 0x04) {
            wearableData = parseAckData(prefix);
        }*/
        else if(appId == 0x01 && subId == 0x01) {
            wearableData = parseWallClockConfigRequest();
        } else if(appId == 0x01 && subId == 0x05) {
            wearableData = parseSystemResetResult(bits);
        } else if(appId == 0x01 && subId == 0x06) {
            wearableData = parseSystemShutdownResult(bits);
        } else if(appId == 0x02 && subId == 0x00) {
            wearableData = parseDeviceVersionResult(bits);
        }  else if(appId == 0x02 && subId == 0x01) {
            wearableData = parseDeviceAddress(bits);
        }  else if(appId == 0x02 && subId == 0x02) {
            wearableData = parseBatteryStatus(bits);
        }  else if(appId == 0x02 && subId == 0x03) {
            wearableData = parseRssiLevel(bits);
        }  else if(appId == 0x02 && subId == 0x04) {
            wearableData = parseTxPowerLevel(bits);
        } /* else if(appId == 0x03 && subId == 0x00) {
            wearableData = parseAckData(prefix);
        }*/
        else if(appId == 0x03 && subId == 0x01) {
            wearableData = parseHeartRateResult(bits);
        }  else if(appId == 0x03 && subId == 0x02) {
            wearableData = parseSleepData(bits);
        }  else if(appId == 0x03 && subId == 0x03) {
            wearableData = parseActivityResult(bits);
        }  else if(appId == 0x03 && subId == 0x05) {
            wearableData = parsePedometerResult(bits);
        }  else if(appId == 0x03 && subId == 0x06) {
            wearableData = parsePostureDetectionResult(bits);
        }  else if (appId == 0x04 && subId == 0x01) {
            wearableData = parseRegularData(bits);
        }  else if (appId == 0x04 && subId == 0x02) {
            wearableData = parseExtensionRegularData(bits);
        }  else if (appId == 0x05 && subId == 0x01) {
            wearableData = parseHealthEventNotification(bits);
        }  else if (appId == 0x05 && subId == 0x03) {
            wearableData = parseDeviceEventNotification(bits);
        }
        if (wearableData != null && bits.substring(8, 9).equals("1")) {
            wearableData.setNeedAck(true);
            wearableData.setSequence(parseSequence(bits));
        }
        return wearableData;
    }

    private static WearableData parseWallClockConfigRequest() {
        return new WallClockConfigRequest();
    }

    private static String parseSequence(String bits) {
        return bits.substring(9, 12);
    }

    private static WearableData parseAckData(String bits){
        ACKData ackData = new ACKData();
        if(bits.equals(Constants.TRANSPORT_HEAD_ACKED_LINK_ACK_RESPONSE))
            ackData.setSuccess(true);
        else
            ackData.setSuccess(false);
        return ackData;
    }
    private static WearableData parseHeartRateResult(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_HEART_RATE_REPORT, 2)) {
            return null;
        }
        HeartRateResult heartRateResult = new HeartRateResult();
        heartRateResult.setTimestamp(parseTimestamp(bits, 40));
        int heartRate = Integer.parseInt(bits.substring(64, 72), 2);
        int sq = Integer.parseInt(bits.substring(72, 80), 2);
        heartRateResult.setHearRate(heartRate);
        heartRateResult.setSq(sq);
        return heartRateResult;
    }
    private static WearableData parseDeviceEventNotification(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_DEVICE_EVENT_NOTIFICATION, 2)) {
            return null;
        }
        DeviceEventNotification deviceEventNotification = new DeviceEventNotification();
        deviceEventNotification.setTimestamp(parseTimestamp(bits, 40));
        deviceEventNotification.setEventId(Integer.parseInt(bits.substring(64, 72), 2));
        deviceEventNotification.setEventValue(Integer.parseInt(bits.substring(72, 80), 2));
        return deviceEventNotification;
    }
    private static WearableData parseHealthEventNotification(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_HEALTH_EVENT_NOTIFICATION, 2)) {
            return null;
        }
        HealthEventNotification healthEventNotification = new HealthEventNotification();
        healthEventNotification.setTimestamp(parseTimestamp(bits, 40));
        healthEventNotification.setEventId(Integer.parseInt(bits.substring(64, 72), 2));
        healthEventNotification.setEventValue(Integer.parseInt(bits.substring(72, 80), 2));
        return healthEventNotification;
    }
    private static WearableData parseExtensionRegularData(String bits) {
        ExtensionRegularData extensionRegularData = new ExtensionRegularData();
        extensionRegularData.setTimestamp(parseTimestamp(bits, 40));
        int length = Integer.parseInt(bits.substring(64, 72), 2);
        for (int i = 0; i < length; i++) {
            int motionData = Integer.parseInt(bits.substring(72 + i * 2, 72 + (i + 1) * 2), 2);
            extensionRegularData.addMotionData(motionData);
        }
        return extensionRegularData;
    }
    private static WearableData parsePostureDetectionResult(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_POSTURE_DETECTION, 2)) {
            return null;
        }
        PostureDetectionResult postureDetectionResult = new PostureDetectionResult();
        postureDetectionResult.setTimestamp(parseTimestamp(bits, 40));

        postureDetectionResult.setType(Integer.parseInt(bits.substring(64, 72), 2));
        postureDetectionResult.setDuration(Integer.parseInt(bits.substring(80, 88) + bits.substring(72, 80), 2));

        return postureDetectionResult;
    }
    private static WearableData parsePedometerResult(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_PEDOMETER_REPORT, 2)) {
            return null;
        }
        PedometerResult pedometerResult = new PedometerResult();
        pedometerResult.setTimestamp(parseTimestamp(bits, 40));
        pedometerResult.setStepCount(Integer.parseInt(bits.substring(72, 80) + bits.substring(64, 72), 2));
        pedometerResult.setWalkDistance(Integer.parseInt(bits.substring(88, 96) + bits.substring(80, 88), 2));

        return pedometerResult;
    }
    private static WearableData parseActivityResult(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_ACTIVITY_REPORT, 2)) {
            return null;
        }
        ActivityResult activityResult = new ActivityResult();
        activityResult.setTimestamp(parseTimestamp(bits, 40));
        activityResult.setEnergyActivityType(Integer.parseInt(bits.substring(64, 72), 2));
        activityResult.setMotionActivityType(Integer.parseInt(bits.substring(72, 80), 2));

        return activityResult;
    }
    private static WearableData parseSleepData(String bits) {
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_SLEEP_REPORT, 2)) {
            return null;
        }
        SleepData sleepData = new SleepData();
        sleepData.setTimestamp(parseTimestamp(bits, 40));
        sleepData.setOnBedTime(parseTimestamp(bits, 64));
        sleepData.setOffBedTime(parseTimestamp(bits, 88));
        sleepData.setInterruptionTimes(Integer.parseInt(bits.substring(112, 120), 2));
        sleepData.setDeepSleepTime(Integer.parseInt(bits.substring(128, 136) + bits.substring(120, 128), 2));
        sleepData.setLightSleepTime(Integer.parseInt(bits.substring(144, 152) + bits.substring(136, 144), 2));
        sleepData.setRestingHearRate(Integer.parseInt(bits.substring(152, 160), 2));
        //long quality = (sleepData.getDeepSleepTime() + sleepData.getLightSleepTime()) * 100 / (sleepData.getOffBedTime() - sleepData.getOnBedTime());
        //sleepData.setQuality((int) quality);
        return sleepData;
    }
    private static WearableData parseRegularData(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_BASIC_REGULAR_REPORT,2)) {
            return null;
        }
        RegularData regularData = new RegularData();
        long timestamp = parseTimestamp(bits, 40);
        int heartRate = Integer.parseInt(bits.substring(64, 72), 2);
        int sq = Integer.parseInt(bits.substring(72, 80), 2);
        int temperature = Integer.parseInt(bits.substring(80, 88), 2);
        int humidity = Integer.parseInt(bits.substring(88, 96), 2);
        int pressure = Integer.parseInt(bits.substring(104, 112) + bits.substring(96, 104), 2);
        int staticTime = Integer.parseInt(bits.substring(112, 120), 2);
        int walkTime = Integer.parseInt(bits.substring(120, 128), 2);
        int steps = Integer.parseInt(bits.substring(136, 144) + bits.substring(128, 136), 2);
        regularData.setTimestamp(timestamp);
        regularData.setHrData(heartRate);
        regularData.setSq(sq);
        regularData.setTemperature(temperature);
        regularData.setHumidity(humidity);
        regularData.setPressure(pressure);
        regularData.setStaticTime(staticTime);
        regularData.setWalkTime(walkTime);
        regularData.setSteps(steps);
        return regularData;
    }

    private static long parseTimestamp(String bits, int start) {
        Calendar time = Calendar.getInstance();
        String timeBits = bits.substring(start + 8, start + 16) + bits.substring(start, start + 8) + bits.substring(start + 16,start + 24);
        start = 0;
        time.set(Calendar.MINUTE, Integer.parseInt(timeBits.substring(start, start + 6), 2));
        time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeBits.substring(start + 6, start + 11), 2));
        time.set(Calendar.DAY_OF_MONTH, Integer.parseInt(timeBits.substring(start + 11, start + 16), 2));
        time.set(Calendar.SECOND, Integer.parseInt(timeBits.substring(start + 18, start + 24), 2));
        return time.getTimeInMillis();
    }
    /*private static long parseTimestampWithMinute(String bits, int start) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.HOUR_OF_DAY,0);
        int minutes = Integer.parseInt(bits.substring(start + 8, start + 16) + bits.substring(start, start + 8), 2);
        time.add(Calendar.MINUTE, minutes);
        return time.getTimeInMillis();
    }*/
    private static WearableData parseBondResult(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_DEVICE_BONDING_RESP,2)) {
            return null;
        }
        BondResult bondResult = new BondResult();
        // TODO for work
//        String tokenBit = bits.substring(40, 72);
//        bondResult.setToken(ParserUtil.binStrToString(tokenBit));
        int status = Integer.parseInt(bits.substring(72, 80), 2);
        boolean result = status == 0;
        boolean needToBond = status == 3;
        bondResult.setIsBonded(result);
        bondResult.setNeedToBond(needToBond);
        return bondResult;
    }
    private static WearableData parseUnBondResult(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_DEVICE_UNBONDING_RESP,2)) {
            return null;
        }
        UnBondResult unBondResult = new UnBondResult();
        // TODO for work
//        String tokenBit = bits.substring(40, 72);
//        unBondResult.setToken(ParserUtil.binStrToString(tokenBit));
        int status = Integer.parseInt(bits.substring(72, 80), 2);
        boolean result = status == 0;
        unBondResult.setIsUnBonded(result);
        return unBondResult;
    }
    private static WearableData parseDfuResult(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_DFU_OTA,2)) {
            return null;
        }
        DfuResult dfuResult = new DfuResult();
        int status = Integer.parseInt(bits.substring(40, 44), 2);
        int errorCode = Integer.parseInt(bits.substring(44, 48), 2);
        dfuResult.setStatus(status);
        dfuResult.setErrorCode(errorCode);
        return dfuResult;
    }
    private static WearableData parseBatteryStatus(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_BATTERY_STATUS,2)) {
            return null;
        }
        BatteryStatus batteryStatus = new BatteryStatus();
        long timestamp = parseTimestamp(bits, 40);
        int percent = Integer.parseInt(bits.substring(64, 72), 2);
        batteryStatus.setTimestamp(timestamp);
        batteryStatus.setPercent(percent);
        return batteryStatus;
    }
    private static WearableData parseRssiLevel(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_RSSI_LEVEL,2)) {
            return null;
        }
        RssiLevel rssiLevel = new RssiLevel();
        int level = Integer.parseInt(bits.substring(48, 56) + bits.substring(40, 48), 2);
        rssiLevel.setLevel(level);
        return rssiLevel;
    }
    private static WearableData parseTxPowerLevel(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_TX_POWER_LEVEL,2)) {
            return null;
        }
        TxPowerLevel txPowerLevel = new TxPowerLevel();
        int level = Integer.parseInt(bits.substring(48, 56) + bits.substring(40, 48), 2);
        txPowerLevel.setLevel(level);
        return txPowerLevel;
    }
    private static WearableData parseSystemResetResult(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_SYSTEM_SOFTWARE_RESET,2)) {
            return null;
        }
        SystemResetResult systemResetResult = new SystemResetResult();
        int status = Integer.parseInt(bits.substring(40, 48), 2);
       systemResetResult.setSuccess(status == 0);
        return systemResetResult;
    }
    private static WearableData parseSystemShutdownResult(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_SYSTEM_SOFTWARE_RESET,2)) {
            return null;
        }
        SystemShutdownResult systemShutdownResult = new SystemShutdownResult();
        int status = Integer.parseInt(bits.substring(40, 48), 2);
        systemShutdownResult.setSuccess(status == 0);
        return systemShutdownResult;
    }
    private static WearableData parseDeviceVersionResult(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_FIRMWARE_HARDWARE_VERSION,2)) {
            return null;
        }
        DeviceVersionResult deviceVersionResult = new DeviceVersionResult();
        int fm = Integer.parseInt(bits.substring(40, 48), 2);
        int fc = Integer.parseInt(bits.substring(48, 56), 2);
        int fl = Integer.parseInt(bits.substring(56, 64), 2);
        int hm = Integer.parseInt(bits.substring(64, 72), 2);
        int hc = Integer.parseInt(bits.substring(72, 80), 2);
        int hl = Integer.parseInt(bits.substring(80, 88), 2);
        int ams1 = Integer.parseInt(bits.substring(88, 96), 2);
        int ams2 = Integer.parseInt(bits.substring(96, 104), 2);
        deviceVersionResult.setFirmwareVersion(fm + "." + fc + "." + fl);
        deviceVersionResult.setHardwareVersion(hm + "." + hc + "." + hl);
        deviceVersionResult.setAmsVersion(ams1 + "." + ams2);
        return deviceVersionResult;
    }
    private static WearableData parseDeviceAddress(String bits){
        if (bits.length() < Integer.parseInt(Constants.TRANSPORT_HEAD_LENGTH_DEVICE_ADDRESS,2)) {
            return null;
        }
        DeviceAddress deviceAddress = new DeviceAddress();
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(Integer.parseInt(bits.substring(40, 48), 2))).append(":").
                append(Integer.toHexString(Integer.parseInt(bits.substring(40, 48), 2))).append(":").
                append(Integer.toHexString(Integer.parseInt(bits.substring(48, 56), 2))).append(":").
                append(Integer.toHexString(Integer.parseInt(bits.substring(56, 64), 2))).append(":").
                append(Integer.toHexString(Integer.parseInt(bits.substring(64, 72), 2))).append(":").
                append(Integer.toHexString(Integer.parseInt(bits.substring(72, 80), 2)));
        deviceAddress.setAddress(sb.toString());
        return deviceAddress;
    }
    private static String bytesToBits(byte[] bytes) {
        StringBuilder bits = new StringBuilder();
        for (byte b : bytes) {
            String bs = Integer.toBinaryString(b & 0xFF);
            while (bs.length() < 8) {
                bs = "0" + bs;
            }
            bits.append(bs);
        }
        return bits.toString();
    }

}
