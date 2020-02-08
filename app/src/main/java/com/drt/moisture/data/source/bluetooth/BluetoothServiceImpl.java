package com.drt.moisture.data.source.bluetooth;

import android.content.Context;
import android.util.Log;

import com.drt.moisture.App;
import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.SetDeviceInfoParame;
import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.bluetooth.response.*;
import com.drt.moisture.data.source.bluetooth.resquest.*;
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
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }


                sppDataCallback.delivery(object);
            } catch (InstantiationException | IllegalAccessException | FieldConvertException | RkFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startMeasure(final String name, final int measureModel, final int interval, final int time, final SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry) {
        startMeasure(name, measureModel, interval, time, 1, sppDataCallback, retry);
    }

    @Override
    public void startMeasure(final String name, final int measureModel, final int interval, final int time, final int index, final SppDataCallback<StartMeasureResponse> sppDataCallback, final boolean retry) {
        this.sppDataCallback = sppDataCallback;

        StartMeasureRequest startMeasureRequest = new StartMeasureRequest();
        startMeasureRequest.setCmdGroup((byte) 0xA5);
        startMeasureRequest.setCmd((byte) 0x05);
        startMeasureRequest.setResponse((byte) 0x01);
        startMeasureRequest.setReserved(0);
        startMeasureRequest.setIndex(index);
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
                        if (currentRetryCount >= 2) {
                            currentRetryCount = 0;
                            timeoutTimer.cancel();
                            timeoutTimer = null;
                        } else {
                            if (timeoutTimer != null) {
                                currentRetryCount++;
                                startMeasure(name, measureModel, interval, time, sppDataCallback, true);
                            }
                        }

                    }
                }, 300, 300);
            }

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startCorrect(final int mode, final int type, final int time, final SppDataCallback<StartMeasureResponse> sppDataCallback, boolean retry) {
        this.sppDataCallback = sppDataCallback;

        StartCorrectRequest startCorrectRequest = new StartCorrectRequest();
        startCorrectRequest.setCmdGroup((byte) 0xA5);
        startCorrectRequest.setCmd((byte) 0x09);
        startCorrectRequest.setResponse((byte) 0x01);
        startCorrectRequest.setReserved(0);

//       校准方式<br/>0x01：单点校准<br/>0x02：两点校准
        startCorrectRequest.setModel((byte) mode);

//        校准类型<br/>0x01：氯化钠校准<br/>0x02：氯化镁校准
        startCorrectRequest.setType((byte) type);

        startCorrectRequest.setTime((byte) time);
        if (0x02 == mode) {
            startCorrectRequest.setStep((byte) type);
        } else {
            startCorrectRequest.setStep((byte) mode);
        }


        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(startCorrectRequest), this);

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
                        if (currentRetryCount >= 2) {
                            currentRetryCount = 0;
                            timeoutTimer.cancel();
                            timeoutTimer = null;
                        } else {
                            if (timeoutTimer != null) {
                                currentRetryCount++;
                                startCorrect(mode, type, time, sppDataCallback, true);
                            }
                        }

                    }
                }, 300, 300);
            }

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopMeasure(SppDataCallback<StopMeasureResponse> sppDataCallback) {
        stopMeasure(1, sppDataCallback);
    }

    @Override
    public void stopMeasure(int index, SppDataCallback<StopMeasureResponse> sppDataCallback) {
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
            timeoutTimer = null;
        }
        this.sppDataCallback = sppDataCallback;

        StopMeasureRequest request = new StopMeasureRequest();
        request.setCmdGroup((byte) 0xA5);
        request.setCmd((byte) 0x07);
        request.setResponse((byte) 0x01);
        request.setReserved(0);
        request.setIndex((byte) index);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(request), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopCorrect(SppDataCallback<StopMeasureResponse> sppDataCallback) {
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
            timeoutTimer = null;
        }
        this.sppDataCallback = sppDataCallback;

        StopMeasureRequest request = new StopMeasureRequest();
        request.setCmdGroup((byte) 0xA5);
        request.setCmd((byte) 0x0A);
        request.setResponse((byte) 0x01);
        request.setReserved(0);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(request), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
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

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryDashboardRecord(long time, int pointCount, SppDataCallback<DashboardRecordDataResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        DashboardRecordDataRequest recordDataRequest = new DashboardRecordDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA3);
        recordDataRequest.setCmd((byte) 0x0A);
        recordDataRequest.setResponse((byte) 0x01);
        recordDataRequest.setReserved(0);
        recordDataRequest.setTime(ByteConvert.uintToBytes(time));
//        节点索引：<br>用每一位表示一个节点，且可以组合使用<br>0x0001：1号节点<br>0x0002：2号节点<br>0x0010：5号节点<br>0x001f：1~5，共5个节点
        switch (pointCount) {
            case 1:
                recordDataRequest.setIndex(0x01);
                break;
            case 2:
                recordDataRequest.setIndex(0x03);
                break;
            case 3:
                recordDataRequest.setIndex(0x07);
                break;
            case 4:
                recordDataRequest.setIndex(0x0F);
                break;
            case 5:
                recordDataRequest.setIndex(0x1F);
                break;
            default:
                recordDataRequest.setIndex(0x01);
                break;
        }
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(recordDataRequest), this);

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAutoRecord(long time, SppDataCallback<AutoRecordDataResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        RecordDataRequest recordDataRequest = new RecordDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA3);
        recordDataRequest.setCmd((byte) 0x05);
        recordDataRequest.setResponse((byte) 0x01);
        recordDataRequest.setReserved(0);
        recordDataRequest.setTime(ByteConvert.uintToBytes(time));
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(recordDataRequest), this);

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryCorrect(int measureModel, int type, int interval, long time, SppDataCallback<CorrectDataResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        CorrectDataRequest recordDataRequest = new CorrectDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA4);
//        命令编号
//        0x05 单点氯化钠
//        0x06 单点氯化镁
//        0x15 多点氯化钠
//        0x16 多点氯化镁

//        校准方式<br/>0x01：单点校准<br/>0x02：两点校准
//                        校准类型<br/>0x01：氯化钠校准<br/>0x02：氯化镁校准

        if (measureModel == 0x01 && type == 0x01) {
            recordDataRequest.setCmd((byte) 0x05);
        } else if (measureModel == 0x01 && type == 0x02) {
            recordDataRequest.setCmd((byte) 0x06);
        } else if (measureModel == 0x02 && type == 0x01) {
            recordDataRequest.setCmd((byte) 0x15);
        } else if (measureModel == 0x02 && type == 0x02) {
            recordDataRequest.setCmd((byte) 0x16);
        }


        recordDataRequest.setResponse((byte) 0x01);
        if (interval * 3 >= time) {
            recordDataRequest.setType(0x01);
        } else {
            recordDataRequest.setType(0x00);
        }

//        水分活度数据类型<br/>0x00：实际测试值<br/>0x01：真实值


        recordDataRequest.setReserved(0x00);
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(recordDataRequest), this);

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
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
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDeviceInfo(SetDeviceInfoParame setDeviceInfoRequest, SppDataCallback<ParameterSetResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        SetDeviceInfoRequest deviceInfoRequest = new SetDeviceInfoRequest();
        deviceInfoRequest.setCmdGroup((byte) 0xA1);
        deviceInfoRequest.setCmd((byte) 0x85);
        deviceInfoRequest.setResponse((byte) 0x01);
        deviceInfoRequest.setReserved(0);


        byte[] sn = new byte[16];
        byte[] snTemp = setDeviceInfoRequest.getSN().getBytes();

        for (int i = 0; i < 16; i++) {
            if (i < snTemp.length) {
                sn[i] = snTemp[i];
            } else {
                sn[i] = 0;
            }
        }

        byte[] battery = new byte[16];
        byte[] batteryTemp = setDeviceInfoRequest.getBattery().getBytes();

        for (int i = 0; i < 16; i++) {
            if (i < batteryTemp.length) {
                battery[i] = batteryTemp[i];
            } else {
                battery[i] = 0;
            }
        }

        byte[] model = new byte[16];
        byte[] modelTemp = setDeviceInfoRequest.getModel().getBytes();

        for (int i = 0; i < 16; i++) {
            if (i < modelTemp.length) {
                model[i] = modelTemp[i];
            } else {
                model[i] = 0;
            }
        }

        byte[] name = new byte[16];
        byte[] nameTemp = setDeviceInfoRequest.getName().getBytes();

        for (int i = 0; i < 16; i++) {
            if (i < nameTemp.length) {
                name[i] = nameTemp[i];
            } else {
                name[i] = 0;
            }
        }

        deviceInfoRequest.setSN(sn);
        deviceInfoRequest.setBattery(battery);
        deviceInfoRequest.setModel(model);
        deviceInfoRequest.setName(name);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(deviceInfoRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTime(long time, SppDataCallback<ParameterSetResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        SetTimeRequest setTimeRequest = new SetTimeRequest();
        setTimeRequest.setCmdGroup((byte) 0xA2);
        setTimeRequest.setCmd((byte) 0x05);
        setTimeRequest.setResponse((byte) 0x01);
        setTimeRequest.setReserved(0);
        setTimeRequest.setTime(time);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setTimeRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCdsl(int count, SppDataCallback<ParameterSetResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        SetCdslRequest setTimeRequest = new SetCdslRequest();
        setTimeRequest.setCmdGroup((byte) 0xA2);
        setTimeRequest.setCmd((byte) 0x0A);
        setTimeRequest.setResponse((byte) 0x01);
        setTimeRequest.setReserved(0);
        setTimeRequest.setCount(count);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setTimeRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTime(long time) {
        SetTimeRequest setTimeRequest = new SetTimeRequest();
        setTimeRequest.setCmdGroup((byte) 0xA2);
        setTimeRequest.setCmd((byte) 0x05);
        setTimeRequest.setResponse((byte) 0x02);
        setTimeRequest.setReserved(0);
        setTimeRequest.setTime(time);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setTimeRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMeasureParame(final MeasureParame measureParame, final SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry, final int type) {
        this.sppDataCallback = sppDataCallback;
        SetMeasureParameRequest setMeasureParameRequest = new SetMeasureParameRequest();
        setMeasureParameRequest.setCmdGroup((byte) 0xA2);
        setMeasureParameRequest.setCmd((byte) 0x06);
        setMeasureParameRequest.setResponse((byte) 0x01);
        setMeasureParameRequest.setReserved(0);
        setMeasureParameRequest.setType(type);
        setMeasureParameRequest.setA(measureParame.getA());
        setMeasureParameRequest.setB(measureParame.getB());
        setMeasureParameRequest.setC(measureParame.getC());
        setMeasureParameRequest.setD(measureParame.getD());
        setMeasureParameRequest.setE(measureParame.getE());
        setMeasureParameRequest.setF(measureParame.getF());
        setMeasureParameRequest.setG(measureParame.getG());
        setMeasureParameRequest.setH(measureParame.getH());
        setMeasureParameRequest.setI(measureParame.getI());
        setMeasureParameRequest.setJ(measureParame.getJ());
        setMeasureParameRequest.setK(measureParame.getK());
        setMeasureParameRequest.setL(measureParame.getL());
        setMeasureParameRequest.setM(measureParame.getM());
        setMeasureParameRequest.setN(measureParame.getN());
        setMeasureParameRequest.setO(measureParame.getCdsl());

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setMeasureParameRequest), this);

            if (!retry) {
                expectResponseCode = 0xA2;
                currentRetryCount = 0;
                if (timeoutTimer != null) {
                    timeoutTimer.cancel();
                }
                timeoutTimer = new Timer();
                timeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (currentRetryCount >= 2) {
                            currentRetryCount = 0;
                            timeoutTimer.cancel();
                            timeoutTimer = null;
                        } else {
                            if (timeoutTimer != null) {
                                currentRetryCount++;
                                setMeasureParame(measureParame, sppDataCallback, true, type);
                            }
                        }

                    }
                }, 800, 800);
            }


        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCorrectParame(final CorrectParame measureParame, final SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry, final int type) {
        this.sppDataCallback = sppDataCallback;
        SetCorrectParameRequest setCorrectParameRequest = new SetCorrectParameRequest();
        setCorrectParameRequest.setCmdGroup((byte) 0xA2);
        setCorrectParameRequest.setCmd((byte) 0x07);
        setCorrectParameRequest.setResponse((byte) 0x01);
        setCorrectParameRequest.setReserved(0);
        setCorrectParameRequest.setType(type);
        setCorrectParameRequest.setA(measureParame.getA());
        setCorrectParameRequest.setB(measureParame.getB());
        setCorrectParameRequest.setC(measureParame.getC());
        setCorrectParameRequest.setD(measureParame.getD());
        setCorrectParameRequest.setE(measureParame.getE());
        setCorrectParameRequest.setF(measureParame.getF());
        setCorrectParameRequest.setG(measureParame.getG());
        setCorrectParameRequest.setH(measureParame.getH());
        setCorrectParameRequest.setI(measureParame.getI());
        setCorrectParameRequest.setJ(measureParame.getJ());

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setCorrectParameRequest), this);

            if (!retry) {
                expectResponseCode = 0xA2;
                currentRetryCount = 0;
                if (timeoutTimer != null) {
                    timeoutTimer.cancel();
                }
                timeoutTimer = new Timer();
                timeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (currentRetryCount >= 2) {
                            currentRetryCount = 0;
                            timeoutTimer.cancel();
                            timeoutTimer = null;
                        } else {
                            if (timeoutTimer != null) {
                                currentRetryCount++;
                                setCorrectParame(measureParame, sppDataCallback, true, type);
                            }
                        }

                    }
                }, 800, 800);
            }

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setHumidityParame(HumidityParame measureParame, SppDataCallback<ParameterSetResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        SetHumidityParameRequest setMeasureParameRequest = new SetHumidityParameRequest();
        setMeasureParameRequest.setCmdGroup((byte) 0xA2);
        setMeasureParameRequest.setCmd((byte) 0x09);
        setMeasureParameRequest.setResponse((byte) 0x01);
        setMeasureParameRequest.setReserved(0);
        setMeasureParameRequest.setA(measureParame.getA());
        setMeasureParameRequest.setB(measureParame.getB());
        setMeasureParameRequest.setC(measureParame.getC());
        setMeasureParameRequest.setD(measureParame.getD());
        setMeasureParameRequest.setE(measureParame.getE());
        setMeasureParameRequest.setF(measureParame.getF());
        setMeasureParameRequest.setG(measureParame.getG());
        setMeasureParameRequest.setH(measureParame.getH());
        setMeasureParameRequest.setI(measureParame.getI());
        setMeasureParameRequest.setJ(measureParame.getJ());
        setMeasureParameRequest.setK(measureParame.getK());
        setMeasureParameRequest.setL(measureParame.getL());
        setMeasureParameRequest.setM(measureParame.getM());
        setMeasureParameRequest.setN(measureParame.getN());
        setMeasureParameRequest.setO(measureParame.getO());

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setMeasureParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset(SppDataCallback<ParameterSetResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        QueryParameRequest queryParameRequest = new QueryParameRequest();
        queryParameRequest.setCmdGroup((byte) 0xA1);
        queryParameRequest.setCmd((byte) 0x07);
        queryParameRequest.setResponse((byte) 0x01);
        queryParameRequest.setReserved(0);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(queryParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryMeasureParame(int type, SppDataCallback<SetMeasureParameRequest> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        QueryParameRequest queryParameRequest = new QueryParameRequest();
        queryParameRequest.setCmdGroup((byte) 0xA2);
        queryParameRequest.setCmd((byte) 0x86);
        queryParameRequest.setResponse((byte) 0x01);
        queryParameRequest.setReserved(0);
        queryParameRequest.setType(type);
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(queryParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryCorrectParam(int type, SppDataCallback<SetCorrectParameRequest> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        QueryParameRequest queryParameRequest = new QueryParameRequest();
        queryParameRequest.setCmdGroup((byte) 0xA2);
        queryParameRequest.setCmd((byte) 0x87);
        queryParameRequest.setResponse((byte) 0x01);
        queryParameRequest.setReserved(0);
        queryParameRequest.setType(type);
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(queryParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryHumidityParam(SppDataCallback<SetHumidityParameRequest> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        QueryParameRequest queryParameRequest = new QueryParameRequest();
        queryParameRequest.setCmdGroup((byte) 0xA2);
        queryParameRequest.setCmd((byte) 0x89);
        queryParameRequest.setResponse((byte) 0x01);
        queryParameRequest.setReserved(0);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(queryParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryRateParame(SppDataCallback<SetRateRequest> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        QueryParameRequest queryParameRequest = new QueryParameRequest();
        queryParameRequest.setCmdGroup((byte) 0xA2);
        queryParameRequest.setCmd((byte) 0x88);
        queryParameRequest.setResponse((byte) 0x01);
        queryParameRequest.setReserved(0);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(queryParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRateParam(final int rate, final int ratio, final SppDataCallback<ParameterSetResponse> sppDataCallback, boolean retry) {
        this.sppDataCallback = sppDataCallback;
        SetRateRequest setRateRequest = new SetRateRequest();
        setRateRequest.setCmdGroup((byte) 0xA2);
        setRateRequest.setCmd((byte) 0x08);
        setRateRequest.setResponse((byte) 0x01);
        setRateRequest.setReserved(0);
        setRateRequest.setRate(rate);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(setRateRequest), this);

            if (!retry) {
                expectResponseCode = 0xA2;
                currentRetryCount = 0;
                if (timeoutTimer != null) {
                    timeoutTimer.cancel();
                }
                timeoutTimer = new Timer();
                timeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (currentRetryCount >= 2) {
                            currentRetryCount = 0;
                            timeoutTimer.cancel();
                            timeoutTimer = null;
                        } else {
                            if (timeoutTimer != null) {
                                currentRetryCount++;
                                setRateParam(rate, ratio, sppDataCallback, true);
                            }
                        }

                    }
                }, 300, 300);
            }
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryHisRecord(String name, int index, SppDataCallback<HisRecordDataResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;

        HisRecordDataRequest recordDataRequest = new HisRecordDataRequest();
        recordDataRequest.setCmdGroup((byte) 0xA3);
        recordDataRequest.setCmd((byte) 0x06);
        recordDataRequest.setResponse((byte) 0x01);
        recordDataRequest.setReserved(0);
        recordDataRequest.setTime(ByteConvert.uintToBytes(System.currentTimeMillis() / 1000));
        recordDataRequest.setName(name);
        recordDataRequest.setIndex(index);
        if ("".equals(name)) {
            recordDataRequest.setQueryModel(0x02);
        } else {
            recordDataRequest.setQueryModel(0x03);
        }
        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(recordDataRequest), this);

        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void querySoc(SppDataCallback<SocResponse> sppDataCallback) {
        this.sppDataCallback = sppDataCallback;
        QueryParameRequest queryParameRequest = new QueryParameRequest();
        queryParameRequest.setCmdGroup((byte) 0xA1);
        queryParameRequest.setCmd((byte) 0x06);
        queryParameRequest.setResponse((byte) 0x01);
        queryParameRequest.setReserved(0);

        try {
            App.getInstance().getBluetoothClient().write(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), BluetoothDataUtil.encode(queryParameRequest), this);
        } catch (IllegalAccessException | RkFieldException | FieldConvertException e) {
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
