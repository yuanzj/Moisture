package com.drt.moisture.syssetting;

import com.drt.moisture.App;
import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;
import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;

public class SettingModel implements SettingContract.Model {

    private DataCallback<DeviceInfo> dataCallback;

    private DataCallback<SetMeasureParameRequest> setMeasureParameRequestDataCallback;

    private DataCallback<SetCorrectParameRequest> setCorrectParameRequestDataCallback;

    private DataCallback<SetHumidityParameRequest> setHumidityParameRequestDataCallback;

    private DataCallback<ParameterSetResponse> parameterSetResponseDataCallback;

    private DataCallback<SetRateRequest> setRateRequestDataCallback;

    private DeviceInfo deviceInfo = new DeviceInfo();

    @Override
    public void queryDeviceInfo(DataCallback<DeviceInfo> _dataCallback) {
        this.dataCallback = _dataCallback;
        App.getInstance().getBluetoothService().queryDeviceInfo(new SppDataCallback<DeviceInfoResponse>() {

            @Override
            public void delivery(DeviceInfoResponse deviceInfoResponse) {
                if (dataCallback != null) {
                    deviceInfo.setBattery(new String(deviceInfoResponse.getBattery()));
                    deviceInfo.setModel(new String(deviceInfoResponse.getModel()));
                    deviceInfo.setName(new String(deviceInfoResponse.getName()));
                    deviceInfo.setSN(new String(deviceInfoResponse.getSN()));
                    deviceInfo.setVersion(new String(deviceInfoResponse.getVersion()));
                    dataCallback.delivery(deviceInfo);
                }
            }

            @Override
            public Class<DeviceInfoResponse> getEntityType() {
                return DeviceInfoResponse.class;
            }
        });

    }

    @Override
    public void queryMeasureConfig(DataCallback<SetMeasureParameRequest> _dataCallback) {
        this.setMeasureParameRequestDataCallback = _dataCallback;
        App.getInstance().getBluetoothService().queryMeasureParame(0x01, new SppDataCallback<SetMeasureParameRequest>() {
            @Override
            public void delivery(SetMeasureParameRequest setMeasureParameRequest) {
                if (setMeasureParameRequestDataCallback != null) {
                    setMeasureParameRequestDataCallback.delivery(setMeasureParameRequest);
                }
            }

            @Override
            public Class<SetMeasureParameRequest> getEntityType() {
                return SetMeasureParameRequest.class;
            }
        });
    }

    @Override
    public void queryCorrectConfig(DataCallback<SetCorrectParameRequest> _dataCallback) {
        this.setCorrectParameRequestDataCallback = _dataCallback;
        App.getInstance().getBluetoothService().queryCorrectParam(0x01, new SppDataCallback<SetCorrectParameRequest>() {
            @Override
            public void delivery(SetCorrectParameRequest setMeasureParameRequest) {
                if (setCorrectParameRequestDataCallback != null) {
                    setCorrectParameRequestDataCallback.delivery(setMeasureParameRequest);
                }
            }

            @Override
            public Class<SetCorrectParameRequest> getEntityType() {
                return SetCorrectParameRequest.class;
            }
        });
    }

    @Override
    public void queryHumidityConfig(DataCallback<SetHumidityParameRequest> _dataCallback) {
        this.setHumidityParameRequestDataCallback = _dataCallback;
        App.getInstance().getBluetoothService().queryHumidityParam(new SppDataCallback<SetHumidityParameRequest>() {
            @Override
            public void delivery(SetHumidityParameRequest setMeasureParameRequest) {
                if (setHumidityParameRequestDataCallback != null) {
                    setHumidityParameRequestDataCallback.delivery(setMeasureParameRequest);
                }
            }

            @Override
            public Class<SetHumidityParameRequest> getEntityType() {
                return SetHumidityParameRequest.class;
            }
        });
    }

    @Override
    public void queryRate(DataCallback<SetRateRequest> _dataCallback) {
        this.setRateRequestDataCallback = _dataCallback;
        App.getInstance().getBluetoothService().queryRateParame(new SppDataCallback<SetRateRequest>() {
            @Override
            public void delivery(SetRateRequest setMeasureParameRequest) {
                if (setRateRequestDataCallback != null) {
                    setRateRequestDataCallback.delivery(setMeasureParameRequest);
                }
            }

            @Override
            public Class<SetRateRequest> getEntityType() {
                return SetRateRequest.class;
            }
        });
    }

    @Override
    public void setTime(long time, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setTime(time, new SppDataCallback<ParameterSetResponse>() {

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
        App.getInstance().getBluetoothService().setMeasureParame(measureParame, new SppDataCallback<ParameterSetResponse>() {

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
        },false,0x01);
    }

    @Override
    public void setCorrectParame(CorrectParame measureParame, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setCorrectParame(measureParame, new SppDataCallback<ParameterSetResponse>() {

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
        },false,0x01);
    }

    @Override
    public void setHumidityParame(HumidityParame measureParame, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setHumidityParame(measureParame, new SppDataCallback<ParameterSetResponse>() {

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
    public void setRateParame(int rate, int ratio, DataCallback<ParameterSetResponse> dataCallback) {
        this.parameterSetResponseDataCallback = dataCallback;
        App.getInstance().getBluetoothService().setRateParam(rate, ratio, new SppDataCallback<ParameterSetResponse>() {

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
        },false);
    }

}
