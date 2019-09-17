package com.drt.moisture.data.source.bluetooth;

import android.content.Context;
import android.util.Log;

import com.drt.moisture.App;
import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.bluetooth.response.DeviceInfoResponse;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;
import com.drt.moisture.data.source.bluetooth.response.StopMeasureResponse;
import com.drt.moisture.data.source.bluetooth.resquest.DeviceInfoRequest;
import com.drt.moisture.data.source.bluetooth.resquest.RecordDataRequest;
import com.drt.moisture.data.source.bluetooth.resquest.StartMeasureRequest;
import com.drt.moisture.data.source.bluetooth.resquest.StopMeasureRequest;
import com.drt.moisture.measure.MeasureModel;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.UUIDUtils;
import com.rokyinfo.convert.exception.FieldConvertException;
import com.rokyinfo.convert.exception.RkFieldException;
import com.rokyinfo.convert.util.ByteConvert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;

public class BluetoothServiceImpl implements BluetoothService, BleWriteResponse {

    private static final String TAG = BluetoothServiceImpl.class.getSimpleName();

    private volatile SppDataCallback sppDataCallback;

    private volatile int currentRetryCount = 0;

    private volatile Timer timeoutTimer;

    private int expectResponseCode;

    public BluetoothServiceImpl(Context context) {

    }

    @Override
    public void parse(byte[] data) {
        if (sppDataCallback != null) {
            try {
                Object object = BluetoothDataUtil.decode(sppDataCallback.getEntityType(), data);
                Method getCmdGroup = null;//得到方法对象
                try {
                    getCmdGroup = sppDataCallback.getEntityType().getMethod("getCmdGroup");
                    Object CmdGroup = getCmdGroup.invoke(object);//调用借钱方法，得到返回值
                    if (timeoutTimer != null && expectResponseCode == Integer.parseInt(CmdGroup.toString())) {
                        timeoutTimer.cancel();
                        timeoutTimer = null;
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


                sppDataCallback.delivery(object);
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
    public void startMeasure(final String name, final int measureModel, final int interval, final int time, final SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry) {
        this.sppDataCallback = sppDataCallback;

        StartMeasureRequest startMeasureRequest = new StartMeasureRequest();
        startMeasureRequest.setCmdGroup((byte) 0xA5);
        startMeasureRequest.setCmd((byte) 0x05);
        startMeasureRequest.setResponse((byte) 0x01);
        startMeasureRequest.setReserved(0);
        startMeasureRequest.setName(name);

//        测量方式<br/>0x01：自动测量<br/>0x02：定时测量，为该参数时，需要发送接下来的1字节定时时间
        if (measureModel == 0) {
            // 定时测量
            startMeasureRequest.setModel((byte) 0x02);
        } else {
            startMeasureRequest.setModel((byte) 0x01);
        }

//        测量间隔<br/>0x01：1.5s<BR/>0x02：3s<BR/>0x03：5s

        if (interval == 1500) {
            startMeasureRequest.setInterval((byte) 0x01);
        } else if (interval == 3000) {
            startMeasureRequest.setInterval((byte) 0x02);
        } else if (interval == 5000) {
            startMeasureRequest.setInterval((byte) 0x03);
        } else {
            startMeasureRequest.setInterval((byte) 0x01);
        }

        startMeasureRequest.setTime((byte) time);
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(startMeasureRequest), this);
            currentRetryCount++;
            if (!retry) {
                expectResponseCode = 0xA5;
                currentRetryCount = 0;
                if (timeoutTimer != null) {
                    timeoutTimer.cancel();
                }
                timeoutTimer = new Timer();
                timeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (currentRetryCount > 3) {
                            currentRetryCount = 0;
                            timeoutTimer.cancel();
                            timeoutTimer = null;
                        } else {
                            startMeasure(name, measureModel, interval, time, sppDataCallback, true);
                        }

                    }
                }, 3000, 3000);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RkFieldException e) {
            e.printStackTrace();
        } catch (FieldConvertException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void stopMeasure(SppDataCallback<StopMeasureResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        StopMeasureRequest deviceInfoRequest = new StopMeasureRequest();
        deviceInfoRequest.setCmdGroup((byte) 0xA5);
        deviceInfoRequest.setCmd((byte) 0x07);
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
