package com.drt.moisture.dashboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.bluetooth.response.DeviceStatusResponse;
import com.drt.moisture.data.source.bluetooth.resquest.SendAutoStartMsg;
import com.drt.moisture.data.source.bluetooth.resquest.SendUpdateAlarmMsg;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.util.LineChartMarkView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.inuker.bluetooth.library.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author yuanzhijian
 */
public class DashboardActivity extends BluetoothBaseActivity<DashboardPresenter> implements DashboardContract.View {

    private static final String TAG = DashboardActivity.class.getSimpleName();

    int[] colors = new int[]{R.color.btnOrange, R.color.btnGreen, R.color.btnBlue, R.color.btnRed1, R.color.btnRed};

    public volatile boolean isFront = false;

    @BindView(R.id.parent_chart)
    RelativeLayout parentChart;


    LineChart chart;

    @BindView(R.id.btnStartMeasure)
    LinearLayout btnStartMeasure;

    @BindView(R.id.btnStopMeasure)
    LinearLayout btnStopMeasure;

    @BindView(R.id.temperature)
    TextView temperature;

    @BindView(R.id.activeness)
    TextView activeness;

    @BindView(R.id.alreadyRunning)
    TextView alreadyRunning;

    @BindView(R.id.temperature1)
    TextView temperature1;

    @BindView(R.id.activeness1)
    TextView activeness1;

    @BindView(R.id.alreadyRunning1)
    TextView alreadyRunning1;

    @BindView(R.id.temperature2)
    TextView temperature2;

    @BindView(R.id.activeness2)
    TextView activeness2;

    @BindView(R.id.alreadyRunning2)
    TextView alreadyRunning2;

    @BindView(R.id.temperature3)
    TextView temperature3;

    @BindView(R.id.activeness3)
    TextView activeness3;

    @BindView(R.id.alreadyRunning3)
    TextView alreadyRunning3;

    @BindView(R.id.temperature4)
    TextView temperature4;

    @BindView(R.id.activeness4)
    TextView activeness4;

    @BindView(R.id.alreadyRunning4)
    TextView alreadyRunning4;

    @BindView(R.id.point1)
    View point1;

    @BindView(R.id.point2)
    View point2;

    @BindView(R.id.point3)
    View point3;

    @BindView(R.id.point4)
    View point4;

    @BindView(R.id.point5)
    View point5;

    ProgressDialog progressdialog;

    CountDownLatch countDownLatch;
    Map<Integer, Boolean> startStatus = new ConcurrentHashMap<>();

    public static void setDashboardPresenter(DashboardPresenter mDashboardPresenter) {
        DashboardActivity.mDashboardPresenter = mDashboardPresenter;
    }

    private static DashboardPresenter mDashboardPresenter;

    public static void setDashboardActivity(DashboardActivity dashboardActivity) {
        DashboardActivity.dashboardActivity = dashboardActivity;
    }

    private static DashboardActivity dashboardActivity;

    public static DashboardPresenter getDashboardPresenter() {
        return DashboardActivity.mDashboardPresenter;
    }

    public static DashboardActivity getDashboardActivity() {
        return dashboardActivity;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(DeviceStatusResponse socResponse) {
        if (isFront) {
            // 根据测点数量发送开始指令
            int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
            mPresenter.queryMeasureResult(pointCount);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendAutoStartMsg(SendAutoStartMsg mSendAutoStartMsg) {
        if (isFront) {
            Toast.makeText(this, "即将启动定时测量", Toast.LENGTH_LONG).show();
            startMeasure();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(AppConfig appConfig) {
        if (isFront) {
            point1.setTag(1);
            point2.setTag(2);
            point3.setTag(3);
            point4.setTag(4);
            point5.setTag(5);

            View[] pointViews = new View[]{point1, point2, point3, point4, point5};
            int pointCount = appConfig.getPointCount();
            for (int i = 0; i < pointViews.length; i++) {
                View point = pointViews[i];
                if (i < pointCount) {
                    point.setAlpha(1f);
                    point.setEnabled(true);
                } else {
                    point.setAlpha(0.32f);
                    point.setEnabled(false);
                }
                point.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DashboardActivity.this, MeasureActivity.class);
                        intent.putExtra("index", (Integer) view.getTag());
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardActivity = this;
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
        point1.setTag(1);
        point2.setTag(2);
        point3.setTag(3);
        point4.setTag(4);
        point5.setTag(5);

        View[] pointViews = new View[]{point1, point2, point3, point4, point5};
        int pointCount = appConfig.getPointCount();
        for (int i = 0; i < pointViews.length; i++) {
            View point = pointViews[i];
            if (i < pointCount) {
                point.setAlpha(1f);
                point.setEnabled(true);
            } else {
                point.setAlpha(0.32f);
                point.setEnabled(false);
            }
            point.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DashboardActivity.this, MeasureActivity.class);
                    intent.putExtra("index", (Integer) view.getTag());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dashboard;
    }

    @Override
    public void initView() {
        mPresenter = new DashboardPresenter();
        mPresenter.attachView(this);
        mDashboardPresenter = mPresenter;

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                boolean noRunning = true;
                for (int i = 0; i < measureValueList.size(); i++) {

                    MeasureValue measureValue = measureValueList.get(i);

                    if (measureValue.getMeasureStatus() == 0x01 || measureValue.getMeasureStatus() == 0x03) {
                        noRunning = false;
                        break;
                    }
                }

                if (noRunning) {
                    updateUI(MeasureStatus.NORMAL);
                    return;
                }
                updateUI(MeasureStatus.RUNNING);


                addEntry(measureValueList);
                DecimalFormat df = new DecimalFormat("0.0000");
                df.setRoundingMode(RoundingMode.DOWN);

                switch (measureValueList.size()) {
                    case 1:
                        if (measureValueList.get(0).getMeasureStatus() != 0 && measureValueList.get(0).getMeasureStatus() != 0x02) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        break;
                    case 2:
                        if (measureValueList.get(0).getMeasureStatus() != 0 && measureValueList.get(0).getMeasureStatus() != 0x02) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0 && measureValueList.get(1).getMeasureStatus() != 0x02) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        break;
                    case 3:
                        if (measureValueList.get(0).getMeasureStatus() != 0 && measureValueList.get(0).getMeasureStatus() != 0x02) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0 && measureValueList.get(1).getMeasureStatus() != 0x02) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        if (measureValueList.get(2).getMeasureStatus() != 0 && measureValueList.get(2).getMeasureStatus() != 0x02) {
                            temperature2.setText(String.format("%.2f", measureValueList.get(2).getTemperature()) + "°C");
                            activeness2.setText(df.format(measureValueList.get(2).getActivity()));
                        }
                        break;
                    case 4:
                        if (measureValueList.get(0).getMeasureStatus() != 0 && measureValueList.get(0).getMeasureStatus() != 0x02) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0 && measureValueList.get(1).getMeasureStatus() != 0x02) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        if (measureValueList.get(2).getMeasureStatus() != 0 && measureValueList.get(2).getMeasureStatus() != 0x02) {
                            temperature2.setText(String.format("%.2f", measureValueList.get(2).getTemperature()) + "°C");
                            activeness2.setText(df.format(measureValueList.get(2).getActivity()));
                        }
                        if (measureValueList.get(3).getMeasureStatus() != 0 && measureValueList.get(3).getMeasureStatus() != 0x02) {
                            temperature3.setText(String.format("%.2f", measureValueList.get(3).getTemperature()) + "°C");
                            activeness3.setText(df.format(measureValueList.get(3).getActivity()));
                        }

                        break;
                    case 5:
                        if (measureValueList.get(0).getMeasureStatus() != 0 && measureValueList.get(0).getMeasureStatus() != 0x02) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0 && measureValueList.get(1).getMeasureStatus() != 0x02) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        if (measureValueList.get(2).getMeasureStatus() != 0 && measureValueList.get(2).getMeasureStatus() != 0x02) {
                            temperature2.setText(String.format("%.2f", measureValueList.get(2).getTemperature()) + "°C");
                            activeness2.setText(df.format(measureValueList.get(2).getActivity()));
                        }
                        if (measureValueList.get(3).getMeasureStatus() != 0 && measureValueList.get(3).getMeasureStatus() != 0x02) {
                            temperature3.setText(String.format("%.2f", measureValueList.get(3).getTemperature()) + "°C");
                            activeness3.setText(df.format(measureValueList.get(3).getActivity()));
                        }
                        if (measureValueList.get(4).getMeasureStatus() != 0 && measureValueList.get(4).getMeasureStatus() != 0x02) {
                            temperature4.setText(String.format("%.2f", measureValueList.get(4).getTemperature()) + "°C");
                            activeness4.setText(df.format(measureValueList.get(4).getActivity()));
                        }

                        break;
                }
            }
        });
    }

    @Override
    public void onDone(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFront) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("提示");//设置title
                    builder.setMessage("测点" + index + "测量完成");//设置内容
                    //点击确认按钮事件
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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

        switch (mPresenter.getMeasureStatus()) {
            case RUNNING:
                onError(new Exception("测量中..."));
                return;
            case BT_NOT_CONNECT:
                onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                break;
        }
        progressdialog = new ProgressDialog(DashboardActivity.this);
        progressdialog.setTitle("提示");
        progressdialog.setMessage("启动所有测量，请稍后...");
        progressdialog.setCancelable(false);
        progressdialog.show();

        if (chart != null) {
            parentChart.removeAllViews();
        }
        chart = (LineChart) getLayoutInflater().inflate(R.layout.chart_view, parentChart, false);
        parentChart.addView(chart);
        initChartView();
        // 校准时间
        App.getInstance().getBluetoothService().setTime(System.currentTimeMillis() / 1000);

        new Thread(new Runnable() {
            public void run() {

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 根据测点数量发送开始指令
                int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
                countDownLatch = new CountDownLatch(pointCount);

                startStatus.clear();
                for (int i = 0; i < pointCount; i++) {
                    startStatus.put(i + 1, false);
                }

                int index = 1;
                List<String> historyList = App.getInstance().getLocalDataService().queryHistory(index);
                Collections.reverse(historyList);
                String name = historyList.get(0);
                AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
                mPresenter.startMeasure(appConfig.getMeasureMode(), name, index);

                try {
                    countDownLatch.await(8, TimeUnit.SECONDS);
                    mPresenter.queryMeasureResult(pointCount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mPresenter.setMeasureStatus(MeasureStatus.NORMAL);
                    onError(new Exception("启动超时，请重试"));
                }
                hideLoading();
            }
        }).start();

    }

    @OnClick(R.id.btnStopMeasure)
    public void stopMeasure() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否确认停止测量？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressdialog = new ProgressDialog(DashboardActivity.this);
                                progressdialog.setTitle("提示");
                                progressdialog.setMessage("停止所有测量，请稍后...");
                                progressdialog.setCancelable(false);
                                progressdialog.show();
                            }
                        });
                        // 根据测点数量发送停止指令
                        new Thread(new Runnable() {
                            public void run() {
                                mPresenter.stopAll();
                                hideLoading();
                            }
                        }).start();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        builder.show();

    }

    @Override
    public void updateUI(final MeasureStatus measureStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (measureStatus) {
                    case BT_NOT_CONNECT:
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(0.32f);
                        break;
                    case RUNNING:
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(1.0f);
                        break;
                    default:
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        break;
                }
            }
        });
    }

    @Override
    public void updateRunningStatus(final Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
                DashboardModel.MeasureRunningStatus measureRunningStatus;
                switch (pointCount) {
                    case 1:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning.setText(measureRunningStatus.getRunningTime());
                        }
                        break;
                    case 2:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning1.setText(measureRunningStatus.getRunningTime());
                        }
                        break;
                    case 3:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning1.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(3);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning2 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning2.setText(measureRunningStatus.getRunningTime());
                        }
                        break;
                    case 4:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning1.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(3);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning2 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning2.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(4);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning3 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning3.setText(measureRunningStatus.getRunningTime());
                        }
                        break;
                    case 5:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning1.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(3);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning2 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning2.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(4);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning3 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning3.setText(measureRunningStatus.getRunningTime());
                        }
                        measureRunningStatus = measureRunningStatusMap.get(5);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning4 != null && measureRunningStatus.getRunningTime() != null) {
                            alreadyRunning4.setText(measureRunningStatus.getRunningTime());
                        }
                        break;
                }

            }
        });
    }

    @Override
    public void onStartMeasureSuccess(final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean indexFlag = startStatus.get(index);
                // 未启动
                if (indexFlag != null && !indexFlag) {
                    startStatus.put(index, true);

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 启动下一个测点
                    int nextIndex = (index + 1);
                    if (startStatus.get(nextIndex) != null && !startStatus.get(nextIndex)) {
                        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(nextIndex);
                        List<String> historyList = App.getInstance().getLocalDataService().queryHistory(nextIndex);
                        Collections.reverse(historyList);
                        String name = historyList.get(0);
                        mPresenter.startMeasure(appConfig.getMeasureMode(), name, nextIndex);
                    }

                    // 释放测点
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        mPresenter.attachView(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.attachView(DashboardActivity.this);
                if (mPresenter.isRunning()) {
                    updateUI(MeasureStatus.RUNNING);
                } else {
                    updateUI(MeasureStatus.NORMAL);

                }

            }
        }, 500);

        EventBus.getDefault().post(new SendUpdateAlarmMsg());

        if (getIntent().getBooleanExtra("autoStart", false)) {
            Toast.makeText(this, "即将启动定时测量", Toast.LENGTH_LONG).show();
            startMeasure();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    @Override
    protected void onDestroy() {
        dashboardActivity = null;
        mPresenter.onDestroy();
        super.onDestroy();
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

    Queue<IndexEntry> queue = new LinkedList<>();

    public static class IndexEntry {
        private float index;
        private float maxValue;
        private float minValue;

        public float getIndex() {
            return index;
        }

        public void setIndex(float index) {
            this.index = index;
        }

        public float getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(float maxValue) {
            this.maxValue = maxValue;
        }

        public float getMinValue() {
            return minValue;
        }

        public void setMinValue(float minValue) {
            this.minValue = minValue;
        }

        @Override
        public String toString() {
            return "IndexEntry{" +
                    "index=" + index +
                    ", maxValue=" + maxValue +
                    ", minValue=" + minValue +
                    '}';
        }
    }

    private void addEntry(List<MeasureValue> measureValueList) {
        if (chart == null) {
            chart = (LineChart) getLayoutInflater().inflate(R.layout.chart_view, parentChart, false);
            parentChart.addView(chart);
            initChartView();
        }
        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        IndexEntry indexEntry = new IndexEntry();

        for (int i = 0; i < measureValueList.size(); i++) {

            MeasureValue measureValue = measureValueList.get(i);
            measureValue.setIndex(i + 1);
            ILineDataSet set = data.getDataSetByIndex(i);

            if (set == null) {
                set = createActivitySet(i);
                data.addDataSet(set);
            }

            if (measureValue.getMeasureStatus() == 0x01 || measureValue.getMeasureStatus() == 0x03 && measureValue.getActivity() > 0) {

                data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getActivity(), measureValue), i);
                if (minY > (float) measureValue.getActivity()) {
                    minY = (float) measureValue.getActivity();
                }
                if (maxY < (float) measureValue.getActivity()) {
                    maxY = (float) measureValue.getActivity();
                }
            } else {
                data.addEntry(new Entry(set.getEntryCount(), -1, measureValue), i);
            }
        }

        indexEntry.setMaxValue(maxY);
        indexEntry.setMinValue(minY);

        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();


        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        if (data.getXMax() >= 50) {
            chart.getXAxis().resetAxisMaximum();
            chart.setVisibleXRangeMaximum(50);
        } else {
            chart.getXAxis().setAxisMaximum(50);
        }


        if (maxY > 0 && minY > 0) {
            queue.add(indexEntry);
        }
        while (queue.size() > 51) {
            queue.remove();
        }

        if (queue.size() > 0) {
            float currentMinY = Float.MAX_VALUE, currentMaxY = Float.MIN_VALUE;
            for (IndexEntry item : queue) {
                if (currentMinY > item.getMinValue()) {
                    currentMinY = item.getMinValue();
                }
                if (currentMaxY < item.getMaxValue()) {
                    currentMaxY = item.getMaxValue();
                }
            }

            float space = (currentMaxY - currentMinY) / 8.0F;
            Log.e("yzj1", "setAxisMinimum：" + (currentMinY - space) + "，setAxisMaximum:" + (currentMaxY + space));

            if (space > 0 && space < 1.0) {
                float minValue = currentMinY - space;
                float maxValue = currentMaxY + space;
                if ((maxValue - minValue) < 0.02F) {
                    float temp = (0.02F - (maxValue - minValue)) / 2.0f;
                    minValue -= temp;
                    maxValue += temp;
                }

                chart.getAxisLeft().setAxisMinimum(minValue);
                chart.getAxisLeft().setAxisMaximum(maxValue);
            }
        }

        chart.moveViewToX(data.getXMax());
    }

    private LineDataSet createActivitySet(int i) {

        LineDataSet d2 = new LineDataSet(null, "测点" + (i + 1) + "水分活度");
        d2.setLineWidth(2.0f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(getResources().getColor(colors[i], getTheme()));
        d2.setColor(getResources().getColor(colors[i], getTheme()));
        d2.setCircleColor(getResources().getColor(colors[i], getTheme()));
        d2.setDrawValues(false);
        d2.setDrawCircles(false);

        d2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d2.setValueFormatter(new ValueFormatter() {
            public String getFormattedValue(float value) {
                DecimalFormat df = new DecimalFormat("0.0000");
                df.setRoundingMode(RoundingMode.DOWN);
                return df.format(value);
            }
        });
        return d2;
    }

    private void initChartView() {
        queue.clear();

        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
//        chart.setMinimumHeight((outSize.x - getResources().getDimensionPixelSize(R.dimen.padding_default) * 2) / 2);

        // apply styling
        // holder.chart.setValueTypeface(mTf);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setNoDataText("没有测量数据。请点击右上角蓝牙按钮连接设备后开始测量!");
        chart.setNoDataTextColor(getColor(R.color.colorSecondBody));

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(50);
        xAxis.setLabelCount(10, false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (chart.getLineData() != null
                        && chart.getLineData().getMaxEntryCountSet() != null
                        && chart.getLineData().getMaxEntryCountSet().getEntryCount() > (int) value
                        && chart.getLineData().getMaxEntryCountSet().getEntryForIndex((int) value) != null) {
                    Entry entry = chart.getLineData().getMaxEntryCountSet().getEntryForIndex((int) value);
                    if (entry.getData() instanceof MeasureValue) {
                        return ((MeasureValue) entry.getData()).getReportTime();
                    } else {
                        return "";
                    }
                } else {
                    return "";
                }
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(10, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(1.0000f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMaximum(1.0000f);
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);

        LineChartMarkView mv = new LineChartMarkView(this, xAxis.getValueFormatter());
        mv.setChartView(chart);
        chart.setMarker(mv);
        chart.invalidate();

        chart.setDoubleTapToZoomEnabled(false);//双击屏幕缩放
        chart.setScaleEnabled(false);
        chart.setScaleXEnabled(false);
        chart.setScaleYEnabled(false);
        chart.setDragEnabled(false);
    }

    public void playSound() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
