package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DashboardRecordDataResponse2 extends DashboardRecordDataResponse {

    @RkField(position = 11, length = 2)
    private short temperature1;

    @RkField(position = 13, length = 2)
    private int activity1;

    @RkField(position = 15, length = 1)
    private int status1;

    @RkField(position = 16, length = 2)
    private short humidity1;

    @RkField(position = 18, length = 2)
    private short temperature2;

    @RkField(position = 20, length = 2)
    private int activity2;

    @RkField(position = 22, length = 1)
    private int status2;

    @RkField(position = 23, length = 2)
    private short humidity2;

    public short getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(short temperature1) {
        this.temperature1 = temperature1;
    }

    public int getActivity1() {
        return activity1;
    }

    public void setActivity1(int activity1) {
        this.activity1 = activity1;
    }

    public int getStatus1() {
        return status1;
    }

    public void setStatus1(int status1) {
        this.status1 = status1;
    }

    public short getHumidity1() {
        return humidity1;
    }

    public void setHumidity1(short humidity1) {
        this.humidity1 = humidity1;
    }

    public short getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(short temperature2) {
        this.temperature2 = temperature2;
    }

    public int getActivity2() {
        return activity2;
    }

    public void setActivity2(int activity2) {
        this.activity2 = activity2;
    }

    public int getStatus2() {
        return status2;
    }

    public void setStatus2(int status2) {
        this.status2 = status2;
    }

    public short getHumidity2() {
        return humidity2;
    }

    public void setHumidity2(short humidity2) {
        this.humidity2 = humidity2;
    }
}
