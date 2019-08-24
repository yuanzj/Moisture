package com.drt.moisture.data.source.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.source.LocalDataService;

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
        appConfig.setMeasuringTime(sp.getInt("measuringTime", 10));
        appConfig.setPeriod(sp.getInt("period", 1000));
        return appConfig;
    }

    @Override
    public synchronized void setAppConfig(AppConfig appConfig) {
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);

        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("measuringTime", appConfig.getMeasuringTime());
        editor.putInt("period", appConfig.getPeriod());

        editor.commit();
    }
}
