package com.homecare.app.model.ble;

import com.homecare.app.model.Constants;

public class HealthEventNotification extends WearableData implements Constants {
    private long timestamp;
    private int eventId;
    private int eventValue;

    @Override
    public String toCloudMessage() {
        return null;
    }

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

    public int getType() {
        int type = -1;
        switch (eventId) {
            case 0: {
                if (eventValue == 1) {
                    type = ALERT_SOS;
                }
                break;
            }
            case 1: {
                if (eventValue > 0) {
                    type = ALERT_FALL;
                }
                break;
            }
            case 2: {
                if (eventValue == 1) {
                    type = NOTE_SIT_TOO_LONG;
                }
                break;
            }
            case 3: {
                type = ALERT_HR_TOO_FAST;
                break;
            }
            case 4: {
                type = ALERT_HR_TOO_SLOW;
                break;
            }
        }
        return type;
    }
}
