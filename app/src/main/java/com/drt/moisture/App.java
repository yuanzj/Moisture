package com.drt.moisture;

import android.app.Application;

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
        return connectMacAddress;
    }

    public void setConnectMacAddress(String connectMacAddress) {
        this.connectMacAddress = connectMacAddress;
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