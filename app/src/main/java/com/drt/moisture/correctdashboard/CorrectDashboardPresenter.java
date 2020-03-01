package com.drt.moisture.correctdashboard;

import com.drt.moisture.App;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BasePresenter;

import java.util.List;
import java.util.Map;

public class CorrectDashboardPresenter extends BasePresenter<CorrectDashboardContract.View> implements CorrectDashboardContract.Presenter {

    private CorrectDashboardContract.Model model;

    private MeasureStatus measureStatus;

    public CorrectDashboardPresenter() {
        model = new CorrectDashboardModel();
        measureStatus = MeasureStatus.NORMAL;
    }

    @Override
    public int geCorrectTime(int index) {
        return model.getCorrectTime(index);
    }

    @Override
    public void setCorrectTime(int period, int index) {
        model.setCorrectTime(period, index);
    }


    @Override
    public void startCorrect(int _model, int type, int count) {
        if (!isViewAttached()) {
            return;
        }

        model.startCorrect(_model, type, count, new CorrectDataCallback<StartMeasureResponse>() {

            @Override
            public void runningStatus(Map<Integer, CorrectDashboardModel.MeasureRunningStatus> measureRunningStatusMap) {

            }

            @Override
            public void success(StartMeasureResponse value) {
                if (isViewAttached()) {
                    mView.onStartMeasureSuccess();
                }
            }

            @Override
            public void correctDone(int stop) {

            }

            @Override
            public void error(Throwable e) {

            }
        });


        setMeasureStatus(MeasureStatus.RUNNING);
    }

    @Override
    public void stopCorrect(boolean sendCommand) {
        if (isRunning()) {
            model.stopCorrect(sendCommand);
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
    public void queryCorrectResult(int pointCount) {
        model.startQuery(pointCount, new CorrectDataCallback<List<MeasureValue>>() {

            @Override
            public void runningStatus(Map<Integer, CorrectDashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
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
            public void correctDone(int step) {
                if (isViewAttached()) {

                    AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(1);
                    if (appConfig.getCorrectMode() == 2 && step == 0) {
                        setMeasureStatus(MeasureStatus.STEP_ONE_DONE);
                        mView.updateStep("请接着放置氯化镁饱和液");
                    } else {
                        setMeasureStatus(MeasureStatus.DONE);
                        mView.onDone();
                    }
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
        CorrectDashboardModel.MeasureRunningStatus measureRunningStatus = model.getCorrectRunningStatus(index);
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
