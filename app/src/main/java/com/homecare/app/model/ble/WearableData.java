package com.homecare.app.model.ble;

public abstract class WearableData {
    private String sequence;
    private boolean isAck;
    private boolean needAck;

    public final String getSequence() {
        return sequence;
    }

    public final void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public final boolean isAck() {
        return isAck;
    }

    public final void setAck(boolean isAck) {
        this.isAck = isAck;
    }

    public final boolean isNeedAck() {
        return needAck;
    }

    public final void setNeedAck(boolean needAck) {
        this.needAck = needAck;
    }

    public abstract String toCloudMessage();
}
