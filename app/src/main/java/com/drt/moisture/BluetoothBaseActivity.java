package com.drt.moisture;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.util.StatusBarUtil;
import com.zhjian.bluetooth.spp.BluetoothSPP;
import com.zhjian.bluetooth.spp.BluetoothState;
import com.zhjian.bluetooth.spp.DeviceList;

import net.yzj.android.common.base.BaseMvpActivity;
import net.yzj.android.common.base.BasePresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/9/5 0005.
 */

public abstract class BluetoothBaseActivity<T extends BasePresenter> extends BaseMvpActivity<T> implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = BluetoothBaseActivity.class.getSimpleName();

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
    private ImageButton titleBack;
    /*标题名称*/
    private TextView titleName;

    @BindView(R.id.title_rightImage)
    ImageButton btnBluetooth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 系统 6.0 以上 状态栏白底黑字的实现方法
        StatusBarUtil.setLightStatusBar(this.getWindow());
        // 竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initToolbar();

        // 初始化蓝牙按钮
        initBack();
        btnBluetooth.setVisibility(View.VISIBLE);

        App.getInstance().getBluetoothSPP().setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {

                switch (state) {
                    case BluetoothState.STATE_CONNECTED:
                        break;
                    case BluetoothState.STATE_CONNECTING:
                        Toast.makeText(getApplicationContext(), "设备连接中...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothState.STATE_LISTEN:
                        break;
                    case BluetoothState.STATE_NONE:
                        break;
                    case BluetoothState.STATE_NULL:
                        break;
                    default:
                        break;
                }
            }
        });

        App.getInstance().getBluetoothSPP().setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                // Do something when successfully connected
                Log.d(TAG, "onDeviceConnected:" + name);
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth_connected);
                Toast.makeText(getApplicationContext(), "设备已连接", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                // Do something when connection was disconnected
                Log.d(TAG, "onDeviceDisconnected");
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);
                Toast.makeText(getApplicationContext(), "连接已断开", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                // Do something when connection failed
                Log.d(TAG, "onDeviceConnectionFailed");
                btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);
                Toast.makeText(getApplicationContext(), "蓝牙连接失败！", Toast.LENGTH_SHORT).show();
            }
        });

        App.getInstance().getBluetoothSPP().setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                Log.d(TAG, new String(data) + "=====message:" + message);
                try {
                    App.getInstance().getBluetoothService().parse(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getBluetoothStatus() == BluetoothState.STATE_CONNECTED) {
            btnBluetooth.setImageResource(R.mipmap.ic_bluetooth_connected);
        } else {
            btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);
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
        setTitleName(getTitle().toString());
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
            titleBack.setImageResource(resourcesId);
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

    @OnClick(R.id.title_rightImage)
    public void connect() {

        if (!App.getInstance().getBluetoothSPP().isBluetoothEnabled()) {
            // Do somthing if bluetooth is disable
            Toast.makeText(this, "请在设置中打开蓝牙！", Toast.LENGTH_SHORT).show();
        } else {

            if (getBluetoothStatus() == BluetoothState.STATE_CONNECTED || getBluetoothStatus() == BluetoothState.STATE_CONNECTING) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("当前蓝牙已经连接，是否确认关闭当前连接选择新的设备？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getInstance().getBluetoothSPP().disconnect();
                                // Do something if bluetooth is already enable
                                App.getInstance().getBluetoothSPP().startService(BluetoothState.DEVICE_OTHER);
                                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
            } else {
                // Do something if bluetooth is already enable
                App.getInstance().getBluetoothSPP().startService(BluetoothState.DEVICE_OTHER);
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }


        }
    }

    @Override
    protected void onDestroy() {
        App.getInstance().getBluetoothSPP().setBluetoothStateListener(null);
        App.getInstance().getBluetoothSPP().setBluetoothConnectionListener(null);
        App.getInstance().getBluetoothSPP().setOnDataReceivedListener(null);
        super.onDestroy();
    }

    protected int getBluetoothStatus() {
        return App.getInstance().getBluetoothSPP().getServiceState();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                App.getInstance().getBluetoothSPP().connect(data);
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                App.getInstance().getBluetoothSPP().setupService();
                connect();
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

}