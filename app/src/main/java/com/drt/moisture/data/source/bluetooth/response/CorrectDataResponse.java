package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class CorrectDataResponse {

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
    private int temperature;

    @RkField(position = 11, length = 2)
    private int activity;

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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
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
        return "RecordDataResponse{" +
                "CmdGroup=" + CmdGroup +
                ", Cmd=" + Cmd +
                ", Response=" + Response +
                ", Reserved=" + Reserved +
                ", time=" + time +
                ", temperature=" + temperature +
                ", activity=" + activity +
                '}';
    }
}
