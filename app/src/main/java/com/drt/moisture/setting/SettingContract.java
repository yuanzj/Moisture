package com.drt.moisture.setting;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;

import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;
import net.yzj.android.common.base.BaseView;

public interface SettingContract {

    interface Model {
        /**
         * 查询设备信息
         *
         * @param dataCallback
         */
        void queryDeviceInfo(DataCallback<DeviceInfo> dataCallback);

        void queryMeasureConfig(DataCallback<SetMeasureParameRequest> dataCallback);

        void queryCorrectConfig(DataCallback<SetCorrectParameRequest> dataCallback);

        void queryRate(DataCallback<SetRateRequest> dataCallback);

        void setTime(long time, DataCallback<ParameterSetResponse> dataCallback);

        void setMeasureParame(MeasureParame measureParame, DataCallback<ParameterSetResponse> dataCallback);

        void setCorrectParame(CorrectParame measureParame, DataCallback<ParameterSetResponse> dataCallback);

        void setRateParame(int rate, int ratio, DataCallback<ParameterSetResponse> dataCallback);
    }

    interface View extends BaseView {

        void onDeviceInfoSuccess(DeviceInfo deviceInfo);

        void onMeasureConfigSuccess(SetMeasureParameRequest deviceInfo);

        void onCorrectConfigSuccess(SetCorrectParameRequest deviceInfo);

        void onRateSuccess(SetRateRequest deviceInfo);

        void onSetTimeSuccess();

        void onSetParameSuccess();

    }

    interface Presenter {
        void queryDeviceInfo();

        void queryMeasureConfig();

        void queryCorrectConfig();

        void queryRate();

        void setTime(long time);

        void setMeasureParame(MeasureParame measureParame);

        void setCorrectParame(CorrectParame measureParame);

        void setRateParame(int rate,int ratio);
    }
}
