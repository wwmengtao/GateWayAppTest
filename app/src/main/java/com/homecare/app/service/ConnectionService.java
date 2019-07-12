package com.homecare.app.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.homecare.app.receiver.ConnectionChangeReceiver;
import com.homecare.app.util.ExceptionUtil;

public class ConnectionService extends Service {

    private ConnectionChangeReceiver receiver;
    private static ConnectionService instance;
    private static final String TAG = ConnectionService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        this.receiver = new ConnectionChangeReceiver();
        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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

    public static ConnectionService getInstance() {
        return instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
