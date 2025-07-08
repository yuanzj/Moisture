package com.drt.moisture;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.correct.CorrectActivity;
import com.drt.moisture.correctdashboard.CorrectDashboardActivity;
import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.report.ReportActivity;
import com.drt.moisture.setting.SettingActivity;
import com.drt.moisture.util.AppPermission;
import com.drt.moisture.util.CustomTextUtil;
import com.drt.moisture.util.MyLog;
import com.drt.moisture.util.StorageHelper;
import com.inuker.bluetooth.library.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BluetoothBaseActivity<MainPresenter> {

    private long exitTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBack.setVisibility(View.GONE);

        // 请求存储权限
        if (!AppPermission.isGrantExternalRW(this)) {
            AppPermission.requestStoragePermissions(this);
            
            // Android 14+ 如果没有完整存储权限，提示用户
            if (android.os.Build.VERSION.SDK_INT >= 34 && !AppPermission.hasFullStorageAccess()) {
                MyLog.w("MainActivity", "Android 14+ detected, may need MANAGE_EXTERNAL_STORAGE permission for full access");
            }
        } else {
            MyLog.delFile();
        }
        
        // 检查存储访问权限并提示用户
        StorageHelper.checkAndPromptStorageAccess(this);

        // 请求蓝牙权限
        if (!AppPermission.hasBluetoothPermissions(this)) {
            AppPermission.requestBluetoothPermissions(this);
        }

        if (!App.getInstance().isRunning) {
            App.getInstance().initAutoConnect();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);

        setTitle(R.string.app_name);
        
        // 设置自定义工厂名称（完整名称）
        TextView factoryNameText = findViewById(R.id.factory_name_text);
        if (factoryNameText != null) {
            MyLog.d("MainActivity", "Found factory name full TextView, loading custom text...");
            String customFactoryName = CustomTextUtil.loadFactoryName(this, R.string.factory_name_full, false);
            MyLog.d("MainActivity", "Setting factory name full to: " + customFactoryName);
            factoryNameText.setText(customFactoryName);
        } else {
            MyLog.e("MainActivity", "Factory name full TextView not found!");
        }
        
        // 设置自定义工厂名称（短名称）
        TextView factoryShortNameText = findViewById(R.id.factory_name_short_text);
        if (factoryShortNameText != null) {
            MyLog.d("MainActivity", "Found factory name short TextView, loading custom text...");
            String customFactoryShortName = CustomTextUtil.loadFactoryName(this, R.string.factory_name, true);
            MyLog.d("MainActivity", "Setting factory name short to: " + customFactoryShortName);
            factoryShortNameText.setText(customFactoryShortName);
        } else {
            MyLog.e("MainActivity", "Factory name short TextView not found!");
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序喔~", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
            }
            return true; //
        }
        return false;
    }

    public void onClick(View view) {

        int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();

        switch (view.getId()) {
            case R.id.menu_01:
                if (pointCount == 1) {
                    if (DashboardActivity.getDashboardPresenter() != null) {
                        DashboardActivity.getDashboardPresenter().onDestroy();
                        DashboardActivity.setDashboardPresenter(null);
                    }

                    if (DashboardActivity.getDashboardActivity() != null) {
                        DashboardActivity.setDashboardActivity(null);
                    }

                    Intent intent = new Intent(this, MeasureActivity.class);
                    intent.putExtra("index", 1);
                    startActivity(intent);
                } else {
                    if (DashboardActivity.getDashboardPresenter() != null) {
                        DashboardActivity.getDashboardPresenter().onDestroy();
                        DashboardActivity.setDashboardPresenter(null);
                    }

                    if (DashboardActivity.getDashboardActivity() != null) {
                        DashboardActivity.setDashboardActivity(null);
                    }

                    startActivity(new Intent(this, DashboardActivity.class));
                }
                break;
            case R.id.menu_02:
                if (pointCount == 1) {
                    if (CorrectDashboardActivity.getDashboardPresenter() != null) {
                        CorrectDashboardActivity.getDashboardPresenter().onDestroy();
                        CorrectDashboardActivity.setCorrectDashboardPresenter(null);
                    }

                    if (CorrectDashboardActivity.getCorrectDashboardActivity() != null) {
                        CorrectDashboardActivity.setCorrectDashboardActivity(null);
                    }

                    Intent intent = new Intent(this, CorrectActivity.class);
                    intent.putExtra("index", 1);
                    startActivity(intent);
                } else {
                    if (CorrectDashboardActivity.getDashboardPresenter() != null) {
                        CorrectDashboardActivity.getDashboardPresenter().onDestroy();
                        CorrectDashboardActivity.setCorrectDashboardPresenter(null);
                    }

                    if (CorrectDashboardActivity.getCorrectDashboardActivity() != null) {
                        CorrectDashboardActivity.setCorrectDashboardActivity(null);
                    }

                    startActivity(new Intent(this, CorrectDashboardActivity.class));
                }
                break;
            case R.id.menu_03:
                startActivity(new Intent(this, ReportActivity.class));
                break;
            case R.id.menu_04:
                startActivity(new Intent(this, SettingActivity.class));
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().isRunning = false;
        App.getInstance().getBluetoothSPP().disconnect();
    }

    @Override
    public void setBleConnectStatus(int status) {

    }

//    private Timer timer;

    @Override
    protected void onResume() {
        super.onResume();
        
        // 每次resume时重新加载自定义工厂名称（完整名称）
        TextView factoryNameText = findViewById(R.id.factory_name_text);
        if (factoryNameText != null) {
            MyLog.d("MainActivity", "onResume: Reloading factory name full...");
            String customFactoryName = CustomTextUtil.loadFactoryName(this, R.string.factory_name_full, false);
            MyLog.d("MainActivity", "onResume: Setting factory name full to: " + customFactoryName);
            factoryNameText.setText(customFactoryName);
        }
        
        // 每次resume时重新加载自定义工厂名称（短名称）
        TextView factoryShortNameText = findViewById(R.id.factory_name_short_text);
        if (factoryShortNameText != null) {
            MyLog.d("MainActivity", "onResume: Reloading factory name short...");
            String customFactoryShortName = CustomTextUtil.loadFactoryName(this, R.string.factory_name, true);
            MyLog.d("MainActivity", "onResume: Setting factory name short to: " + customFactoryShortName);
            factoryShortNameText.setText(customFactoryShortName);
        }
        
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (App.getInstance().getConnectMacAddress() != null && App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress()) != Constants.STATUS_DEVICE_CONNECTED) {
//                    EventBus.getDefault().post(new BleEvent());
//                }
//            }
//        }, 0, 3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        
        switch (requestCode) {
            case 1: // 存储权限
                if (allGranted) {
                    MyLog.d("MainActivity", "Storage permissions granted");
                    MyLog.delFile();
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= 34) {
                        // Android 14+ 权限被拒绝，显示存储权限对话框
                        Toast.makeText(this, "存储权限被拒绝，可能无法读取自定义工厂名称", Toast.LENGTH_LONG).show();
                        StorageHelper.showStoragePermissionDialog(this);
                    } else {
                        Toast.makeText(this, "拒绝存储权限将无法保存日志！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case 2: // 蓝牙权限
                if (allGranted) {
                    MyLog.d("MainActivity", "Bluetooth permissions granted");
                } else {
                    Toast.makeText(this, "拒绝蓝牙权限将无法连接设备！", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
