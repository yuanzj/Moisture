package com.inuker.bluetooth.library;

/**
 * 蓝牙库常量定义 - 空实现版本
 */
public class Constants {
    // 请求状态常量
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_FAILED = -1;
    
    // 连接状态常量
    public static final int STATUS_CONNECTED = 20;
    public static final int STATUS_DISCONNECTED = 21;
    public static final int STATUS_DEVICE_CONNECTING = 22;
    public static final int STATUS_DEVICE_CONNECTED = 20; // 与STATUS_CONNECTED保持一致
    public static final int STATUS_UNKNOWN = -1;
    
    // 蓝牙状态常量
    public static final int STATE_OFF = 10;
    public static final int STATE_TURNING_ON = 11;
    public static final int STATE_ON = 12;
    public static final int STATE_TURNING_OFF = 13;
    
    // 扫描类型
    public static final int SCAN_TYPE_DEVICE_NAME = 1;
    public static final int SCAN_TYPE_MAC = 2;
}