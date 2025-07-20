package com.drt.moisture;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.data.UsbConnectEvent;
import com.drt.moisture.data.source.BluetoothService;
import com.drt.moisture.data.source.LocalDataService;
import com.drt.moisture.data.source.bluetooth.BluetoothServiceImpl;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.TimingSetResponse;
import com.drt.moisture.data.source.bluetooth.resquest.SendUpdateAlarmMsg;
import com.drt.moisture.data.source.local.LocalDataServiceImpl;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.util.MyLog;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.zhjian.bluetooth.spp.BluetoothSPP;
import com.zhjian.bluetooth.spp.HexString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.wch.uartlib.WCHUARTManager;
import cn.wch.uartlib.callback.IDataCallback;



public class App extends Application {

    private static App app;

    private LocalDataService localDataService;

    private BluetoothService bluetoothService;

    private BluetoothSPP bluetoothSPP;

    private BluetoothClient mClient;

    private UsbDevice currentUsbDevice;
    
    private IDataCallback currentUsbDataCallback;
    
    private UsbManager usbManager;
    
    // USB连接状态锁，防止并发操作
    private final Object usbConnectionLock = new Object();
    
    // 标识是否正在连接中，防止重复连接
    private volatile boolean isConnecting = false;
    
    public static final String ACTION_USB_PERMISSION = "com.drt.moisture.USB_PERMISSION";

    private String connectMacAddress;

    private String deviceSoc;

    public volatile boolean isRunning;

    public volatile boolean pickDevice;

    /**
     * 0: USB转串口 (原来的串口模式)
     * 1：蓝牙
     */
    public volatile int connectedModel;

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

    public UsbDevice getCurrentUsbDevice() {
        return currentUsbDevice;
    }

    public void setCurrentUsbDevice(UsbDevice usbDevice) {
        synchronized (usbConnectionLock) {
            this.currentUsbDevice = usbDevice;
        }
    }

    // USB转串口相关方法
    public boolean isUsbConnected() {
        synchronized (usbConnectionLock) {
            return currentUsbDevice != null && WCHUARTManager.getInstance().isConnected(currentUsbDevice);
        }
    }

    public boolean hasUsbPermission(UsbDevice device) {
        return usbManager != null && usbManager.hasPermission(device);
    }
    
    public void requestUsbPermission(UsbDevice device, android.content.Context context) {
        if (usbManager != null && !usbManager.hasPermission(device)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(ACTION_USB_PERMISSION), 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            usbManager.requestPermission(device, permissionIntent);
            MyLog.d("USB_PERMISSION", "正在申请USB设备权限");
        }
    }
    
    public boolean autoConnectUsbDevice() {
        synchronized (usbConnectionLock) {
            // 防止重复连接
            if (isConnecting) {
                MyLog.w("USB_AUTO_CONNECT", "正在连接中，跳过重复连接请求");
                return false;
            }
            
            // 检查是否已连接
            if (isUsbConnected()) {
                MyLog.d("USB_AUTO_CONNECT", "设备已连接，无需重复连接");
                return true;
            }
            
            isConnecting = true;
            try {
                // 获取可用的USB设备列表
                ArrayList<UsbDevice> usbDeviceList = WCHUARTManager.getInstance().enumDevice();
                if (usbDeviceList == null || usbDeviceList.isEmpty()) {
                    MyLog.e("USB_AUTO_CONNECT", "未找到可用的USB设备");
                    return false;
                }
                
                // 自动选择第一个设备
                UsbDevice firstDevice = usbDeviceList.get(0);
                String deviceName = firstDevice.getProductName();
                if (deviceName == null || deviceName.isEmpty()) {
                    deviceName = "USB设备";
                }
                String deviceInfo = deviceName + " (VID:" + String.format("%04X", firstDevice.getVendorId()) + 
                                   " PID:" + String.format("%04X", firstDevice.getProductId()) + ")";
                
                MyLog.d("USB_AUTO_CONNECT", "自动选择设备: " + deviceInfo);
                
                // 设置当前设备并尝试连接
                setCurrentUsbDevice(firstDevice);
                boolean connected = openUsbDevice();
                
                if (connected) {
                    // 连接成功后注册默认数据回调
                    setupDefaultUsbDataCallback();
                    // 发布USB连接成功事件，通知界面更新
                    EventBus.getDefault().post(new UsbConnectEvent(true));
                    MyLog.d("USB_AUTO_CONNECT", "已发布USB连接成功事件");
                }
                
                return connected;
                
            } catch (Exception e) {
                MyLog.e("USB_AUTO_CONNECT", "自动连接失败: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                isConnecting = false;
            }
        }
    }

    public boolean openUsbDevice() {
        if (currentUsbDevice == null) {
            MyLog.e("USB_CONNECT", "设备引用为空，无法打开");
            return false;
        }
        
        // 检查权限
        if (!hasUsbPermission(currentUsbDevice)) {
            MyLog.e("USB_CONNECT", "没有USB设备权限，无法打开");
            return false;
        }
        
        // 检查是否已经连接
        if (isUsbConnected()) {
            MyLog.d("USB_CONNECT", "设备已经连接，无需重复打开");
            return true;
        }
        
        try {
            MyLog.d("USB_CONNECT", "尝试打开USB设备: " + currentUsbDevice.getDeviceName());
            boolean success = WCHUARTManager.getInstance().openDevice(currentUsbDevice);
            if (success) {
                MyLog.i("USB_CONNECT", "USB设备打开成功");
                // 连接成功后立即设置数据回调
                setupDefaultUsbDataCallback();
            } else {
                MyLog.e("USB_CONNECT", "USB设备打开失败");
            }
            return success;
        } catch (Exception e) {
            MyLog.e("USB_CONNECT", "打开USB设备异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void closeUsbDevice() {
        synchronized (usbConnectionLock) {
            if (currentUsbDevice != null) {
                try {
                    // 先取消数据回调
                    unregisterUsbDataCallback();
                    // 断开设备连接
                    WCHUARTManager.getInstance().disconnect(currentUsbDevice);
                    setCurrentUsbDevice(null);
                    // 发布USB断开连接事件，通知界面更新
                    EventBus.getDefault().post(new UsbConnectEvent(false));
                    MyLog.d("USB_DISCONNECT", "已发布USB断开连接事件");
                } catch (Exception e) {
                    MyLog.e("USB_DISCONNECT", "断开连接异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void registerUsbDataCallback(IDataCallback callback) {
        synchronized (usbConnectionLock) {
            if (currentUsbDevice != null && callback != null) {
                // 检查是否已有相同回调，避免重复注册
                if (currentUsbDataCallback == callback) {
                    MyLog.d("USB_CALLBACK", "相同回调已存在，跳过重复注册");
                    return;
                }
                
                // 先取消之前的回调，避免重复注册
                unregisterUsbDataCallback();
                
                try {
                    WCHUARTManager.getInstance().registerDataCallback(currentUsbDevice, callback);
                    currentUsbDataCallback = callback;
                    MyLog.d("USB_CALLBACK", "USB数据回调注册成功");
                } catch (Exception e) {
                    MyLog.e("USB_CALLBACK", "USB数据回调注册失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                MyLog.w("USB_CALLBACK", "设备未连接或回调为空，无法注册");
            }
        }
    }
    
    public void unregisterUsbDataCallback() {
        synchronized (usbConnectionLock) {
            if (currentUsbDataCallback != null) {
                try {
                    // 尝试取消注册，如果驱动不支持就忽略异常
                    // WCH驱动可能不支持单独取消回调，所以用try-catch保护
                    currentUsbDataCallback = null;
                    MyLog.d("USB_CALLBACK", "USB数据回调引用已清理");
                } catch (Exception e) {
                    MyLog.w("USB_CALLBACK", "清理回调时出现异常: " + e.getMessage());
                }
            }
        }
    }
    
    private void setupDefaultUsbDataCallback() {
        if (currentUsbDevice != null) {
            IDataCallback callback = new IDataCallback() {
                @Override
                public void onData(int serialNumber, byte[] buffer, int length) {
                    if (buffer != null && length > 0) {
                        byte[] data = new byte[length];
                        System.arraycopy(buffer, 0, data, 0, length);
                        MyLog.i("USB_RX", HexString.bytesToHex(data));
                        
                        // 在后台线程中处理数据解析，避免主线程阻塞
                        new Thread(() -> {
                            try {
                                getBluetoothService().parse(data);
                            } catch (Exception e) {
                                MyLog.e("USB_RX", "数据解析异常: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            };
            
            registerUsbDataCallback(callback);
            MyLog.d("USB_AUTO_CONNECT", "默认数据回调已注册");
        }
    }

    public boolean sendUsbData(byte[] data) {
        synchronized (usbConnectionLock) {
            if (!isUsbConnected()) {
                MyLog.e("USB_TX", "USB设备未连接，无法发送数据");
                return false;
            }
            
            if (data == null || data.length == 0) {
                MyLog.e("USB_TX", "发送数据为空");
                return false;
            }
            
            // 防止在连接过程中发送数据
            if (isConnecting) {
                MyLog.w("USB_TX", "设备正在连接中，暂时无法发送数据");
                return false;
            }
            
            try {
                MyLog.i("USB_TX", HexString.bytesToHex(data));
                int result = WCHUARTManager.getInstance().syncWriteData(currentUsbDevice, 0, data, data.length, 2000);
                if (result > 0) {
                    MyLog.d("USB_TX", "数据发送成功，字节数: " + result);
                    return true;
                } else {
                    MyLog.e("USB_TX", "数据发送失败，返回值: " + result);
                    return false;
                }
            } catch (Exception e) {
                MyLog.e("USB_TX", "数据发送异常: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
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
        
        // 初始化WCH UART管理器
        WCHUARTManager.getInstance().init(this);
        WCHUARTManager.setDebug(true);
        
        // 初始化USB管理器
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        mClient = new BluetoothClient(this);


        SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        connectedModel = sharedPreferences.getInt("connectedModel", 0);
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

                if (connectedModel == 1 && !pickDevice && App.getInstance().getConnectMacAddress() != null && App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress()) != Constants.STATUS_DEVICE_CONNECTED) {
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