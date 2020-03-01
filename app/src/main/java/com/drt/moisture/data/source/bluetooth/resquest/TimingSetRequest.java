package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class TimingSetRequest {

    @RkField(position = 0, length = 1)
    private int CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 1)
    private int time1h;

    @RkField(position = 6, length = 1)
    private int time1m;

    @RkField(position = 7, length = 1)
    private int time2h;

    @RkField(position = 8, length = 1)
    private int time2m;

    @RkField(position = 9, length = 1)
    private int time3h;

    @RkField(position = 10, length = 1)
    private int time3m;

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

    public int getTime1h() {
        return time1h;
    }

    public void setTime1h(int time1h) {
        this.time1h = time1h;
    }

    public int getTime1m() {
        return time1m;
    }

    public void setTime1m(int time1m) {
        this.time1m = time1m;
    }

    public int getTime2h() {
        return time2h;
    }

    public void setTime2h(int time2h) {
        this.time2h = time2h;
    }

    public int getTime2m() {
        return time2m;
    }

    public void setTime2m(int time2m) {
        this.time2m = time2m;
    }

    public int getTime3h() {
        return time3h;
    }

    public void setTime3h(int time3h) {
        this.time3h = time3h;
    }

    public int getTime3m() {
        return time3m;
    }

    public void setTime3m(int time3m) {
        this.time3m = time3m;
    }

    @Override
    public String toString() {
        return "TimingSetResponse{" +
                "CmdGroup=" + CmdGroup +
                ", Cmd=" + Cmd +
                ", Response=" + Response +
                ", Reserved=" + Reserved +
                ", time1h=" + time1h +
                ", time1m=" + time1m +
                ", time2h=" + time2h +
                ", time2m=" + time2m +
                ", time3h=" + time3h +
                ", time3m=" + time3m +
                '}';
    }
}
