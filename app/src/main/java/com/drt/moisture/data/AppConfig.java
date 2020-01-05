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

    /**
     * 查询频率
     */
    private int ratio;

    /**
     * 测量点数量
     */
    private int pointCount;

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

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "period=" + period +
                ", measuringTime=" + measuringTime +
                ", correctTime=" + correctTime +
                ", ratio=" + ratio +
                ", pointCount=" + pointCount +
                '}';
    }
}
