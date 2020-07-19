package com.drt.moisture.data;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name="Water Activity Table")
public class MeasureValueEn  extends MeasureValue{

    @SmartColumn(id =1,name = "point")
    private int index;

    @SmartColumn(id =2,name = "time")
    private String reportTime;

    @SmartColumn(id =3,name = "Sample Name")
    private String name;

    @SmartColumn(id =4,name = "temp")
    private double temperature;

    @SmartColumn(id =5,name = "Aw value")
    private double activity;

    @SmartColumn(id =6,name = "environment value")
    private double humidity;

    /**
     * 该字段只在自动测量模式下使用
     * <br> 0x00：定时测量结束
     * <br> 0x01：自动测量进行中
     * <br> 0x02：自动测量结束
     * <br> 0x03：定时测量进行中
     */
    private int measureStatus;

    /**
     * 运行状态
     */
    private boolean running;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public int getMeasureStatus() {
        return measureStatus;
    }

    public void setMeasureStatus(int measureStatus) {
        this.measureStatus = measureStatus;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public String toString() {
        return "MeasureValue{" +
                "reportTime='" + reportTime + '\'' +
                ", name='" + name + '\'' +
                ", temperature=" + temperature +
                ", activity=" + activity +
                ", humidity=" + humidity +
                ", measureStatus=" + measureStatus +
                '}';
    }
}
