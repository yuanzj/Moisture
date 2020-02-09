package com.drt.moisture.correctdashboard;

import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.CorrectDataCallback;
import com.drt.moisture.data.source.bluetooth.response.StartMeasureResponse;

import net.yzj.android.common.base.BaseView;

import java.util.List;
import java.util.Map;


public interface CorrectDashboardContract {
    interface Model {

        /**
         * 根据测点编号或者校正时间
         * @param index
         * @return
         */
        int getCorrectTime(int index);

        /**
         * 设置校正时间
         * @param period
         * @param index
         */
        void setCorrectTime(int period, int index);

        /**
         * 启动校正
         * @param type
         * @param model
         * @param count
         * @param callback
         */
        void startCorrect(int model, int type, int count, CorrectDataCallback<StartMeasureResponse> callback);

        /**
         * 启动查询
         * @param pointCount
         * @param callback
         */
        void startQuery(int pointCount, CorrectDataCallback<List<MeasureValue>> callback);

        /**
         * 指定节点编号，停止校正
         * @param sendCommand
         */
        void stopCorrect(boolean sendCommand);

        /**
         * 停止所有校正
         */
        void stopAll();

        /**
         * 获取节点校正状态
         * @param index
         * @return
         */
        CorrectDashboardModel.MeasureRunningStatus getCorrectRunningStatus(int index);

        /**
         * 释放资源
         */
        void onDestroy();
    }

    interface View extends BaseView {

        void updateUI(MeasureStatus measureStatus);

        void updateStep(String message);

        void updateRunningStatus(Map<Integer, CorrectDashboardModel.MeasureRunningStatus> measureRunningStatusMap);

        void onStartMeasureSuccess();

        void onSuccess(List<MeasureValue> measureValue);

        void onDone();

        @Override
        void onError(Throwable throwable);

    }

    interface Presenter {

        /**
         * 获取校正耗时
         *
         * @return
         */
        int geCorrectTime(int index);

        /**
         * 设置校正耗时
         *
         * @param period
         */
        void setCorrectTime(int period, int index);

        /**
         * 启动校正
         *
         * @param model       校准模式
         * @param type        校准类型
         * @param count       节点数量
         */
        void startCorrect(int model, int type, int count);

        void stopCorrect(boolean sendCommand);

        void stopAll();

        void queryCorrectResult(int pointCount);

        void onDestroy();

    }
}
