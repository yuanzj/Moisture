package com.drt.moisture.dashboard;

import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BaseView;

import java.util.List;
import java.util.Map;


public interface DashboardContract {
    interface Model {

        /**
         * 获取测量耗时
         *
         * @return
         */
        int getMeasureTime(int index);

        /**
         * 设置测量耗时
         *
         * @param period
         */
        void setMeasureTime(int period, int index);

        /**
         * 设置测量模式
         * @param model
         */
        void setMeasureModel(int index, int model);

        void startMeasure(String name, int model, int index,MeasureDataCallback<StartMeasureResponse> callback);

        void startAutoStopTimer(final int index);

        void startQuery(int model, int pointCount, MeasureDataCallback<List<MeasureValue>> callback);

        void stopQuery(boolean sendCommand, int index);

    }

    interface View extends BaseView {

        void updateUI(MeasureStatus measureStatus);

        void alreadyRunning(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap, String time);

        void onStartMeasureSuccess(int index);

        void onSuccess(List<MeasureValue> measureValue);

        @Override
        void onError(Throwable throwable);

    }

    interface Presenter {

        /**
         * 获取测量耗时
         *
         * @return
         */
        int getMeasureTime(int index);

        /**
         * 设置测量耗时
         *
         * @param period
         */
        void setMeasureTime(int period, int index);

        /**
         * 设置测量模式
         * @param model
         */
        void setMeasureModel(int index, int model);

        /**
         * 启动测量
         *
         * @param model       测试模式 0 定时，1： 自动
         * @param measureName 测量项目名称
         */
        void startMeasure(int model, String measureName, int index);

        void startAutoStopTimer(final int index);

        void stopMeasure(boolean sendCommand, int index);

        void queryMeasureResult(int pointCount);

    }
}
