package com.drt.moisture.util;

/**
 * 蓝牙连接状态常量
 * 替代不可用的com.inuker.bluetooth.library.Constants
 */
public class BluetoothConstants {
    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_DEVICE_CONNECTED = 2;
    public static final int STATUS_DEVICE_CONNECTING = 1;
    
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_FAILED = -1;
}