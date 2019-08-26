package com.drt.moisture.setting;

import com.drt.moisture.App;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;

public class SettingModel implements SettingContract.Model, SppDataCallback<DeviceInfoResponse> {

    private DataCallback<DeviceInfo> dataCallback;
    private DeviceInfo deviceInfo = new DeviceInfo();

    @Override
    public void queryDeviceInfo(DataCallback<DeviceInfo> dataCallback) {
        this.dataCallback = dataCallback;
        App.getInstance().getBluetoothService().queryDeviceInfo(SettingModel.this);

    }

    @Override
    public void delivery(DeviceInfoResponse deviceInfoResponse) {

        if (this.dataCallback != null) {
            deviceInfo.setBattery(new String(deviceInfoResponse.getBattery()));
            deviceInfo.setModel(new String(deviceInfoResponse.getModel()));
            deviceInfo.setName(new String(deviceInfoResponse.getName()));
            deviceInfo.setSN(new String(deviceInfoResponse.getSN()));
            deviceInfo.setVersion(new String(deviceInfoResponse.getVersion()));
            this.dataCallback.delivery(deviceInfo);
        }

    }

    @Override
    public Class<DeviceInfoResponse> getEntityType() {
        return DeviceInfoResponse.class;
    }
}
