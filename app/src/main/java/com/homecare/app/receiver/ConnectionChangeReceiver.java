package com.homecare.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.homecare.app.MainActivity;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.service.CloudService;

public class ConnectionChangeReceiver extends BroadcastReceiver {

    public ConnectionChangeReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        if (InfoContainer.infoInvalid()) {
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            updateCloudView(false);
        } else {
            updateCloudView(true);
            CloudService.getInstance().uploadFromLocalStorage(InfoContainer.elderlyId);
        }
    }

    private void updateCloudView(boolean connected) {
        android.os.Message msg = new android.os.Message();
        msg.obj = connected;
        Handler handler = MainActivity.cloudViewHandler;
        msg.setTarget(handler);
        handler.sendMessage(msg);
    }

}
