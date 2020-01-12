package com.drt.moisture.dashboard;

import android.text.TextUtils;

import com.drt.moisture.App;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BasePresenter;

import java.util.List;

public class DashboardPresenter extends BasePresenter<DashboardContract.View> implements DashboardContract.Presenter {

    private DashboardContract.Model model;

    private MeasureStatus measureStatus;

    public DashboardPresenter() {
        model = new DashboardModel();
        measureStatus = MeasureStatus.NORMAL;
    }

    @Override
    public int getMeasureTime() {
        return model.getMeasureTime();
    }

    @Override
    public void setMeasureTime(int period) {
        model.setMeasureTime(period);
    }

    @Override
    public void startMeasure(final int measureModel, String measureName, final int index) {
        if (!isViewAttached()) {
            return;
        }

        if (TextUtils.isEmpty(measureName)) {
            mView.onError(new Exception("请输入测量样品名称"));
            return;
        }

        App.getInstance().getLocalDataService().setHistory(index, measureName);

        model.startMeasure(measureName, measureModel, index,new MeasureDataCallback<StartMeasureResponse>() {
            @Override
            public void runningTime(String time) {

            }

            @Override
            public void success(StartMeasureResponse value) {
                if (isViewAttached()) {
                    mView.onError(new Exception("测点"+ index + "启动成功。"));
                }
            }

            @Override
            public void measureDone() {

            }

            @Override
            public void error(Throwable e) {

            }
        });


        setMeasureStatus(MeasureStatus.RUNNING);
    }

    @Override
    public void stopMeasure(boolean sendCommand, int index) {
        if (measureStatus == MeasureStatus.RUNNING) {
            model.stopQuery(sendCommand, index);
            setMeasureStatus(MeasureStatus.STOP);
        } else {
            if (isViewAttached()) {
                mView.onError(new Exception("尚未开始测量"));
            }
        }
    }

    @Override
    public void queryMeasureResult(int pointCount) {
        model.startQuery(0, pointCount, new MeasureDataCallback<List<MeasureValue>>() {

            @Override
            public void runningTime(String time) {
                if (isViewAttached()) {
                    mView.alreadyRunning(time);
                }
            }

            @Override
            public void success(List<MeasureValue> value) {
                if (isViewAttached()) {
                    // 绑定数据
                    mView.onSuccess(value);
                }
            }

            @Override
            public void measureDone() {
                if (isViewAttached()) {
                    setMeasureStatus(MeasureStatus.DONE);
                }
            }

            @Override
            public void error(Throwable e) {
                setMeasureStatus(MeasureStatus.ERROR);
                if (isViewAttached()) {
                    mView.onError(e);
                }
            }
        });
        setMeasureStatus(MeasureStatus.RUNNING);
    }

    public void setMeasureStatus(MeasureStatus measureStatus) {
        this.measureStatus = measureStatus;
        if (isViewAttached()) {
            mView.updateUI(measureStatus);
        }
    }


    public MeasureStatus getMeasureStatus() {
        return measureStatus;
    }
}
