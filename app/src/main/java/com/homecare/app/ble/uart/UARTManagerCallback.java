package com.homecare.app.ble.uart;

import com.homecare.app.ble.profile.BleManagerCallback;

public interface UARTManagerCallback extends BleManagerCallback {

    public void onDataReceived(final byte[] data);

}
