package com.drt.moisture.measure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.drt.moisture.CustomActionBarActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.MeasureValue;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author yuanzhijian
 */
public class MeasureActivity extends CustomActionBarActivity<MeasurePresenter> implements MeasureContract.View {

    private static final String TAG = MeasureActivity.class.getSimpleName();

    @BindView(R.id.title_rightImage)
    ImageButton btnBluetooth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBack();
        btnBluetooth.setVisibility(View.VISIBLE);
        btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_measure;
    }

    @Override
    public void initView() {
        mPresenter = new MeasurePresenter();
        mPresenter.attachView(this);



    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void startMeasure() {

    }

    @Override
    public void stopMeasure() {

    }

    @Override
    public void onSuccess(MeasureValue measureValue) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @OnClick(R.id.title_rightImage)
    public void actionBluetooth() {

    }
}
