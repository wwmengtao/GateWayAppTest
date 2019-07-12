package com.homecare.app.ble.scanner;

import android.bluetooth.BluetoothDevice;

public class ExtendedBluetoothDevice {
    public final BluetoothDevice device;
    /** The name is not parsed by some Android devices, f.e. Sony Xperia Z1 with Android 4.3 (C6903). It needs to be parsed manually. */
    public String name;
    private boolean disconnect = false;
    public int rssi;
    public boolean isBonded;

    public ExtendedBluetoothDevice(BluetoothDevice device, String name, int rssi, boolean isBonded) {
        this.device = device;
        this.name = name;
        this.rssi = rssi;
        this.isBonded = isBonded;
    }

    public boolean isDisconnect() {
        return disconnect;
    }

    public void setDisconnect(boolean disconnect) {
        this.disconnect = disconnect;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExtendedBluetoothDevice) {
            final ExtendedBluetoothDevice that = (ExtendedBluetoothDevice) o;
            return device.getAddress().equals(that.device.getAddress());
        }
        return super.equals(o);
    }

    /**
     * Class used as a temporary comparator to find the device in the List of {@link ExtendedBluetoothDevice}s. This must be done this way, because List#indexOf and List#contains use the parameter's
     * equals method, not the object's from list. See {@link DeviceListAdapter#updateRssiOfBondedDevice(String, int)} for example
     */
    public static class AddressComparator {
        public String address;

        @Override
        public boolean equals(Object o) {
            if (o instanceof ExtendedBluetoothDevice) {
                final ExtendedBluetoothDevice that = (ExtendedBluetoothDevice) o;
                return address.equals(that.device.getAddress());
            }
            return super.equals(o);
        }
    }
}
