package com.drt.moisture.measure;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.dashboard.DashboardContract;
import com.drt.moisture.dashboard.DashboardModel;
import com.drt.moisture.dashboard.DashboardPresenter;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.bluetooth.SppDataCallback;
import com.drt.moisture.data.source.bluetooth.response.ParameterSetResponse;
import com.drt.moisture.util.BarometricPressureUtil;
import com.drt.moisture.util.Test1;
import com.inuker.bluetooth.library.Constants;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author yuanzhijian
 */
public class MeasureActivity extends BluetoothBaseActivity<DashboardPresenter> implements DashboardContract.View {

    private static final String TAG = MeasureActivity.class.getSimpleName();

//    int[] colors = new int[]{R.color.btnOrange, R.color.btnGreen, R.color.btnBlue, R.color.btnRed1, R.color.btnRed};

    @BindView(R.id.table)
    SmartTable table;

//    LineChart chart;

    @BindView(R.id.btnStartMeasure)
    LinearLayout btnStartMeasure;

    @BindView(R.id.btnStopMeasure)
    LinearLayout btnStopMeasure;

    @BindView(R.id.spinner1)
    Spinner spMeasureModel;

    @BindView(R.id.spinner2)
    Spinner spMeasureTime;

    @BindView(R.id.mesasureName)
    EditText measureName;

    @BindView(R.id.alreadyRunning)
    TextView alreadyRunning;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.temperature)
    TextView temperature;

    @BindView(R.id.activeness)
    TextView activeness;

    @BindView(R.id.history)
    ImageButton history;

    ProgressDialog progressdialog;

    int index;

    private static MeasureActivity instance;

    public static MeasureActivity getInstance() {
        return instance;
    }

    private PowerManager.WakeLock mWakelock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);// init powerManager
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "moisture"); // this target for tell OS which app control screen
        mWakelock.acquire(); // Wake up Screen and keep screen lighting

        instance = this;

        index = getIntent().getIntExtra("index", 1);
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
        Log.d("yzj", appConfig.toString());
        spMeasureModel.setSelection(appConfig.getMeasureMode());
        spMeasureTime.setSelection(appConfig.getMeasuringTime() - 5);
        if (appConfig.getMeasureMode() == 0) {
            spMeasureTime.setEnabled(true);
        } else {
            spMeasureTime.setEnabled(false);
        }
        List<String> names = App.getInstance().getLocalDataService().queryHistory(index);
        measureName.setText(names.get(names.size() - 1));
        measureName.setOnEditorActionListener((v, actionId, event) -> {
            Log.e("输入完点击确认执行该方法", "输入结束");
            if (!TextUtils.isEmpty(measureName.getText())) {
                App.getInstance().getLocalDataService().setHistory(index, measureName.getText().toString());
                App.getInstance().getBluetoothService().setMeasureName(index, measureName.getText().toString(), new SppDataCallback<ParameterSetResponse>() {

                    @Override
                    public void delivery(ParameterSetResponse parameterSetResponse) {

                    }

                    @Override
                    public Class<ParameterSetResponse> getEntityType() {
                        return ParameterSetResponse.class;
                    }
                }, false);
            }

            return false;
        });

        setTitleName("天然气水合物相平衡仪");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_measure;
    }

    @Override
    public void initView() {
        mPresenter = DashboardActivity.getDashboardPresenter();
        if (mPresenter == null) {
            mPresenter = new DashboardPresenter();
            mPresenter.attachView(this);
        }
        mPresenter.attachView(this);


        initTableView();

        spMeasureModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spMeasureModel:onItemSelected:position:" + position);
                if (position == 0) {
                    spMeasureTime.setEnabled(true);
                } else {
                    spMeasureTime.setEnabled(false);
                }
                mPresenter.setMeasureModel(position, index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "spMeasureModel:onNothingSelected:view:" + parent);
            }
        });

        spMeasureTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spMeasureTime:spMeasureModelonItemSelected:position:" + position);
                mPresenter.setMeasureTime(position + 5, index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "spMeasureTime:onNothingSelected:view:" + parent);
            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onSuccess(final List<MeasureValue> measureValueList) {
        if (index == 0) {
            return;
        }
        if (DashboardActivity.getDashboardActivity() != null) {
            DashboardActivity.getDashboardActivity().onSuccess(measureValueList);
        }

        final MeasureValue measureValue = measureValueList.get(index - 1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (measureValue.getMeasureStatus() == 0x01 || measureValue.getMeasureStatus() == 0x03) {
                    updateUI(MeasureStatus.RUNNING);
                    addEntry(measureValue);


                }
                if (measureValue.getMeasureStatus() != 0 && measureValue.getMeasureStatus() != 0x02) {
                    time.setText(measureValue.getReportTime());
                    temperature.setText(String.format("%.2f", measureValue.getTemperature()) + "°C");
                    DecimalFormat df = new DecimalFormat("0.0000");
                    df.setRoundingMode(RoundingMode.DOWN);
                    activeness.setText(df.format(measureValue.getActivity()));
                } else {
                    updateUI(MeasureStatus.NORMAL);

                }
            }
        });
    }

    @Override
    public void onDone(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MeasureActivity.this);
                builder.setTitle(getString(R.string.content_affirm_title));//设置title
                builder.setMessage(getString(R.string.content_point) + index + getString(R.string.content_measure_done));//设置内容
                //点击确认按钮事件
                builder.setPositiveButton(getString(R.string.content_affirm_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //创建出AlertDialog对象
                AlertDialog alertDialog = builder.create();
                //点击对话框之外的地方不消失
                alertDialog.setCanceledOnTouchOutside(false);
                //设置显示
                alertDialog.show();
                playSound();
            }
        });
    }

    @Override
    public void onError(final Throwable throwable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btnStartMeasure)
    public void startMeasure() {

        switch (mPresenter.getMeasureStatus(index)) {
            case STOP:
            case ERROR:
            case NORMAL:
            case DONE:
                break;
            case RUNNING:
                onError(new Exception(getString(R.string.content_measuring)));
                return;
            case BT_NOT_CONNECT:
                onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                return;
        }
        progressdialog = new ProgressDialog(MeasureActivity.this);
        progressdialog.setTitle(getString(R.string.content_affirm_title));
        progressdialog.setMessage(getString(R.string.content_start_title));
        progressdialog.setCancelable(false);
        progressdialog.show();

        // 初始化表格
        List<BarometricPressure> list = new ArrayList<>();
        for (int i = 0; i <= 25; i++) {
//            list.add(new BarometricPressure(i, BarometricPressureUtil.Button111_Click(i, 0.02)));
            list.add(new BarometricPressure(i, "--"));
        }
        table.setData(list);
        // 校准时间
        App.getInstance().getBluetoothService().setTime(System.currentTimeMillis() / 1000);
        // 根据测点数量发送开始指令
        final int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
        new Thread(new Runnable() {
            public void run() {
                mPresenter.startMeasure(spMeasureModel.getSelectedItemPosition(), measureName.getText().toString(), index);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mPresenter.queryMeasureResult(pointCount);
                hideLoading();
            }
        }).start();

    }

    private void initTableView() {
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
        FontStyle.setDefaultTextSize(getResources().getDimensionPixelSize(R.dimen.sizeTitle));
        table.getConfig().setMinTableWidth(outSize.x / 2 - getResources().getDimensionPixelOffset(R.dimen.padding_default) * 2);
        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setContentBackground((canvas, rect, paint) -> {

        });

        // 初始化表格
        List<BarometricPressure> list = new ArrayList<>();
        for (int i = 0; i <= 25; i++) {
            list.add(new BarometricPressure(i, "--"));
        }
        table.setData(list);
    }

    @OnClick(R.id.btnStopMeasure)
    public void stopMeasure() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.content_affirm_title))
                .setMessage(getString(R.string.content_affirm_stop))
                .setPositiveButton(getString(R.string.content_affirm_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.stopMeasure(true, index);
                    }
                })
                .setNegativeButton(getString(R.string.content_affirm_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        builder.show();

    }

    @OnClick(R.id.history)
    public void onClickHistory() {
        List<String> historyList = App.getInstance().getLocalDataService().queryHistory(index);
        Collections.reverse(historyList);
        final String[] items = historyList.toArray(new String[historyList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.content_his_name))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        measureName.setText(items[i]);
                    }
                });
        builder.create().show();
    }

    @Override
    public void updateUI(final MeasureStatus measureStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (measureStatus) {
                    case BT_NOT_CONNECT:
                        measureName.setEnabled(false);
                        history.setEnabled(false);
                        history.setAlpha(0.32f);
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(0.32f);
                        spMeasureModel.setEnabled(false);
                        spMeasureTime.setEnabled(false);
                        break;
                    case RUNNING:
                        measureName.setEnabled(false);
                        history.setEnabled(false);
                        history.setAlpha(0.32f);
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(1.0f);
                        spMeasureModel.setEnabled(false);
                        spMeasureTime.setEnabled(false);
                        break;
                    default:
                        measureName.setEnabled(true);
                        history.setEnabled(true);
                        history.setAlpha(1.0f);
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        spMeasureModel.setEnabled(true);
                        if (spMeasureModel.getSelectedItemPosition() == 0) {
                            spMeasureTime.setEnabled(true);
                        } else {
                            spMeasureTime.setEnabled(false);
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void updateRunningStatus(final Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
        if (DashboardActivity.getDashboardActivity() != null) {
            DashboardActivity.getDashboardActivity().updateRunningStatus(measureRunningStatusMap);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DashboardModel.MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(index);
                if (measureRunningStatus != null && measureRunningStatus.isRunning() && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                    alreadyRunning.setText(measureRunningStatus.getRunningTime());
                }
            }
        });
    }

    @Override
    public void onStartMeasureSuccess(final int _index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MeasureActivity.this, "测点" + _index + "启动成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(mPresenter.getMeasureStatus(index));

        if (getIntent().getBooleanExtra("autoStart", false)) {
            getIntent().putExtra("autoStart", false);
            Toast.makeText(this, "即将启动定时测量", Toast.LENGTH_LONG).show();
            startMeasure();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!TextUtils.isEmpty(measureName.getText())) {
            App.getInstance().getLocalDataService().setHistory(index, measureName.getText().toString());
            App.getInstance().getBluetoothService().setMeasureName(index, measureName.getText().toString(), new SppDataCallback<ParameterSetResponse>() {

                @Override
                public void delivery(ParameterSetResponse parameterSetResponse) {

                }

                @Override
                public Class<ParameterSetResponse> getEntityType() {
                    return ParameterSetResponse.class;
                }
            }, false);
        }
    }

    @Override
    protected void onDestroy() {
//        mPresenter.stopCorrect(false, index);
        if (DashboardActivity.getDashboardPresenter() == null) {
            mPresenter.onDestroy();
        }
        instance = null;
        super.onDestroy();
        mWakelock.release(); // release control.stop to keep screen lighting
    }

    @Override
    public void setBleConnectStatus(int status) {
        if (status != Constants.STATUS_CONNECTED) {
            if (mPresenter.getMeasureStatus() == MeasureStatus.RUNNING) {
                mPresenter.setMeasureStatus(MeasureStatus.RUNNING);
            } else {
                mPresenter.setMeasureStatus(MeasureStatus.BT_NOT_CONNECT);
            }
        } else {
            if (mPresenter.getMeasureStatus() == MeasureStatus.RUNNING) {
                mPresenter.setMeasureStatus(MeasureStatus.RUNNING);
            } else {
                mPresenter.setMeasureStatus(MeasureStatus.NORMAL);
            }
        }
    }


    private void addEntry(MeasureValue measureValue) {
        double aw = measureValue.getActivity();
        // 初始化表格
        List<BarometricPressure> list = new ArrayList<>();
        // 0.85—1
        if (aw >= 0.85 && aw <= 1.0) {
            for (int i = 0; i <= 25; i++) {
                double[] y = {100};
                double t= i;
                list.add(new BarometricPressure(i, String.format("%.3f", Test1.GetP(t, y, aw))));
            }
        } else {
            for (int i = 0; i <= 25; i++) {
                list.add(new BarometricPressure(i, "--"));
            }
        }

        table.setData(list);
    }


    public void playSound() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
