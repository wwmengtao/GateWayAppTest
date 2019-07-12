package com.homecare.app.model;

public class Message implements Constants {
    private long timestamp;
    private int type;
    private String detail;
    private String gps;
    private String desc;

    public Message() {}

    public static Message toAlertMessage(long timestamp, int type, String gps) {
        Message msg = new Message();
        msg.timestamp = timestamp;
        msg.type = type;
        msg.gps = gps;
        return msg;
    }

    public static Message toNoteMessage(long timestamp, int type, String detail) {
        Message msg = new Message();
        msg.timestamp = timestamp;
        msg.type = type;
        msg.detail = detail;
        return msg;
    }

    public Message(String desc) {
        this.desc = desc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String toCloudMessage() {
        if (type < ALERT_NOTE_DELIMITER) {
            return timestamp + "," + type + "," + gps;
        } else {
            return timestamp + "," + type + "," + detail;
        }
    }

    public boolean isAlert() {
        return type < ALERT_NOTE_DELIMITER;
    }
}
