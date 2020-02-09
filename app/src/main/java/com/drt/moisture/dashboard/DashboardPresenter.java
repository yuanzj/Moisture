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

    DashboardPresenter() {
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
    public void startMeasure(int measureModel, String measureName, int index) {
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
            public void runningStatus(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap) {

            }

            @Override
            public void success(StartMeasureResponse value) {
                if (isViewAttached()) {
                    mView.onStartMeasureSuccess(value.getIndex());
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
            model.stopMeasure(sendCommand, index);
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
        mView.updateUI(MeasureStatus.DONE);
    }

    @Override
    public void queryMeasureResult(int pointCount) {
        model.startQuery(pointCount, new MeasureDataCallback<List<MeasureValue>>() {

            @Override
            public void runningStatus(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
                if (isViewAttached()) {
                    mView.updateRunningStatus(measureRunningStatusMap);
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
                    setMeasureStatus(MeasureStatus.DONE);
                    mView.onDone(index);
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
            mView.updateUI(measureStatus);
        }
    }

    public MeasureStatus getMeasureStatus() {
        if (measureStatus != MeasureStatus.RUNNING && isRunning()) {
            measureStatus = MeasureStatus.RUNNING;
        }
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
            MeasureStatus measureRunningStatus = getMeasureStatus(index);
            if (measureRunningStatus == MeasureStatus.RUNNING) {
                return true;
            }
        }
        return false;
    }
}
