package com.drt.moisture.data.source;

import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;

public interface BluetoothService {

    /**
     * 处理蓝牙返回数据
     *
     * @param data
     */
    void parse(byte[] data);

    void recordQuery(long time, SppDataCallback<RecordDataResponse> sppDataCallback);
}
