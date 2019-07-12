package com.homecare.app.service;

import android.util.Log;
import com.homecare.app.model.*;
import com.homecare.app.model.ble.ElderlyInfo;
import com.homecare.app.model.ble.ExtensionRegularData;
import com.homecare.app.model.ble.HeartRateResult;
import com.homecare.app.model.ble.RegularData;
import com.homecare.app.model.ble.SleepData;
import com.homecare.app.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CloudService implements Constants {

    private static final String HOST = "http://123.57.155.43";
    private static final String cloudHost = HOST + "/cloud/gatewayapp";
    private static final String cloudConfigHost = HOST + "/cloud/config";
    private static final String cloudApkUri = HOST + "/" + APK_FILE_NAME;

    private static final String NULL_VALUE = "*";
    private static final String TAG = CloudService.class.getSimpleName();

    private static final CloudService instance = new CloudService();

    private CloudService() {}

    public static CloudService getInstance() {
        return instance;
    }

    public SGInfo getSGInfo(String elderlyId, int timeout) {
        try {
            SGInfo info = new SGInfo();
            WearableInfo wearableInfo = new WearableInfo();
            info.setWearableInfo(wearableInfo);
            String content = HttpUtil.sendRequest(cloudHost + "/sginfo?eid=" + elderlyId, HttpUtil.GET, timeout);
            if (StringUtils.isNotEmpty(content)) {
                String[] temp = StringUtils.split(content, ',');
                int index = 0;
                String deviceAddress = temp[index++];
                if (!deviceAddress.equals(NULL_VALUE)) {
                    wearableInfo.setDeviceAddress(deviceAddress);
                }
                String deviceName = temp[index++];
                if (!deviceName.equals(NULL_VALUE)) {
                    wearableInfo.setDeviceName(deviceName);
                }
                String firmwareVersion = temp[index++];
                if (!firmwareVersion.equals(NULL_VALUE)) {
                    wearableInfo.setFirmwareVersion(firmwareVersion);
                }
                String amsVersion = temp[index++];
                if (!amsVersion.equals(NULL_VALUE)) {
                    wearableInfo.setAmsVersion(amsVersion);
                }
                String hardwareVersion = temp[index++];
                if (!hardwareVersion.equals(NULL_VALUE)) {
                    wearableInfo.setHardwareVersion(hardwareVersion);
                }
                boolean isMale = Boolean.parseBoolean(temp[index++]);
                String birthday = temp[index++];
                int height = Integer.parseInt(temp[index++]);
                int weight = Integer.parseInt(temp[index++]);
                ElderlyInfo elderlyInfo = new ElderlyInfo(isMale, birthday, height, weight);
                info.setElderlyInfo(elderlyInfo);
                int hrCollectInterval = Integer.parseInt(temp[index++]);
                int hrFastThreshold = Integer.parseInt(temp[index++]);
                int hrSlowThreshold = Integer.parseInt(temp[index++]);
                int sittingThreshold = Integer.parseInt(temp[index++]);
                int lyingThreshold = Integer.parseInt(temp[index++]);
                ElderlyConfig elderlyConfig = new ElderlyConfig(hrCollectInterval, hrFastThreshold, hrSlowThreshold, sittingThreshold, lyingThreshold);
                info.setElderlyConfig(elderlyConfig);
                int lowWearablePowerThreshold = Integer.parseInt(temp[index++]);
                int lowPhonePowerThreshold = Integer.parseInt(temp[index++]);
                wearableInfo.setLowWearablePowerThreshold(lowWearablePowerThreshold);
                wearableInfo.setLowPhonePowerThreshold(lowPhonePowerThreshold);
                String token = temp[index++];
                if (!token.equals(NULL_VALUE)) {
                    info.setToken(token);
                } else {
                    info.setToken("");
                }
                if (temp.length == 18) {
                    elderlyInfo.setName(temp[index]);
                }
            }
            return info;
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to get sensor gateway info by elderlyId: " + elderlyId + ", due to: " + e.getMessage());
            return null;
        }
    }

    public void bondDevice(String elderlyId, String deviceAddress) {
        try {
            HttpUtil.sendRequest(cloudHost + "/bonddevice?eid=" + elderlyId + "&daddr=" + deviceAddress, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to bond device by elderlyId: " + elderlyId + " and deviceAddress: " + deviceAddress + ", due to: " + e.getMessage());
        }
    }

    public void unbondDevice(String elderlyId) {
        try {
            HttpUtil.sendRequest(cloudHost + "/unbonddevice?eid=" + elderlyId, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to bond device by elderlyId: " + elderlyId + ", due to: " + e.getMessage());
        }
    }

    private ExecutorService dataUploader = Executors.newSingleThreadExecutor();
    public void uploadRegularData(final String elderlyId, final RegularData data) {
        dataUploader.execute(new Runnable() {
            @Override
            public void run() {
                upload(cloudHost + "/regulardata?eid=" + elderlyId, elderlyId, data.toCloudMessage(), 2, SdService.REGULAR_DATA);
            }
        });
    }

    public void uploadExtensionRegularData(final String elderlyId, final ExtensionRegularData data) {
        final String cloudMessage = data.toCloudMessage();
        if (cloudMessage == null) {
            return;
        }
        dataUploader.execute(new Runnable() {
            @Override
            public void run() {
                upload(cloudHost + "/motiondata?eid=" + elderlyId, elderlyId, cloudMessage, 2, SdService.MOTION_DATA);
            }
        });
    }

    public SleepData uploadSleepData(final String elderlyId, final SleepData data) throws Exception {
        Future<SleepData> result = dataUploader.submit(new Callable<SleepData>() {
            @Override
            public SleepData call() {
                String result = upload(cloudHost + "/sleepdata?eid=" + elderlyId, elderlyId, data.toCloudMessage(), 2, SdService.SLEEP_DATA);
                if (StringUtils.isNotEmpty(result)) {
                    data.setQualityDesc(result);
                }
                return data;
            }
        });
        return result.get();
    }

    public void uploadHeartRateResult(final String elderlyId, final HeartRateResult heartRateResult) {
        dataUploader.execute(new Runnable() {
            @Override
            public void run() {
                upload(cloudHost + "/hrdata?eid=" + elderlyId, elderlyId, heartRateResult.toCloudMessage(), 2, -1);
            }
        });
    }

    private ExecutorService noteUploader = Executors.newSingleThreadExecutor();
    private ExecutorService alertUploader = Executors.newSingleThreadExecutor();
    private long lastWearableDisconnectTs;
    public void uploadMessage(final String elderlyId, final Message message) {
        // TODO for work
        if (message.getType() == ALERT_WEARABLE_DISCONNECT) {
            long currentTime = System.currentTimeMillis();
            if (lastWearableDisconnectTs > 0 && lastWearableDisconnectTs > currentTime - 5 * 60 * 1000) {
                return;
            }
            lastWearableDisconnectTs = currentTime;
        }
        final String cloudUrl = cloudHost + "/message?eid=" + elderlyId;
        final String content = message.toCloudMessage();
        if (message.isAlert()) {
            alertUploader.execute(new Runnable() {
                @Override
                public void run() {
                    upload(cloudUrl, elderlyId, content, 2, SdService.ALERT);
                }
            });
        } else {
            noteUploader.execute(new Runnable() {
                @Override
                public void run() {
                    upload(cloudUrl, elderlyId, content, 2, SdService.NOTE);
                }
            });
        }
    }

    public void uploadFromLocalStorage(final String elderlyId) {
        alertUploader.execute(new Runnable() {
            @Override
            public void run() {
                upload(cloudHost + "/message?eid=" + elderlyId, elderlyId, null, 1, SdService.ALERT);
            }
        });
        noteUploader.execute(new Runnable() {
            @Override
            public void run() {
                upload(cloudHost + "/message?eid=" + elderlyId, elderlyId, null, 1, SdService.NOTE);
            }
        });
        dataUploader.execute(new Runnable() {
            @Override
            public void run() {
                upload(cloudHost + "/sleepdata?eid=" + elderlyId, elderlyId, null, 1, SdService.SLEEP_DATA);
                upload(cloudHost + "/regulardata?eid=" + elderlyId, elderlyId, null, 1, SdService.REGULAR_DATA);
                upload(cloudHost + "/motiondata?eid=" + elderlyId, elderlyId, null, 1, SdService.MOTION_DATA);
            }
        });
    }

    private String upload(String cloudUrl, String elderlyId, String newContent, int maxTryTimes, int localTarget) {
        String result = null;
        List<String> localContentList = SdService.getInstance().getLocalContentList(localTarget);
        boolean hasLocalContent = localContentList.size() > 0;
        if (newContent != null) {
            localContentList.add(newContent);
        }
        try {
            while (localContentList.size() > 0) {
                String content = localContentList.get(0);
                int tryTimes = 0;
                while (tryTimes < maxTryTimes) {
                    try {
                        result = HttpUtil.sendRequest(cloudUrl, HttpUtil.POST, content);
                        break;
                    } catch (IOException e) {
                        tryTimes++;
                        if (tryTimes == maxTryTimes) {
                            String errMsg = "Failed to upload the content to url: " + cloudUrl + " for elderlyId: " + elderlyId + ", content: " + content + ", due to: " + e.getMessage();
                            throw new IOException(errMsg);
                        }
                    }
                }
                localContentList.remove(0);
            }
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        SdService.getInstance().storeContentListToLocal(localContentList, localTarget);
        if (hasLocalContent && localContentList.isEmpty()) {
            Message msg = Message.toNoteMessage(System.currentTimeMillis(), Constants.NOTE_GATEWAYAPP_CONNECTION_RECOVERED, "*");
            try {
                HttpUtil.sendRequest(cloudHost + "/message?eid=" + elderlyId, HttpUtil.POST, msg.toCloudMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return result;
    }

    public String getLastWearableFirmwareVersion(String hardwareVersion) {
        String lastVersion = null;
        try {
            lastVersion = HttpUtil.sendRequest(cloudConfigHost + "/firmwareversion?hv=" + hardwareVersion, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to get last wearable firmware version by hardwareVersion: " + hardwareVersion + ", due to: " + e.getMessage());
        }
        return lastVersion;
    }

    public String getLastMobileAppVersion() {
        String lastVersion = null;
        try {
            lastVersion = HttpUtil.sendRequest(cloudConfigHost + "/gatewayappversion", HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to get last gateway app version" + ", due to: " + e.getMessage());
        }
        return lastVersion;
    }

    public void updateSGInfo(String elderlyId, String attribute, Object newValue) {
        try {
            HttpUtil.sendRequest(cloudHost + "/sginfo?eid=" + elderlyId, HttpUtil.POST, attribute + ":" + newValue);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to update sensor gateway info : " + attribute + "," + newValue + ", due to: " + e.getMessage());
        }
    }

    public void registerPushClientId(String clientId, String elderlyId) {
        try {
            HttpUtil.sendRequest(cloudHost + "/registerpush?pcid=" + clientId + "&eid=" + elderlyId, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to register push clientId: " + clientId + " and elderlyId: " + elderlyId + ", due to: " + e.getMessage());
        }
    }

    public String generateToken(String elderlyId) {
        String token = null;
        try {
            token = HttpUtil.sendRequest(cloudHost + "/gentoken?eid=" + elderlyId, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to generate token for elderlyId: " + elderlyId + ", due to: " + e.getMessage());
        }
        return token;
    }
    public boolean sendHeartbeat(String elderlyId) {
        try {
            HttpUtil.sendRequest(cloudHost + "/heartbeat?eid=" + elderlyId + "&ts=" + System.currentTimeMillis(), HttpUtil.GET);
            return true;
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to send heartbeat for elderlyId: " + elderlyId + ", due to: " + e.getMessage());
            return false;
        }
    }

    public boolean sendHeartbeat(String elderlyId, int timeout) {
        try {
            HttpUtil.sendRequest(cloudHost + "/heartbeat?eid=" + elderlyId + "&ts=" + System.currentTimeMillis(), HttpUtil.GET, timeout);
            return true;
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to send heartbeat for elderlyId: " + elderlyId + ", due to: " + e.getMessage());
            return false;
        }
    }

    public void updateDeviceVersion(String elderlyId, String firmwareVersion, String amsVersion, String hardwareVersion) {
        try {
            HttpUtil.sendRequest(cloudHost + "/deviceversion?eid=" + elderlyId + "&fv=" + firmwareVersion + "&av=" + amsVersion + "&hv=" + hardwareVersion, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to upload device version by elderlyId: " + elderlyId + ", firmwareVersion: " + firmwareVersion + ", amsVersion: " + amsVersion + ", hardwareVersion: " + hardwareVersion + ", due to: " + e.getMessage());
        }
    }

    public void updateBatteryStatus(String elderlyId, long timestamp, int percent) {
        try {
            HttpUtil.sendRequest(cloudHost + "/battery?eid=" + elderlyId + "&ts=" + timestamp + "&p=" + percent, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to upload battery status by elderlyId: " + elderlyId + ", timestamp: " + timestamp + ", percent: " + percent + ", due to: " + e.getMessage());
        }
    }

    public boolean downloadApk(File localFile) {
        boolean download = true;
        try {
            HttpUtil.download(cloudApkUri, localFile);
        } catch (IOException e) {
            Log.e(TAG, "Failed to download apk due to: " + e.getMessage());
            download = false;
        }
        return download;
    }

    public boolean downloadFirmware(String hardwareVersion, File localFile) {
        boolean download = true;
        try {
            HttpUtil.download(HOST + "/" + hardwareVersion + "_" + FIRMWARE_FILE_NAME, localFile);
        } catch (IOException e) {
            Log.e(TAG, "Failed to download firmware due to: " + e.getMessage());
            download = false;
        }
        return download;
    }

    public String validateLogin(String account, String password, String phoneId) {
        try {
            if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
                return "";
            }
            return HttpUtil.sendRequest(cloudHost + "/login?account=" + account + "&password=" + password + "&phoneid=" + phoneId, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to validate login by account: " + account + ", password: " + password + ", phoneId: " + phoneId + ", due to: " + e.getMessage());
            return null;
        }
    }

    public void logout(String elderlyId) {
        try {
            HttpUtil.sendRequest(cloudHost + "/logout?account=" + elderlyId, HttpUtil.GET);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to logout by elderlyId: " + elderlyId + ", due to: " + e.getMessage());
        }
    }

    public Boolean validateMobile(String mobile) {
        try {
            String content = HttpUtil.sendRequest(cloudHost + "/validate?eid=" + mobile, HttpUtil.GET);
            return StringUtils.isEmpty(content);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to validate by mobile: " + mobile + ", due to: " + e.getMessage());
            return null;
        }
    }

    public Boolean register(RegisterInfo registerInfo) {
        try {
            String content = HttpUtil.sendRequest(cloudHost + "/register", HttpUtil.POST, registerInfo.toCloudMessage());
            return StringUtils.isNotEmpty(content);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to register: " + registerInfo.toCloudMessage() + ", due to: " + e.getMessage());
            return null;
        }
    }

}
