package com.drt.moisture.measure;

import android.annotation.SuppressLint;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.AutoRecordDataResponse;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;
import com.drt.moisture.data.source.bluetooth.response.StopMeasureResponse;
import com.drt.moisture.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MeasureModel implements MeasureContract.Model {

    private static final String TAG = MeasureModel.class.getSimpleName();

    private Timer runningTimer, stopTimer, clockerTime;

    private Date startTime;

    private volatile MeasureDataCallback<MeasureValue> measureDataCallback;

    private MeasureValue measureValue = new MeasureValue();

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
    public void startMeasure(String name, int model, final MeasureDataCallback<StartMeasureResponse> callback) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        int period = appConfig.getPeriod();
        int measuringTime = appConfig.getMeasuringTime();
        App.getInstance().getBluetoothService().startMeasure(name, model, period, measuringTime, new SppDataCallback<StartMeasureResponse>() {

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
    public synchronized void startQuery(final int model, MeasureDataCallback<MeasureValue> callback) {
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
                // 发送蓝牙请求
                if (model == 0) {
                    App.getInstance().getBluetoothService().queryRecord(System.currentTimeMillis() / 1000, sppDataCallback);
                } else {
                    App.getInstance().getBluetoothService().queryAutoRecord(System.currentTimeMillis() / 1000, sppDataCallback2);

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
                measureDataCallback.runningTime(DateUtil.dateDistance(startTime, new Date()));
            }
        }, 0, 1000);

        // 启动关闭测量定时器
        if (model == 0) {
            int measuringTime = appConfig.getMeasuringTime();
            if (stopTimer != null) {
                stopTimer.cancel();
            }
            stopTimer = new Timer();
            stopTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopQuery(true);
                    measureDataCallback.measureDone();
                }
            }, measuringTime * 60 * 1000);
        } else {
            // 按照业务达到一定的平准值后停止测量
        }
    }

    @Override
    public void stopQuery(boolean sendCommand) {
        if (runningTimer != null) {
            runningTimer.cancel();
        }
        if (stopTimer != null) {
            stopTimer.cancel();
        }
        if (clockerTime != null) {
            clockerTime.cancel();
        }

        if (sendCommand) {
            App.getInstance().getBluetoothService().stopMeasure(new SppDataCallback<StopMeasureResponse>() {

                @Override
                public void delivery(StopMeasureResponse stopMeasureResponse) {

                }

                @Override
                public Class<StopMeasureResponse> getEntityType() {
                    return StopMeasureResponse.class;
                }
            });
        }
    }

    SppDataCallback sppDataCallback = new SppDataCallback<RecordDataResponse>(){

        @Override
        public void delivery(RecordDataResponse recordDataResponse) {
            if (measureDataCallback != null) {

                measureValue.setTemperature(recordDataResponse.getTemperature() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity() / 10000.0);
                measureValue.setHumidity(0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(0);
                measureDataCallback.success(measureValue);
            }
        }

        @Override
        public Class<RecordDataResponse> getEntityType() {
            return RecordDataResponse.class;
        }
    };

    SppDataCallback sppDataCallback2 = new SppDataCallback<AutoRecordDataResponse>(){

        @Override
        public void delivery(AutoRecordDataResponse recordDataResponse) {
            if (measureDataCallback != null) {

                measureValue.setTemperature(recordDataResponse.getTemperature() / 100.0);
                measureValue.setActivity(recordDataResponse.getActivity() / 10000.0);
                measureValue.setHumidity(0);
                measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
                measureValue.setName("");
                measureValue.setMeasureStatus(recordDataResponse.getMeasureStatus());
                measureDataCallback.success(measureValue);
            }
        }

        @Override
        public Class<AutoRecordDataResponse> getEntityType() {
            return AutoRecordDataResponse.class;
        }
    };
}
