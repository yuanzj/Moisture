package com.drt.moisture.setting;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.SetDeviceInfoParame;
import com.drt.moisture.data.source.bluetooth.response.CdslSetResponse;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;

import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;
import net.yzj.android.common.base.BasePresenter;

public class SettingPresenter extends BasePresenter<SettingContract.View> implements SettingContract.Presenter {

    private SettingContract.Model model;

    public SettingPresenter() {
        model = new SettingModel();
    }


    @Override
    public void queryDeviceInfo() {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryDeviceInfo(new DataCallback<DeviceInfo>() {
            @Override
            public void delivery(DeviceInfo deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onDeviceInfoSuccess(deviceInfo);
                }
            }
        });
    }

    @Override
    public void setDeviceInfo(SetDeviceInfoParame setDeviceInfoParame) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setDeviceInfo(setDeviceInfoParame, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetParameSuccess();
                }
            }
        });
    }

    @Override
    public void queryMeasureConfig() {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryMeasureConfig(new DataCallback<SetMeasureParameRequest>() {
            @Override
            public void delivery(SetMeasureParameRequest deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onMeasureConfigSuccess(deviceInfo);
                }
            }
        });
    }

    @Override
    public void queryCorrectConfig() {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryCorrectConfig(new DataCallback<SetCorrectParameRequest>() {
            @Override
            public void delivery(SetCorrectParameRequest deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onCorrectConfigSuccess(deviceInfo);
                }
            }
        });
    }

    @Override
    public void queryHumidityConfig() {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryHumidityConfig(new DataCallback<SetHumidityParameRequest>() {
            @Override
            public void delivery(SetHumidityParameRequest deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onHumidityConfigSuccess(deviceInfo);
                }
            }
        });
    }

    @Override
    public void queryRate() {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryRate(new DataCallback<SetRateRequest>() {
            @Override
            public void delivery(SetRateRequest deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onRateSuccess(deviceInfo);
                }
            }
        });
    }

    @Override
    public void setTime(long time) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setTime(time, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetTimeSuccess();
                }
            }
        });
    }

    @Override
    public void setMeasureParame(MeasureParame measureParame) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setMeasureParame(measureParame, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetParameSuccess();
                }
            }
        });
    }

    @Override
    public void setCorrectParame(CorrectParame measureParame) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setCorrectParame(measureParame, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetParameSuccess();
                }
            }
        });
    }

    @Override
    public void setHumidityParame(HumidityParame measureParame) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setHumidityParame(measureParame, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetParameSuccess();
                }
            }
        });
    }

    @Override
    public void setRateParame(int rate,int ratio) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setRateParame(rate,ratio, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetParameSuccess();
                }
            }
        });
    }

    @Override
    public void setCdsl(int count) {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.setCdsl(count, new DataCallback<ParameterSetResponse>() {
            @Override
            public void delivery(ParameterSetResponse deviceInfo) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSetParameSuccess();
                }
            }
        });
    }
}
