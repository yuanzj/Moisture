package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class StartMeasureRequest {

    @RkField(position = 0, length = 1)
    private byte CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 30)
    private String name;

    @RkField(position = 35, length = 1)
    private byte interval;

    @RkField(position = 36, length = 1)
    private byte model;

    @RkField(position = 37, length = 1)
    private byte time;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getInterval() {
        return interval;
    }

    public void setInterval(byte interval) {
        this.interval = interval;
    }

    public byte getModel() {
        return model;
    }

    public void setModel(byte model) {
        this.model = model;
    }

    public byte getTime() {
        return time;
    }

    public void setTime(byte time) {
        this.time = time;
    }
}