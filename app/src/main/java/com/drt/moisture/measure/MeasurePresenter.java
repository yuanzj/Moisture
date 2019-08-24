package com.drt.moisture.measure;

import android.text.TextUtils;
import android.widget.EditText;

import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.DataCallback;

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

        model.startQuery(measureModel, new DataCallback<MeasureValue>() {

            @Override
            public void runningTime(String time) {
                mView.alreadyRunning(time);
            }

            @Override
            public void success(MeasureValue value) {
                // 绑定数据
                mView.onSuccess(value);
            }

            @Override
            public void measureDone() {
                setMeasureStatus(MeasureStatus.DONE);
            }

            @Override
            public void error(Throwable e) {
                setMeasureStatus(MeasureStatus.ERROR);
                mView.onError(e);
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
            mView.onError(new Exception("尚未开始测量"));
        }
    }

    public void setMeasureStatus(MeasureStatus measureStatus) {
        this.measureStatus = measureStatus;
        mView.updateUI(measureStatus);
    }

    /**
     * 测量状态
     */
    public enum MeasureStatus {
        /**
         * 蓝牙未连接
         */
        BT_NOT_CONNECT,
        /**
         * 待开始
         */
        NORMAL,
        /**
         * 测量中
         */
        RUNNING,

        /**
         * 测量结束
         */
        STOP,
        /**
         * 测量异常中断
         */
        ERROR,
        /**
         * 测试结束
         */
        DONE

    }
}
