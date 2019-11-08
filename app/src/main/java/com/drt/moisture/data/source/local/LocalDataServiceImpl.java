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
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        AppConfig appConfig = new AppConfig();
        appConfig.setMeasuringTime(sp.getInt("measuringTime", 5));
        appConfig.setCorrectTime(sp.getInt("correctTime", 15));
        appConfig.setPeriod(sp.getInt("period", 1500));
        appConfig.setRatio(sp.getInt("ratio", 0));
        return appConfig;
    }

    @Override
    public synchronized void setAppConfig(AppConfig appConfig) {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);

        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("measuringTime", appConfig.getMeasuringTime());
        editor.putInt("period", appConfig.getPeriod());
        editor.putInt("correctTime", appConfig.getCorrectTime());
        editor.putInt("ratio", appConfig.getRatio());
        editor.apply();
    }

    @Override
    public List<String> queryHistory() {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        String listJson = sp.getString("historyList", "[\"样品1\"]");
        if (listJson != null && listJson.length() > 0) {
            return new Gson().fromJson(listJson, new TypeToken<List<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    @Override
    public void setHistory(String name) {
        List<String> historyList = queryHistory();
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
        editor.putString("historyList", new Gson().toJson(historyList));

        editor.apply();
    }

    @Override
    public void clearAll() {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
