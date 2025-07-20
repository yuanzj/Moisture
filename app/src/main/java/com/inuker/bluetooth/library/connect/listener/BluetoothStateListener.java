package com.inuker.bluetooth.library.connect.listener;

/**
 * 蓝牙状态监听器 - 空实现版本
 */
public abstract class BluetoothStateListener {
    public abstract void onBluetoothStateChanged(boolean opened);
}