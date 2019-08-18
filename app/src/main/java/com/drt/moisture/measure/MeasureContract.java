package com.drt.moisture.measure;

import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.DataCallback;

import net.yzj.android.common.base.BaseView;


public interface MeasureContract {
    interface Model {

        /**
         * 设置测量时长
         * @param period
         */
        void setMeasurePeriod(int period);

        void startQuery(int model, DataCallback<MeasureValue> callback);

        void stopQuery();

    }

    interface View extends BaseView {

        void startMeasure();

        void stopMeasure();

        void onSuccess(MeasureValue measureValue);

        @Override
        void onError(Throwable throwable);

    }

    interface Presenter {

        /**
         * 设置测量时长
         * @param period
         */
        void setMeasurePeriod(int period);

        /**
         * 启动测量
         * @param model 测试模式 0 定时，1： 自动
         * @param callback
         */
        void startMeasure(int model, DataCallback<MeasureValue> callback);

        void stopMeasure();
    }
}
