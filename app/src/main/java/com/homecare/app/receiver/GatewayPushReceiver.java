package com.homecare.app.receiver;

import android.content.*;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import com.homecare.app.MainActivity;
import com.homecare.app.model.Constants;
import com.homecare.app.model.ElderlyConfig;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.SGInfo;
import com.homecare.app.model.ble.*;
import com.homecare.app.service.CloudService;
import com.igexin.sdk.PushConsts;
import org.apache.commons.lang3.StringUtils;

public class GatewayPushReceiver extends BroadcastReceiver implements Constants {

    private static final String TAG = GatewayPushReceiver.class.getSimpleName();

    public GatewayPushReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (InfoContainer.infoInvalid()) {
            return;
        }
        String elderlyId = InfoContainer.elderlyId;
        SGInfo sgInfo = InfoContainer.sgInfo;
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传（payload）数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null)
                {
                    String data = new String(payload);
                    String[] info = StringUtils.split(data, ',');
                    int type = Integer.parseInt(info[0]);
                    switch (type) {
                        case PUSH_TYPE_CONFIG: {
                            String[] attributes = StringUtils.split(info[1], '|');
                            if (attributes == null) {
                                return;
                            }
                            ElderlyInfo elderlyInfo = sgInfo.getElderlyInfo();
                            ElderlyConfig elderlyConfig = sgInfo.getElderlyConfig();
                            for (String attributeStr : attributes) {
                                String[] temp = StringUtils.split(attributeStr, ':');
                                if (temp == null) {
                                    continue;
                                }
                                String attribute = temp[0];
                                String newValue = temp[1];
                                if (SG_ATTR_HEIGHT.equals(attribute)) {
                                    elderlyInfo.setHeight(Integer.parseInt(newValue));
                                    MainActivity.sendAckedRequest(elderlyInfo, 3);
                                } else if (SG_ATTR_WEIGHT.equals(attribute)) {
                                    elderlyInfo.setWeight(Integer.parseInt(newValue));
                                    MainActivity.sendAckedRequest(elderlyInfo, 3);
                                } else if (SG_ATTR_HRFAST.equals(attribute)) {
                                    int newHrFast = Integer.parseInt(newValue);
                                    HealthThresholdConfig hrThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_HIGH_HEART_RATE, newHrFast);
                                    MainActivity.sendAckedRequest(hrThresholdConfig, 3);
                                    elderlyConfig.setHrFastThreshold(newHrFast);
                                } else if (SG_ATTR_HRSLOW.equals(attribute)) {
                                    int newHrSlow = Integer.parseInt(newValue);
                                    HealthThresholdConfig hrThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_LOW_HEART_RATE, newHrSlow);
                                    MainActivity.sendAckedRequest(hrThresholdConfig, 3);
                                    elderlyConfig.setHrSlowThreshold(newHrSlow);
                                } else if (SG_ATTR_MONITORINTERVAL.equals(attribute)) {
                                    int newMonitorInterval = Integer.parseInt(newValue);
                                    RegularReportConfig regularReportConfig = new RegularReportConfig(newMonitorInterval, true, true, true, true, true, true, true, true, true, false, true, false, false);
                                    MainActivity.sendAckedRequest(regularReportConfig, 3);
                                    elderlyConfig.setHrCollectInterval(newMonitorInterval);
                                } else if (SG_ATTR_GENDER.equals(attribute)) {
                                    elderlyInfo.setMale(Boolean.parseBoolean(newValue));
                                    MainActivity.sendAckedRequest(elderlyInfo, 3);
                                } else if (SG_ATTR_BIRTHDAY.equals(attribute)) {
                                    elderlyInfo.setBirthday(newValue);
                                    MainActivity.sendAckedRequest(elderlyInfo, 3);
                                }
                            }
                            break;
                        }
                        case PUSH_TYPE_NOTE: {
                            int noteType;
                            try {
                                noteType = Integer.parseInt(info[1]);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, e.getMessage());
                                return;
                            }
                            switch (noteType) {
                                case JOB_DRUG: {
                                    // 用药提醒
                                    break;
                                }
                                case NOTE_CHECK: {
                                    HeartRateRequest heartRateRequest = new HeartRateRequest();
                                    MainActivity.sendAckedRequest(heartRateRequest, 1);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                break;
            //添加其他case
            //.........
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                String cid = bundle.getString("clientid");
                /* 第三方应用需要将ClientID上传到第三方服务器，并且将当前用户帐号和ClientID进行关联，
                以便以后通过用户帐号查找ClientID进行消息推送。有些情况下ClientID可能会发生变化，为保证获取最新的ClientID，
                请应用程序在每次获取ClientID广播后，都能进行一次关联绑定 */
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                if (StringUtils.isNotEmpty(cid) && elderlyId != null) {
                    CloudService.getInstance().registerPushClientId(cid, elderlyId);
                }
                break;
            default:
                break;
        }
    }

}
