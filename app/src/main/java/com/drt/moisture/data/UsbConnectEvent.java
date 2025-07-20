package com.drt.moisture.data;

public class UsbConnectEvent {
    private boolean connected;
    
    public UsbConnectEvent(boolean connected) {
        this.connected = connected;
    }
    
    public boolean isConnected() {
        return connected;
    }
}