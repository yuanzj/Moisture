package com.drt.moisture.measure;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author yuanzhijian
 */
public class MeasureActivity extends BluetoothBaseActivity<DashboardPresenter> implements DashboardContract.View {

    private static final String TAG = MeasureActivity.class.getSimpleName();

    int[] colors = new int[]{R.color.btnOrange, R.color.btnGreen, R.color.btnBlue, R.color.btnRed1, R.color.btnRed};

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setTitleName("测点" + index + "测量");
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

        if (chart != null) {
            parentChart.removeAllViews();
        }
        chart = (LineChart) getLayoutInflater().inflate(R.layout.chart_view, parentChart, false);
        parentChart.addView(chart);
        initChartView();

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
                onError(new Exception("测量中..."));
                return;
            case BT_NOT_CONNECT:
                onError(new Exception("设备尚未连接，请点击右上角蓝牙按钮连接设备"));
                return;
            default:
                return;
        }
        progressdialog = new ProgressDialog(MeasureActivity.this);
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

    @OnClick(R.id.btnStopMeasure)
    public void stopMeasure() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否确认停止测量？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.stopMeasure(true, index);
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

    @OnClick(R.id.history)
    public void onClickHistory() {
        List<String> historyList = App.getInstance().getLocalDataService().queryHistory(index);
        Collections.reverse(historyList);
        final String[] items = historyList.toArray(new String[historyList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("历史样品名称")
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!TextUtils.isEmpty(measureName.getText())) {
            App.getInstance().getLocalDataService().setHistory(index, measureName.getText().toString());
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

    Queue<DashboardActivity.IndexEntry> queue = new LinkedList<>();

    private void addEntry(MeasureValue measureValue) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createActivitySet();
            data.addDataSet(set);
        }
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        DashboardActivity.IndexEntry indexEntry = new DashboardActivity.IndexEntry();

        if (measureValue.getActivity() > 0) {
            data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getActivity(), measureValue), 0);
            if (minY > (float) measureValue.getActivity()) {
                minY = (float) measureValue.getActivity();
            }
            if (maxY < (float) measureValue.getActivity()) {
                maxY = (float) measureValue.getActivity();
            }
        } else {
            data.addEntry(new Entry(set.getEntryCount(), -1, measureValue), 0);
        }
        indexEntry.setIndex(set.getEntryCount());
        indexEntry.setMaxValue(maxY);
        indexEntry.setMinValue(minY);

        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        if (data.getXMax() >= 30) {
            chart.getXAxis().resetAxisMaximum();
            chart.setVisibleXRangeMaximum(30);
        } else {
            chart.getXAxis().setAxisMaximum(30);
        }
        if (maxY > 0 && minY > 0) {
            Log.e("yzj", indexEntry.toString());
            queue.add(indexEntry);
        }
        while (queue.size() > 31) {
            queue.remove();
        }

        if (queue.size() > 0) {
            float currentMinY = Float.MAX_VALUE, currentMaxY = Float.MIN_VALUE;
            for (DashboardActivity.IndexEntry item : queue) {
                if (currentMinY > item.getMinValue()) {
                    currentMinY = item.getMinValue();
                }
                if (currentMaxY < item.getMaxValue()) {
                    currentMaxY = item.getMaxValue();
                }
            }

            float space = (currentMaxY - currentMinY) / 8.0F;
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

        LineDataSet d2 = new LineDataSet(null, "水分活度");
        d2.setLineWidth(2f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(getResources().getColor(colors[index - 1], getTheme()));
        d2.setColor(getResources().getColor(colors[index - 1], getTheme()));
        d2.setCircleColor(getResources().getColor(colors[index - 1], getTheme()));
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
        xAxis.setAxisMaximum(30);
        xAxis.setLabelCount(6, false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setAxisMaximum(30);
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
        chart.setDragEnabled(true);
    }

    public void playSound() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
