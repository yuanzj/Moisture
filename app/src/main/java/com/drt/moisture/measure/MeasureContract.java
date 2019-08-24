package com.drt.moisture.measure;

import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.DataCallback;

import net.yzj.android.common.base.BaseView;


public interface MeasureContract {
    interface Model {

        /**
         * 获取测量时长
         *
         * @return
         */
        int getMeasureTime();

        /**
         * 设置测量时长
         *
         * @param period
         */
        void setMeasureTime(int period);

        void startQuery(int model, DataCallback<MeasureValue> callback);

        void stopQuery();

    }

    interface View extends BaseView {

        void updateUI(MeasurePresenter.MeasureStatus measureStatus);

        void alreadyRunning(String time);

        void onSuccess(MeasureValue measureValue);

        @Override
        void onError(Throwable throwable);

    }

    interface Presenter {

        /**
         * 获取测量时长
         *
         * @return
         */
        int getMeasureTime();

        /**
         * 设置测量时长
         *
         * @param period
         */
        void setMeasureTime(int period);

        /**
         * 启动测量
         *
         * @param model       测试模式 0 定时，1： 自动
         * @param measureName 测量项目名称
         */
        void startMeasure(int model, String measureName);

        void stopMeasure();
    }
}
