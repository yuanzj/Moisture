package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DashboardRecordDataResponse4 extends DashboardRecordDataResponse {

    @RkField(position = 0, length = 1)
    private byte CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 4)
    private long time;

    @RkField(position = 9, length = 2)
    private short index;

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

    @RkField(position = 25, length = 2)
    private short temperature3;

    @RkField(position = 27, length = 2)
    private int activity3;

    @RkField(position = 29, length = 1)
    private int status3;

    @RkField(position = 30, length = 2)
    private short humidity3;

    @RkField(position = 32, length = 2)
    private short temperature4;

    @RkField(position = 34, length = 2)
    private int activity4;

    @RkField(position = 36, length = 1)
    private int status4;

    @RkField(position = 37, length = 2)
    private short humidity4;

    
    public byte getCmdGroup() {
        return CmdGroup;
    }

    
    public void setCmdGroup(byte cmdGroup) {
        CmdGroup = cmdGroup;
    }

    
    public byte getCmd() {
        return Cmd;
    }

    
    public void setCmd(byte cmd) {
        Cmd = cmd;
    }

    
    public byte getResponse() {
        return Response;
    }

    
    public void setResponse(byte response) {
        Response = response;
    }

    
    public int getReserved() {
        return Reserved;
    }

    
    public void setReserved(int reserved) {
        Reserved = reserved;
    }

    
    public long getTime() {
        return time;
    }

    
    public void setTime(long time) {
        this.time = time;
    }

    
    public short getIndex() {
        return index;
    }

    
    public void setIndex(short index) {
        this.index = index;
    }

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

    public short getTemperature3() {
        return temperature3;
    }

    public void setTemperature3(short temperature3) {
        this.temperature3 = temperature3;
    }

    public int getActivity3() {
        return activity3;
    }

    public void setActivity3(int activity3) {
        this.activity3 = activity3;
    }

    public int getStatus3() {
        return status3;
    }

    public void setStatus3(int status3) {
        this.status3 = status3;
    }

    public short getHumidity3() {
        return humidity3;
    }

    public void setHumidity3(short humidity3) {
        this.humidity3 = humidity3;
    }

    public short getTemperature4() {
        return temperature4;
    }

    public void setTemperature4(short temperature4) {
        this.temperature4 = temperature4;
    }

    public int getActivity4() {
        return activity4;
    }

    public void setActivity4(int activity4) {
        this.activity4 = activity4;
    }

    public int getStatus4() {
        return status4;
    }

    public void setStatus4(int status4) {
        this.status4 = status4;
    }

    public short getHumidity4() {
        return humidity4;
    }

    public void setHumidity4(short humidity4) {
        this.humidity4 = humidity4;
    }
}
