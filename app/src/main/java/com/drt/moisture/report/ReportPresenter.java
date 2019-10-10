package com.drt.moisture.report;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import net.yzj.android.common.base.BasePresenter;

import java.util.List;

public class ReportPresenter extends BasePresenter<ReportContract.View> implements ReportContract.Presenter {

    private ReportContract.Model model;

    public ReportPresenter() {
        model = new ReportModel();
    }

    @Override
    public void queryReport(EditText measureName) {

        if (TextUtils.isEmpty(measureName.getText())) {
            mView.onError(new Exception("请输入样品名称"));
            return;
        }

        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryReport(measureName.getText().toString(), new MeasureDataCallback<List<MeasureValue>>() {
            @Override
            public void runningTime(String time) {

            }

            @Override
            public void success(List<MeasureValue> value) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onSuccess(value);
                }
            }

            @Override
            public void measureDone() {
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
