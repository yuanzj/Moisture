package com.drt.moisture.setting;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DataCallback;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.SetDeviceInfoParame;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.CdslSetResponse;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;

import com.drt.moisture.data.source.bluetooth.response.TimingSetResponse;
import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetTimeRequest;
import com.drt.moisture.data.source.bluetooth.resquest.TimingSetRequest;

import net.yzj.android.common.base.BaseView;

public interface SettingContract {

    interface Model {
        /**
         * 查询设备信息
         *
         * @param dataCallback
         */
        void queryDeviceInfo(DataCallback<DeviceInfo> dataCallback);

        void setDeviceInfo(SetDeviceInfoParame setDeviceInfoParame, DataCallback<ParameterSetResponse> dataCallback);

        void queryMeasureConfig(DataCallback<SetMeasureParameRequest> dataCallback);

        void queryCorrectConfig(DataCallback<SetCorrectParameRequest> dataCallback);

        void queryHumidityConfig(DataCallback<SetHumidityParameRequest> dataCallback);

        void queryRate(DataCallback<SetRateRequest> dataCallback);

        void queryTiming(DataCallback<TimingSetResponse> dataCallback);

        void setTime(long time, DataCallback<ParameterSetResponse> dataCallback);

        void setMeasureParame(MeasureParame measureParame, DataCallback<ParameterSetResponse> dataCallback);

        void setCorrectParame(CorrectParame measureParame, DataCallback<ParameterSetResponse> dataCallback);

        void setHumidityParame(HumidityParame measureParame, DataCallback<ParameterSetResponse> dataCallback);

        void setRateParame(int rate, int ratio, DataCallback<ParameterSetResponse> dataCallback);

        void setCdsl(int count, DataCallback<ParameterSetResponse> sppDataCallback);

        void setTiming(TimingSetRequest timingSetRequest, DataCallback<ParameterSetResponse> sppDataCallback);

    }

    interface View extends BaseView {

        void onStartSetTimer();

        void onDeviceInfoSuccess(DeviceInfo deviceInfo);

        void onMeasureConfigSuccess(SetMeasureParameRequest deviceInfo);

        void onCorrectConfigSuccess(SetCorrectParameRequest deviceInfo);

        void onHumidityConfigSuccess(SetHumidityParameRequest deviceInfo);

        void onRateSuccess(SetRateRequest deviceInfo);

        void onTimingSuccess(TimingSetResponse timingSetResponse);

        void onSetTimeSuccess();

        void onSetParameSuccess();

    }

    interface Presenter {
        void queryDeviceInfo();

        void setDeviceInfo(SetDeviceInfoParame setDeviceInfoParame);

        void queryMeasureConfig();

        void queryCorrectConfig();

        void queryHumidityConfig();

        void queryRate();

        void queryTiming();

        void setTime(long time);

        void setMeasureParame(MeasureParame measureParame);

        void setCorrectParame(CorrectParame measureParame);

        void setHumidityParame(HumidityParame measureParame);

        void setRateParame(int rate,int ratio);

        void setCdsl(int count);

        void setTiming(TimingSetRequest timingSetRequest);
    }
}
