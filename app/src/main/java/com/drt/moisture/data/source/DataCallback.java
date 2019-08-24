package com.drt.moisture.data.source;

/**
 * @author yuanzhijian
 */
public interface DataCallback<T> {

    void runningTime(String time);

    void success(T value);

    void measureDone();

    void error(Throwable e);
}
