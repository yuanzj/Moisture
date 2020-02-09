package com.drt.moisture.correct;


import com.drt.moisture.dashboard.DashboardModel;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;

import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;
import net.yzj.android.common.base.BasePresenter;

import java.util.Map;

public class CorrectPresenter extends BasePresenter<CorrectContract.View> implements CorrectContract.Presenter {

    private CorrectContract.Model model;

    private MeasureStatus measureStatus;

    public CorrectPresenter() {
        model = new CorrectModel();
        measureStatus = MeasureStatus.NORMAL;
    }

    @Override
    public int getCorrectTime() {
        return model.getCorrectTime();
    }

    @Override
    public void setCorrectTime(int period) {
        model.setCorrectTime(period);
    }

    @Override
    public void startCorrect(final int measureModel, final int type) {
        if (!isViewAttached()) {
            return;
        }

        switch (measureStatus) {
            case STOP:
            case ERROR:
            case NORMAL:
            case DONE:
                break;
            case RUNNING:
                mView.onError(new Exception("校正中..."));
                return;
            case BT_NOT_CONNECT:
                mView.onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                return;
        }

        model.startCorrect(measureModel, type, getCorrectTime(), new MeasureDataCallback<StartMeasureResponse>() {

            @Override
            public void runningStatus(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
                
            }

            @Override
            public void success(StartMeasureResponse value) {
                model.startCorrect(measureModel, type, new CorrectDataCallback<MeasureValue>() {

                    @Override
                    public void runningTime(String time) {
                        if (isViewAttached()) {
                            mView.alreadyRunning(time);
                        }
                    }

                    @Override
                    public void success(MeasureValue value) {
                        if (isViewAttached()) {
                            // 绑定数据
                            mView.onSuccess(value);
                        }
                    }

                    @Override
                    public void correctDone(int step) {
                        if (isViewAttached()) {
                            if (measureModel == 2 && step == 0) {
                                setMeasureStatus(MeasureStatus.STEP_ONE_DONE);
                                mView.updateStep("请接着放置氯化镁饱和液");
                            } else {
                                setMeasureStatus(MeasureStatus.DONE);
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
            public void measureDone(int index) {

            }

            @Override
            public void error(Throwable e) {

            }
        });


        setMeasureStatus(MeasureStatus.RUNNING);
    }

    @Override
    public void stopCorrect(boolean sendCommand) {
        if (measureStatus == MeasureStatus.RUNNING) {
            model.stopCorrect(sendCommand);
            setMeasureStatus(MeasureStatus.STOP);
        } else {
            if (isViewAttached()) {
                mView.onError(new Exception("尚未开始校正"));
            }
        }
    }

    public void setMeasureStatus(MeasureStatus measureStatus) {
        if (MeasureStatus.STEP_ONE_DONE == measureStatus) {
            this.measureStatus = MeasureStatus.DONE;
        } else {
            this.measureStatus = measureStatus;
        }
        if (isViewAttached()) {
            mView.updateUI(measureStatus);
        }
    }


}
