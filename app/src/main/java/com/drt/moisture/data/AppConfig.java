package com.drt.moisture.data;

public class AppConfig {

    /**
     * 测量间隔时间（单位秒）
     */
    private int period;

    /**
     * 测试时间 (单位分钟)
     */
    private int measuringTime;

    /**
     * 校准时间 (单位分钟)
     */
    private int correctTime;

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getMeasuringTime() {
        return measuringTime;
    }

    public void setMeasuringTime(int measuringTime) {
        this.measuringTime = measuringTime;
    }

    public int getCorrectTime() {
        return correctTime;
    }

    public void setCorrectTime(int correctTime) {
        this.correctTime = correctTime;
    }
}
