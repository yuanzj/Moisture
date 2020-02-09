package com.drt.moisture.data.source;

import com.drt.moisture.correctdashboard.CorrectDashboardModel;

import java.util.Map;

/**
 * @author yuanzhijian
 */
public interface CorrectDataCallback<T> {

    void runningStatus(Map<Integer, CorrectDashboardModel.MeasureRunningStatus> measureRunningStatusMap);

    void success(T value);

    void correctDone(int stop);

    void error(Throwable e);
}
