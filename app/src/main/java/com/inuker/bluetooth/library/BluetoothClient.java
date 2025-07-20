package com.inuker.bluetooth.library;

import android.content.Context;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.response.SearchResponse;

/**
 * 蓝牙客户端 - 空实现版本
 */
public class BluetoothClient {
    
    public BluetoothClient(Context context) {
        // 空实现
    }
    
    public static BluetoothClient getInstance(Context context) {
        return new BluetoothClient(context);
    }
    
    public void connect(String mac, BleConnectResponse response) {
        // 空实现 - 直接返回失败
        if (response != null) {
            response.onResponse(Constants.REQUEST_FAILED);
        }
    }
    
    public void disconnect(String mac) {
        // 空实现
    }
    
    public void search(SearchRequest request, SearchResponse response) {
        // 空实现 - 直接返回失败
        if (response != null) {
            response.onSearchStopped();
        }
    }
    
    public void stopSearch() {
        // 空实现
    }
    
    public void write(String mac, String serviceUUID, String characterUUID, byte[] bytes, BleWriteResponse response) {
        // 空实现 - 直接返回失败
        if (response != null) {
            response.onResponse(Constants.REQUEST_FAILED);
        }
    }
    
    public void write(String mac, java.util.UUID serviceUUID, java.util.UUID characterUUID, byte[] bytes, BleWriteResponse response) {
        // 空实现 - 直接返回失败
        if (response != null) {
            response.onResponse(Constants.REQUEST_FAILED);
        }
    }
    
    public void notify(String mac, String serviceUUID, String characterUUID, BleNotifyResponse response) {
        // 空实现 - 直接返回失败
        if (response != null) {
            response.onResponse(Constants.REQUEST_FAILED);
        }
    }
    
    public void unnotify(String mac, String serviceUUID, String characterUUID, BleNotifyResponse response) {
        // 空实现 - 直接返回失败
        if (response != null) {
            response.onResponse(Constants.REQUEST_FAILED);
        }
    }
    
    public void registerConnectStatusListener(String mac, BleConnectStatusListener listener) {
        // 空实现
    }
    
    public void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener) {
        // 空实现
    }
    
    public void registerBluetoothStateListener(BluetoothStateListener listener) {
        // 空实现
    }
    
    public void unregisterBluetoothStateListener(BluetoothStateListener listener) {
        // 空实现
    }
    
    public boolean isBluetoothOpened() {
        return false; // 空实现 - 返回蓝牙未开启
    }
    
    public boolean openBluetooth() {
        return false; // 空实现 - 返回开启失败
    }
    
    public boolean closeBluetooth() {
        return false; // 空实现 - 返回关闭失败
    }
    
    public int getConnectStatus(String mac) {
        return Constants.STATUS_DISCONNECTED; // 空实现 - 返回未连接状态
    }
}