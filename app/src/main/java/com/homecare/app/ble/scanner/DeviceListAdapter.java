package com.homecare.app.ble.scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.homecare.app.R;

import java.util.ArrayList;

/**
 * DeviceListAdapter class is list adapter for showing scanned Devices name, address and RSSI image based on RSSI values.
 */
public class DeviceListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    private static final ArrayList<ExtendedBluetoothDevice> mListBondedValues = new ArrayList<>();
    private static final ArrayList<ExtendedBluetoothDevice> mListValues = new ArrayList<>();
    private final Context mContext;
    private final ExtendedBluetoothDevice.AddressComparator comparator = new ExtendedBluetoothDevice.AddressComparator();

    public DeviceListAdapter(Context context) {
        mContext = context;
    }

    public void addBondedDevice(ExtendedBluetoothDevice device) {
        if (!mListBondedValues.contains(device)) {
            mListBondedValues.add(device);
        }
        notifyDataSetChanged();
    }

    public static int getBondedDeviceCount() {
        return mListBondedValues.size();
    }

    /**
     * Looks for the device with the same address as given one in the list of bonded devices. If the device has been found it updates its RSSI value.
     *
     * @param address
     *            the device address
     * @param rssi
     *            the RSSI of the scanned device
     */
    public void updateRssiOfBondedDevice(String address, int rssi) {
        comparator.address = address;
        final int indexInBonded = mListBondedValues.indexOf(comparator);
        if (indexInBonded >= 0) {
            ExtendedBluetoothDevice previousDevice = mListBondedValues.get(indexInBonded);
            previousDevice.rssi = rssi;
            notifyDataSetChanged();
        }
    }

    /**
     * If such device exists on the bonded device list, this method does nothing. If not then the device is updated (rssi value) or added.
     *
     * @param device
     *            the device to be added or updated
     */
    public void addOrUpdateDevice(ExtendedBluetoothDevice device) {
        final boolean indexInBonded = mListBondedValues.contains(device);
        if (indexInBonded) {
            return;
        }

        final int indexInNotBonded = mListValues.indexOf(device);
        if (indexInNotBonded >= 0) {
            ExtendedBluetoothDevice previousDevice = mListValues.get(indexInNotBonded);
            previousDevice.rssi = device.rssi;
            notifyDataSetChanged();
            return;
        }
        mListValues.add(device);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        final int availableCount = mListValues.isEmpty() ? 2 : mListValues.size() + 1; // 1 for title, 1 for empty text
        if (bondedCount == 1)
            return availableCount;
        return bondedCount + availableCount;
    }

    @Override
    public Object getItem(int position) {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        if (mListBondedValues.isEmpty()) {
            if (position == 0)
                return R.string.scanner_subtitle_not_bonded;
            else
                return mListValues.get(position - 1);
        } else {
            if (position == 0)
                return R.string.scanner_subtitle_bonded;
            if (position < bondedCount)
                return mListBondedValues.get(position - 1);
            if (position == bondedCount)
                return R.string.scanner_subtitle_not_bonded;
            return mListValues.get(position - bondedCount - 1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_TITLE;

        if (!mListBondedValues.isEmpty() && position == mListBondedValues.size() + 1)
            return TYPE_TITLE;

        if (position == getCount() - 1 && mListValues.isEmpty())
            return TYPE_EMPTY;

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final int type = getItemViewType(position);

        View view = oldView;
        switch (type) {
            case TYPE_EMPTY:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_empty, parent, false);
                }
                break;
            case TYPE_TITLE:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_title, parent, false);
                }
                final TextView title = (TextView) view;
                title.setText((Integer) getItem(position));
                break;
            default:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_row, parent, false);
                    final ViewHolder holder = new ViewHolder();
                    holder.name = (TextView) view.findViewById(R.id.name);
                    holder.state = (TextView) view.findViewById(R.id.state);
                    holder.address = (TextView) view.findViewById(R.id.address);
                    holder.rssi = (ImageView) view.findViewById(R.id.rssi);
                    view.setTag(holder);
                }

                final ExtendedBluetoothDevice device = (ExtendedBluetoothDevice) getItem(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                final String name = device.name;
                holder.name.setText(name != null ? name : mContext.getString(R.string.not_available));
                if (device.isDisconnect()) {
                    holder.state.setText("连接断开");
                } else {
                    holder.state.setText("");
                }
                holder.address.setText(device.device.getAddress());
                if (!device.isBonded || device.rssi != ScannerFragment.NO_RSSI) {
                    final int rssiPercent = (int) (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f));
                    holder.rssi.setImageLevel(rssiPercent);
                    holder.rssi.setVisibility(View.VISIBLE);
                } else {
                    holder.rssi.setVisibility(View.GONE);
                }
                break;
        }

        return view;
    }

    public void removeBondedDevice(ExtendedBluetoothDevice d) {
        mListBondedValues.remove(d);
        notifyDataSetChanged();
    }

    public void removeDevice(ExtendedBluetoothDevice d) {
        mListValues.remove(d);
        notifyDataSetChanged();
    }

    public static void updateDeviceStatus(String deviceAddress, boolean disconnect) {
        for (ExtendedBluetoothDevice device : mListBondedValues) {
            if (deviceAddress.equals(device.device.getAddress())) {
                device.setDisconnect(disconnect);
                break;
            }
        }
    }

    private class ViewHolder {
        private TextView name;
        private TextView state;
        private TextView address;
        private ImageView rssi;
    }
}
