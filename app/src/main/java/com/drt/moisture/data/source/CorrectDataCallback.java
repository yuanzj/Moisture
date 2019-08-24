package com.drt.moisture.data.source;

/**
 * @author yuanzhijian
 */
public interface CorrectDataCallback<T> {

    void runningTime(String time);

    void success(T value);

    void correctDone(int stop);

    void error(Throwable e);
}
