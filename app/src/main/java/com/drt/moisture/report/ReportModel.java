package com.drt.moisture.report;

import android.annotation.SuppressLint;

import com.drt.moisture.App;
import com.drt.moisture.correct.CorrectModel;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.HisRecordDataResponse;
import com.drt.moisture.data.source.bluetooth.response.RecordDataResponse;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReportModel implements ReportContract.Model, SppDataCallback<HisRecordDataResponse> {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    MeasureDataCallback<List<MeasureValue>> report;
    Set<String> temp = new HashSet<>();

    String measureName;
    Date startTime, endTime;

    volatile boolean running;

    volatile int pointIndex;

    @Override
    public void queryReport(int index, final String measureName, final Date startTime, final Date endTime, final MeasureDataCallback<List<MeasureValue>> report) {
        pointIndex = index;
        temp.clear();
        this.measureName = measureName;
        this.report = report;
        this.startTime = startTime;
        this.endTime = endTime;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                // 发送蓝牙请求
                for (int i = 1; i <= 2000; i++) {

                    if (!running) {
                        break;
                    }

                    App.getInstance().getBluetoothService().queryHisRecord(pointIndex, measureName, i, 0x00, ReportModel.this);
//                    HisRecordDataResponse recordDataResponse = new HisRecordDataResponse();
//                    recordDataResponse.setName("text");
//                    recordDataResponse.setTime(System.currentTimeMillis()/1000);
//                    delivery( recordDataResponse);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

                report.measureDone(1);

            }
        };
        running = true;
        new Thread(runnable).start();
    }

    volatile int retryCount;
    volatile boolean isSuccess;

    @Override
    public void stop() {
        running = false;
        new Thread(() -> {
            retryCount = 0;
            isSuccess = false;
            while ((retryCount < 3) && (!isSuccess)) {
                App.getInstance().getBluetoothService().queryHisRecord(pointIndex, measureName, 1, 0x55, new SppDataCallback<HisRecordDataResponse>() {

                    @Override
                    public void delivery(HisRecordDataResponse hisRecordDataResponse) {
                        isSuccess = true;
                    }

                    @Override
                    public Class<HisRecordDataResponse> getEntityType() {
                        return HisRecordDataResponse.class;
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retryCount++;
            }
        }).start();

    }

    @Override
    public void delivery(HisRecordDataResponse recordDataResponse) {

        if (recordDataResponse.getResponse() == 4 || recordDataResponse.getResponse() == 6) {
            return;
        }

        if (recordDataResponse.getTime() == 0) {
            running = false;
            return;
        }

        Date reportDate = new Date(recordDataResponse.getTime() * 1000);
        if (reportDate.after(startTime) && reportDate.before(endTime) && !temp.contains(recordDataResponse.getIndex() + sdf.format(reportDate))) {
            temp.add(recordDataResponse.getIndex() + sdf.format(reportDate));
            List<MeasureValue> values = new ArrayList<>();
            MeasureValue measureValue = new MeasureValue();
            measureValue.setIndex(recordDataResponse.getIndex());
            measureValue.setTemperature(recordDataResponse.getTemperature() / 100.0);
            measureValue.setActivity(recordDataResponse.getActivity() / 10000.0);
            measureValue.setReportTime(sdf.format(reportDate));
            measureValue.setName(recordDataResponse.getName());
            measureValue.setHumidity(recordDataResponse.getHumidity() / 10000.0);
            values.add(measureValue);
            if (report != null) {
                report.success(values);
            }

        }

        if (recordDataResponse.getResponse() == 5) {
            running = false;
        }
    }

    @Override
    public Class<HisRecordDataResponse> getEntityType() {
        return HisRecordDataResponse.class;
    }
}
