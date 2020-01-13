package com.drt.moisture.data.source;

import com.drt.moisture.dashboard.DashboardModel;

import java.util.Map;

/**
 * @author yuanzhijian
 */
public interface MeasureDataCallback<T> {

    void runningTime(String time);

    void runningTime(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap, String time);

    void success(T value);

    void measureDone();

    void error(Throwable e);
}
