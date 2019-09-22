package com.drt.moisture.setting;

import com.drt.moisture.App;
import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;

public class SettingModel implements SettingContract.Model, SppDataCallback<DeviceInfoResponse> {

    private DataCallback<DeviceInfo> dataCallback;
    private DataCallback<ParameterSetResponse> parameterSetResponseDataCallback;
    private DeviceInfo deviceInfo = new DeviceInfo();

    @Override
    public void queryDeviceInfo(DataCallback<DeviceInfo> dataCallback) {
        this.dataCallback = dataCallback;
        App.getInstance().getBluetoothService().queryDeviceInfo(SettingModel.this);

    }

    @Override
    public void setTime(long time, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setTime(time, new SppDataCallback<ParameterSetResponse>(){

            @Override
            public void delivery(ParameterSetResponse parameterSetResponse) {
                if (parameterSetResponseDataCallback != null) {
                    parameterSetResponseDataCallback.delivery(parameterSetResponse);
                }
            }

            @Override
            public Class<ParameterSetResponse> getEntityType() {
                return ParameterSetResponse.class;
            }
        });
    }

    @Override
    public void setMeasureParame(MeasureParame measureParame, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setMeasureParame(measureParame, new SppDataCallback<ParameterSetResponse>(){

            @Override
            public void delivery(ParameterSetResponse parameterSetResponse) {
                if (parameterSetResponseDataCallback != null) {
                    parameterSetResponseDataCallback.delivery(parameterSetResponse);
                }
            }

            @Override
            public Class<ParameterSetResponse> getEntityType() {
                return ParameterSetResponse.class;
            }
        });
    }

    @Override
    public void setCorrectParame(CorrectParame measureParame, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setCorrectParame(measureParame, new SppDataCallback<ParameterSetResponse>(){

            @Override
            public void delivery(ParameterSetResponse parameterSetResponse) {
                if (parameterSetResponseDataCallback != null) {
                    parameterSetResponseDataCallback.delivery(parameterSetResponse);
                }
            }

            @Override
            public Class<ParameterSetResponse> getEntityType() {
                return ParameterSetResponse.class;
            }
        });
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
