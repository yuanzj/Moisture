package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class SetHumidityParameRequest {

    @RkField(position = 0, length = 1)
    private int CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 4)
    private int a;

    @RkField(position = 9, length = 4)
    private int b;

    @RkField(position = 13, length = 4)
    private int c;

    @RkField(position = 17, length = 4)
    private int d;

    @RkField(position = 21, length = 4)
    private int e;

    @RkField(position = 25, length = 4)
    private int f;

    @RkField(position = 29, length = 4)
    private int g;

    @RkField(position = 33, length = 4)
    private int h;

    @RkField(position = 37, length = 4)
    private int i;

    @RkField(position = 41, length = 4)
    private int j;

    @RkField(position = 45, length = 4)
    private int k;

    @RkField(position = 49, length = 4)
    private int l;

    @RkField(position = 53, length = 4)
    private int m;

    @RkField(position = 57, length = 4)
    private int n;

    @RkField(position = 61, length = 4)
    private int o;

    @RkField(position = 65, length = 4)
    private int p;

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

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getO() {
        return o;
    }

    public void setO(int o) {
        this.o = o;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
}
