package com.drt.moisture.dashboard;

import android.annotation.SuppressLint;

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
import java.util.concurrent.ConcurrentHashMap;

public class DashboardModel implements DashboardContract.Model {

    private static final String TAG = DashboardModel.class.getSimpleName();

    private Timer runningTimer, clockerTime;

    private Map<Integer, Timer> timerList = new ConcurrentHashMap<>();
    private Map<Integer, MeasureRunningStatus> measureRunningStatusMap = new ConcurrentHashMap<>();

    private Date startTime;

    private volatile MeasureDataCallback<List<MeasureValue>> measureDataCallback;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

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
                MeasureRunningStatus mMeasureRunningStatus = new MeasureRunningStatus();
                mMeasureRunningStatus.setModel(model);
                mMeasureRunningStatus.setRunning(true);
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
    public void startAutoStopTimer(final int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        int measuringTime = appConfig.getMeasuringTime();
        Timer stopTimer = timerList.get(index);
        if (stopTimer != null) {
            stopTimer.cancel();
        }
        if (appConfig.getMeasureMode() == 0) {
            stopTimer = new Timer();
            stopTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopQuery(true, index);
                    measureDataCallback.measureDone();
                }
            }, measuringTime * 60 * 1000);
            timerList.put(index, stopTimer);
        }
    }

    @Override
    public synchronized void startQuery(final int model, final int pointCount, MeasureDataCallback<List<MeasureValue>> callback) {
        this.measureDataCallback = callback;
        // 启动定时查询定时器
        if (runningTimer != null) {
            runningTimer.cancel();
        }
        runningTimer = new Timer();
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        int period = appConfig.getPeriod();
        startTime = new Date();
        runningTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress()) != Constants.STATUS_DEVICE_CONNECTED) {
                    EventBus.getDefault().post(new BleEvent());
                    return;
                }

                // 发送蓝牙请求
                switch (pointCount) {
                    case 1:
                        App.getInstance().getBluetoothService().queryDashboardRecord(System.currentTimeMillis() / 1000, pointCount, point1CallBack);
                        break;
                    case 2:
                        App.getInstance().getBluetoothService().queryDashboardRecord(System.currentTimeMillis() / 1000, pointCount, point2CallBack);
                        break;
                    case 3:
                        App.getInstance().getBluetoothService().queryDashboardRecord(System.currentTimeMillis() / 1000, pointCount, point3CallBack);
                        break;
                    case 4:
                        App.getInstance().getBluetoothService().queryDashboardRecord(System.currentTimeMillis() / 1000, pointCount, point4CallBack);
                        break;
                    case 5:
                        App.getInstance().getBluetoothService().queryDashboardRecord(System.currentTimeMillis() / 1000, pointCount, point5CallBack);
                        break;
                    default:
                        App.getInstance().getBluetoothService().queryDashboardRecord(System.currentTimeMillis() / 1000, pointCount, point1CallBack);
                        break;
                }

//                DashboardRecordDataResponse3 mDashboardRecordDataResponse3 = new DashboardRecordDataResponse3();
//                mDashboardRecordDataResponse3.setTime(System.currentTimeMillis() / 1000);
//
//                mDashboardRecordDataResponse3.setActivity1((int) (Math.random() * 10000));
//                mDashboardRecordDataResponse3.setHumidity1((short) 100);
//                mDashboardRecordDataResponse3.setStatus1(0);
//                mDashboardRecordDataResponse3.setTemperature1((short) (60 * 100));
//
//                mDashboardRecordDataResponse3.setActivity2((int) (Math.random() * 10000));
//                mDashboardRecordDataResponse3.setHumidity2((short) 100);
//                mDashboardRecordDataResponse3.setStatus2(0);
//                mDashboardRecordDataResponse3.setTemperature2((short) (60 * 100));
//
//                mDashboardRecordDataResponse3.setActivity3((int) (Math.random() * 10000));
//                mDashboardRecordDataResponse3.setHumidity3((short) 100);
//                mDashboardRecordDataResponse3.setStatus3(0);
//                mDashboardRecordDataResponse3.setTemperature3((short) (60 * 100));
//
//                point3CallBack.delivery(mDashboardRecordDataResponse3);

            }
        }, 0, period);

        // 启动1S一次的运行时间通知
        if (clockerTime != null) {
            clockerTime.cancel();
        }
        clockerTime = new Timer();
        clockerTime.schedule(new TimerTask() {
            @Override
            public void run() {
                measureDataCallback.runningTime(measureRunningStatusMap, DateUtil.dateDistance1(startTime, new Date()));
            }
        }, 0, 1000);

    }

    private volatile long stopTime = 0;

    @Override
    public void stopQuery(boolean sendCommand, int index) {
        if (runningTimer != null) {
            runningTimer.cancel();
        }
        if (timerList.get(index) != null) {
            timerList.get(index).cancel();
        }
        if (clockerTime != null) {
            clockerTime.cancel();
        }
        MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(index);
        if (measureRunningStatus != null) {
            measureRunningStatus.setRunning(false);
            measureRunningStatusMap.put(index, measureRunningStatus);
        }

        if (sendCommand) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if ((System.currentTimeMillis() - stopTime) < 200) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    stopTime = System.currentTimeMillis();
                    App.getInstance().getBluetoothService().stopMeasure(stopMeasureResponse);
                }
            }, 200);
        }
    }

    SppDataCallback point1CallBack = new SppDataCallback<DashboardRecordDataResponse1>() {

        @Override
        public void delivery(DashboardRecordDataResponse1 recordDataResponse) {
            if (measureDataCallback != null) {

                MeasureValue measureValue = new MeasureValue();

                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity1() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity4() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity4() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature5() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity5() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity5() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
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

    public static class MeasureRunningStatus {
        int model;
        boolean isRunning;

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
    }
}
