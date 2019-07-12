package com.homecare.app.model.ble;

import com.homecare.app.model.Constants;

public class DeviceEventNotification extends WearableData implements Constants {
    private long timestamp;
    private int eventId;
    private int eventValue;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventValue() {
        return eventValue;
    }

    public void setEventValue(int eventValue) {
        this.eventValue = eventValue;
    }

    @Override
    public String toCloudMessage() {
        return null;
    }

    public int getType() {
        int type = -1;
        switch (eventId) {
            case 0: {
                if (eventValue <= 10) {
                    type = ALERT_WEARABLE_LOW_POWER;
                } else {
                    type = NOTE_WEARABLE_LOW_POWER;
                }
                break;
            }
//            case 1: {
//                type = ALERT_WEARABLE_DISCONNECT;
//                break;
//            }
        }
        return type;
    }
}
