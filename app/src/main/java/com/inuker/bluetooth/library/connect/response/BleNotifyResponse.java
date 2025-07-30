package com.inuker.bluetooth.library.connect.response;

import java.util.UUID;

/**
 * 蓝牙通知响应 - 空实现版本
 */
public abstract class BleNotifyResponse {
    public abstract void onNotify(UUID service, UUID character, byte[] value);
    public abstract void onResponse(int code);
}