package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

public class DeviceStatusResponse {

    @RkField(position = 0, length = 1)
    private int CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 2)
    private int voltage;

    @RkField(position = 7, length = 1)
    private int soc;

    /**
     *  0x00：空闲<br/>0x01：自动测量<br/>0x02：定时测量<br/>0x03：校准
     */
    @RkField(position = 8, length = 1)
    private int status;

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

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SocResponse{" +
                "CmdGroup=" + CmdGroup +
                ", Cmd=" + Cmd +
                ", Response=" + Response +
                ", Reserved=" + Reserved +
                ", voltage=" + voltage +
                ", soc=" + soc +
                ", status=" + status +
                '}';
    }
}
