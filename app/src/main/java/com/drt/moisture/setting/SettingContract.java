package com.drt.moisture.setting;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;

import net.yzj.android.common.base.BaseView;

public interface SettingContract {

    interface Model {
        /**
         * 查询设备信息
         *
         * @param dataCallback
         */
        void queryDeviceInfo(DataCallback<DeviceInfo> dataCallback);

        void setTime(long time, DataCallback<ParameterSetResponse> dataCallback);

        void setMeasureParame(MeasureParame measureParame, DataCallback<ParameterSetResponse> dataCallback);

        void setCorrectParame(CorrectParame measureParame, DataCallback<ParameterSetResponse> dataCallback);
    }

    interface View extends BaseView {

        void onDeviceInfoSuccess(DeviceInfo deviceInfo);

        void onSetTimeSuccess();

        void onSetParameSuccess();

    }

    interface Presenter {
        void queryDeviceInfo();

        void setTime(long time);

        void setMeasureParame(MeasureParame measureParame);

        void setCorrectParame(CorrectParame measureParame);
    }
}
