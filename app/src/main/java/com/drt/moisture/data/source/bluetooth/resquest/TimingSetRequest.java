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
    private int time1y;

    @RkField(position = 6, length = 1)
    private int time1month;

    @RkField(position = 7, length = 1)
    private int time1day;

    @RkField(position = 8, length = 1)
    private int time1h;

    @RkField(position = 9, length = 1)
    private int time1m;

    @RkField(position = 10, length = 1)
    private int time2y;

    @RkField(position = 11, length = 1)
    private int time2month;

    @RkField(position = 12, length = 1)
    private int time2day;

    @RkField(position = 13, length = 1)
    private int time2h;

    @RkField(position = 14, length = 1)
    private int time2m;

    @RkField(position = 15, length = 1)
    private int time3y;

    @RkField(position = 16, length = 1)
    private int time3month;

    @RkField(position = 17, length = 1)
    private int time3day;

    @RkField(position = 18, length = 1)
    private int time3h;

    @RkField(position = 19, length = 1)
    private int time3m;

    @RkField(position = 20, length = 4)
    private int timestamp;

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

    public int getTime1y() {
        return time1y;
    }

    public void setTime1y(int time1y) {
        this.time1y = time1y;
    }

    public int getTime1month() {
        return time1month;
    }

    public void setTime1month(int time1month) {
        this.time1month = time1month;
    }

    public int getTime2y() {
        return time2y;
    }

    public void setTime2y(int time2y) {
        this.time2y = time2y;
    }

    public int getTime2month() {
        return time2month;
    }

    public void setTime2month(int time2month) {
        this.time2month = time2month;
    }

    public int getTime3y() {
        return time3y;
    }

    public void setTime3y(int time3y) {
        this.time3y = time3y;
    }

    public int getTime3month() {
        return time3month;
    }

    public void setTime3month(int time3month) {
        this.time3month = time3month;
    }

    public int getTime1day() {
        return time1day;
    }

    public void setTime1day(int time1day) {
        this.time1day = time1day;
    }

    public int getTime2day() {
        return time2day;
    }

    public void setTime2day(int time2day) {
        this.time2day = time2day;
    }

    public int getTime3day() {
        return time3day;
    }

    public void setTime3day(int time3day) {
        this.time3day = time3day;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
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
