package com.drt.moisture.measure;

import android.text.TextUtils;

import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import net.yzj.android.common.base.BasePresenter;

public class MeasurePresenter extends BasePresenter<MeasureContract.View> implements MeasureContract.Presenter {

    private MeasureContract.Model model;

    private MeasureStatus measureStatus;

    public MeasurePresenter() {
        model = new MeasureModel();
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
    public void startMeasure(int measureModel, String measureName) {
        if (!isViewAttached()) {
            return;
        }
        if (TextUtils.isEmpty(measureName)) {
            mView.onError(new Exception("请输入测量样品名称"));
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
    public void stopMeasure() {
        if (measureStatus == MeasureStatus.RUNNING) {
            model.stopQuery();
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


}
