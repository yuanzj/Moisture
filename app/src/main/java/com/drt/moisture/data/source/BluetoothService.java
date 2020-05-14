package com.drt.moisture.data.source;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.SetDeviceInfoParame;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.*;
import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetDeviceInfoRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;
import com.drt.moisture.data.source.bluetooth.resquest.TimingSetRequest;

public interface BluetoothService {

    /**
     * 处理蓝牙返回数据
     *
     * @param data
     */
    void parse(byte[] data);

    /**
     * 开始测量
     *
     * @param name
     * @param measureModel
     * @param interval
     * @param time
     * @param sppDataCallback
     */
    void startMeasure(String name, int measureModel, int interval, int time, SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry);

    /**
     * 开始测量
     *
     * @param name
     * @param measureModel
     * @param interval
     * @param time
     * @param index
     * @param sppDataCallback
     * @param retry
     */
    void startMeasure(String name, int measureModel, int interval, int time, int index, SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry);


    /**
     * 开始校准
     *
     * @param mode
     * @param type
     * @param time
     * @param sppDataCallback
     */
    void startCorrect(int mode, int type, int time, SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry);

    /**
     * 开始校准
     *
     * @param mode
     * @param type
     * @param count
     * @param sppDataCallback
     */
    void startCorrect(int mode, int type, int time, int count, SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry);


    /**
     * 停止测量
     *
     * @param sppDataCallback
     */
    void stopMeasure(SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 停止测量
     *
     * @param sppDataCallback
     */
    void stopMeasure(int index, SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 停止校准
     *
     * @param sppDataCallback
     */
    void stopCorrect(SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 停止校准
     *
     * @param sppDataCallback
     */
    void stopCorrect(int pintCount, SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param time
     * @param sppDataCallback
     */
    void queryRecord(long time, SppDataCallback<RecordDataResponse> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param time
     * @param sppDataCallback
     */
    void queryDashboardRecord(long time, int pointCount, SppDataCallback<DashboardRecordDataResponse> sppDataCallback);


    /**
     * 查询自动测量
     *
     * @param time
     * @param sppDataCallback
     */
    void queryAutoRecord(long time, SppDataCallback<AutoRecordDataResponse> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param type
     * @param sppDataCallback
     */
    void queryCorrect(int measureModel, int type, int interval, long time, SppDataCallback<CorrectDataResponse> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param type
     * @param sppDataCallback
     */
    void queryCorrect(int measureModel, int type, int interval, long time, int pointCount, SppDataCallback<CorrectDataResponse> sppDataCallback);

    /**
     * 查询设备信息
     *
     * @param sppDataCallback
     */
    void queryDeviceInfo(SppDataCallback<DeviceInfoResponse> sppDataCallback, boolean retry);


    /**
     * 设置设备信息
     *
     * @param setDeviceInfoRequest
     * @param sppDataCallback
     */
    void setDeviceInfo(SetDeviceInfoParame setDeviceInfoRequest, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     * 设置时间
     *
     * @param time
     * @param sppDataCallback
     */
    void setTime(long time, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     * 设置测点数量
     *
     * @param count
     * @param sppDataCallback
     */
    void setCdsl(int count, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     * 设置时间
     *
     * @param time
     */
    void setTime(long time);


    /**
     * 设置测量参数
     *
     * @param measureParame
     * @param sppDataCallback
     */
    void setMeasureParame(MeasureParame measureParame, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry, int type);


    /**
     * 设置校准参数
     *
     * @param correctParame
     * @param sppDataCallback
     */
    void setCorrectParame(CorrectParame correctParame, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry, int type);

    /**
     * 设置频率
     *
     * @param sppDataCallback
     */
    void setRateParam(int rate, int ratio, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     * @param humidityParame
     * @param sppDataCallback
     */
    void setHumidityParame(HumidityParame humidityParame, SppDataCallback<ParameterSetResponse> sppDataCallback);

    /**
     * 重置
     *
     * @param sppDataCallback
     */
    void reset(SppDataCallback<ParameterSetResponse> sppDataCallback);

    /**
     * 查询测量参数
     *
     * @param sppDataCallback
     */
    void queryMeasureParame(int index, int type, SppDataCallback<SetMeasureParameRequest> sppDataCallback);

    /**
     * 查询校准参数
     *
     * @param sppDataCallback
     */
    void queryCorrectParam(int index, int type, SppDataCallback<SetCorrectParameRequest> sppDataCallback);

    /**
     * 查询湿度
     *
     * @param sppDataCallback
     */
    void queryHumidityParam(int index, int type, SppDataCallback<SetHumidityParameRequest> sppDataCallback);

    /**
     * 设置定时时间
     *
     * @param timingSetRequest
     * @param sppDataCallback
     */
    void setTiming(TimingSetRequest timingSetRequest, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     * 设置定时时间
     *
     * @param sppDataCallback
     */
    void setMeasureName(int index, String name, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     * 查询测量参数
     *
     * @param sppDataCallback
     */
    void queryRateParame(SppDataCallback<SetRateRequest> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param sppDataCallback
     */
    void queryHisRecord(int pointIndex, String name, int index, int status, SppDataCallback<HisRecordDataResponse> sppDataCallback);


    /**
     * 查询电量
     *
     * @param sppDataCallback
     */
    void querySoc(SppDataCallback<SocResponse> sppDataCallback);

    /**
     * 查询测点数量
     *
     * @param sppDataCallback
     */
    void queryClsl(SppDataCallback<CdslSetResponse> sppDataCallback);


    /**
     * 查询定时时间
     *
     * @param sppDataCallback
     */
    void queryTiming(SppDataCallback<TimingSetResponse> sppDataCallback, boolean retry);

}
