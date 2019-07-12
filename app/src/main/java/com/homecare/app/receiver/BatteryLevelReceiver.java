package com.homecare.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import com.homecare.app.model.Constants;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.Message;
import com.homecare.app.model.SGInfo;
import com.homecare.app.service.CloudService;
import com.homecare.app.service.GpsService;

public class BatteryLevelReceiver extends BroadcastReceiver implements Constants {

    private boolean upload = false;

    public BatteryLevelReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        if (InfoContainer.infoInvalid()) {
            return;
        }
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
        int batteryPct = level * 100 / scale;
        if (batteryPct > 0) {
            String elderlyId = InfoContainer.elderlyId;
            SGInfo sgInfo = InfoContainer.sgInfo;
            sgInfo.setPhoneBattery(batteryPct);
            int lowPowerThreshold = sgInfo.getWearableInfo().getLowPhonePowerThreshold();
            if (batteryPct == lowPowerThreshold || batteryPct == lowPowerThreshold / 2) {
                if (!upload) {
                    CloudService.getInstance().uploadMessage(elderlyId, Message.toNoteMessage(System.currentTimeMillis(), NOTE_PHONE_LOW_POWER, batteryPct + ""));
                    upload = true;
                }
            } else if (batteryPct == 5) {
                if (!upload) {
                    CloudService.getInstance().uploadMessage(elderlyId, Message.toAlertMessage(System.currentTimeMillis(), ALERT_PHONE_LOW_POWER, GpsService.getInstance().getGps()));
                    upload = true;
                }
            } else {
                upload = false;
            }
        }
    }

}
