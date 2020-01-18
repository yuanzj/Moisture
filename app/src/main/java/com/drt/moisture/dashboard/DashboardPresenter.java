package com.drt.moisture.dashboard;

import android.text.TextUtils;

import com.drt.moisture.App;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BasePresenter;

import java.util.List;
import java.util.Map;

public class DashboardPresenter extends BasePresenter<DashboardContract.View> implements DashboardContract.Presenter {

    private DashboardContract.Model model;

    private MeasureStatus measureStatus;

    public DashboardPresenter() {
        model = new DashboardModel();
        measureStatus = MeasureStatus.NORMAL;
    }

    @Override
    public int getMeasureTime(int index) {
        return model.getMeasureTime(index);
    }

    @Override
    public void setMeasureTime(int period, int index) {
        model.setMeasureTime(period, index);
    }

    @Override
    public void setMeasureModel(int index, int _model) {
        model.setMeasureModel(index, _model);
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

        model.startMeasure(measureName, measureModel, index, new MeasureDataCallback<StartMeasureResponse>() {
            @Override
            public void runningTime(String time) {

            }

            @Override
            public void runningTime(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap, String time) {

            }

            @Override
            public void success(StartMeasureResponse value) {
                if (isViewAttached()) {
                    mView.onStartMeasureSuccess(index);
                }
            }

            @Override
            public void measureDone(int index) {

            }


            @Override
            public void error(Throwable e) {

            }
        });


        setMeasureStatus(MeasureStatus.RUNNING);
    }

    @Override
    public void stopMeasure(boolean sendCommand, int index) {
        DashboardModel.MeasureRunningStatus measureRunningStatus = model.getMeasureRunningStatus(index);

        if (measureRunningStatus != null && measureRunningStatus.isRunning) {
            model.stopQuery(sendCommand, index);
            setMeasureStatus(MeasureStatus.STOP);
        } else {
            if (isViewAttached()) {
                mView.onError(new Exception("尚未开始测量"));
            }
        }
    }

    @Override
    public void stopAll() {
        model.stopAll();
        mView.updateUI(MeasureStatus.DONE, 0);
    }

    @Override
    public void queryMeasureResult(int pointCount) {
        model.startQuery(0, pointCount, new MeasureDataCallback<List<MeasureValue>>() {

            @Override
            public void runningTime(String time) {

            }

            @Override
            public void runningTime(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap, String time) {
                if (isViewAttached()) {
                    mView.alreadyRunning(measureRunningStatusMap, time);
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
            public void measureDone(int index) {
                if (isViewAttached()) {
                    setMeasureStatus(MeasureStatus.DONE, index);
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

    @Override
    public void onDestroy() {
        model.onDestroy();
    }

    public void setMeasureStatus(MeasureStatus measureStatus) {
        this.measureStatus = measureStatus;
        if (isViewAttached()) {
            mView.updateUI(measureStatus, 1);
        }
    }

    public void setMeasureStatus(MeasureStatus measureStatus, int index) {
        this.measureStatus = measureStatus;
        if (isViewAttached()) {
            mView.updateUI(measureStatus, index);
        }
    }


    public MeasureStatus getMeasureStatus() {
        return measureStatus;
    }

    public MeasureStatus getMeasureStatus(int index) {
        DashboardModel.MeasureRunningStatus measureRunningStatus = model.getMeasureRunningStatus(index);
        if (measureRunningStatus == null) {
            return MeasureStatus.NORMAL;
        }

        if (measureRunningStatus.isRunning) {
            return MeasureStatus.RUNNING;
        } else {
            return MeasureStatus.NORMAL;
        }
    }

    public boolean isRunning() {
        for (int index = 1; index < 6; index++) {
            DashboardModel.MeasureRunningStatus measureRunningStatus = model.getMeasureRunningStatus(index);
            if (measureRunningStatus != null && measureRunningStatus.isRunning) {
                return true;
            }
        }
        return false;
    }
}
