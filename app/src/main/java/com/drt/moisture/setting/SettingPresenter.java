package com.drt.moisture.setting;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;

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
}
