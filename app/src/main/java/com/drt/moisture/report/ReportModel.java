package com.drt.moisture.report;

import android.annotation.SuppressLint;

import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ReportModel implements ReportContract.Model {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void queryReport(final MeasureDataCallback<List<MeasureValue>> report) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送蓝牙请求
                // 获取数据

                List<MeasureValue> values = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    MeasureValue measureValue = new MeasureValue();
                    measureValue.setTemperature(new Random().nextInt(50));
                    measureValue.setActivity(new Random().nextInt(100));
                    measureValue.setReportTime(sdf.format(new Date()));
                    values.add(measureValue);
                }
                report.success(values);
            }
        };

        new Thread(runnable).start();


    }
}
