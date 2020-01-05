package com.drt.moisture.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * 返回格式化的时间
     * @param startDate
     * @param endDate
     * @return
     */
    public static String dateDistance(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 0) {
            timeLong = 0;
        }
        if (timeLong < 60 * 1000)
            return timeLong / 1000 + "秒";
        else if (timeLong < 60 * 60 * 1000) {
            long second = (timeLong % (1000 * 60)) / 1000;
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟 " + second + "秒";
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时";
        } else if ((timeLong / 1000 / 60 / 60 / 24) < 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(startDate);
        }
    }

    public static String dateDistance1(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 0) {
            timeLong = 0;
        }
        if (timeLong < 60 * 1000)
            return  String.format("00:%02d", timeLong / 1000);
        else if (timeLong < 60 * 60 * 1000) {
            long second = (timeLong % (1000 * 60)) / 1000;
            timeLong = timeLong / 1000 / 60;
            return  String.format("%02d:%02d", timeLong , second);
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时";
        } else if ((timeLong / 1000 / 60 / 60 / 24) < 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(startDate);
        }
    }

}
