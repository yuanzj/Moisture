package com.drt.moisture.correct;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.correctdashboard.CorrectDashboardActivity;
import com.drt.moisture.correctdashboard.CorrectDashboardContract;
import com.drt.moisture.correctdashboard.CorrectDashboardModel;
import com.drt.moisture.correctdashboard.CorrectDashboardPresenter;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author yuanzhijian
 */
public class CorrectActivity extends BluetoothBaseActivity<CorrectDashboardPresenter> implements CorrectDashboardContract.View {

    private static final String TAG = CorrectActivity.class.getSimpleName();

    @BindView(R.id.parent_chart)
    RelativeLayout parentChart;

    LineChart chart;

    @BindView(R.id.btnStartMeasure)
    LinearLayout btnStartMeasure;

    @BindView(R.id.btnStopMeasure)
    LinearLayout btnStopMeasure;

    @BindView(R.id.spinner1)
    Spinner spMeasureModel;

    @BindView(R.id.spinner2)
    Spinner spMeasureTime;

    @BindView(R.id.mesasureName)
    TextView correctTitle;

    @BindView(R.id.alreadyRunning)
    TextView alreadyRunning;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.temperature)
    TextView temperature;

    @BindView(R.id.activeness)
    TextView activeness;

    @BindView(R.id.tip)
    View tip;

    int index;

    int pointCount;

    private PowerManager.WakeLock mWakelock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);// init powerManager
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "moisture"); // this target for tell OS which app control screen
        mWakelock.acquire(); // Wake up Screen and keep screen lighting

        index = getIntent().getIntExtra("index", 1);
        pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);


        //配置spinner控件展现样式，spinner只是承载多项数据，下面是以何种方式展现这些数据
        ArrayAdapter<String> localArrayAdapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.custom_spiner_text_item, getResources().getStringArray(R.array.correct_model));
        localArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMeasureModel.setAdapter(localArrayAdapter);

        localArrayAdapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.custom_spiner_text_item, getResources().getStringArray(R.array.correct_time_array));
        localArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMeasureTime.setAdapter(localArrayAdapter);

        if (appConfig.getCorrectMode() == 2) {
            spMeasureModel.setSelection(2);
        } else {
            if (appConfig.getCorrectType() == 1) {
                spMeasureModel.setSelection(0);
            } else {
                spMeasureModel.setSelection(1);
            }
        }

        spMeasureTime.setSelection(mPresenter.geCorrectTime(index) - 15);

        setTitleName(getString(R.string.content_point) + index + getString(R.string.content_jz_title));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_correct;
    }

    @Override
    public void initView() {
        mPresenter = CorrectDashboardActivity.getDashboardPresenter();
        if (mPresenter == null) {
            mPresenter = new CorrectDashboardPresenter();
            mPresenter.attachView(this);
        }
        mPresenter.attachView(this);

//        if (chart != null) {
//            parentChart.removeAllViews();
//        }
//        chart = (LineChart) getLayoutInflater().inflate(R.layout.chart_view, parentChart, false);
//        parentChart.addView(chart);
//        initChartView();

        spMeasureModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spMeasureModel:onItemSelected:position:" + position);
                if (position == 0) {
                    correctTitle.setText(getString(R.string.content_qxfz_lhn_1));
                } else if (position == 1) {
                    correctTitle.setText(getString(R.string.content_qxfz_lhm));
                } else {
                    correctTitle.setText(getString(R.string.content_qxfz_lhn));
                }

                int model;
                int type;
                if (position == 2) {
                    tip.setVisibility(View.VISIBLE);
//                            双点校正
                    model = 0x02;
                    type = 0x01;
                } else if (position == 1) {
                    tip.setVisibility(View.INVISIBLE);

//                            氯化镁校正
                    model = 0x01;
                    type = 0x02;
                } else {
                    tip.setVisibility(View.INVISIBLE);

//                            氯化钠校正
                    model = 0x01;
                    type = 0x01;
                }
                mPresenter.setCorrectMode(model, index);
                mPresenter.setCorrectType(type, index);
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
                mPresenter.setCorrectTime(position + 15, index);
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

    }

    @Override
    public void onSuccess(final List<MeasureValue> measureValueList) {
        if (CorrectDashboardActivity.getCorrectDashboardActivity() != null) {
            CorrectDashboardActivity.getCorrectDashboardActivity().onSuccess(measureValueList);
        }
        if (index == 0) {
            return;
        }
        final MeasureValue measureValue = measureValueList.get(index - 1);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                addEntry(measureValue);
                time.setText(measureValue.getReportTime());
                temperature.setText(String.format("%.2f", measureValue.getTemperature()) + "°C");
                DecimalFormat df = new DecimalFormat("0.0000");
                df.setRoundingMode(RoundingMode.DOWN);
                activeness.setText(df.format(measureValue.getActivity()));
            }
        });
    }

    @Override
    public void onDone() {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.content_affirm_title))
                .setMessage(correctTitle.getText()).setPositiveButton(getString(R.string.content_affirm_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

//                        校准方式<br/>0x01：单点校准<br/>0x02：两点校准
//                        校准类型<br/>0x01：氯化钠校准<br/>0x02：氯化镁校准

//        <item>氯化钠校正</item>
//        <item>氯化镁校正</item>
//        <item>双点校正</item>

                        int model;
                        int type;
                        if (spMeasureModel.getSelectedItemPosition() == 2) {
//                            双点校正
                            model = 0x02;
                            type = 0x01;
                        } else if (spMeasureModel.getSelectedItemPosition() == 1) {
//                            氯化镁校正
                            model = 0x01;
                            type = 0x02;
                        } else {
//                            氯化钠校正
                            model = 0x01;
                            type = 0x01;
                        }

//                        if (chart != null) {
//                            parentChart.removeAllViews();
//                        }
//                        chart = (LineChart) getLayoutInflater().inflate(R.layout.chart_view, parentChart, false);
//                        parentChart.addView(chart);
//                        initChartView();

                        AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig(index);
                        appConfig.setCorrectMode(model);
                        appConfig.setCorrectType(type);
                        App.getInstance().getLocalDataService().setAppConfig(index, appConfig);

                        mPresenter.startCorrect(model, type, pointCount);
                    }
                }).setNegativeButton(getString(R.string.content_affirm_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog  alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @OnClick(R.id.btnStopMeasure)
    public void stopMeasure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.content_affirm_title))
                .setMessage(getString(R.string.content_stop_correct1))
                .setPositiveButton(getString(R.string.content_affirm_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.stopCorrect(false);
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
        AlertDialog  alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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
                        spMeasureModel.setEnabled(false);
                        spMeasureTime.setEnabled(false);
                        break;
                    case NORMAL:
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        spMeasureModel.setEnabled(true);
                        spMeasureTime.setEnabled(true);
                        break;
                    case RUNNING:
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(1.0f);
                        spMeasureModel.setEnabled(false);
                        spMeasureTime.setEnabled(false);
                        break;
                    case STOP:
                    case ERROR:
                    case DONE:
                        if (measureStatus == MeasureStatus.DONE) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CorrectActivity.this);
                            builder.setTitle(getString(R.string.content_affirm_title));//设置title
                            builder.setMessage(getString(R.string.content_correct_done));//设置内容
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
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        spMeasureModel.setEnabled(true);
                        spMeasureTime.setEnabled(true);
                        break;
                    case STEP_ONE_DONE:
                        btnStartMeasure.setAlpha(1.0f);
                        btnStopMeasure.setAlpha(0.32f);
                        spMeasureModel.setEnabled(true);
                        spMeasureTime.setEnabled(true);
                        break;
                    default:
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
                correctTitle.setText(message);
                AlertDialog.Builder builder = new AlertDialog.Builder(CorrectActivity.this)
                        .setTitle(getString(R.string.content_affirm_title))
                        .setMessage(message).setPositiveButton(getString(R.string.content_affirm_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                mPresenter.startCorrect(0x02, 0x02, pointCount);

                            }
                        }).setNegativeButton(getString(R.string.content_affirm_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog  alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    @Override
    public void updateRunningStatus(final Map<Integer, CorrectDashboardModel.MeasureRunningStatus> measureRunningStatusMap) {
        if (CorrectDashboardActivity.getCorrectDashboardActivity() != null) {
            CorrectDashboardActivity.getCorrectDashboardActivity().updateRunningStatus(measureRunningStatusMap);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CorrectDashboardModel.MeasureRunningStatus measureRunningStatus = measureRunningStatusMap.get(index);
                if (measureRunningStatus != null && measureRunningStatus.isRunning() && alreadyRunning != null && measureRunningStatus.getRunningTime() != null) {
                    alreadyRunning.setText(measureRunningStatus.getRunningTime());
                }
            }
        });
    }

    @Override
    public void onStartMeasureSuccess() {
        int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
        mPresenter.queryCorrectResult(pointCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(mPresenter.getMeasureStatus(index));
    }

    @Override
    protected void onDestroy() {
//        mPresenter.stopCorrect(false);
        mPresenter.onDestroy();
        super.onDestroy();
        mWakelock.release(); // release control.stop to keep screen lighting
    }

    @Override
    public void setBleConnectStatus(int status) {
        if (status != Constants.STATUS_CONNECTED) {
            mPresenter.setMeasureStatus(MeasureStatus.BT_NOT_CONNECT);
        } else {
            mPresenter.setMeasureStatus(MeasureStatus.NORMAL);
        }
    }

    float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;

    private void addEntry(MeasureValue measureValue) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet
//        set = data.getDataSetByIndex(0);
//        if (set == null) {
//            set = createTemperatureSet();
//            data.addDataSet(set);
//        }
//        data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getTemperature()), 0);
//        data.notifyDataChanged();

                set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createActivitySet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getActivity(), measureValue), 0);
        if ((float) measureValue.getActivity() != 0.0F) {
            if (minY > (float) measureValue.getActivity()) {
                minY = (float) measureValue.getActivity();
            }
            if (maxY < (float) measureValue.getActivity()) {
                maxY = (float) measureValue.getActivity();
            }
        }
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        if (data.getXMax() >= 30) {
            chart.getXAxis().resetAxisMaximum();
            chart.setVisibleXRangeMaximum(30);
        } else {
            chart.getXAxis().setAxisMaximum(30);
        }
        if (minY != maxY && maxY > minY) {
            Log.e("yzj", "minY:" + minY + ",maxY:" + maxY);
            chart.getAxisLeft().setAxisMinimum(minY); // this replaces setStartAtZero(true)
            chart.getAxisLeft().setAxisMaximum(maxY);
        }
        chart.moveViewToX(data.getXMax());
    }

//    private LineDataSet createTemperatureSet() {
//
//        LineDataSet d1 = new LineDataSet(null, "温度");
//        d1.setLineWidth(2.5f);
//        d1.setCircleRadius(4.5f);
//        d1.setHighLightColor(getResources().getColor(R.color.colorAccent, getTheme()));
//        d1.setColor(getResources().getColor(R.color.colorAccent, getTheme()));
//        d1.setCircleColor(getResources().getColor(R.color.colorAccent, getTheme()));
//        d1.setDrawValues(true);
//        return d1;
//    }

    private LineDataSet createActivitySet() {

        LineDataSet d2 = new LineDataSet(null, getString(R.string.content_sfhd));
        d2.setLineWidth(2f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setCircleColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setDrawValues(false);
        d2.setDrawCircles(false);
        d2.setMode(LineDataSet.Mode.LINEAR);
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
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
//        chart.setMinimumHeight((outSize.x - getResources().getDimensionPixelSize(R.dimen.padding_default) * 2) / 2);

        // apply styling
        // holder.chart.setValueTypeface(mTf);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setNoDataText("没有校正数据。请点击右上角蓝牙按钮连接设备后开始校正!");
        chart.setNoDataTextColor(getColor(R.color.colorSecondBody));

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(30);
        xAxis.setLabelCount(6, false);
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
                    return ((MeasureValue) entry.getData()).getReportTime();
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
    }

    public void playSound() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
