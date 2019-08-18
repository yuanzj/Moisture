package com.drt.moisture.measure;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.DataCallback;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MeasureModel implements MeasureContract.Model {

    private Timer startTimer, stopTimer;

    @Override
    public void setMeasurePeriod(int period) {
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        appConfig.setPeriod(period);
        App.getInstance().getLocalDataService().setAppConfig(appConfig);
    }

    @Override
    public void startQuery(int model, final DataCallback<MeasureValue> callback) {
        if (startTimer != null) {
            startTimer.cancel();
        }
        startTimer = new Timer();
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        int period = appConfig.getPeriod();

        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 发送蓝牙请求
                MeasureValue measureValue = new MeasureValue();
                measureValue.setTemperature(new Random().nextInt(50));
                measureValue.setActivity(new Random().nextInt(100));
                callback.success(measureValue);
            }
        }, 0, period);

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
                }
            }, measuringTime);
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
    }

}
