package com.drt.moisture;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTING;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.data.UsbConnectEvent;
import com.drt.moisture.data.UsbPermissionRequiredEvent;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.CdslSetResponse;
import com.drt.moisture.data.source.bluetooth.response.SocResponse;
import com.drt.moisture.util.CustomContentManager;
import com.drt.moisture.util.MyLog;
import com.drt.moisture.util.StatusBarUtil;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.UUIDUtils;
import com.zhjian.bluetooth.spp.HexString;

import net.yzj.android.common.base.BaseMvpActivity;
import net.yzj.android.common.base.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.wch.uartlib.WCHUARTManager;

/**
 * Created by Administrator on 2016/9/5 0005.
 */

public abstract class BluetoothBaseActivity<T extends BasePresenter> extends BaseMvpActivity<T> implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = BluetoothBaseActivity.class.getSimpleName();

    public static final int REQUEST_CONNECT_DEVICE = 384;

    /*Toolbar*/
    private Toolbar toolBar;
    /**/
    /*是否第一次加载图标(主要针对首页一对多fragment)*/
    private boolean title_menu_first = true;
    /*是否第一次加载返回*/
    private boolean title_back_first = true;
    /*是否是返回(有可能是代表别的功能)*/
    private boolean is_title_back = true;
    /*返回*/
    protected Button titleBack;
    /*标题名称*/
    private TextView titleName, secondTitle;

    @BindView(R.id.title_rightImage)
    ImageButton btnBluetooth;

    private long lastConnectTime;
    
    private BroadcastReceiver usbPermissionReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);  //事件的注册
        // 系统 6.0 以上 状态栏白底黑字的实现方法
        StatusBarUtil.setLightStatusBar(this.getWindow());
        // 竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initToolbar();

        // 初始化蓝牙按钮
        initBack();
        btnBluetooth.setVisibility(View.VISIBLE);


        if (App.getInstance().connectedModel == 0) {
            // USB转串口模式 - 仅设置权限监听，数据回调由App层统一管理
            setupUsbPermissionReceiver();
        } else {
            App.getInstance().getBluetoothClient().registerBluetoothStateListener(mBluetoothStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.getInstance().connectedModel == 0) {
            // USB转串口模式 - 检查连接状态
            if (App.getInstance().isUsbConnected()) {
                btnBluetooth.setImageResource(R.mipmap.icon_usb_connected);
                setBleConnectStatus(Constants.STATUS_CONNECTED);
                secondTitle.setText("USB已连接");
                App.getInstance().getBluetoothService().queryClsl(cdslSetResponseSppDataCallback);
            } else {
                btnBluetooth.setImageResource(R.mipmap.icon_usb_disconnected);
                setBleConnectStatus(STATUS_DISCONNECTED);
                secondTitle.setText(R.string.content_not_connect_1);
            }
        } else {
            if (getBluetoothStatus() == Constants.STATUS_DEVICE_CONNECTED) {
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth_connected);
                App.getInstance().getBluetoothClient().registerConnectStatusListener(App.getInstance().getConnectMacAddress(), mBleConnectStatusListener);
                setBleConnectStatus(Constants.STATUS_CONNECTED);
                secondTitle.setText(getString(R.string.content_battery));
                App.getInstance().getBluetoothService().queryClsl(cdslSetResponseSppDataCallback);
            } else {
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);
                setBleConnectStatus(STATUS_DISCONNECTED);
                secondTitle.setText(R.string.content_not_connect);
            }
        }
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolBar = findViewById(R.id.toolbar);
        toolBar.setTitle("");
        toolBar.setTitleTextColor(Color.WHITE);
        titleName = findViewById(R.id.title_name);
        secondTitle = findViewById(R.id.second_title);
        setTitleName(getTitle().toString());
        
        ImageView logoImage = findViewById(R.id.logo_image);
        if (logoImage != null) {
            if (CustomContentManager.getInstance(this).isBrandLogoVisible()) {
                logoImage.setVisibility(View.VISIBLE);
            } else {
                logoImage.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置返回
     *
     * @param back        :是否返回：是-->返回，不是则设置其他图标
     * @param resourcesId :图标id,返回时随意设置，不使用
     */
    protected void setTitleBack(final boolean back, int resourcesId) {
        is_title_back = back;
        if (title_back_first || titleBack == null) {
            titleBack = findViewById(R.id.title_back);
            titleBack.setOnClickListener(this);
            title_back_first = false;
        }
        titleBack.setVisibility(View.VISIBLE);
        if (!back) {
//            titleBack.setImageResource(resourcesId);
        }
    }

    protected void initBack() {
        setTitleBack(true, R.id.title_back);
    }

    /**
     * 设置title
     *
     * @param title ：title
     */
    protected void setTitleName(String title) {
        titleName.setText(title);
    }

    /**
     * title右侧:图标类
     */
    protected void setRightRes() {
        //扩展menu
//        toolBar.inflateMenu(R.menu.base_toolbar_menu);
        //添加监听
//        toolBar.setOnMenuItemClickListener(this);
    }

    /**
     * 显示title图标
     *
     * @param itemId :itemId :图标对应的选项id（1个到3个）,最多显示3两个
     */
    protected void showTitleRes(int... itemId) {
        if (title_menu_first) {
            setRightRes();
            title_menu_first = false;
        }
        for (int item : itemId) {
            //显示
            toolBar.getMenu().findItem(item).setVisible(true);//通过id查找,也可以用setIcon()设置图标
        }
    }

    protected void hideTitleRes(int... itemId) {
        if (title_menu_first) {
            setRightRes();
            title_menu_first = false;
        }
        for (int item : itemId) {
            //显示
            toolBar.getMenu().findItem(item).setVisible(false);//通过id查找,也可以用setIcon()设置图标
        }
    }

    /**
     * 隐藏title图标
     *
     * @param itemId :图标对应的选项id
     */
    protected void goneTitleRes(int... itemId) {
        if (titleBack != null)
            titleBack.setVisibility(View.GONE);
        for (int item : itemId) {
            //隐藏
            toolBar.getMenu().findItem(item).setVisible(false);
        }
    }

    /**
     * title右侧文字
     *
     * @param str :文字内容
     */
    protected void setTitleRightText(String str) {
        TextView textView = findViewById(R.id.title_rightTv);
        textView.setVisibility(View.VISIBLE);
        textView.setText(str);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_back && is_title_back) {
            onBackPressed();
        }
    }

    /**
     * toolbar菜单监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    private void setupUsbPermissionReceiver() {
        usbPermissionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (App.ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                MyLog.i("USB_PERMISSION", "USB权限已获得，开始连接设备");
                                // 权限获得后尝试连接
                                connectToUsbDeviceWithPermission(device);
                            }
                        } else {
                            MyLog.e("USB_PERMISSION", "USB权限被拒绝");
//                            Toast.makeText(BluetoothBaseActivity.this, "USB权限被拒绝，自动申请权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(App.ACTION_USB_PERMISSION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(usbPermissionReceiver, filter);
        }
    }

    private void showUsbDeviceSelection() {
        try {
            // 获取可用的USB设备
            ArrayList<UsbDevice> usbDeviceList = WCHUARTManager.getInstance().enumDevice();
            if (usbDeviceList == null || usbDeviceList.isEmpty()) {
                Toast.makeText(this, "未找到USB转串口设备", Toast.LENGTH_SHORT).show();
                return;
            }

            // 自动选择第一个可用设备进行连接
            UsbDevice firstDevice = usbDeviceList.get(0);
            String deviceName = firstDevice.getProductName();
            if (deviceName == null || deviceName.isEmpty()) {
                deviceName = "USB设备";
            }
            String deviceInfo = deviceName + " (VID:" + String.format("%04X", firstDevice.getVendorId()) + 
                               " PID:" + String.format("%04X", firstDevice.getProductId()) + ")";
            
            Log.d("USB连接", "自动选择设备: " + deviceInfo);
            Toast.makeText(this, "连接设备: " + deviceName, Toast.LENGTH_SHORT).show();
            
            connectToUsbDevice(firstDevice);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "获取USB设备失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToUsbDevice(UsbDevice device) {
        // 检查USB权限
        if (!App.getInstance().hasUsbPermission(device)) {
            MyLog.d("USB_CONNECT", "没有USB权限，申请权限");
            App.getInstance().requestUsbPermission(device, this);
            return;
        }
        
        // 有权限，直接连接
        connectToUsbDeviceWithPermission(device);
    }
    
    private void connectToUsbDeviceWithPermission(UsbDevice device) {
        try {
            // 先设置设备引用，再尝试打开
            App.getInstance().setCurrentUsbDevice(device);
            
            // 尝试打开USB设备
            boolean success = App.getInstance().openUsbDevice();
            if (success) {
                // 连接成功，数据回调已在App层自动设置，Activity无需重复设置
                
                btnBluetooth.setImageResource(R.mipmap.icon_usb_connected);
                setBleConnectStatus(STATUS_CONNECTED);
                secondTitle.setText("USB已连接");
                Toast.makeText(this, "USB设备连接成功", Toast.LENGTH_SHORT).show();
                
                // 连接成功后查询设备状态
                App.getInstance().getBluetoothService().queryClsl(cdslSetResponseSppDataCallback);
            } else {
                // 连接失败，清理设备引用
                App.getInstance().setCurrentUsbDevice(null);
                Toast.makeText(this, "USB设备连接失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // 发生异常，清理资源
            App.getInstance().setCurrentUsbDevice(null);
            MyLog.e("USB_CONNECT", "连接错误: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "连接错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void connectBluetooth() {
        if (!App.getInstance().getBluetoothClient().isBluetoothOpened()) {
            App.getInstance().getBluetoothClient().openBluetooth();
        } else {
            if (getBluetoothStatus() == Constants.STATUS_DEVICE_CONNECTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.content_affirm_title))
                        .setMessage("当前蓝牙已经连接，是否确认关闭当前连接选择新的设备？")
                        .setPositiveButton(getString(R.string.content_affirm_ok), (dialogInterface, i) -> {
                            App.getInstance().getBluetoothClient().disconnect(App.getInstance().getConnectMacAddress());
                            Intent intent = new Intent(getApplicationContext(), BleScanActivity.class);
                            startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                        })
                        .setNegativeButton(getString(R.string.content_affirm_cancel), (dialogInterface, i) -> {
                        });
                builder.show();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                } else {
                    Intent intent = new Intent(getApplicationContext(), BleScanActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                }
            }
        }
    }


    @OnClick(R.id.title_rightImage)
    public void connect() {
        if (App.getInstance().connectedModel == 0) {
            // USB转串口模式
            if (!App.getInstance().isUsbConnected()) {
                // 显示USB设备选择
                showUsbDeviceSelection();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.content_affirm_title))
                        .setMessage("当前USB设备已经连接，是否确认断开连接？")
                        .setPositiveButton(getString(R.string.content_affirm_ok), (dialogInterface, i) -> {
                            App.getInstance().closeUsbDevice();
                            btnBluetooth.setImageResource(R.mipmap.icon_usb_disconnected);
                            setBleConnectStatus(STATUS_DISCONNECTED);
                            secondTitle.setText(R.string.content_not_connect_1);
                        })
                        .setNegativeButton(getString(R.string.content_affirm_cancel), (dialogInterface, i) -> {
                        });
                builder.show();
            }
        } else {
            // 蓝牙模式
            connectBluetooth();
        }
    }

    @Override
    protected void onDestroy() {
        // 清理USB相关资源
        if (App.getInstance().connectedModel == 0) {
            // 不在Activity中清理USB回调，由App层统一管理生命周期
            // 只清理Activity特有的权限广播接收器
            if (usbPermissionReceiver != null) {
                try {
                    unregisterReceiver(usbPermissionReceiver);
                    MyLog.d("USB_PERMISSION", "USB权限广播接收器已注销");
                } catch (Exception e) {
                    MyLog.e("USB_PERMISSION", "注销广播接收器失败: " + e.getMessage());
                }
                usbPermissionReceiver = null;
            }
        }

        App.getInstance().getBluetoothSPP().setBluetoothStateListener(null);
        App.getInstance().getBluetoothSPP().setBluetoothConnectionListener(null);
        App.getInstance().getBluetoothSPP().setOnDataReceivedListener(null);
        App.getInstance().getBluetoothClient().unregisterBluetoothStateListener(mBluetoothStateListener);
        if (App.getInstance().getConnectMacAddress() != null) {
            App.getInstance().getBluetoothClient().unregisterConnectStatusListener(App.getInstance().getConnectMacAddress(), mBleConnectStatusListener);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200://刚才的识别码
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                    Intent intent = new Intent(getApplicationContext(), BleScanActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                } else {//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    Toast.makeText(this, "未开启定位权限,请手动到设置去开启权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    protected int getBluetoothStatus() {
        if (App.getInstance().getConnectMacAddress() != null) {
            return App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress());
        } else {
            return Constants.STATUS_UNKNOWN;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                lastConnectTime = System.currentTimeMillis();
                
                // 蓝牙连接结果处理
                SearchResult searchResult = data.getParcelableExtra("SearchResult");
                App.getInstance().setConnectMacAddress(searchResult.getAddress());

                App.getInstance().getBluetoothClient().registerConnectStatusListener(App.getInstance().getConnectMacAddress(), mBleConnectStatusListener);
                App.getInstance().getBluetoothClient().connect(App.getInstance().getConnectMacAddress(), bleConnectResponse);
            }
        }
    }

    private BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        @Override
        public void onResponse(int code, BleGattProfile profile) {
            if (code == REQUEST_SUCCESS) {
                App.getInstance().getBluetoothClient().notify(App.getInstance().getConnectMacAddress(), UUIDUtils.makeUUID(0xFFE0), UUIDUtils.makeUUID(0xFFE1), bleNotifyResponse);
            }
        }
    };

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {

        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Log.d(TAG, "onBluetoothStateChanged:" + openOrClosed);
        }

    };



    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {

            if (status == STATUS_DEVICE_CONNECTING) {
                Toast.makeText(getApplicationContext(), "设备连接中...", Toast.LENGTH_SHORT).show();
                secondTitle.setText("连接中");
            } else if (status == STATUS_CONNECTED) {
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth_connected);
                Toast.makeText(getApplicationContext(), "设备已连接", Toast.LENGTH_SHORT).show();
                setBleConnectStatus(STATUS_CONNECTED);
                secondTitle.setText(getString(R.string.content_battery));
            } else if (status == STATUS_DISCONNECTED) {
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);
                Toast.makeText(getApplicationContext(), "连接已断开", Toast.LENGTH_SHORT).show();
                App.getInstance().getBluetoothClient().unregisterConnectStatusListener(mac, mBleConnectStatusListener);
                setBleConnectStatus(STATUS_DISCONNECTED);
                secondTitle.setText("连接已断开");
            }
        }
    };

    private static final BleNotifyResponse bleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            if (value != null && value.length > 0) {
                MyLog.i("RX", HexString.bytesToHex(value));
            }

            try {
                App.getInstance().getBluetoothService().parse(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                Toast.makeText(App.getInstance(), "开启监听蓝牙返回数据成功！", Toast.LENGTH_SHORT).show();
                // 发送校时指令
                App.getInstance().getBluetoothService().setTime(System.currentTimeMillis() / 1000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        App.getInstance().getBluetoothService().queryClsl(cdslSetResponseSppDataCallback);
                    }
                }, 100);
            }
        }
    };



    public abstract void setBleConnectStatus(int status);

    private static final SppDataCallback<CdslSetResponse> cdslSetResponseSppDataCallback = new SppDataCallback<CdslSetResponse>() {

        @Override
        public void delivery(CdslSetResponse cdslSetResponse) {
            EventBus.getDefault().post(new SocResponse()); //普通事件发布
            AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
            appConfig.setPointCount(cdslSetResponse.getCount());
            if (appConfig.getPointCount() > 0 && appConfig.getPointCount() < 6) {
                App.getInstance().getLocalDataService().setAppConfig(appConfig);
                Log.d(TAG, "缓存测量测点：" + cdslSetResponse.getCount());
                EventBus.getDefault().post(appConfig); //普通事件发布s
            }
        }

        @Override
        public Class<CdslSetResponse> getEntityType() {
            return CdslSetResponse.class;
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(SocResponse socResponse) {
//        App.getInstance().setDeviceSoc(String.valueOf(socResponse.getSoc()));
        if (App.getInstance().connectedModel == 0) {
            secondTitle.setText(getString(R.string.content_battery_1));
        } else {
            secondTitle.setText(getString(R.string.content_battery));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEvent(BleEvent bleEvent) {
        if ((System.currentTimeMillis() - lastConnectTime) > 5 * 1000) {
            lastConnectTime = System.currentTimeMillis();
            searchDevice();
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUsbConnectEvent(UsbConnectEvent usbConnectEvent) {
        if (App.getInstance().connectedModel == 0) { // 仅在USB模式下处理
            if (usbConnectEvent.isConnected()) {
                // USB连接成功，更新界面状态
                btnBluetooth.setImageResource(R.mipmap.icon_usb_connected);
                setBleConnectStatus(Constants.STATUS_CONNECTED);
                secondTitle.setText("USB已连接");
                MyLog.d("USB_UI_UPDATE", "USB连接状态已更新到界面");
                
                // 查询设备状态
                App.getInstance().getBluetoothService().queryClsl(cdslSetResponseSppDataCallback);
            } else {
                // USB断开连接，更新界面状态
                btnBluetooth.setImageResource(R.mipmap.icon_usb_disconnected);
                setBleConnectStatus(STATUS_DISCONNECTED);
                secondTitle.setText(R.string.content_not_connect_1);
                MyLog.d("USB_UI_UPDATE", "USB断开状态已更新到界面");
            }
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUsbPermissionRequired(UsbPermissionRequiredEvent event) {
        if (App.getInstance().connectedModel == 0) { // 仅在USB模式下处理
            MyLog.d("USB_PERMISSION", "接收到USB权限请求事件，自动触发连接流程");
            // 自动触发连接按钮的操作
            connect();
        }
    }

    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2).build();

        App.getInstance().getBluetoothClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("BleScanActivity.onSearchStarted");
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (device != null && device.getAddress() != null && device.getAddress().equals(App.getInstance().getConnectMacAddress())) {
                Intent data = new Intent();
                data.putExtra("SearchResult", device);
                onActivityResult(REQUEST_CONNECT_DEVICE, Activity.RESULT_OK, data);
            }
        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("BleScanActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("BleScanActivity.onSearchCanceled");
        }
    };
}