package com.drt.moisture.data;

public class MeasureValue {

    private double temperature;

    private int activity;

    private String reportTime;

    private String runningTime;


    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    @Override
    public String toString() {
        return "MeasureValue{" +
                "temperature=" + temperature +
                ", activity=" + activity +
                ", reportTime='" + reportTime + '\'' +
                ", runningTime='" + runningTime + '\'' +
                '}';
    }
}
