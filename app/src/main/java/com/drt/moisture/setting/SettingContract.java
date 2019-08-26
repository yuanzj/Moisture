package com.drt.moisture.setting;

import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;

import net.yzj.android.common.base.BaseView;

public interface SettingContract {

    interface Model {
        /**
         * 查询设备信息
         *
         * @param dataCallback
         */
        void queryDeviceInfo(DataCallback<DeviceInfo> dataCallback);
    }

    interface View extends BaseView {

        void onDeviceInfoSuccess(DeviceInfo deviceInfo);

    }

    interface Presenter {
        void queryDeviceInfo();
    }
}
