package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class StopMeasureRequest {

    @RkField(position = 0, length = 1)
    private byte CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

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

}
