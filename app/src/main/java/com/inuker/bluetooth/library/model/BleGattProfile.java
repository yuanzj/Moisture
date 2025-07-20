package com.inuker.bluetooth.library.model;

import java.util.List;

/**
 * 蓝牙GATT配置文件 - 空实现版本
 */
public class BleGattProfile {
    
    public List<BleGattService> getServices() {
        return null; // 空实现
    }
    
    public BleGattService getService(String uuid) {
        return null; // 空实现
    }
}