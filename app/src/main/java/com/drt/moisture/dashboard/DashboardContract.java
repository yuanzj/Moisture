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
         * 根据测点编号或者测量时间
         * @param index
         * @return
         */
        int getMeasureTime(int index);

        /**
         * 设置测量时间
         * @param period
         * @param index
         */
        void setMeasureTime(int period, int index);

        /**
         * 设置测量模式
         * @param index
         * @param model
         */
        void setMeasureModel(int index, int model);

        /**
         * 启动测量
         * @param name
         * @param model
         * @param index
         * @param callback
         */
        void startMeasure(String name, int model, int index, MeasureDataCallback<StartMeasureResponse> callback);

        /**
         * 启动查询
         * @param pointCount
         * @param callback
         */
        void startQuery(int pointCount, MeasureDataCallback<List<MeasureValue>> callback);

        /**
         * 指定节点编号，停止测量
         * @param sendCommand
         * @param index
         */
        void stopMeasure(boolean sendCommand, int index);

        /**
         * 停止所有测量
         */
        void stopAll();

        /**
         * 获取节点测量状态
         * @param index
         * @return
         */
        DashboardModel.MeasureRunningStatus getMeasureRunningStatus(int index);

        /**
         * 释放资源
         */
        void onDestroy();
    }

    interface View extends BaseView {

        void updateUI(MeasureStatus measureStatus);

        void updateRunningStatus(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap);

        void onStartMeasureSuccess(int index);

        void onSuccess(List<MeasureValue> measureValue);

        void onDone(int index);

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
         *
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

        void stopMeasure(boolean sendCommand, int index);

        void stopAll();

        void queryMeasureResult(int pointCount);

        void onDestroy();

    }
}
