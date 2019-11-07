package com.drt.moisture.measure;

import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BaseView;


public interface MeasureContract {
    interface Model {

        /**
         * 获取测量耗时
         *
         * @return
         */
        int getMeasureTime();

        /**
         * 设置测量耗时
         *
         * @param period
         */
        void setMeasureTime(int period);

        void startMeasure(String name, int model, MeasureDataCallback<StartMeasureResponse> callback);

        void startQuery(int model, MeasureDataCallback<MeasureValue> callback);

        void stopQuery(boolean sendCommand);

    }

    interface View extends BaseView {

        void updateUI(MeasureStatus measureStatus);

        void alreadyRunning(String time);

        void onSuccess(MeasureValue measureValue);

        @Override
        void onError(Throwable throwable);

    }

    interface Presenter {

        /**
         * 获取测量耗时
         *
         * @return
         */
        int getMeasureTime();

        /**
         * 设置测量耗时
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

        void stopMeasure(boolean sendCommand);
    }
}
