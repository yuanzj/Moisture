package com.drt.moisture.report;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.drt.moisture.dashboard.DashboardModel;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import net.yzj.android.common.base.BasePresenter;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportPresenter extends BasePresenter<ReportContract.View> implements ReportContract.Presenter {

    private ReportContract.Model model;

    public ReportPresenter() {
        model = new ReportModel();
    }

    @Override
    public void queryReport(EditText measureName, Date startTime, Date endTime) {

//        if (TextUtils.isEmpty(measureName.getText())) {
//            mView.onError(new Exception("请输入样品名称"));
//            return;
//        }

        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryReport(measureName.getText().toString(),startTime, endTime, new MeasureDataCallback<List<MeasureValue>>() {

            @Override
            public void runningStatus(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap) {

            }

            @Override
            public void success(List<MeasureValue> value) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSuccess(value);
                }
            }

            @Override
            public void measureDone(int index) {
                if (isViewAttached()) {
                    mView.onDone();
                }
            }

            @Override
            public void error(Throwable e) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onError(e);
                }
            }
        });
        measureName.setEnabled(false);
    }

    @Override
    public void stop() {
        model.stop();
    }
}
