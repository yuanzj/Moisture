package com.drt.moisture.dashboard;

import android.text.TextUtils;

import com.drt.moisture.App;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BasePresenter;

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
    public void startMeasure(final int measureModel, String measureName) {
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
                mView.onError(new Exception("测量中..."));
                return;
            case BT_NOT_CONNECT:
                mView.onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                return;
        }

        if (TextUtils.isEmpty(measureName)) {
            mView.onError(new Exception("请输入测量样品名称"));
            return;
        }

        App.getInstance().getLocalDataService().setHistory(measureName);

        model.startMeasure(measureName, measureModel, new MeasureDataCallback<StartMeasureResponse>() {
            @Override
            public void runningTime(String time) {

            }

            @Override
            public void success(StartMeasureResponse value) {
                model.startQuery(measureModel, new MeasureDataCallback<MeasureValue>() {

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
                        if (value.getMeasureStatus() == 0x02) {
                            stopMeasure(true);
                            model.stopQuery(true);
                            setMeasureStatus(MeasureStatus.DONE);
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
    public void stopMeasure(boolean sendCommand) {
        if (measureStatus == MeasureStatus.RUNNING) {
            model.stopQuery(sendCommand);
            setMeasureStatus(MeasureStatus.STOP);
        } else {
            if (isViewAttached()) {
                mView.onError(new Exception("尚未开始测量"));
            }
        }
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
