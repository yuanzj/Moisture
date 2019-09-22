package com.drt.moisture.data.source.bluetooth.resquest;

import com.rokyinfo.convert.RkField;

public class SetMeasureParameRequest {

    @RkField(position = 0, length = 1)
    private byte CmdGroup;

    @RkField(position = 1, length = 1)
    private byte Cmd;

    @RkField(position = 2, length = 1)
    private byte Response;

    @RkField(position = 3, length = 2)
    private int Reserved;

    @RkField(position = 5, length = 1)
    private int n;

    @RkField(position = 6, length = 4)
    private long a;

    @RkField(position = 10, length = 4)
    private long b;

    @RkField(position = 14, length = 4)
    private long c;

    @RkField(position = 18, length = 4)
    private long d;

    @RkField(position = 22, length = 4)
    private long e;

    @RkField(position = 26, length = 4)
    private long f;

    @RkField(position = 30, length = 4)
    private long g;

    @RkField(position = 34, length = 4)
    private long h;

    @RkField(position = 38, length = 4)
    private long i;

    @RkField(position = 42, length = 4)
    private long j;

    @RkField(position = 46, length = 4)
    private long k;

    @RkField(position = 50, length = 4)
    private long l;

    @RkField(position = 54, length = 4)
    private long m;

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

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public long getA() {
        return a;
    }

    public void setA(long a) {
        this.a = a;
    }

    public long getB() {
        return b;
    }

    public void setB(long b) {
        this.b = b;
    }

    public long getC() {
        return c;
    }

    public void setC(long c) {
        this.c = c;
    }

    public long getD() {
        return d;
    }

    public void setD(long d) {
        this.d = d;
    }

    public long getE() {
        return e;
    }

    public void setE(long e) {
        this.e = e;
    }

    public long getF() {
        return f;
    }

    public void setF(long f) {
        this.f = f;
    }

    public long getG() {
        return g;
    }

    public void setG(long g) {
        this.g = g;
    }

    public long getH() {
        return h;
    }

    public void setH(long h) {
        this.h = h;
    }

    public long getI() {
        return i;
    }

    public void setI(long i) {
        this.i = i;
    }

    public long getJ() {
        return j;
    }

    public void setJ(long j) {
        this.j = j;
    }

    public long getK() {
        return k;
    }

    public void setK(long k) {
        this.k = k;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public long getM() {
        return m;
    }

    public void setM(long m) {
        this.m = m;
    }
}
