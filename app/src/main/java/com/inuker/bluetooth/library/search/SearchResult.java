package com.inuker.bluetooth.library.search;

import android.bluetooth.BluetoothDevice;
import java.io.Serializable;

/**
 * 搜索结果 - 空实现版本
 */
public class SearchResult implements Serializable {
    
    public String getName() {
        return null; // 空实现
    }
    
    public String getAddress() {
        return null; // 空实现
    }
    
    public int getRssi() {
        return 0; // 空实现
    }
    
    public byte[] getScanRecord() {
        return null; // 空实现
    }
    
    public BluetoothDevice getDevice() {
        return null; // 空实现
    }
}