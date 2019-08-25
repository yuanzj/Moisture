package com.drt.moisture.data.source.bluetooth;

import android.content.Context;

import com.drt.moisture.App;
import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;
import com.drt.moisture.data.source.bluetooth.resquest.RecordDataRequest;
import com.rokyinfo.convert.exception.FieldConvertException;
import com.rokyinfo.convert.exception.RkFieldException;

public class BluetoothServiceImpl implements BluetoothService {

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
    public void recordQuery(long time, SppDataCallback<RecordDataResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        RecordDataRequest recordDataRequest = new RecordDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA3);
        recordDataRequest.setCmd((byte) 0x05);
        recordDataRequest.setResponse((byte) 0x01);
        recordDataRequest.setReserved(0);
        recordDataRequest.setTime(time);
        try {
            App.getInstance().getBluetoothSPP().send(BluetoothDataUtil.encode(recordDataRequest), false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RkFieldException e) {
            e.printStackTrace();
        } catch (FieldConvertException e) {
            e.printStackTrace();
        }
    }

}
