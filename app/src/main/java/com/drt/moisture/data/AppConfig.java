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

    /**
     * 启动时间
     */
    private long startTime;

    /**
     * 运行状态
     */
    private boolean runStatus;

    /**
     * 测量模式
     */
    private int measureMode;

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isRunStatus() {
        return runStatus;
    }

    public void setRunStatus(boolean runStatus) {
        this.runStatus = runStatus;
    }

    public int getMeasureMode() {
        return measureMode;
    }

    public void setMeasureMode(int measureMode) {
        this.measureMode = measureMode;
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
