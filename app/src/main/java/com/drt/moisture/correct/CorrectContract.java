package com.drt.moisture.correct;

import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;

import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;
import net.yzj.android.common.base.BaseView;


public interface CorrectContract {
    interface Model {

        /**
         * 获取测量时长
         *
         * @return
         */
        int getCorrectTime();

        /**
         * 设置测量时长
         *
         * @param period
         */
        void setCorrectTime(int period);

        void startCorrect(int mode, int type, int time, final MeasureDataCallback<StartMeasureResponse> callback);

        void startCorrect(int measureModel,  int type, CorrectDataCallback<MeasureValue> callback);

        void stopCorrect(boolean sendCommand);

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
        int getCorrectTime();

        /**
         * 设置测量时长
         *
         * @param period
         */
        void setCorrectTime(int period);

        /**
         * 启动测量
         *
         * @param measureModel 测试模式 0 定时，1： 自动
         */
        void startCorrect(final int measureModel, final int type);

        void stopCorrect(boolean sendCommand);
    }
}
