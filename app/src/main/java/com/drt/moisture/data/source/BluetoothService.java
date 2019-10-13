package com.drt.moisture.data.source;

import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.*;
import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;

public interface BluetoothService {

    /**
     * 处理蓝牙返回数据
     *
     * @param data
     */
    void parse(byte[] data);

    /**
     * 开始测量
     * @param name
     * @param measureModel
     * @param interval
     * @param time
     * @param sppDataCallback
     */
    void startMeasure(String name, int measureModel, int interval, int time, SppDataCallback<StartMeasureResponse> sppDataCallback,final boolean retry);

    /**
     * 开始校准
     * @param mode
     * @param type
     * @param time
     * @param sppDataCallback
     */
    void startCorrect(int mode, int type, int time, SppDataCallback<StartMeasureResponse> sppDataCallback,final boolean retry);


    /**
     * 停止测量
     * @param sppDataCallback
     */
    void stopMeasure(SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 停止校准
     * @param sppDataCallback
     */
    void stopCorrect(SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param time
     * @param sppDataCallback
     */
    void queryRecord(long time, SppDataCallback<RecordDataResponse> sppDataCallback);


    /**
     * 查询自动测量
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
    void queryCorrect(int measureModel,  int type, int interval, long time, SppDataCallback<CorrectDataResponse> sppDataCallback);

    /**
     * 查询设备信息
     *
     * @param sppDataCallback
     */
    void queryDeviceInfo(SppDataCallback<DeviceInfoResponse> sppDataCallback);


    /**
     * 设置时间
     * @param time
     * @param sppDataCallback
     */
    void setTime(long time, SppDataCallback<ParameterSetResponse> sppDataCallback);

    /**
     * 设置时间
     * @param time
     */
    void setTime(long time);


    /**
     * 设置测量参数
     * @param measureParame
     * @param sppDataCallback
     */
    void setMeasureParame(MeasureParame measureParame, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry, int type);


    /**
     * 设置校准参数
     * @param correctParame
     * @param sppDataCallback
     */
    void setCorrectParame(CorrectParame correctParame, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry, int type);

    /**
     * 设置频率
     * @param sppDataCallback
     */
    void setRateParam(int rate, int ratio, SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry);

    /**
     *
     * @param humidityParame
     * @param sppDataCallback
     */
    void setHumidityParame(HumidityParame humidityParame, SppDataCallback<ParameterSetResponse> sppDataCallback);

    void reset(SppDataCallback<ParameterSetResponse> sppDataCallback);
    /**
     * 查询测量参数
     * @param sppDataCallback
     */
    void queryMeasureParame(int type, SppDataCallback<SetMeasureParameRequest> sppDataCallback);

    /**
     * 查询校准参数
     * @param sppDataCallback
     */
    void queryCorrectParam(int type, SppDataCallback<SetCorrectParameRequest> sppDataCallback);

    /**
     * 查询湿度
     * @param sppDataCallback
     */
    void queryHumidityParam(SppDataCallback<SetHumidityParameRequest> sppDataCallback);


    /**
     * 查询测量参数
     * @param sppDataCallback
     */
    void queryRateParame(SppDataCallback<SetRateRequest> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param sppDataCallback
     */
    void queryHisRecord(String name,int index, SppDataCallback<HisRecordDataResponse> sppDataCallback);


    /**
     * 查询电量
     * @param sppDataCallback
     */
    void querySoc(SppDataCallback<SocResponse> sppDataCallback);

}
