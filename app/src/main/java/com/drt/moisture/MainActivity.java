package com.drt.moisture;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.drt.moisture.correct.CorrectActivity;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.report.ReportActivity;


public class MainActivity extends CustomActionBarActivity<MainPresenter> {

    private long exitTime = 0;

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
        }

    }
}
