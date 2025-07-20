package com.inuker.bluetooth.library.connect.listener;

/**
 * 蓝牙连接状态监听器 - 空实现版本
 */
public abstract class BleConnectStatusListener {
    public abstract void onConnectStatusChanged(String mac, int status);
}