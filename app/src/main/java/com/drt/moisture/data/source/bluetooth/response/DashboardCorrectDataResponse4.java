package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DashboardCorrectDataResponse4 extends CorrectDataResponse{

    @RkField(position = 0, length = 1)
    private int CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 4)
    private long time;

    @RkField(position = 9, length = 2)
    private short temperature1;

    @RkField(position = 11, length = 2)
    private int activity1;

    @RkField(position = 13, length = 1)
    private int index1;

    @RkField(position = 14, length = 2)
    private short temperature2;

    @RkField(position = 16, length = 2)
    private int activity2;

    @RkField(position = 18, length = 1)
    private int index2;

    @RkField(position = 19, length = 2)
    private short temperature3;

    @RkField(position = 21, length = 2)
    private int activity3;

    @RkField(position = 23, length = 1)
    private int index3;

    @RkField(position = 24, length = 2)
    private int temperature4;

    @RkField(position = 26, length = 2)
    private int activity4;

    @RkField(position = 28, length = 1)
    private int index4;

    public int getCmdGroup() {
        return CmdGroup;
    }

    public void setCmdGroup(int cmdGroup) {
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

    public int getIndex1() {
        return index1;
    }

    public void setIndex1(int index1) {
        this.index1 = index1;
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

    public int getIndex2() {
        return index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
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

    public int getIndex3() {
        return index3;
    }

    public void setIndex3(int index3) {
        this.index3 = index3;
    }

    public int getTemperature4() {
        return temperature4;
    }

    public void setTemperature4(int temperature4) {
        this.temperature4 = temperature4;
    }

    public int getActivity4() {
        return activity4;
    }

    public void setActivity4(int activity4) {
        this.activity4 = activity4;
    }

    public int getIndex4() {
        return index4;
    }

    public void setIndex4(int index4) {
        this.index4 = index4;
    }

    @Override
    public String toString() {
        return "DashboardCorrectDataResponse1{" +
                "CmdGroup=" + CmdGroup +
                ", Cmd=" + Cmd +
                ", Response=" + Response +
                ", Reserved=" + Reserved +
                ", time=" + time +
                ", temperature1=" + temperature1 +
                ", activity1=" + activity1 +
                ", index1=" + index1 +
                '}';
    }
}
