package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DashboardRecordDataResponse1 extends DashboardRecordDataResponse {

    @RkField(position = 11, length = 2)
    private short temperature1;

    @RkField(position = 13, length = 2)
    private int activity1;

    @RkField(position = 15, length = 1)
    private int status1;

    @RkField(position = 16, length = 2)
    private short humidity1;


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
}
