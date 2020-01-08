package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class SetCorrectParameRequest {

    @RkField(position = 0, length = 1)
    private byte CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 1)
    private int type;

    @RkField(position = 6, length = 4)
    private int a;

    @RkField(position = 10, length = 4)
    private int b;

    @RkField(position = 14, length = 4)
    private int c;

    @RkField(position = 18, length = 4)
    private int d;

    @RkField(position = 22, length = 4)
    private int e;

    @RkField(position = 26, length = 4)
    private int f;

    @RkField(position = 30, length = 4)
    private int g;

    @RkField(position = 34, length = 4)
    private int h;

    @RkField(position = 38, length = 4)
    private int i;

    @RkField(position = 42, length = 1)
    private int j;

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

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }
}
