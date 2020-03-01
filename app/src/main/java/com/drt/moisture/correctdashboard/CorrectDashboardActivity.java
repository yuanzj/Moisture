package com.drt.moisture.correctdashboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.correct.CorrectActivity;
import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.measure.MeasureActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.inuker.bluetooth.library.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author yuanzhijian
 */
public class CorrectDashboardActivity extends BluetoothBaseActivity<CorrectDashboardPresenter> implements CorrectDashboardContract.View {

    private static final String TAG = CorrectDashboardActivity.class.getSimpleName();

    int[] colors = new int[]{R.color.btnOrange, R.color.btnGreen, R.color.btnBlue, R.color.btnRed1, R.color.btnRed};

    boolean isFront = false;

    @BindView(R.id.chart)
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

    private static CorrectDashboardPresenter mCorrectDashboardPresenter;

    private static CorrectDashboardActivity correctDashboardActivity;

    public static CorrectDashboardPresenter getDashboardPresenter() {
        return CorrectDashboardActivity.mCorrectDashboardPresenter;
    }

    public static CorrectDashboardActivity getCorrectDashboardActivity() {
        return correctDashboardActivity;
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
                        Intent intent = new Intent(CorrectDashboardActivity.this, CorrectActivity.class);
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
        correctDashboardActivity = this;
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
                    Intent intent = new Intent(CorrectDashboardActivity.this, CorrectActivity.class);
                    intent.putExtra("index", (Integer) view.getTag());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_correct_dashboard;
    }

    @Override
    public void initView() {
        mPresenter = new CorrectDashboardPresenter();
        mPresenter.attachView(this);
        mCorrectDashboardPresenter = mPresenter;

        initChartView();
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

                    if (measureValue.getMeasureStatus() == 0x01) {
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
                        if (measureValueList.get(0).getMeasureStatus() != 0) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        break;
                    case 2:
                        if (measureValueList.get(0).getMeasureStatus() != 0) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        break;
                    case 3:
                        if (measureValueList.get(0).getMeasureStatus() != 0) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        if (measureValueList.get(2).getMeasureStatus() != 0) {
                            temperature2.setText(String.format("%.2f", measureValueList.get(2).getTemperature()) + "°C");
                            activeness2.setText(df.format(measureValueList.get(2).getActivity()));
                        }
                        break;
                    case 4:
                        if (measureValueList.get(0).getMeasureStatus() != 0) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        if (measureValueList.get(2).getMeasureStatus() != 0) {
                            temperature2.setText(String.format("%.2f", measureValueList.get(2).getTemperature()) + "°C");
                            activeness2.setText(df.format(measureValueList.get(2).getActivity()));
                        }
                        if (measureValueList.get(3).getMeasureStatus() != 0) {
                            temperature3.setText(String.format("%.2f", measureValueList.get(3).getTemperature()) + "°C");
                            activeness3.setText(df.format(measureValueList.get(3).getActivity()));
                        }

                        break;
                    case 5:
                        if (measureValueList.get(0).getMeasureStatus() != 0) {
                            temperature.setText(String.format("%.2f", measureValueList.get(0).getTemperature()) + "°C");
                            activeness.setText(df.format(measureValueList.get(0).getActivity()));
                        }
                        if (measureValueList.get(1).getMeasureStatus() != 0) {
                            temperature1.setText(String.format("%.2f", measureValueList.get(1).getTemperature()) + "°C");
                            activeness1.setText(df.format(measureValueList.get(1).getActivity()));
                        }
                        if (measureValueList.get(2).getMeasureStatus() != 0) {
                            temperature2.setText(String.format("%.2f", measureValueList.get(2).getTemperature()) + "°C");
                            activeness2.setText(df.format(measureValueList.get(2).getActivity()));
                        }
                        if (measureValueList.get(3).getMeasureStatus() != 0) {
                            temperature3.setText(String.format("%.2f", measureValueList.get(3).getTemperature()) + "°C");
                            activeness3.setText(df.format(measureValueList.get(3).getActivity()));
                        }
                        if (measureValueList.get(4).getMeasureStatus() != 0) {
                            temperature4.setText(String.format("%.2f", measureValueList.get(4).getTemperature()) + "°C");
                            activeness4.setText(df.format(measureValueList.get(4).getActivity()));
                        }

                        break;
                }
            }
        });
    }

    @Override
    public void onDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFront) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CorrectDashboardActivity.this);
                    builder.setTitle("提示");//设置title
                    builder.setMessage("矫正完成");//设置内容
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
                onError(new Exception("矫正中..."));
                return;
            case BT_NOT_CONNECT:
                onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                break;
        }
        progressdialog = new ProgressDialog(CorrectDashboardActivity.this);
        progressdialog.setTitle("提示");
        progressdialog.setMessage("启动所有矫正，请稍后...");
        progressdialog.setCancelable(false);
        progressdialog.show();

        chart.clear();
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
                AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(1);
                mPresenter.startCorrect(appConfig.getCorrectMode(), appConfig.getCorrectType(), pointCount);
            }
        }).start();

    }

    @OnClick(R.id.btnStopMeasure)
    public void stopMeasure() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否确认停止矫正？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressdialog = new ProgressDialog(CorrectDashboardActivity.this);
                                progressdialog.setTitle("提示");
                                progressdialog.setMessage("停止所有矫正，请稍后...");
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
    public void updateStep(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(CorrectDashboardActivity.this)
                        .setTitle("提示")
                        .setMessage(message).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
                                mPresenter.startCorrect(0x02, 0x02, appConfig.getPointCount());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public void updateRunningStatus(final Map<Integer, CorrectDashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
                CorrectDashboardModel.MeasureRunningStatus measureRunningStatus;
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
    public void onStartMeasureSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoading();
                int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
                mPresenter.queryCorrectResult(pointCount);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        mPresenter.attachView(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.attachView(CorrectDashboardActivity.this);
                if (mPresenter.isRunning()) {
                    updateUI(MeasureStatus.RUNNING);
                } else {
                    updateUI(MeasureStatus.NORMAL);

                }

            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    @Override
    protected void onDestroy() {
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

    private void addEntry(List<MeasureValue> measureValueList) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        for (int i = 0; i < measureValueList.size(); i++) {

            MeasureValue measureValue = measureValueList.get(i);
            ILineDataSet set = data.getDataSetByIndex(i);
            if (set == null) {
                set = createActivitySet(i);
                data.addDataSet(set);
            }
            if (measureValue.getMeasureStatus() == 0x01) {
                data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getActivity(), measureValue.getReportTime()), i);
            } else {
//                data.addEntry(new Entry(set.getEntryCount(), -1, measureValue.getReportTime()), i);
            }
        }
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(30);
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        chart.moveViewToX((float) (data.getEntryCount() - 1));
    }

    private LineDataSet createActivitySet(int i) {


        LineDataSet d2 = new LineDataSet(null, "测点" + (i + 1) + "水分活度");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(getResources().getColor(colors[i], getTheme()));
        d2.setColor(getResources().getColor(colors[i], getTheme()));
        d2.setCircleColor(getResources().getColor(colors[i], getTheme()));
        d2.setDrawValues(true);
        d2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d2.setValueFormatter(new ValueFormatter() {
            public String getFormattedValue(float value) {
                DecimalFormat df = new DecimalFormat("0.00");
                df.setRoundingMode(RoundingMode.DOWN);
                return df.format(value);
            }
        });
        return d2;
    }

    private void initChartView() {
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
//        chart.setMinimumHeight((outSize.x - getResources().getDimensionPixelSize(R.dimen.padding_default) * 2) / 2);

        // apply styling
        // holder.chart.setValueTypeface(mTf);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setNoDataText("没有矫正数据。请点击右上角蓝牙按钮连接设备后开始矫正!");
        chart.setNoDataTextColor(getColor(R.color.colorSecondBody));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (chart.getLineData() != null
                        && chart.getLineData().getDataSetByIndex(0) != null
                        && chart.getLineData().getDataSetByIndex(0).getEntryCount() > (int) value
                        && chart.getLineData().getDataSetByIndex(0).getEntryForIndex((int) value) != null) {
                    Entry entry = chart.getLineData().getDataSetByIndex(0).getEntryForIndex((int) value);
                    return (String) entry.getData();
                } else {
                    return "";
                }
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(1.0000f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMaximum(1.0000f);
    }

    public void playSound() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
