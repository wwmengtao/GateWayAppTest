package com.homecare.app.ble.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.homecare.app.R;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.SGInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * ScannerFragment class scan required BLE devices and shows them in a list. This class scans and filter devices with standard BLE Service UUID and devices with custom BLE Service UUID It contains a
 * list and a button to scan/cancel. There is a interface {@link OnDeviceSelectedListener} which is implemented by activity in order to receive selected device. The scanning will continue for 5
 * seconds and then stop
 */
public class ScannerFragment extends DialogFragment {
    private final static String TAG = "ScannerFragment";

    private final static String PARAM_UUID = "param_uuid";
    private final static String DISCOVERABLE_REQUIRED = "discoverable_required";
    private final static long SCAN_DURATION = 5000;

    private BluetoothAdapter mBluetoothAdapter;
    private OnDeviceSelectedListener mListener;
    private DeviceListAdapter mAdapter;
    private final Handler mHandler = new Handler();
    private Button mScanButton;

    private boolean mDiscoverableRequired;
    private UUID mUuid;

    private boolean mIsScanning = false;

    /* package */static final int NO_RSSI = -1000;

    /**
     * Static implementation of fragment so that it keeps data when phone orientation is changed For standard BLE Service UUID, we can filter devices using normal android provided command
     * startScanLe() with required BLE Service UUID For custom BLE Service UUID, we will use class ScannerServiceParser to filter out required device.
     */
    public static ScannerFragment getInstance(final UUID uuid, final boolean discoverableRequired) {
        final ScannerFragment fragment = new ScannerFragment();

        final Bundle args = new Bundle();
        if (uuid != null) {
            args.putParcelable(PARAM_UUID, new ParcelUuid(uuid));
        }
        args.putBoolean(DISCOVERABLE_REQUIRED, discoverableRequired);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Interface required to be implemented by activity.
     */
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
        void onDeviceSelectedToBond(final BluetoothDevice device, final String name);

        void onDeviceSelectedToUnbond(final BluetoothDevice device);

        /**
         * Fired when scanner dialog has been cancelled without selecting a device.
         */
        void onDialogCanceled();

        boolean isDeviceConnected();
    }

    /**
     * This will make sure that {@link OnDeviceSelectedListener} interface is implemented by activity.
     */
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnDeviceSelectedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDeviceSelectedListener");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args.containsKey(PARAM_UUID)) {
            final ParcelUuid pu = args.getParcelable(PARAM_UUID);
            if (pu != null) {
                mUuid = pu.getUuid();
            }
        }
        mDiscoverableRequired = args.getBoolean(DISCOVERABLE_REQUIRED);

        final BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
    }

    @Override
    public void onDestroyView() {
        stopScan();
        super.onDestroyView();
    }

    /**
     * When dialog is created then set AlertDialog with list and button views.
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_device_selection, null);
        final ListView listview = (ListView) dialogView.findViewById(android.R.id.list);

        listview.setEmptyView(dialogView.findViewById(android.R.id.empty));
        mAdapter = new DeviceListAdapter(getActivity());
        SGInfo sgInfo = InfoContainer.sgInfo;
        String deviceAddress = sgInfo.getWearableInfo().getDeviceAddress();
        String deviceName = sgInfo.getWearableInfo().getDeviceName();
        if (deviceAddress != null) {
            BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
            Log.i(TAG, "device: " + deviceName + ", " + deviceAddress + " bond state: " + bluetoothDevice.getBondState() + "," + mListener.isDeviceConnected());
            ExtendedBluetoothDevice device = new ExtendedBluetoothDevice(bluetoothDevice, deviceName, NO_RSSI, true);
            mAdapter.addBondedDevice(device);
            if (!mListener.isDeviceConnected()) {
                DeviceListAdapter.updateDeviceStatus(deviceAddress, true);
            }
        }
        listview.setAdapter(mAdapter);

        builder.setTitle(R.string.scanner_title);
        final AlertDialog dialog = builder.setView(dialogView).create();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice) mAdapter.getItem(position);
                if (d.isBonded) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("解绑手环吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("解绑", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mListener.onDeviceSelectedToUnbond(d.device);
                            mAdapter.removeBondedDevice(d);
                            startScan();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    if (mAdapter.getBondedDeviceCount() > 0) {
                        Toast.makeText(getActivity(), "请先解绑已绑定的手环", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    stopScan();
                    dialog.dismiss();
                    mListener.onDeviceSelectedToBond(d.device, d.name);
                    mAdapter.removeDevice(d);
                    d.isBonded = true;
                    d.rssi = NO_RSSI;
                    mAdapter.addBondedDevice(d);
                }
            }
        });

        mScanButton = (Button) dialogView.findViewById(R.id.action_cancel);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.action_cancel) {
                    if (mIsScanning) {
                        dialog.cancel();
                    } else {
                        startScan();
                    }
                }
            }
        });

        if (savedInstanceState == null)
            startScan();
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        mListener.onDialogCanceled();
    }

    /**
     * Scan for 5 seconds and then stop scanning when a BluetoothLE device is found then mLEScanCallback is activated This will perform regular scan for custom BLE Service UUID and then filter out.
     * using class ScannerServiceParser
     */
    private void startScan() {
        mScanButton.setText(R.string.scanner_action_cancel);

        // Samsung Note II with Android 4.3 build JSS15J.N7100XXUEMK9 is not filtering by UUID at all. We must parse UUIDs manually
        mBluetoothAdapter.startLeScan(mLEScanCallback);

        mIsScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsScanning) {
                    stopScan();
                }
            }
        }, SCAN_DURATION);
    }

    /**
     * Stop scan if user tap Cancel button.
     */
    private void stopScan() {
        if (mIsScanning) {
            mScanButton.setText(R.string.scanner_action_scan);
            mBluetoothAdapter.stopLeScan(mLEScanCallback);
            mIsScanning = false;
        }
    }

    /**
     * if scanned device already in the list then update it otherwise add as a new device
     */
    private void addScannedDevice(final BluetoothDevice device, final String name, final int rssi, final boolean isBonded) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addOrUpdateDevice(new ExtendedBluetoothDevice(device, name, rssi, isBonded));
                }
            });
    }

//    /**
//     * if scanned device already in the list then update it otherwise add as a new device.
//     */
//    private void updateScannedDevice(final BluetoothDevice device, final int rssi) {
//        if (getActivity() != null)
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mAdapter.updateRssiOfBondedDevice(device.getAddress(), rssi);
//                }
//            });
//    }

    /**
     * Callback for scanned devices class {@link ScannerServiceParser} will be used to filter devices with custom BLE service UUID then the device will be added in a list.
     */
    private final BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            if (device != null) {
//                updateScannedDevice(device, rssi);
                try {
                    if (ScannerServiceParser.decodeDeviceAdvData(scanRecord, mUuid, mDiscoverableRequired)) {
                        // On some devices device.getName() is always null. We have to parse the name manually :(
                        // This bug has been found on Sony Xperia Z1 (C6903) with Android 4.3.
                        // https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
                        String deviceName = ScannerServiceParser.decodeDeviceName(scanRecord);
                        String configDeviceName = InfoContainer.sgInfo.getWearableInfo().getDeviceName();
                        if (StringUtils.isNotEmpty(deviceName) && deviceName.equals(configDeviceName)) {
                            addScannedDevice(device, deviceName, rssi, false);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Invalid data in Advertisement packet " + e.toString(), e);
                }
            }
        }
    };
}
