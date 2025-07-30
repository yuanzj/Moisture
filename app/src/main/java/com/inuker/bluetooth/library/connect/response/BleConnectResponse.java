package com.inuker.bluetooth.library.connect.response;

import com.inuker.bluetooth.library.model.BleGattProfile;

/**
 * 蓝牙连接响应 - 空实现版本
 */
public abstract class BleConnectResponse {
    public abstract void onResponse(int code, BleGattProfile data);
    
    public void onResponse(int code) {
        onResponse(code, null);
    }
}