package com.drt.moisture.report;

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
    public void queryReport() {
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.queryReport(new MeasureDataCallback<List<MeasureValue>>() {
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

            }

            @Override
            public void error(Throwable e) {
                if (isViewAttached()) {
                    mView.hideLoading();
                    mView.onError(e);
                }
            }
        });
    }
}
