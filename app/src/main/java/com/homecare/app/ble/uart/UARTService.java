package com.homecare.app.ble.uart;

import android.app.NotificationManager;
import android.os.*;
import android.util.Log;
import com.homecare.app.MainActivity;
import com.homecare.app.ble.profile.BleManager;
import com.homecare.app.ble.profile.BleProfileService;
import com.homecare.app.ble.scanner.DeviceListAdapter;
import com.homecare.app.ble.util.ParserUtil;
import com.homecare.app.model.*;
import com.homecare.app.model.Message;
import com.homecare.app.model.ble.*;
import com.homecare.app.service.CloudService;
import com.homecare.app.service.GpsService;
import com.homecare.app.service.SdService;
import com.homecare.app.util.TimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UARTService extends BleProfileService implements UARTManagerCallback, Constants {
    private final static int NOTIFICATION_ID = 349; // random

    private static final String TAG = UARTService.class.getSimpleName();

    private UARTManager mManager;

    private static ExecutorService ackReqSender = Executors.newSingleThreadExecutor();
    private static ExecutorService ackRespSender = Executors.newSingleThreadExecutor();
//    private static ExecutorService respHandler = Executors.newSingleThreadExecutor();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final String[] sequences = new String[]{"000", "001", "010", "011","100","101","110","111"};
    private static AtomicInteger index = new AtomicInteger(0);
    private String receivedSequence = "";
    private Map<String, String> dataMap = new ConcurrentHashMap<>();

    private final LocalBinder mBinder = new UARTBinder();

    public class UARTBinder extends LocalBinder implements UARTInterface {
        @Override
        public void send(final byte[] bytes) {
            mManager.send(bytes);
        }

        @Override
        public void sendAckedRequest(ExchangeInfo request, int maxTryTimes) {
            request.setSequence(getSequence());
            ackReqSender.execute(new AckRequestTask(request, maxTryTimes));
        }
    }

    @Override
    protected LocalBinder getBinder() {
        return mBinder;
    }

    @Override
    protected BleManager<UARTManagerCallback> initializeManager() {
        return mManager = new UARTManager(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private class AckRequestTask implements Runnable {
        private ExchangeInfo exchangeInfo;
        private int maxTryTimes;
        private boolean needToSleep = false;
        public AckRequestTask(ExchangeInfo exchangeInfo, int maxTryTimes) {
            this.exchangeInfo = exchangeInfo;
            this.maxTryTimes = maxTryTimes;
        }
        public AckRequestTask(ExchangeInfo exchangeInfo, int maxTryTimes, boolean needToSleep) {
            this(exchangeInfo, maxTryTimes);
            this.needToSleep = needToSleep;
        }
        @Override
        public void run() {
            if (needToSleep) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            while (maxTryTimes-- > 0) {
                try {
                    mManager.send(exchangeInfo.toBytes());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(System.currentTimeMillis()) + " send() error: " + e.getMessage(), SdService.RAW_DATA_LOG);
                }
                synchronized (UARTService.class) {
                    try {
                        UARTService.class.wait(5000);
                        if (exchangeInfo.getSequence().equals(receivedSequence)) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
    }

    private class AckResponseTask implements Runnable {
        private ExchangeInfo exchangeInfo;
        public AckResponseTask(ExchangeInfo exchangeInfo) {
            this.exchangeInfo = exchangeInfo;
        }
        @Override
        public void run() {
            mManager.send(exchangeInfo.toBytes());
        }
    }

    @Override
    public void onDestroy() {
        // when user has disconnected from the sensor, we have to cancel the notification that we've created some milliseconds before using unbindService
        cancelNotification();
        super.onDestroy();
    }

    @Override
    protected void onRebind() {
        // when the activity rebinds to the service, remove the notification
        cancelNotification();
    }

    @Override
    protected void onUnbind() {
        // when the activity closes we need to show the notification that user is connected to the sensor
//        createNotification(R.string.uart_notification_connected_message, 0);
    }

    @Override
    protected void onServiceStarted() {
    }

    /**
     * Cancels the existing notification. If there is no active notification this method does nothing
     */
    private void cancelNotification() {
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onDeviceDisconnected() {
        super.onDeviceDisconnected();
        updateWearableView(false);
    }

    @Override
    public void onLinklossOccur() {
        super.onLinklossOccur();
        Log.e(TAG, "BLE link is lost.");
        SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(System.currentTimeMillis()) + ": BLE link is lost.", SdService.RAW_DATA_LOG);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                if (!InfoContainer.infoInvalid() && !MainActivity.isBLEConnected()) {
                    uploadMessage(ALERT_WEARABLE_DISCONNECT, System.currentTimeMillis(), InfoContainer.elderlyId, "*");
                    DeviceListAdapter.updateDeviceStatus(mDeviceAddress, true);
                    updateWearableView(false);
                    InfoContainer.sgInfo.getWearableInfo().setDisconnect(true);
                }
            }
        }, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDeviceConnected() {
        super.onDeviceConnected();
    }

    @Override
    public void onServicesDiscovered(boolean optionalServicesFound) {
        super.onServicesDiscovered(optionalServicesFound);
        if (InfoContainer.infoInvalid()) {
            return;
        }
        // TODO for work
//        String bondToken = CloudService.getInstance().generateBondToken(InfoContainer.elderlyId);
//        InfoContainer.sgInfo.setBondToken(bondToken);
        SGInfo sgInfo = InfoContainer.sgInfo;
        if (sgInfo.getWearableInfo().getDeviceAddress() == null) {
            BondRequest request = new BondRequest(sgInfo.getBondToken());
            request.setSequence(getSequence());
            ackReqSender.execute(new AckRequestTask(request, 5, true));
        }
    }

    private void onCustomizedConnected(SGInfo sgInfo, boolean needToSleep) {
        WearableInfo wearableInfo = sgInfo.getWearableInfo();
        BatteryStatusRequest batteryStatusRequest = new BatteryStatusRequest();
        batteryStatusRequest.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(batteryStatusRequest, 3, needToSleep));
        DeviceVersionRequest deviceVersionRequest = new DeviceVersionRequest();
        deviceVersionRequest.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(deviceVersionRequest, 3));
//        if (wearableInfo.isDisconnect()) {
            uploadMessage(NOTE_WEARABLE_NORMAL, System.currentTimeMillis(), InfoContainer.elderlyId, "*");
            wearableInfo.setDisconnect(false);
//        }
        //set wall clock
        WallClockConfig wallClockConfig = new WallClockConfig();
        wallClockConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(wallClockConfig, 3));
        sgInfo.setLastWallClockTs(System.currentTimeMillis());
        DeviceListAdapter.updateDeviceStatus(mDeviceAddress, false);
        updateWearableView(true);
    }

    private String getSequence() {
        return sequences[index.getAndIncrement() % sequences.length];
    }

    private void updateWearableView(Object obj) {
        android.os.Message msg = new android.os.Message();
        msg.obj = obj;
        Handler handler = MainActivity.wearableViewHandler;
        msg.setTarget(handler);
        handler.sendMessage(msg);
    }

    private void onCustomizedBonded() {
        SGInfo sgInfo = InfoContainer.sgInfo;
        ElderlyInfo elderlyInfo = sgInfo.getElderlyInfo();
        ElderlyConfig elderlyConfig = sgInfo.getElderlyConfig();

        //set elderly info
        elderlyInfo.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(elderlyInfo, 3));

        //feature configuration
        FeatureConfig featureConfig = new FeatureConfig(false, true, true, false, false, false);
        featureConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(featureConfig, 3));
        //regular report configuration
        RegularReportConfig regularReportConfig = new RegularReportConfig(sgInfo.getMonitorInterval(), true, true, true, true, true, true, true, true, true, false, true, false, false);
        regularReportConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(regularReportConfig, 3));

        //device threshold configuration
        DeviceReportThresholdConfig deviceReportThresholdConfig = new DeviceReportThresholdConfig(sgInfo.getWearableInfo().getLowWearablePowerThreshold(), 0);
        deviceReportThresholdConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(deviceReportThresholdConfig, 3));
        //device event configuration
        DeviceEventConfig deviceEventConfig = new DeviceEventConfig(false, true);
        deviceEventConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(deviceEventConfig, 3));

        //health threshold configuration
        HealthThresholdConfig fastHrThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_HIGH_HEART_RATE, elderlyConfig.getHrFastThreshold());
        fastHrThresholdConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(fastHrThresholdConfig, 3));
        HealthThresholdConfig slowHrThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_LOW_HEART_RATE, elderlyConfig.getHrSlowThreshold());
        slowHrThresholdConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(slowHrThresholdConfig, 3));
//        HealthThresholdConfig sitThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_LONG_TIME_SIT, elderlyConfig.getSittingThreshold());
//        sitThresholdConfig.setSequence(getSequence());
//        ackReqSender.execute(new AckRequestTask(sitThresholdConfig, 3));
        //health event configuration
        HealthEventConfig healthEventConfig = new HealthEventConfig(true, false);
        healthEventConfig.setSequence(getSequence());
        ackReqSender.execute(new AckRequestTask(healthEventConfig, 3));
    }

    private void onCustomizedUnBonded() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                mManager.disconnect();
            }
        }, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onDataReceived(final byte[] data) {
//        respHandler.execute(new Runnable() {
//            @Override
//            public void run() {
                String elderlyId = InfoContainer.elderlyId;
                SGInfo sgInfo = InfoContainer.sgInfo;
                if (InfoContainer.infoInvalid()) {
                    SdService.getInstance().appendContentToLocal("sg info is invalid.", SdService.RAW_DATA_LOG);
                    return;
                }
                WearableInfo wearableInfo = sgInfo.getWearableInfo();
                try {
                    WearableData wearableData = UARTParser.parseWearableData(data);
                    if (wearableData == null) {
                        return;
                    }
                    receivedSequence = wearableData.getSequence();
                    if (wearableData.isAck()) {
                        synchronized (UARTService.class) {
                            UARTService.class.notifyAll();
                        }
                        return;
                    }
                    if (wearableData.isNeedAck()) {
                        ackRespSender.execute(new AckResponseTask(new ACKRequest(wearableData.getSequence())));
                    }
                    if (wearableData instanceof RegularData) {
                        RegularData regularData = (RegularData) wearableData;
                        if (isDuplicatedFromWristband("RD", data)) {
                            return;
                        }
                        CloudService.getInstance().uploadRegularData(elderlyId, regularData);
                        HeartRateResult heartRateResult = new HeartRateResult();
                        heartRateResult.setHearRate(regularData.getHrData());
                        heartRateResult.setSq(regularData.getSq());
                        heartRateResult.setTimestamp(regularData.getTimestamp());
                        if (needToUpdateHrView(regularData.getTimestamp(), sgInfo.getMonitorInterval())) {
                            updateWearableView(heartRateResult);
                        }
                        SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(regularData.getTimestamp()) + ": heart rate: " + regularData.getHrData() + "," + regularData.getSq(), SdService.REGULAR_RESULT);
                    } else if (wearableData instanceof ExtensionRegularData) {
                        ExtensionRegularData extensionRegularData = (ExtensionRegularData) wearableData;
                        if (isDuplicatedFromWristband("ERD", data)) {
                            return;
                        }
                        CloudService.getInstance().uploadExtensionRegularData(elderlyId, extensionRegularData);
                        String message = extensionRegularData.toCloudMessage();
                        if (StringUtils.isNotEmpty(message)) {
                            message = StringUtils.substringAfter(message, ",");
                            SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(extensionRegularData.getTimestamp()) + ": motion: " + message, SdService.REGULAR_RESULT);
                        }
                    } else if (wearableData instanceof SleepData) {
                        SleepData sleepData = (SleepData) wearableData;
                        if (isDuplicatedFromWristband("SD", data)) {
                            return;
                        }
                        CloudService.getInstance().uploadSleepData(elderlyId, sleepData);
                    } else if (wearableData instanceof HealthEventNotification) {
                        HealthEventNotification healthEvent = (HealthEventNotification) wearableData;
                        if (isDuplicatedFromWristband("HE:" + healthEvent.getType(), data)) {
                            return;
                        }
                        uploadMessage(healthEvent.getType(), healthEvent.getTimestamp(), elderlyId, "*");
                    } else if (wearableData instanceof DeviceEventNotification) {
                        DeviceEventNotification deviceEvent = (DeviceEventNotification) wearableData;
                        if (isDuplicatedFromWristband("DE:" + deviceEvent.getType(), data)) {
                            return;
                        }
                        uploadMessage(deviceEvent.getType(), deviceEvent.getTimestamp(), elderlyId, deviceEvent.getEventValue() + "");
                    } else if (wearableData instanceof BondResult) {
                        BondResult bondResult = (BondResult) wearableData;
                        if (bondResult.isNeedToBond()) {
                            BondRequest request = new BondRequest(sgInfo.getBondToken());
                            request.setSequence(getSequence());
                            ackReqSender.execute(new AckRequestTask(request, 5, true));
                        } else {
                            if (bondResult.isBonded()) {
                                onCustomizedBonded();
                            }
                            if (wearableInfo.getDeviceAddress() == null) {
                                CloudService.getInstance().bondDevice(elderlyId, mDeviceAddress);
                                wearableInfo.setDeviceAddress(mDeviceAddress);
                            }
                            onCustomizedConnected(sgInfo, false);
                        }
                    } else if (wearableData instanceof UnBondResult) {
                        UnBondResult unBondResult = (UnBondResult) wearableData;
                        if (unBondResult.isUnBonded()) {
                            onCustomizedUnBonded();
                        }
                    } else if (wearableData instanceof DeviceVersionResult) {
                        DeviceVersionResult deviceVersionResult = (DeviceVersionResult) wearableData;
                        String firmwareVersion = deviceVersionResult.getFirmwareVersion();
                        String amsVersion = deviceVersionResult.getAmsVersion();
                        String hardwareVersion = deviceVersionResult.getHardwareVersion();
                        if (!firmwareVersion.equals(wearableInfo.getFirmwareVersion()) || !amsVersion.equals(wearableInfo.getAmsVersion())) {
                            CloudService.getInstance().updateDeviceVersion(elderlyId, firmwareVersion, amsVersion, hardwareVersion);
                            wearableInfo.setFirmwareVersion(firmwareVersion);
                            wearableInfo.setAmsVersion(amsVersion);
                        }
                    } else if (wearableData instanceof HeartRateResult) {
                        HeartRateResult heartRateResult = (HeartRateResult) wearableData;
                        if (isDuplicatedFromWristband("HRR", data)) {
                            return;
                        }
                        CloudService.getInstance().uploadHeartRateResult(elderlyId, heartRateResult);
                        if (needToUpdateHrView(heartRateResult.getTimestamp(), sgInfo.getMonitorInterval())) {
                            updateWearableView(heartRateResult);
                        }
                    } else if (wearableData instanceof DfuResult) {
                        DfuResult dfuResult = (DfuResult) wearableData;
                        if (dfuResult.getStatus() != DfuResult.DFU_STATUS_SUCCESS) {
                            sgInfo.setDfuFailed(true);
                        }
                    } else if (wearableData instanceof BatteryStatus) {
                        BatteryStatus batteryStatus = (BatteryStatus) wearableData;
                        if (isDuplicatedFromWristband("BS", data)) {
                            return;
                        }
                        updateWearableView(batteryStatus);
                        CloudService.getInstance().updateBatteryStatus(elderlyId, batteryStatus.getTimestamp(), batteryStatus.getPercent());
                        SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(System.currentTimeMillis()) + ": " + batteryStatus.getPercent(), SdService.BATTERY);
                    } else if (wearableData instanceof WallClockConfigRequest) {
                        WallClockConfig wallClockConfig = new WallClockConfig();
                        wallClockConfig.setSequence(getSequence());
                        ackReqSender.execute(new AckRequestTask(wallClockConfig, 3));
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    SdService.getInstance().appendContentToLocal(TimeUtil.formatTimestamp(System.currentTimeMillis()) + " onDataReceived() error: " + e.getMessage(), SdService.RAW_DATA_LOG);
                }
//            }
//        });
    }

    private boolean isDuplicatedFromWristband(String type, byte[] data) {
        String rawData = ParserUtil.parse(data);
        rawData = StringUtils.substring(rawData, 19);
        String cachedRawData = dataMap.get(type);
        if (rawData.equals(cachedRawData)) {
            return true;
        } else {
            dataMap.put(type, rawData);
            return false;
        }
    }

    private boolean needToUpdateHrView(long dataTs, int monitorInterval) {
        return dataTs > System.currentTimeMillis() - monitorInterval * 60 * 1000;
    }

    private void uploadMessage(int type, long timestamp, String elderlyId, String detail) {
        if (type > 0) {
            Message message;
            if (type < ALERT_NOTE_DELIMITER) {
                message = Message.toAlertMessage(timestamp, type, GpsService.getInstance().getGps());
            } else {
                message = Message.toNoteMessage(timestamp, type, detail);
            }
            CloudService.getInstance().uploadMessage(elderlyId, message);
        }
    }

}
