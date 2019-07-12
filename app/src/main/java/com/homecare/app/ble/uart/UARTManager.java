package com.homecare.app.ble.uart;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import com.homecare.app.ble.profile.BleManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class UARTManager extends BleManager<UARTManagerCallback> {
    /** Nordic UART Service UUID */
    private final static UUID UART_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /** TX characteristic UUID */
    private final static UUID UART_TX_CHARACTERISTIC_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    /** RX characteristic UUID */
    private final static UUID UART_RX_CHARACTERISTIC_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    private BluetoothGattCharacteristic mTXCharacteristic, mRXCharacteristic;

    public UARTManager(final Context context) {
        super(context);
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    /**
     * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving indication, etc
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Queue<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<Request>();
            requests.push(Request.newEnableNotificationsRequest(mRXCharacteristic));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(UART_SERVICE_UUID);
            if (service != null) {
                mTXCharacteristic = service.getCharacteristic(UART_TX_CHARACTERISTIC_UUID);
                mRXCharacteristic = service.getCharacteristic(UART_RX_CHARACTERISTIC_UUID);
            }
            return mTXCharacteristic != null && mRXCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {
            mTXCharacteristic = null;
            mRXCharacteristic = null;
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            final byte[] data = characteristic.getValue();
            mCallbacks.onDataReceived(data);
        }
    };

    @Override
    protected boolean shouldAutoConnect() {
        // We want the connection to be kept
        return true;
    }

    public void send(final byte[] bytes) {
        if (mTXCharacteristic != null) {
            mTXCharacteristic.setValue(bytes);
            writeCharacteristic(mTXCharacteristic);
        }
    }
}
