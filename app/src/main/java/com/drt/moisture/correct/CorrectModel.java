package com.drt.moisture.correct;

import android.annotation.SuppressLint;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;
import com.drt.moisture.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CorrectModel implements CorrectContract.Model {

    private Timer startTimer, stopTimer, clockerTime;

    private Date startTime;

    /**
     * 0: 氯化钠
     * 1: 氯化镁
     */
    private int step;

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
    public void startCorrect(final int model, final CorrectDataCallback<MeasureValue> callback) {
        if (step > 1) {
            step = 0;
        }

        // 启动1S一次的运行时间通知
        startTime = new Date();
        if (clockerTime != null) {
            clockerTime.cancel();
        }
        clockerTime = new Timer();
        clockerTime.schedule(new TimerTask() {
            @Override
            public void run() {
                callback.runningTime(DateUtil.dateDistance(startTime, new Date()));
            }
        }, 0, 1000);

        // 启动定时查询定时器
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
                measureValue.setReportTime(sdf.format(new Date()));
                callback.success(measureValue);
            }
        }, 0, period);

        // 启动关闭测量定时器
        int measuringTime = appConfig.getMeasuringTime();
        if (stopTimer != null) {
            stopTimer.cancel();
        }
        stopTimer = new Timer();
        stopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopCorrect();
                callback.correctDone(step++);
            }
        }, measuringTime * 60 * 1000);

    }

    @Override
    public void stopCorrect() {
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

}