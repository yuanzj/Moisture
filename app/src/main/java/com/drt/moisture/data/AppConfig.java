package com.drt.moisture.data;

public class AppConfig {

    /**
     * 测量间隔时间
     */
    private int period;

    /**
     * 测试时间
     */
    private int measuringTime;

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


}
