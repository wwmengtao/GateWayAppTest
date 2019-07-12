package com.homecare.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import com.homecare.app.util.ExceptionUtil;

public class GpsService extends Service {

    private static GpsService instance;
    private static final String NULL_VALUE = "*";
    private static final String TAG = GpsService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ExceptionUtil.trackUncaughtException(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public static GpsService getInstance() {
        return instance;
    }

    public String getGps() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                return getGpsResult(location);
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    return getGpsResult(location);
                } else {
                    return NULL_VALUE;
                }
            }
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                return getGpsResult(location);
            } else {
                return NULL_VALUE;
            }
        }
    }

    private String getGpsResult(Location location) {
        return location.getLongitude() + "-" + location.getLatitude();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
