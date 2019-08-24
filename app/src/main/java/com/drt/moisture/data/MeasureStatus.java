package com.drt.moisture.data;

/**
 * 测量状态
 */
public enum MeasureStatus {
    /**
     * 蓝牙未连接
     */
    BT_NOT_CONNECT,
    /**
     * 待开始
     */
    NORMAL,
    /**
     * 测量中
     */
    RUNNING,

    /**
     * 测量结束
     */
    STOP,
    /**
     * 测量异常中断
     */
    ERROR,
    /**
     * 测试结束
     */
    DONE

}