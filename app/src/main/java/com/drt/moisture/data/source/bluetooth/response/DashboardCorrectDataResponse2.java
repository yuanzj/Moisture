package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DashboardCorrectDataResponse2 extends CorrectDataResponse{

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
    private int temperature1;

    @RkField(position = 11, length = 2)
    private int activity1;

    @RkField(position = 13, length = 1)
    private int index1;

    @RkField(position = 14, length = 2)
    private int temperature2;

    @RkField(position = 16, length = 2)
    private int activity2;

    @RkField(position = 18, length = 1)
    private int index2;

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

    public int getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(int temperature1) {
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

    public int getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(int temperature2) {
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
