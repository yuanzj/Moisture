package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DashboardCorrectDataResponse1 extends CorrectDataResponse{

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
