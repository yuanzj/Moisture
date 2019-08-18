package com.drt.moisture.measure;

import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.DataCallback;

import net.yzj.android.common.base.BasePresenter;

public class MeasurePresenter extends BasePresenter<MeasureContract.View> implements MeasureContract.Presenter {

    private MeasureContract.Model model;

    public MeasurePresenter() {
        model = new MeasureModel();
    }

    @Override
    public void setMeasurePeriod(int period) {
        model.setMeasurePeriod(period);
    }

    @Override
    public void startMeasure(int measureModel, DataCallback<MeasureValue> callback) {
        if (!isViewAttached()) {
            return;
        }
        mView.startMeasure();
        model.startQuery(measureModel, new DataCallback<MeasureValue>() {

            @Override
            public void success(MeasureValue value) {
                mView.stopMeasure();
                // 绑定数据
                mView.onSuccess(value);
            }

            @Override
            public void error(Throwable e) {
                mView.stopMeasure();
                mView.onError(e);
            }

        });
    }

    @Override
    public void stopMeasure() {
        model.stopQuery();
        mView.stopMeasure();
    }
}
