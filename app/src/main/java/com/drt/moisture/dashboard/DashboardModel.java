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
import java.util.Timer;
import java.util.TimerTask;

public class DashboardModel implements DashboardContract.Model {

    private static final String TAG = DashboardModel.class.getSimpleName();

    private Timer runningTimer, clockerTime;

    private List<Timer> timerList = new ArrayList<>();

    private Date startTime;

    private volatile MeasureDataCallback<List<MeasureValue>> measureDataCallback;


    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public int getMeasureTime() {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        return appConfig.getMeasuringTime();
    }

    @Override
    public void setMeasureTime(int period) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        appConfig.setMeasuringTime(period);
        App.getInstance().getLocalDataService().setAppConfig(appConfig);
    }

    @Override
    public void startMeasure(String name, int model, int index, final MeasureDataCallback<StartMeasureResponse> callback) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        int period = appConfig.getPeriod();
        int measuringTime = appConfig.getMeasuringTime();
        App.getInstance().getBluetoothService().startMeasure(name, model, period, measuringTime, index, new SppDataCallback<StartMeasureResponse>() {

            @Override
            public void delivery(StartMeasureResponse startMeasureResponse) {
                callback.success(startMeasureResponse);
            }

            @Override
            public Class<StartMeasureResponse> getEntityType() {
                return StartMeasureResponse.class;
            }
        }, false);
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
                measureDataCallback.runningTime(DateUtil.dateDistance1(startTime, new Date()));
            }
        }, 0, 1000);

        // 启动关闭测量定时器
        if (model == 0) {
            int measuringTime = appConfig.getMeasuringTime();
            if (timerList.size() > 0) {
                for (Timer stopTimer : timerList) {
                    stopTimer.cancel();
                }
                timerList.clear();
            }
            for (int i = 0; i < pointCount; i++) {
                Timer stopTimer = new Timer();
                final int currentIndex = i + 1;
                stopTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopQuery(true, currentIndex);
                        measureDataCallback.measureDone();
                    }
                }, measuringTime * 60 * 1000);
                timerList.add(stopTimer);
            }
        } else {
            // 按照业务达到一定的平准值后停止测量
        }
    }

    private volatile long stopTime = 0;

    @Override
    public void stopQuery(boolean sendCommand, int index) {
        if (runningTimer != null) {
            runningTimer.cancel();
        }
        if (timerList.size() >= index) {
            timerList.get(index-1).cancel();
        }
        if (clockerTime != null) {
            clockerTime.cancel();
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
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
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
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus3());
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
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus3());
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity4() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus4());
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
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity2() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus2());
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity3() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus3());
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity4() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus4());
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature5() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity5() / 10000.0);
                measureValue.setHumidity(recordDataResponse.getHumidity5() / 10000.0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getStatus5());
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
}
