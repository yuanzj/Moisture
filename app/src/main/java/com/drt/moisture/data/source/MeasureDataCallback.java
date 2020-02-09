package com.drt.moisture.data.source;

import com.drt.moisture.dashboard.DashboardModel;

import java.util.Map;

/**
 * @author yuanzhijian
 */
public interface MeasureDataCallback<T> {

    void runningStatus(Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap);

    void success(T value);

    void measureDone(int index);

    void error(Throwable e);
}
