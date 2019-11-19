package com.drt.moisture;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.LocalDataService;
import com.drt.moisture.data.source.bluetooth.BluetoothServiceImpl;
import com.drt.moisture.data.source.local.LocalDataServiceImpl;
import com.inuker.bluetooth.library.BluetoothClient;
import com.zhjian.bluetooth.spp.BluetoothSPP;


public class App extends Application {

    private static App app;

    private LocalDataService localDataService;

    private BluetoothService bluetoothService;

    private BluetoothSPP bluetoothSPP;

    private BluetoothClient mClient;

    private String connectMacAddress;

    private String deviceSoc;

    public static App getInstance() {
        if (app == null) {
            throw new IllegalAccessError("App is null");
        }
        return app;
    }

    public LocalDataService getLocalDataService() {
        return localDataService;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public BluetoothSPP getBluetoothSPP() {
        return bluetoothSPP;
    }

    public BluetoothClient getBluetoothClient() {
        return mClient;
    }

    public String getConnectMacAddress() {
        if (connectMacAddress == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            connectMacAddress = sharedPreferences.getString("connectMacAddress", null);
        }
        return connectMacAddress;
    }

    public void setConnectMacAddress(String connectMacAddress) {
        this.connectMacAddress = connectMacAddress;
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        editor.putString("connectMacAddress", connectMacAddress);
        //步骤4：提交
        editor.commit();
    }

    public String getDeviceSoc() {
        return deviceSoc;
    }

    public void setDeviceSoc(String deviceSoc) {
        this.deviceSoc = deviceSoc;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        bluetoothSPP = new BluetoothSPP(this);
        bluetoothSPP.setupService();
        localDataService = new LocalDataServiceImpl(this);
        bluetoothService = new BluetoothServiceImpl(this);

        mClient = new BluetoothClient(this);

    }
}