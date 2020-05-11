package com.drt.moisture;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.drt.moisture.correct.CorrectActivity;
import com.drt.moisture.correctdashboard.CorrectDashboardActivity;
import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.report.ReportActivity;
import com.drt.moisture.setting.SettingActivity;
import com.drt.moisture.util.AppPermission;
import com.drt.moisture.util.MyLog;
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

        if (AppPermission.isGrantExternalRW(this)) {
            // 申请权限
            MyLog.delFile();
        } else {
            //
            Toast.makeText(this, "拒绝存储权限将无法保存日志！", Toast.LENGTH_LONG).show();
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

        setTitle(R.string.app_name_m);
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


}
