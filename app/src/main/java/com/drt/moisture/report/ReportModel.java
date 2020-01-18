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

    String measureName;
    Date startTime, endTime;

    volatile boolean running;

    @Override
    public void queryReport(final String measureName, final Date startTime, final Date endTime, final MeasureDataCallback<List<MeasureValue>> report) {

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

                    App.getInstance().getBluetoothService().queryHisRecord(measureName, i, ReportModel.this);
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

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void delivery(HisRecordDataResponse recordDataResponse) {

        if (recordDataResponse.getResponse() == 4 || recordDataResponse.getResponse() == 6) {
            return;
        }

        if (recordDataResponse.getTime() == 0 || recordDataResponse.getResponse() == 5) {
            running = false;
            return;
        }

        Date reportDate = new Date(recordDataResponse.getTime() * 1000);
        if (reportDate.after(startTime) && reportDate.before(endTime)) {
            List<MeasureValue> values = new ArrayList<>();
            MeasureValue measureValue = new MeasureValue();
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
    }

    @Override
    public Class<HisRecordDataResponse> getEntityType() {
        return HisRecordDataResponse.class;
    }
}
