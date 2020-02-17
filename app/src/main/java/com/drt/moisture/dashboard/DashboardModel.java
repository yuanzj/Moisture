package com.drt.moisture.dashboard;


import android.annotation.SuppressLint;
import android.util.Log;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DashboardRecordDataResponse1;
import com.drt.moisture.data.source.bluetooth.response.DashboardRecordDataResponse2;
import com.drt.moisture.data.source.bluetooth.response.DashboardRecordDataResponse3;
import com.drt.moisture.data.source.bluetooth.response.DashboardRecordDataResponse4;
import com.drt.moisture.data.source.bluetooth.response.DashboardRecordDataResponse5;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;
import com.drt.moisture.data.source.bluetooth.response.StopMeasureResponse;
import com.drt.moisture.util.DateUtil;
import com.inuker.bluetooth.library.Constants;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class DashboardModel implements DashboardContract.Model {

    private static final String TAG = DashboardModel.class.getSimpleName();

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");


    private Timer runningTimer;
    private Date startTime;

    private Map<Integer, MeasureRunningStatus> measureRunningStatusMap = new ConcurrentHashMap<>();
    private BlockingDeque<CommandEntity> commandQueue = new LinkedBlockingDeque<>();

    private volatile MeasureDataCallback<List<MeasureValue>> measureDataCallback;

    private volatile boolean isRunning;

    private volatile int pointCount;

    DashboardModel() {
        isRunning = true;
        statusCheckThread.start();
        queryThread.start();
    }

    @Override
    public int getMeasureTime(int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        return appConfig.getMeasuringTime();
    }

    @Override
    public void setMeasureTime(int period, int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        appConfig.setMeasuringTime(period);
        App.getInstance().getLocalDataService().setAppConfig(index, appConfig);
    }

    @Override
    public void setMeasureModel(int index, int model) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        appConfig.setMeasureMode(model);
        App.getInstance().getLocalDataService().setAppConfig(index, appConfig);
    }

    @Override
    public void startMeasure(String name, final int model, final int index, final MeasureDataCallback<StartMeasureResponse> callback) {

        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        int period = appConfig.getPeriod();
        int measuringTime = appConfig.getMeasuringTime();
        App.getInstance().getBluetoothService().startMeasure(name, model, period, measuringTime, index, new SppDataCallback<StartMeasureResponse>() {

            @Override
            public void delivery(StartMeasureResponse startMeasureResponse) {
                startMeasureResponse.setIndex(index);
                MeasureRunningStatus mMeasureRunningStatus = new MeasureRunningStatus();
                mMeasureRunningStatus.setIndex(index);
                mMeasureRunningStatus.setModel(model);
                mMeasureRunningStatus.setRunning(true);
                mMeasureRunningStatus.setStartTime(System.currentTimeMillis());
                measureRunningStatusMap.put(index, mMeasureRunningStatus);
                callback.success(startMeasureResponse);
            }

            @Override
            public Class<StartMeasureResponse> getEntityType() {
                return StartMeasureResponse.class;
            }
        }, false);

    }

    @Override
    public synchronized void startQuery(int _pointCount, MeasureDataCallback<List<MeasureValue>> callback) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        int period = appConfig.getPeriod();

        this.pointCount = _pointCount;
        this.measureDataCallback = callback;

        startTime = new Date();
        // 启动定时查询定时器
        if (runningTimer != null) {
            runningTimer.cancel();
        }
        runningTimer = new Timer();
        runningTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress()) != Constants.STATUS_DEVICE_CONNECTED) {
                    EventBus.getDefault().post(new BleEvent());
                    return;
                }
                CommandEntity commandEntity = new CommandEntity();
                commandEntity.setType(0);
                commandQueue.push(commandEntity);
            }
        }, 0, period);
    }

    @Override
    public void stopMeasure(boolean sendCommand, int index) {

        Log.d("yzj", "stopCorrect" + index);

        MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(index);
        if (measureRunningStatus != null) {
            measureRunningStatus.setRunning(false);
            measureRunningStatusMap.put(index, measureRunningStatus);
        }
        boolean needCancelTimer = true;
        for (MeasureRunningStatus value : measureRunningStatusMap.values()) {
            if (value.isRunning) {
                needCancelTimer = false;
                break;
            }
        }
        if (needCancelTimer) {
            if (runningTimer != null) {
                runningTimer.cancel();
            }
        }

        if (sendCommand) {
            CommandEntity commandEntity = new CommandEntity();
            commandEntity.setType(1);
            commandEntity.setIndex(index);
            try {
                commandQueue.put(commandEntity);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopAll() {
        for (int i = 0; i < pointCount; i++) {
            int index = i + 1;
            MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(index);
            if (measureRunningStatus != null && measureRunningStatus.isRunning) {
                stopMeasure(true, index);
            }
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
    }

    @Override
    public MeasureRunningStatus getMeasureRunningStatus(int index) {
        return measureRunningStatusMap.get(index);
    }

    SppDataCallback point1CallBack = new SppDataCallback<DashboardRecordDataResponse1>() {

        @Override
        public void delivery(DashboardRecordDataResponse1 recordDataResponse) {
            if (measureDataCallback != null) {

                MeasureValue measureValue = new MeasureValue();

                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus1());
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 1);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureDataCallback.success(Collections.singletonList(measureValue));
            }
        }

        @Override
        public Class<DashboardRecordDataResponse1> getEntityType() {
            return DashboardRecordDataResponse1.class;
        }
    };

    SppDataCallback point2CallBack = new SppDataCallback<DashboardRecordDataResponse2>() {

        @Override
        public void delivery(DashboardRecordDataResponse2 recordDataResponse) {
            if (measureDataCallback != null) {

                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus1());
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 1);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 2);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardRecordDataResponse2> getEntityType() {
            return DashboardRecordDataResponse2.class;
        }
    };

    SppDataCallback point3CallBack = new SppDataCallback<DashboardRecordDataResponse3>() {

        @Override
        public void delivery(DashboardRecordDataResponse3 recordDataResponse) {
            if (measureDataCallback != null) {

                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus1());
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 1);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 2);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus3());
                measureRunningStatus = measureRunningStatusMap.get(3);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 3);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardRecordDataResponse3> getEntityType() {
            return DashboardRecordDataResponse3.class;
        }
    };

    SppDataCallback point4CallBack = new SppDataCallback<DashboardRecordDataResponse4>() {

        @Override
        public void delivery(DashboardRecordDataResponse4 recordDataResponse) {
            if (measureDataCallback != null) {

                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus1());
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 1);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 2);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus3());
                measureRunningStatus = measureRunningStatusMap.get(3);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 3);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity4() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus4());
                measureRunningStatus = measureRunningStatusMap.get(4);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 4);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardRecordDataResponse4> getEntityType() {
            return DashboardRecordDataResponse4.class;
        }
    };

    SppDataCallback point5CallBack = new SppDataCallback<DashboardRecordDataResponse5>() {

        @Override
        public void delivery(DashboardRecordDataResponse5 recordDataResponse) {
            if (measureDataCallback != null) {
                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus1());
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 2);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 2);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus3());
                measureRunningStatus = measureRunningStatusMap.get(3);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 3);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity4() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus4());
                measureRunningStatus = measureRunningStatusMap.get(4);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 4);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature5() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity5() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity5() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus5());
                measureRunningStatus = measureRunningStatusMap.get(5);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.getModel() == 0) {
                        if (measureRunningStatus.isRunning) {
                            measureValue.setMeasureStatus(0x01);
                        } else {
                            measureValue.setMeasureStatus(0x02);
                        }
                    } else {
                        if (measureValue.getMeasureStatus() == 0x02) {
                            stopMeasure(true, 5);
                        }
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardRecordDataResponse5> getEntityType() {
            return DashboardRecordDataResponse5.class;
        }
    };

    private SppDataCallback<StopMeasureResponse> stopMeasureResponse = new SppDataCallback<StopMeasureResponse>() {

        @Override
        public void delivery(StopMeasureResponse stopMeasureResponse) {

        }

        @Override
        public Class<StopMeasureResponse> getEntityType() {
            return StopMeasureResponse.class;
        }
    };

    Thread queryThread = new Thread(new Runnable() {
        @Override
        public void run() {
            long preRunTime = System.currentTimeMillis();
            while (isRunning) {
                try {
                    long interval = System.currentTimeMillis() - preRunTime;
                    if (interval < 300) {
                        Thread.sleep((300 - interval));
                    }

                    CommandEntity commandEntity = commandQueue.take();
                    switch (commandEntity.getType()) {
                        case 0: {
                            long time = System.currentTimeMillis() / 1000;
                            switch (pointCount) {
                                case 1:
                                    App.getInstance().getBluetoothService().queryDashboardRecord(time, pointCount, point1CallBack);
                                    break;
                                case 2:
                                    App.getInstance().getBluetoothService().queryDashboardRecord(time, pointCount, point2CallBack);
                                    break;
                                case 3:
                                    App.getInstance().getBluetoothService().queryDashboardRecord(time, pointCount, point3CallBack);
                                    break;
                                case 4:
                                    App.getInstance().getBluetoothService().queryDashboardRecord(time, pointCount, point4CallBack);
                                    break;
                                case 5:
                                    App.getInstance().getBluetoothService().queryDashboardRecord(time, pointCount, point5CallBack);
                                    break;
                                default:
                                    App.getInstance().getBluetoothService().queryDashboardRecord(time, pointCount, point1CallBack);
                                    break;
                            }
                        }
                        break;
                        case 1: {
                            App.getInstance().getBluetoothService().stopMeasure(commandEntity.getIndex(), stopMeasureResponse);
                            if (measureDataCallback != null) {
                                measureDataCallback.measureDone(commandEntity.getIndex());
                            }
                        }
                        break;
                        default:
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                preRunTime = System.currentTimeMillis();
            }
        }
    });

    Thread statusCheckThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (measureDataCallback != null && startTime != null) {
                    for (MeasureRunningStatus measureRunningStatus : measureRunningStatusMap.values()) {
                        if (measureRunningStatus.isRunning) {
                            measureRunningStatus.setRunningTime(DateUtil.dateDistance1(startTime, new Date()));
                        }
                    }
                    measureDataCallback.runningStatus(measureRunningStatusMap);
                }

                for (MeasureRunningStatus measureRunningStatus : measureRunningStatusMap.values()) {
                    if (measureRunningStatus.isRunning) {
                        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(measureRunningStatus.getIndex());
                        long measuringTime = measureRunningStatus.getStartTime() + appConfig.getMeasuringTime() * 60 * 1000;
                        // 定时测量自动关闭
                        if (new Date().after(new Date(measuringTime))) {
                            stopMeasure(true, measureRunningStatus.getIndex());
                        }
                    }
                }
            }
        }
    });

    public static class MeasureRunningStatus {
        int index;
        int model;
        boolean isRunning;
        long startTime;
        String runningTime;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getModel() {
            return model;
        }

        public void setModel(int model) {
            this.model = model;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getRunningTime() {
            return runningTime;
        }

        public void setRunningTime(String runningTime) {
            this.runningTime = runningTime;
        }
    }

    public static class CommandEntity {

        /**
         * 0：查询
         * 1：停止
         */
        private int type;

        /**
         * 测点编号
         */
        private int index;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
