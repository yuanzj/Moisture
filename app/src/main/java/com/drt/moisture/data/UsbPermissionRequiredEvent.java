package com.drt.moisture.data;

import android.hardware.usb.UsbDevice;

public class UsbPermissionRequiredEvent {
    private UsbDevice device;
    
    public UsbPermissionRequiredEvent(UsbDevice device) {
        this.device = device;
    }
    
    public UsbDevice getDevice() {
        return device;
    }
}