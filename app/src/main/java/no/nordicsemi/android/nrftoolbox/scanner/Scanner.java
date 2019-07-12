package no.nordicsemi.android.nrftoolbox.scanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.SGInfo;

public class Scanner {

    private Activity context;
    private BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler = new Handler();
    private OnDeviceSelectedListener deviceSelectedListener;
    private String selectedDeviceName = null;

    private static final long SCAN_DURATION = 5000;

    private static final String TAG = Scanner.class.getSimpleName();

    public Scanner(Activity context, OnDeviceSelectedListener deviceSelectedListener) {
        this.context = context;
        this.deviceSelectedListener = deviceSelectedListener;
        final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = manager.getAdapter();
    }

    public void startScan() {
        // Samsung Note II with Android 4.3 build JSS15J.N7100XXUEMK9 is not filtering by UUID at all. We must parse UUIDs manually
        mBluetoothAdapter.startLeScan(mLEScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_DURATION);
    }

    private void stopScan() {
        SGInfo sgInfo = InfoContainer.sgInfo;
        mBluetoothAdapter.stopLeScan(mLEScanCallback);
        if (selectedDeviceName == null || sgInfo.isDfuFailed()) {
            deviceSelectedListener.onNoDeviceSelected();
            sgInfo.setDfuFailed(false);
        }
    }

    public interface OnDeviceSelectedListener {
        /**
         * Fired when user selected the device.
         *
         * @param device
         *            the device to connect to
         * @param name
         *            the device name. Unfortunately on some devices {@link BluetoothDevice#getName()} always returns <code>null</code>, f.e. Sony Xperia Z1 (C6903) with Android 4.3. The name has to
         *            be parsed manually form the Advertisement packet.
         */
        void onDeviceSelected(final BluetoothDevice device, final String name);

        void onNoDeviceSelected();
    }

    private final BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            if (device != null) {
                try {
                    if (ScannerServiceParser.decodeDeviceAdvData(scanRecord, null, false)) {
                        // On some devices device.getName() is always null. We have to parse the name manually :(
                        // This bug has been found on Sony Xperia Z1 (C6903) with Android 4.3.
                        // https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
                        final String deviceName = ScannerServiceParser.decodeDeviceName(scanRecord);
                        // TODO for work
                        if ("DfuTarg".equals(deviceName)) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deviceSelectedListener.onDeviceSelected(device, deviceName);
                                }
                            });
                            selectedDeviceName = deviceName;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Invalid data in Advertisement packet " + e.toString(), e);
                }
            }
        }
    };

}
