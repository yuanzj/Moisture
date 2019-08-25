package com.drt.moisture.data.source.bluetooth;


/**
 * Created by apple on 16/4/20.
 */
public interface SppDataCallback<T> {

    void delivery(T t);

    Class<T> getEntityType();

}

