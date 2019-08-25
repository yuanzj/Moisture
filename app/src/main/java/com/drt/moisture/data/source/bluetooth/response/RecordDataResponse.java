package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class RecordDataResponse {

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

//    样品名称	1（0~255）		实际10种（1~10）
//    温度	2（-32768~32767）		扩大100倍
//    水分活度	2（0~65535）		扩大100倍
//    环境湿度	2（0~65535）		扩大100倍

    @RkField(position = 9, length = 1)
    private byte name;

    @RkField(position = 10, length = 2)
    private int temperature;

    @RkField(position = 12, length = 2)
    private int activity;

    @RkField(position = 14, length = 2)
    private int humidity;

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

    public byte getName() {
        return name;
    }

    public void setName(byte name) {
        this.name = name;
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

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "RecordDataResponse{" +
                "CmdGroup=" + CmdGroup +
                ", Cmd=" + Cmd +
                ", Response=" + Response +
                ", Reserved=" + Reserved +
                ", time=" + time +
                ", name=" + name +
                ", temperature=" + temperature +
                ", activity=" + activity +
                ", humidity=" + humidity +
                '}';
    }
}
