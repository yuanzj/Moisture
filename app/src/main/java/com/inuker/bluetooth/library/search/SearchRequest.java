package com.inuker.bluetooth.library.search;

/**
 * 搜索请求 - 空实现版本
 */
public class SearchRequest {
    
    public static class Builder {
        
        public Builder searchBluetoothLeDevice(int scanTime) {
            return this;
        }
        
        public Builder searchBluetoothLeDevice(int scanTime, int scanType) {
            return this;
        }
        
        public Builder searchBluetoothClassicDevice(int scanTime) {
            return this;
        }
        
        public Builder searchDeviceByName(String name) {
            return this;
        }
        
        public Builder searchDeviceByMac(String mac) {
            return this;
        }
        
        public SearchRequest build() {
            return new SearchRequest();
        }
    }
}