package com.drt.moisture.measure;

import android.annotation.SuppressLint;
import android.util.Log;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;
import com.drt.moisture.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MeasureModel implements MeasureContract.Model, SppDataCallback<RecordDataResponse> {

    private static final String TAG = MeasureModel.class.getSimpleName();

    private Timer startTimer, stopTimer, clockerTime;

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
    public void startQuery(int model, MeasureDataCallback<MeasureValue> callback) {
        this.measureDataCallback = callback;
        // 启动定时查询定时器
        if (startTimer != null) {
            startTimer.cancel();
        }
        startTimer = new Timer();
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        int period = appConfig.getPeriod();
        startTime = new Date();
        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 发送蓝牙请求
                App.getInstance().getBluetoothService().queryRecord( System.currentTimeMillis() / 1000, MeasureModel.this);
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
                    stopQuery();
                    measureDataCallback.measureDone();
                }
            }, measuringTime * 60 * 1000);
        } else {
            // 按照业务达到一定的平准值后停止测量
        }
    }

    @Override
    public void stopQuery() {
        if (startTimer != null) {
            startTimer.cancel();
        }
        if (stopTimer != null) {
            stopTimer.cancel();
        }
        if (clockerTime != null) {
            clockerTime.cancel();
        }
    }

    @Override
    public void delivery(RecordDataResponse recordDataResponse) {
        Log.d(TAG, "delivery：" + recordDataResponse);
        if (measureDataCallback != null) {

            measureValue.setTemperature(recordDataResponse.getTemperature() / 100.0);
            measureValue.setActivity(recordDataResponse.getActivity() / 100.0);
            measureValue.setHumidity(recordDataResponse.getHumidity() / 100.0);
            measureValue.setReportTime(sdf.format(new Date(recordDataResponse.getTime() * 1000)));
            measureValue.setName(String.valueOf(recordDataResponse.getName()));
            measureDataCallback.success(measureValue);
        }

    }

    @Override
    public Class<RecordDataResponse> getEntityType() {
        return RecordDataResponse.class;
    }
}
