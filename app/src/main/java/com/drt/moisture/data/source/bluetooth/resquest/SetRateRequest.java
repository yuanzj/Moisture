package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class SetRateRequest {

    @RkField(position = 0, length = 1)
    private byte CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 2)
    private int rate;

    @RkField(position = 7, length = 1)
    private byte ratio;

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public byte getRatio() {
        return ratio;
    }

    public void setRatio(byte ratio) {
        this.ratio = ratio;
    }
}
