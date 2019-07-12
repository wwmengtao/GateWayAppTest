package com.homecare.app.ble.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public class ParserUtil {
    final private static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();


    public static String parse(final BluetoothGattCharacteristic characteristic) {
        return parse(characteristic.getValue());
    }

    public static String parse(final BluetoothGattDescriptor descriptor) {
        return parse(descriptor.getValue());
    }

    public static String parse(final byte[] data) {
        if (data == null || data.length == 0)
            return "";

        final char[] out = new char[data.length * 3 - 1];
        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            out[j * 3] = HEX_ARRAY[v >>> 4];
            out[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            if (j != data.length - 1)
                out[j * 3 + 2] = '-';
        }
        return "(0x) " + new String(out);
    }
    public static String intTotoBinaryString(int value,int length){
        String bs = Integer.toBinaryString(value);
        while (bs.length() < length) {
            bs = "0" + bs;
        }
        return bs;
    }
    public static String bytesToBits(byte[] bytes) {
        StringBuilder bits = new StringBuilder();
        for (byte b : bytes) {
            String bs = intTotoBinaryString(b,8);
            bits.append(bs);
        }
        return bits.toString();
    }
    public static String binStrToString(String bits){
        byte[] b = new byte[bits.length()/8];
        for(int i =0; i< b.length; i ++){
            b[i] = Byte.parseByte(bits.substring(i*8,(i+1)*8),2);
        }
        return new String(b);

    }
    public static byte[] bitsToBytes(String bits){
        byte[] bytes = new byte[bits.length() / 8];
        for (int i = 0; i< bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(bits.substring(i * 8, (i + 1) * 8), 2);
        }
        return bytes;
    }
}
