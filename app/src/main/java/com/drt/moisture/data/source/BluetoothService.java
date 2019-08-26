package com.drt.moisture.data.source;

import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;

public interface BluetoothService {

    /**
     * 处理蓝牙返回数据
     *
     * @param data
     */
    void parse(byte[] data);

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
