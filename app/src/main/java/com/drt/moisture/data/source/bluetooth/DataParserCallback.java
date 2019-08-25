package com.drt.moisture.data.source.bluetooth;

public interface DataParserCallback<T> {

    void delivery(T t);

}
