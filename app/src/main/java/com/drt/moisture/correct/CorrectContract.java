package com.drt.moisture.correct;

import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;

import net.yzj.android.common.base.BaseView;


public interface CorrectContract {
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

        void startCorrect(int model, CorrectDataCallback<MeasureValue> callback);

        void stopCorrect();

    }

    interface View extends BaseView {

        void updateUI(MeasureStatus measureStatus);

        void updateStep(String message);

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
         * @param model 测试模式 0 定时，1： 自动
         */
        void startCorrect(int model);

        void stopCorrect();
    }
}
