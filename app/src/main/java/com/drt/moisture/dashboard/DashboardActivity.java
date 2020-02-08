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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
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
    Map<Integer, Boolean> startStatus = new ConcurrentHashMap<>();

    private static DashboardPresenter mDashboardPresenter;

    private static DashboardActivity dashboardActivity;

    public static DashboardPresenter getDashboardPresenter() {
        return DashboardActivity.mDashboardPresenter;
    }

    public static DashboardActivity getDashboardActivity() {
        return dashboardActivity;
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
        DashboardActivity.mDashboardPresenter = mPresenter;

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
                    updateUI(MeasureStatus.NORMAL, 1);
                    return;
                }
                updateUI(MeasureStatus.RUNNING, 1);

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
            case STOP:
            case ERROR:
            case NORMAL:
            case DONE:
                break;
            case RUNNING:
                onError(new Exception("测量中..."));
                return;
            case BT_NOT_CONNECT:
                onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                return;
        }
        progressdialog = new ProgressDialog(DashboardActivity.this);
        progressdialog.setTitle("提示");
        progressdialog.setMessage("启动所有测量，请稍后...");
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
                countDownLatch = new CountDownLatch(pointCount);

                startStatus.clear();
                for (int i = 0; i < pointCount; i++) {
                    startStatus.put(i + 1, false);
                }

                int index = 1;
                String name = App.getInstance().getLocalDataService().queryHistory(index).get(0);
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
    public void updateUI(final MeasureStatus measureStatus, final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (measureStatus) {
                    case BT_NOT_CONNECT:
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(0.32f);
                        break;
                    case NORMAL:
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        break;
                    case RUNNING:
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(1.0f);
                        break;
                    case STOP:
                    case ERROR:
                    case DONE:
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        if (measureStatus == MeasureStatus.DONE && index > 0) {
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
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void alreadyRunning(final Map<Integer, DashboardModel.MeasureRunningStatus> measureRunningStatusMap, final String time) {
        Log.d(TAG, "alreadyRunning" + time);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
                DashboardModel.MeasureRunningStatus measureRunningStatus = null;
                switch (pointCount) {
                    case 1:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && time != null) {
                            alreadyRunning.setText(time);
                        }
                        break;
                    case 2:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && time != null) {
                            alreadyRunning.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && time != null) {
                            alreadyRunning1.setText(time);
                        }
                        break;
                    case 3:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && time != null) {
                            alreadyRunning.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && time != null) {
                            alreadyRunning1.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(3);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning2 != null && time != null) {
                            alreadyRunning2.setText(time);
                        }
                        break;
                    case 4:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && time != null) {
                            alreadyRunning.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && time != null) {
                            alreadyRunning1.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(3);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning2 != null && time != null) {
                            alreadyRunning2.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(4);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning3 != null && time != null) {
                            alreadyRunning3.setText(time);
                        }
                        break;
                    case 5:
                        measureRunningStatus = measureRunningStatusMap.get(1);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning != null && time != null) {
                            alreadyRunning.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(2);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning1 != null && time != null) {
                            alreadyRunning1.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(3);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning2 != null && time != null) {
                            alreadyRunning2.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(4);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning3 != null && time != null) {
                            alreadyRunning3.setText(time);
                        }
                        measureRunningStatus = measureRunningStatusMap.get(5);
                        if (measureRunningStatus != null && measureRunningStatus.isRunning && alreadyRunning4 != null && time != null) {
                            alreadyRunning4.setText(time);
                        }
                        break;
                    default:
                        if (alreadyRunning != null && time != null) {
                            alreadyRunning.setText(time);
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

                    // 启动下一个节点
                    int nextIndex = (index + 1);
                    if (startStatus.get(nextIndex) != null && !startStatus.get(nextIndex)) {
                        String name = App.getInstance().getLocalDataService().queryHistory(nextIndex).get(0);
                        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(nextIndex);
                        mPresenter.startMeasure(appConfig.getMeasureMode(), name, nextIndex);
                    }

                    // 释放节点
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
                    updateUI(MeasureStatus.RUNNING, 0);
                } else {
                    updateUI(MeasureStatus.NORMAL, 0);

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
        mPresenter.stopAll();
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
                data.addEntry(new Entry(set.getEntryCount(), -1, measureValue.getReportTime()), i);
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
        chart.setNoDataText("没有测量数据。请点击右上角蓝牙按钮连接设备后开始测量!");
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
