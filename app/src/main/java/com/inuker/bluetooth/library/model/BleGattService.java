package com.inuker.bluetooth.library.model;

import java.util.List;

/**
 * 蓝牙GATT服务 - 空实现版本
 */
public class BleGattService {
    
    public String getUUID() {
        return null; // 空实现
    }
    
    public List<BleGattCharacter> getCharacters() {
        return null; // 空实现
    }
    
    public BleGattCharacter getCharacter(String uuid) {
        return null; // 空实现
    }
}