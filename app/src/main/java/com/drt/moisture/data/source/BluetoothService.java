package com.drt.moisture.data.source;

import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;
import com.drt.moisture.data.source.bluetooth.response.StopMeasureResponse;

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
     * 停止测量
     * @param sppDataCallback
     */
    void stopMeasure(SppDataCallback<StopMeasureResponse> sppDataCallback);

    /**
     * 查询测量记录
     *
     * @param time
     * @param sppDataCallback
     */
    void queryRecord(long time, SppDataCallback<RecordDataResponse> sppDataCallback);

    /**
     * 查询设备信息
     *
     * @param sppDataCallback
     */
    void queryDeviceInfo(SppDataCallback<DeviceInfoResponse> sppDataCallback);


}
