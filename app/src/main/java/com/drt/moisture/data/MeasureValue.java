package com.drt.moisture.data;

public class MeasureValue {

    private double temperature;

    private int activity;

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

    @Override
    public String toString() {
        return "MeasureModel{" +
                "temperature=" + temperature +
                ", activity=" + activity +
                '}';
    }

}
