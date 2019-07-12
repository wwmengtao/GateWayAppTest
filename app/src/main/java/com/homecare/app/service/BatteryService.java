package com.homecare.app.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import com.homecare.app.receiver.BatteryLevelReceiver;
import com.homecare.app.util.ExceptionUtil;

public class BatteryService extends Service {

    private BatteryLevelReceiver receiver;
    private static BatteryService instance;
    private static final String TAG = BatteryService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        this.receiver = new BatteryLevelReceiver();
        getApplicationContext().registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        instance = this;
        ExceptionUtil.trackUncaughtException(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        this.receiver = null;
        instance = null;
    }

    public static BatteryService getInstance() {
        return instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
