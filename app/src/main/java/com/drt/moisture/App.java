package com.drt.moisture;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.dashboard.DashboardModel;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.LocalDataService;
import com.drt.moisture.data.source.bluetooth.BluetoothServiceImpl;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.TimingSetResponse;
import com.drt.moisture.data.source.bluetooth.resquest.SendAutoStartMsg;
import com.drt.moisture.data.source.bluetooth.resquest.SendUpdateAlarmMsg;
import com.drt.moisture.data.source.local.LocalDataServiceImpl;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.util.DateUtil;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.zhjian.bluetooth.spp.BluetoothSPP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class App extends Application {

    private static App app;

    private LocalDataService localDataService;

    private BluetoothService bluetoothService;

    private BluetoothSPP bluetoothSPP;

    private BluetoothClient mClient;

    private String connectMacAddress;

    private String deviceSoc;

    public volatile boolean isRunning;

    public volatile boolean pickDevice;


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
        EventBus.getDefault().register(this);  //事件的注册

        app = this;
        bluetoothSPP = new BluetoothSPP(this);
        bluetoothSPP.setupService();
        localDataService = new LocalDataServiceImpl(this);
        bluetoothService = new BluetoothServiceImpl(this);

        mClient = new BluetoothClient(this);
        initAutoConnect();
    }

    public void initAutoConnect() {
        if (statusCheckThread != null) {
            statusCheckThread.interrupt();
        }
        statusCheckThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int pointCount = getLocalDataService().queryAppConfig().getPointCount();

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isInteractive();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
                if (!isScreenOn) {
                    continue;
                }

                if (!pickDevice && App.getInstance().getConnectMacAddress() != null && App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress()) != Constants.STATUS_DEVICE_CONNECTED) {
                    EventBus.getDefault().post(new BleEvent());
                } else {
                    if (date1 == null || date2 == null || date3 == null) {
                        EventBus.getDefault().post(new SendUpdateAlarmMsg());
                    } else {
                        if (DashboardActivity.getDashboardActivity() == null
                                || !DashboardActivity.getDashboardActivity().isFront) {

                            // 客户端直接启动测量
                            if (new Date().after(date1) && new Date().before(new Date(date1.getTime() + 1000 * 60 * 2)) && (lastAutoRunDate1 == null || lastAutoRunDate1.before(date1))) {
                                lastAutoRunDate1 = new Date();
                                // 发指令
                                if (MeasureActivity.getInstance() != null) {
                                    MeasureActivity.getInstance().finish();
                                }
                                if (DashboardActivity.getDashboardActivity() != null) {
                                    DashboardActivity.getDashboardActivity().finish();
                                }

                                if (pointCount == 1) {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(getApplicationContext(), MeasureActivity.class);
                                    intent.putExtra("index", 1);
                                    intent.putExtra("autoStart", true);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(getApplicationContext(), DashboardActivity.class);
                                    intent.putExtra("autoStart", true);
                                    startActivity(intent);
                                }
                            } else if (new Date().after(date2) && new Date().before(new Date(date2.getTime() + 1000 * 60 * 2)) && (lastAutoRunDate2 == null || lastAutoRunDate2.before(date2))) {
                                lastAutoRunDate2 = new Date();
                                // 发指令
                                if (MeasureActivity.getInstance() != null) {
                                    MeasureActivity.getInstance().finish();
                                }
                                if (DashboardActivity.getDashboardActivity() != null) {
                                    DashboardActivity.getDashboardActivity().finish();
                                }
                                if (pointCount == 1) {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(getApplicationContext(), MeasureActivity.class);
                                    intent.putExtra("index", 1);
                                    intent.putExtra("autoStart", true);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(getApplicationContext(), DashboardActivity.class);
                                    intent.putExtra("autoStart", true);
                                    startActivity(intent);
                                }

                            } else if (new Date().after(date3) && new Date().before(new Date(date3.getTime() + 1000 * 60 * 2)) && (lastAutoRunDate3 == null || lastAutoRunDate3.before(date3))) {
                                lastAutoRunDate3 = new Date();
                                // 发指令
                                if (MeasureActivity.getInstance() != null) {
                                    MeasureActivity.getInstance().finish();
                                }
                                if (DashboardActivity.getDashboardActivity() != null) {
                                    DashboardActivity.getDashboardActivity().finish();
                                }
                                if (pointCount == 1) {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(getApplicationContext(), MeasureActivity.class);
                                    intent.putExtra("index", 1);
                                    intent.putExtra("autoStart", true);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(getApplicationContext(), DashboardActivity.class);
                                    intent.putExtra("autoStart", true);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }
            }
        });

        isRunning = true;
        pickDevice = false;
        statusCheckThread.start();
    }

    public volatile Date date1, date2, date3;
    public volatile Date lastAutoRunDate1, lastAutoRunDate2, lastAutoRunDate3;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendUpdateAlarmMsg(SendUpdateAlarmMsg mSendAutoStartMsg) {
        Log.e("yzj", "onSendUpdateAlarmMsg");
        App.getInstance().getBluetoothService().queryTiming(new SppDataCallback<TimingSetResponse>() {
            @Override
            public void delivery(TimingSetResponse setMeasureParameRequest) {


                String time1 = String.format("%04d-%02d-%02d %02d:%02d", setMeasureParameRequest.getTime1y() + 2000, setMeasureParameRequest.getTime1month(), setMeasureParameRequest.getTime1day(), setMeasureParameRequest.getTime1h(), setMeasureParameRequest.getTime1m());
                String time2 = String.format("%04d-%02d-%02d %02d:%02d", setMeasureParameRequest.getTime2y() + 2000, setMeasureParameRequest.getTime2month(), setMeasureParameRequest.getTime2day(), setMeasureParameRequest.getTime2h(), setMeasureParameRequest.getTime2m());
                String time3 = String.format("%04d-%02d-%02d %02d:%02d", setMeasureParameRequest.getTime3y() + 2000, setMeasureParameRequest.getTime3month(), setMeasureParameRequest.getTime3day(), setMeasureParameRequest.getTime3h(), setMeasureParameRequest.getTime3m());
                Log.e("yzj", time1 + " " + time2 + " " + time3);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    date1 = dateFormat.parse(time1);
                    date2 = dateFormat.parse(time2);
                    date3 = dateFormat.parse(time3);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Class<TimingSetResponse> getEntityType() {
                return TimingSetResponse.class;
            }
        }, false);
    }

    Thread statusCheckThread;

    public boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }
}