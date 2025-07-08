package com.drt.moisture.util;

import android.content.Context;

/**
 * 模拟的蓝牙客户端类，替代不可用的BLE库
 */
public class MockBluetoothClient {
    
    public MockBluetoothClient(Context context) {
        // 构造函数
    }
    
    public boolean isBluetoothOpened() {
        return false;
    }
    
    public void openBluetooth() {
        // 空实现
    }
    
    public int getConnectStatus(String macAddress) {
        return BluetoothConstants.STATUS_DISCONNECTED;
    }
    
    public void disconnect(String macAddress) {
        // 空实现
    }
    
    public void connect(String macAddress, Object response) {
        // 空实现
    }
    
    public void notify(String macAddress, Object serviceUUID, Object characterUUID, Object response) {
        // 空实现
    }
    
    public void registerBluetoothStateListener(Object listener) {
        // 空实现
    }
    
    public void unregisterBluetoothStateListener(Object listener) {
        // 空实现
    }
    
    public void registerConnectStatusListener(String macAddress, Object listener) {
        // 空实现
    }
    
    public void unregisterConnectStatusListener(String macAddress, Object listener) {
        // 空实现
    }
    
    public void search(Object request, Object response) {
        // 空实现
    }
}