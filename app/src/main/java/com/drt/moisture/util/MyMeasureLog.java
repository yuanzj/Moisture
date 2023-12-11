package com.drt.moisture.util;

import android.content.Context;
import android.os.Environment;

import com.drt.moisture.measure.BarometricPressure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class MyMeasureLog {

    private static Boolean MYLOG_SWITCH = true; // 日志文件总开关
    private static Boolean MYLOG_WRITE_TO_FILE = true;// 日志写入文件开关
    private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
    //    private static String MYLOG_PATH_SDCARD_DIR = "/sdcard/kantu/log";// 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 7;// sd卡中日志文件的最多保存天数
    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    public Context context;


    public static String log(String startMeasureTime,
                             String name,
                             String durationTime,
                             String temperature,
                             LinkedHashMap<String, Double> gasContent,
                             Double inputTemperature,
                             Double resultantPressure,
                             List<BarometricPressure> list) {

//        时间日期：16:20 2023/5/31
//        样品名称：钻井液
//        测量时长：15min
//        样品温度：25℃
//
//        组分名称        	含量 mol%
//                氮气 N2         	5
//        二氧化碳 CO2       	25
//        甲烷 CH4        	60
//        乙烷 C2H6        	3
//        丙烷 C3H8        	1
//        正丁烷 n-C4H10		1
//        异丁烷 i-C4H10		1
//        戊烷 C5        		1
//        己烷 C6        		1
//        庚烷 C7        		1
//        辛烷 C8        		1
//        合计                100
//
//        输入温度：5.3 ℃
//        水合物生成压力：3.455MPa
//
//                水合物相平衡数据
//        温度℃      压力MPa
//        0.0			2.515
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDay = dateFormat.format(new Date());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("时间日期：" + startMeasureTime + " " + currentDay + "\n");
        stringBuilder.append("样品名称：" + name + "\n");
        stringBuilder.append("测量时长：" + durationTime + "\n");
        stringBuilder.append("样品温度：" + temperature + "\n");
        stringBuilder.append("\n");
        stringBuilder.append("组分名称    含量 mol%\n");
        for (String key : gasContent.keySet()) {
            stringBuilder.append(key + "    " + gasContent.get(key) + "\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append("输入温度：" + inputTemperature + "℃\n");
        stringBuilder.append("水合物生成压力：" + resultantPressure + "MPa\n");
        stringBuilder.append("\n");
        stringBuilder.append("水合物相平衡数据");
        stringBuilder.append("温度℃    压力MPa\n");
        for (BarometricPressure item : list) {
            stringBuilder.append(item.getTemperature() + "    " + item.getPressure() + "\n");
        }

        String currentDay1 = new SimpleDateFormat("_yyyy-MM-dd_").format(new Date());
        return writeLogtoFile(name + currentDay1 + startMeasureTime + ".txt", stringBuilder.toString());
    }


    private static String writeLogtoFile(String fileName, String needWriteMessage) {
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        File dirsFile = new File(path + "/水分活度测量/report");
        File dirsFile = new File(ExcelUtil.getSDPath() + "/水分活度测量");

        if (!dirsFile.exists()) {
            dirsFile.mkdirs();
        }
        //Log.i("创建文件","创建文件");
        File file = new File(dirsFile.toString(), fileName);// MYLOG_PATH_SDCARD_DIR
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile();
            } catch (Exception e) {
            }
        }

        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 删除制定的日志文件
     */
    public static void delFile() {// 删除日志文件
        String needDelFiel = logfile.format(getDateBefore());
        File dirPath = new File(ExcelUtil.getSDPath() + "/水分活度测量/log");
        File file = new File(dirPath, needDelFiel + MYLOGFILEName);// MYLOG_PATH_SDCARD_DIR
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
}