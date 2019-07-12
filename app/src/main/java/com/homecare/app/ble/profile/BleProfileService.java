package com.homecare.app.ble.profile;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public abstract class BleProfileService extends Service implements BleManagerCallback {

    private static final String TAG = "BleProfileService";

    /** The parameter passed when creating the service. Must contain the address of the sensor that we want to connect to */
    public static final String EXTRA_DEVICE_ADDRESS = "no.nordicsemi.android.nrftoolbox.EXTRA_DEVICE_ADDRESS";

    private BleManager<BleManagerCallback> mBleManager;

    protected boolean mBinded;
    private boolean mActivityFinished;
    private boolean mConnected;
    protected String mDeviceAddress;

    public class LocalBinder extends Binder {
        public final boolean isConnected() {
            return mBleManager != null && mBleManager.isConnected();
        }
        public final void disconnect() {
            if (mBleManager != null) {
                mBleManager.disconnect();
            }
        }
    }

    /**
     * Returns the binder implementation. This must return class implementing the additional manager interface that may be used in the binded activity.
     *
     * @return the service binder
     */
    protected LocalBinder getBinder() {
        // default implementation returns the basic binder. You can overwrite the LocalBinder with your own, wider implementation
        return new LocalBinder();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        mBinded = true;
        return getBinder();
    }

    @Override
    public final void onRebind(final Intent intent) {
        mBinded = true;

        if (mActivityFinished)
            onRebind();

        if (mActivityFinished && mConnected) {
            mActivityFinished = false;
            // This method will read the Battery Level value, if possible and then try to enable battery notifications (if it has NOTIFY property).
            // If the Battery Level characteristic has only the NOTIFY property, it will only try to enable notifications.
            mBleManager.readBatteryLevel();
        }
    }

    /**
     * Called when the activity has rebinded to the service after being recreated. This method is not called when the activity was killed and recreated just to change the phone orientation.
     */
    protected void onRebind() {
        // empty
    }

    @Override
    public final boolean onUnbind(final Intent intent) {
        mBinded = false;

        if (mActivityFinished)
            onUnbind();

        // When we are connected, but the application is not open, we are not really interested in battery level notifications. But we will still be receiving other values, if enabled.
        if (mActivityFinished && mConnected)
            mBleManager.setBatteryNotifications(false);

        // we must allow to rebind to the same service
        return true;
    }

    /**
     * Called when the activity has unbinded from the service before being finished. This method is not called when the activity is killed to be recreated just to change the phone orientation.
     */
    protected void onUnbind() {
        // empty
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the manager
        mBleManager = initializeManager();
        mBleManager.setGattCallbacks(this);
    }

    @SuppressWarnings("rawtypes")
    protected abstract BleManager initializeManager();

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent == null || !intent.hasExtra(EXTRA_DEVICE_ADDRESS))
            throw new UnsupportedOperationException("No device address at EXTRA_DEVICE_ADDRESS key");

        mDeviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);

        Log.i(TAG, "Service started");

        // notify user about changing the state to CONNECTING
//        final Intent broadcast = new Intent(BROADCAST_CONNECTION_STATE);
//        broadcast.putExtra(EXTRA_CONNECTION_STATE, STATE_CONNECTING);
//        LocalBroadcastManager.getInstance(BleProfileService.this).sendBroadcast(broadcast);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        final BluetoothDevice device = adapter.getRemoteDevice(mDeviceAddress);
        onServiceStarted();

        mBleManager.connect(device);
        return START_REDELIVER_INTENT;
    }

    /**
     * Called when the service has been started. The device name and address are set. It nRF Logger is installed than logger was also initialized.
     */
    protected void onServiceStarted() {
        // empty default implementation
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // shutdown the manager
        mBleManager.close();
        Log.i(TAG, "Service destroyed");
        mBleManager = null;
        mDeviceAddress = null;
        mConnected = false;
    }

    @Override
    public void onDeviceConnected() {
        mConnected = true;

//        final Intent broadcast = new Intent(BROADCAST_CONNECTION_STATE);
//        broadcast.putExtra(EXTRA_CONNECTION_STATE, STATE_CONNECTED);
//        broadcast.putExtra(EXTRA_DEVICE_ADDRESS, mDeviceAddress);
//        broadcast.putExtra(EXTRA_DEVICE_NAME, mDeviceName);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onDeviceDisconnecting() {
        // do nothing
    }

    @Override
    public void onDeviceDisconnected() {
        mConnected = false;
        mDeviceAddress = null;

//        final Intent broadcast = new Intent(BROADCAST_CONNECTION_STATE);
//        broadcast.putExtra(EXTRA_CONNECTION_STATE, STATE_DISCONNECTED);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

        // user requested disconnection. We must stop the service
        Log.i(TAG, "Stopping service...");
        stopSelf();
    }

    @Override
    public void onLinklossOccur() {
        mConnected = false;

//        final Intent broadcast = new Intent(BROADCAST_CONNECTION_STATE);
//        broadcast.putExtra(EXTRA_CONNECTION_STATE, STATE_LINK_LOSS);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onServicesDiscovered(final boolean optionalServicesFound) {
//        final Intent broadcast = new Intent(BROADCAST_SERVICES_DISCOVERED);
//        broadcast.putExtra(EXTRA_SERVICE_PRIMARY, true);
//        broadcast.putExtra(EXTRA_SERVICE_SECONDARY, optionalServicesFound);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onDeviceReady() {
//        final Intent broadcast = new Intent(BROADCAST_DEVICE_READY);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onDeviceNotSupported() {
//        final Intent broadcast = new Intent(BROADCAST_SERVICES_DISCOVERED);
//        broadcast.putExtra(EXTRA_SERVICE_PRIMARY, false);
//        broadcast.putExtra(EXTRA_SERVICE_SECONDARY, false);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

        // no need for disconnecting, it will be disconnected by the manager automatically
    }

    @Override
    public void onBatteryValueReceived(final int value) {
//        final Intent broadcast = new Intent(BROADCAST_BATTERY_LEVEL);
//        broadcast.putExtra(EXTRA_BATTERY_LEVEL, value);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onBondingRequired() {
        Log.i(TAG, "bonding...");
//        final Intent broadcast = new Intent(BROADCAST_BOND_STATE);
//        broadcast.putExtra(EXTRA_BOND_STATE, BluetoothDevice.BOND_BONDING);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onBonded() {
        Log.i(TAG, "bonded.");
//        final Intent broadcast = new Intent(BROADCAST_BOND_STATE);
//        broadcast.putExtra(EXTRA_BOND_STATE, BluetoothDevice.BOND_BONDED);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public void onError(final String message, final int errorCode) {
//        final Intent broadcast = new Intent(BROADCAST_ERROR);
//        broadcast.putExtra(EXTRA_ERROR_MESSAGE, message);
//        broadcast.putExtra(EXTRA_ERROR_CODE, errorCode);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

//        mBleManager.disconnect();
//        stopSelf();
    }

}
