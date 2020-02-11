package com.drt.moisture.data.source.bluetooth.response;

import com.rokyinfo.convert.RkField;

import java.util.Arrays;

public class DeviceInfoResponse {

    @RkField(position = 0, length = 1)
    private int CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 16)
    private byte[] SN;

    @RkField(position = 21, length = 16)
    private byte[] version;

    @RkField(position = 37, length = 16)
    private byte[] model;

    @RkField(position = 53, length = 16)
    private byte[] name;

    @RkField(position = 69, length = 16)
    private byte[] battery;

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

    public byte[] getSN() {
        return SN;
    }

    public void setSN(byte[] SN) {
        this.SN = SN;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public byte[] getModel() {
        return model;
    }

    public void setModel(byte[] model) {
        this.model = model;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public byte[] getBattery() {
        return battery;
    }

    public void setBattery(byte[] battery) {
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "DeviceInfoResponse{" +
                "CmdGroup=" + CmdGroup +
                ", Cmd=" + Cmd +
                ", Response=" + Response +
                ", Reserved=" + Reserved +
                ", SN=" + Arrays.toString(SN) +
                ", version=" + Arrays.toString(version) +
                ", model=" + Arrays.toString(model) +
                ", name=" + Arrays.toString(name) +
                ", battery=" + Arrays.toString(battery) +
                '}';
    }
}
