package com.drt.moisture.setting;

import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;

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
}
