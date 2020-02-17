package com.drt.moisture.data.source.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.source.LocalDataService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class LocalDataServiceImpl implements LocalDataService {

    private Context context;

    public LocalDataServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public synchronized AppConfig queryAppConfig() {
        return queryAppConfig(1);
    }

    @Override
    public synchronized void setAppConfig(AppConfig appConfig) {
        setAppConfig(1, appConfig);
    }

    @Override
    public List<String> queryHistory() {
        return queryHistory(1);
    }

    @Override
    public void setHistory(String name) {
        setHistory(1, name);
    }

    @Override
    public AppConfig queryAppConfig(int index) {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        AppConfig appConfig = new AppConfig();
        appConfig.setMeasuringTime(sp.getInt("measuringTime" + index, 5));
        appConfig.setCorrectTime(sp.getInt("correctTime" + index, 15));
        appConfig.setPeriod(sp.getInt("period" + index, 1500));
        appConfig.setRatio(sp.getInt("ratio" + index, 0));
        appConfig.setMeasureMode(sp.getInt("measureMode" + index, 0));

        appConfig.setCorrectMode(sp.getInt("correctMode" + index, 1));
        appConfig.setCorrectType(sp.getInt("correctType" + index, 1));

        appConfig.setPointCount(sp.getInt("pointCount", 1));

        return appConfig;
    }

    @Override
    public void setAppConfig(int index, AppConfig appConfig) {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);

        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("measuringTime" + index, appConfig.getMeasuringTime());
        editor.putInt("period" + index, appConfig.getPeriod());
        editor.putInt("correctTime" + index, appConfig.getCorrectTime());
        editor.putInt("ratio" + index, appConfig.getRatio());
        editor.putInt("measureMode" + index, appConfig.getMeasureMode());

        editor.putInt("correctMode" + index, appConfig.getCorrectMode());
        editor.putInt("correctType" + index, appConfig.getCorrectType());

        editor.putInt("pointCount", appConfig.getPointCount());

        editor.apply();
    }

    @Override
    public List<String> queryHistory(int index) {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        String listJson = sp.getString("historyList" + index, "[\"样品1\"]");
        if (listJson != null && listJson.length() > 0) {
            return new Gson().fromJson(listJson, new TypeToken<List<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    @Override
    public void setHistory(int index, String name) {
        List<String> historyList = queryHistory(index);
        if (historyList.size() >= 20) {
            historyList.remove(0);
        }
        for (String item : historyList) {
            if (item.equals(name)) {
                return;
            }
        }

        historyList.add(name);

        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);

        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("historyList" + index, new Gson().toJson(historyList));

        editor.apply();
    }

    @Override
    public void clearAll() {
        AppConfig appConfig = queryAppConfig(1);
        int pointCount = appConfig.getPointCount();
        for (int i = 0; i < pointCount; i++) {
            appConfig = queryAppConfig(i + 1);
            appConfig.setMeasuringTime(5);
            appConfig.setCorrectTime(15);
            appConfig.setPeriod(1500);
            appConfig.setRatio(0);
            appConfig.setMeasureMode(0);
            appConfig.setCorrectMode(1);
            appConfig.setCorrectType(1);
            setAppConfig(i + 1, appConfig);
        }

    }
}
