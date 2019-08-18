package com.drt.moisture.data.source;

/**
 * @author yuanzhijian
 */
public interface DataCallback<T> {

    void success(T value);

    void error(Throwable e);
}
