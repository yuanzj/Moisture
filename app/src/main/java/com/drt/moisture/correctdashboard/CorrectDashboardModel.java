package com.drt.moisture.correctdashboard;


import android.annotation.SuppressLint;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.DashboardCorrectDataResponse1;
import com.drt.moisture.data.source.bluetooth.response.DashboardCorrectDataResponse2;
import com.drt.moisture.data.source.bluetooth.response.DashboardCorrectDataResponse3;
import com.drt.moisture.data.source.bluetooth.response.DashboardCorrectDataResponse4;
import com.drt.moisture.data.source.bluetooth.response.DashboardCorrectDataResponse5;
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

public class CorrectDashboardModel implements CorrectDashboardContract.Model {

    private static final String TAG = CorrectDashboardModel.class.getSimpleName();

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");


    private Timer runningTimer;
    private Date startTime;

    private Map<Integer, MeasureRunningStatus> measureRunningStatusMap = new ConcurrentHashMap<>();
    private BlockingDeque<CommandEntity> commandQueue = new LinkedBlockingDeque<>();

    private volatile CorrectDataCallback<List<MeasureValue>> CorrectDataCallback;

    private volatile boolean isRunning;

    private volatile int pointCount;

    /**
     * 0: 氯化钠
     * 1: 氯化镁
     */
    private int step;

    CorrectDashboardModel() {
        isRunning = true;
        statusCheckThread.start();
        queryThread.start();
    }

    @Override
    public void setCorrectMode(int mode, int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        appConfig.setCorrectMode(mode);
        App.getInstance().getLocalDataService().setAppConfig(index, appConfig);
    }

    @Override
    public void setCorrectType(int type, int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        appConfig.setCorrectType(type);
        App.getInstance().getLocalDataService().setAppConfig(index, appConfig);
    }

    @Override
    public int getCorrectTime(int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        return appConfig.getCorrectTime();
    }

    @Override
    public void setCorrectTime(int time, int index) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        appConfig.setCorrectTime(time);
        App.getInstance().getLocalDataService().setAppConfig(index, appConfig);
    }

    @Override
    public void startCorrect(final int model, final int type, final int count, final CorrectDataCallback<StartMeasureResponse> callback) {
        if (step > 1) {
            step = 0;
        }

        int correctTime = getCorrectTime(1);
        App.getInstance().getBluetoothService().startCorrect(model, type, correctTime, count, new SppDataCallback<StartMeasureResponse>() {

            @Override
            public void delivery(StartMeasureResponse startMeasureResponse) {
                for (int index = 1; index <= count; index++) {
                    startMeasureResponse.setIndex(index);
                    MeasureRunningStatus mMeasureRunningStatus = new MeasureRunningStatus();
                    mMeasureRunningStatus.setIndex(index);
                    mMeasureRunningStatus.setModel(model);
                    mMeasureRunningStatus.setRunning(true);
                    mMeasureRunningStatus.setStartTime(System.currentTimeMillis());
                    measureRunningStatusMap.put(index, mMeasureRunningStatus);
                }
                callback.success(startMeasureResponse);
            }

            @Override
            public Class<StartMeasureResponse> getEntityType() {
                return StartMeasureResponse.class;
            }
        }, false);

    }

    @Override
    public synchronized void startQuery(int _pointCount, CorrectDataCallback<List<MeasureValue>> callback) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(1);
        int period = appConfig.getPeriod();

        this.pointCount = _pointCount;
        this.CorrectDataCallback = callback;

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
    public void stopCorrect(boolean sendCommand) {

        for (int i = 0; i < pointCount; i++) {
            int index = i + 1;
            MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(index);
            if (measureRunningStatus != null) {
                measureRunningStatus.setRunning(false);
                measureRunningStatusMap.put(index, measureRunningStatus);
            }
        }

        if (runningTimer != null) {
            runningTimer.cancel();
        }

        if (sendCommand) {
            CommandEntity commandEntity = new CommandEntity();
            commandEntity.setType(1);
            try {
                commandQueue.put(commandEntity);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopAll() {
        stopCorrect(true);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
    }

    @Override
    public MeasureRunningStatus getCorrectRunningStatus(int index) {
        return measureRunningStatusMap.get(index);
    }

    SppDataCallback point1CallBack = new SppDataCallback<DashboardCorrectDataResponse1>() {

        @Override
        public void delivery(DashboardCorrectDataResponse1 recordDataResponse) {
            if (CorrectDataCallback != null) {

                MeasureValue measureValue = new MeasureValue();

                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                CorrectDataCallback.success(Collections.singletonList(measureValue));
            }
        }

        @Override
        public Class<DashboardCorrectDataResponse1> getEntityType() {
            return DashboardCorrectDataResponse1.class;
        }
    };

    SppDataCallback point2CallBack = new SppDataCallback<DashboardCorrectDataResponse2>() {

        @Override
        public void delivery(DashboardCorrectDataResponse2 recordDataResponse) {
            if (CorrectDataCallback != null) {

                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                CorrectDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardCorrectDataResponse2> getEntityType() {
            return DashboardCorrectDataResponse2.class;
        }
    };

    SppDataCallback point3CallBack = new SppDataCallback<DashboardCorrectDataResponse3>() {

        @Override
        public void delivery(DashboardCorrectDataResponse3 recordDataResponse) {
            if (CorrectDataCallback != null) {

                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(3);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                CorrectDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardCorrectDataResponse3> getEntityType() {
            return DashboardCorrectDataResponse3.class;
        }
    };

    SppDataCallback point4CallBack = new SppDataCallback<DashboardCorrectDataResponse4>() {

        @Override
        public void delivery(DashboardCorrectDataResponse4 recordDataResponse) {
            if (CorrectDataCallback != null) {

                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(3);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(4);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                CorrectDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardCorrectDataResponse4> getEntityType() {
            return DashboardCorrectDataResponse4.class;
        }
    };

    SppDataCallback point5CallBack = new SppDataCallback<DashboardCorrectDataResponse5>() {

        @Override
        public void delivery(DashboardCorrectDataResponse5 recordDataResponse) {
            if (CorrectDataCallback != null) {
                List<MeasureValue> measureValues = new ArrayList<>();

                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature1() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity1() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(1);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature2() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity2() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(2);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature3() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity3() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(3);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature4() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity4() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(4);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                measureValue = new MeasureValue();
                measureValue.setTemperature(recordDataResponse.getTemperature5() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity5() / 10000.0);
                measureValue.setReportTime(SIMPLE_DATE_FORMAT.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureRunningStatus = measureRunningStatusMap.get(5);
                if (measureRunningStatus != null) {
                    if (measureRunningStatus.isRunning) {
                        measureValue.setMeasureStatus(0x01);
                    } else {
                        measureValue.setMeasureStatus(0x02);
                    }
                } else {
                    measureValue.setMeasureStatus(0);
                }
                measureValues.add(measureValue);

                CorrectDataCallback.success(measureValues);
            }
        }

        @Override
        public Class<DashboardCorrectDataResponse5> getEntityType() {
            return DashboardCorrectDataResponse5.class;
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
                            AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(1);
                            int period = appConfig.getPeriod();

                            int correctMode = appConfig.getCorrectMode();
                            int correctType = appConfig.getCorrectType();
                            int correctTime = appConfig.getCorrectTime();
                            long time = correctTime * 60 * 1000 - (System.currentTimeMillis() - startTime.getTime());
                            switch (pointCount) {
                                case 1:
                                    App.getInstance().getBluetoothService().queryCorrect(correctMode, correctType, period, time, pointCount, point1CallBack);
                                    break;
                                case 2:
                                    App.getInstance().getBluetoothService().queryCorrect(correctMode, correctType, period, time, pointCount, point2CallBack);
                                    break;
                                case 3:
                                    App.getInstance().getBluetoothService().queryCorrect(correctMode, correctType, period, time, pointCount, point3CallBack);
                                    break;
                                case 4:
                                    App.getInstance().getBluetoothService().queryCorrect(correctMode, correctType, period, time, pointCount, point4CallBack);
                                    break;
                                case 5:
                                    App.getInstance().getBluetoothService().queryCorrect(correctMode, correctType, period, time, pointCount, point5CallBack);
                                    break;
                                default:
                                    App.getInstance().getBluetoothService().queryCorrect(correctMode, correctType, period, time, pointCount, point1CallBack);
                                    break;
                            }
                        }
                        break;
                        case 1: {
                            App.getInstance().getBluetoothService().stopCorrect(pointCount, stopMeasureResponse);
                            if (CorrectDataCallback != null) {
                                CorrectDataCallback.correctDone(step++);
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
                if (CorrectDataCallback != null && startTime != null) {
                    for (MeasureRunningStatus measureRunningStatus : measureRunningStatusMap.values()) {
                        if (measureRunningStatus.isRunning) {
                            measureRunningStatus.setRunningTime(DateUtil.dateDistance1(startTime, new Date()));
                        }
                    }
                    CorrectDataCallback.runningStatus(measureRunningStatusMap);
                }

                for (MeasureRunningStatus measureRunningStatus : measureRunningStatusMap.values()) {
                    if (measureRunningStatus.isRunning) {
                        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(1);
                        long measuringTime = measureRunningStatus.getStartTime() + appConfig.getCorrectTime() * 60 * 1000;
                        // 定时测量自动关闭
                        if (new Date().after(new Date(measuringTime))) {
                            stopCorrect(true);
                            break;
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


        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }
}
