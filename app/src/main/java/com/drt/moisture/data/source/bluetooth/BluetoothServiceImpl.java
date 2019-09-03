package com.drt.moisture.data.source.bluetooth;

import android.content.Context;
import android.util.Log;

import com.drt.moisture.App;
import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;
import com.drt.moisture.data.source.bluetooth.resquest.DeviceInfoRequest;
import com.drt.moisture.data.source.bluetooth.resquest.RecordDataRequest;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.UUIDUtils;
import com.rokyinfo.convert.exception.FieldConvertException;
import com.rokyinfo.convert.exception.RkFieldException;
import com.rokyinfo.convert.util.ByteConvert;

import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;

public class BluetoothServiceImpl implements BluetoothService, BleWriteResponse{

    private static final String TAG = BluetoothServiceImpl.class.getSimpleName();

    private volatile SppDataCallback sppDataCallback;

    public BluetoothServiceImpl(Context context) {

    }

    @Override
    public void parse(byte[] data) {
        if (sppDataCallback != null) {
            try {
                sppDataCallback.delivery(BluetoothDataUtil.decode(sppDataCallback.getEntityType(), data));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (FieldConvertException e) {
                e.printStackTrace();
            } catch (RkFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void queryRecord(long time, SppDataCallback<RecordDataResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        RecordDataRequest recordDataRequest = new RecordDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA3);
        recordDataRequest.setCmd((byte) 0x05);
        recordDataRequest.setResponse((byte) 0x01);
        recordDataRequest.setReserved(0);
        recordDataRequest.setTime(ByteConvert.uintToBytes(time));
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(recordDataRequest), this);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RkFieldException e) {
            e.printStackTrace();
        } catch (FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryDeviceInfo(SppDataCallback<DeviceInfoResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        DeviceInfoRequest deviceInfoRequest = new DeviceInfoRequest();
        deviceInfoRequest.setCmdGroup((byte) 0xA1);
        deviceInfoRequest.setCmd((byte) 0x05);
        deviceInfoRequest.setResponse((byte) 0x01);
        deviceInfoRequest.setReserved(0);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(deviceInfoRequest), this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RkFieldException e) {
            e.printStackTrace();
        } catch (FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int code) {
        if (code == REQUEST_SUCCESS) {
            Log.d(TAG, "onResponse success");
        }
    }
}
