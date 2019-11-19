package com.drt.moisture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.drt.moisture.correct.CorrectActivity;
import com.drt.moisture.data.BleEvent;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.report.ReportActivity;
import com.drt.moisture.setting.SettingActivity;
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

        switch (view.getId()) {
            case R.id.menu_01:
                startActivity(new Intent(this, MeasureActivity.class));
                break;
            case R.id.menu_02:
                startActivity(new Intent(this, CorrectActivity.class));
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
        App.getInstance().getBluetoothSPP().disconnect();
    }

    @Override
    public void setBleConnectStatus(int status) {

    }

    private Timer timer;

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (App.getInstance().getBluetoothClient().getConnectStatus(App.getInstance().getConnectMacAddress()) != Constants.STATUS_DEVICE_CONNECTED) {
                    EventBus.getDefault().post(new BleEvent());
                }
            }
        },0, 3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
