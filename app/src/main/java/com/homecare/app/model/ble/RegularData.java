package com.homecare.app.model.ble;

public class RegularData extends WearableData {
    private long timestamp;
    private int hrData;
    private int sq;
    private int temperature;
    private int humidity;
    private int pressure;
    private int staticTime;
    private int walkTime;
    private int steps;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getHrData() {
        return hrData;
    }

    public void setHrData(int hrData) {
        this.hrData = hrData;
    }

    public int getSq() {
        return sq;
    }

    public void setSq(int sq) {
        this.sq = sq;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getStaticTime() {
        return staticTime;
    }

    public void setStaticTime(int staticTime) {
        this.staticTime = staticTime;
    }

    public int getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(int walkTime) {
        this.walkTime = walkTime;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @Override
    public String toCloudMessage() {
        return timestamp + "," + hrData + "," + sq + "," + temperature + "," + humidity + "," + pressure + "," + staticTime + "," + walkTime + "," + steps;
    }
}
