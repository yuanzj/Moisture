package com.drt.moisture.measure;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.dashboard.DashboardActivity;
import com.drt.moisture.dashboard.DashboardContract;
import com.drt.moisture.dashboard.DashboardPresenter;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author yuanzhijian
 */
public class MeasureActivity extends BluetoothBaseActivity<DashboardPresenter> implements DashboardContract.View {

    private static final String TAG = MeasureActivity.class.getSimpleName();

    @BindView(R.id.chart)
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getIntent().getIntExtra("index", 1);
        spMeasureTime.setSelection(mPresenter.getMeasureTime() - 5);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_measure;
    }

    @Override
    public void initView() {
        mPresenter = DashboardActivity.getDashboardPresenter();
        mPresenter.attachView(this);

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
                mPresenter.setMeasureTime(position + 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "spMeasureTime:onNothingSelected:view:" + parent);
            }
        });

        measureName.setText(App.getInstance().getLocalDataService().queryHistory().get(0));
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

        final MeasureValue measureValue = measureValueList.get(index - 1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addEntry(measureValue);
                time.setText(measureValue.getReportTime());
                temperature.setText(String.format("%.2f", measureValue.getTemperature()) + "°C");
                DecimalFormat df = new DecimalFormat("0.0000");
                df.setRoundingMode(RoundingMode.DOWN);
                activeness.setText(df.format(measureValue.getActivity()));
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
        chart.clear();

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressdialog = new ProgressDialog(MeasureActivity.this);
                progressdialog.setTitle("提示");
                progressdialog.setMessage("启动所有测量，请稍后...");
                progressdialog.setCancelable(false);
                progressdialog.show();
            }
        });
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
                    case NORMAL:
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
                    case RUNNING:
                        measureName.setEnabled(false);
                        history.setEnabled(false);
                        history.setAlpha(0.32f);
                        btnStartMeasure.setAlpha(0.32f);
                        btnStopMeasure.setAlpha(1.0f);
                        spMeasureModel.setEnabled(false);
                        spMeasureTime.setEnabled(false);
                        break;
                    case STOP:
                    case ERROR:
                    case DONE:
                        if (measureStatus == MeasureStatus.DONE) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MeasureActivity.this);
                            builder.setTitle("提示");//设置title
                            builder.setMessage("测量完成");//设置内容
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
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void alreadyRunning(final String time) {
        Log.d(TAG, "alreadyRunning" + time);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (alreadyRunning != null && time != null) {
                    alreadyRunning.setText(time);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
//        mPresenter.stopMeasure(false, index);
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
//        data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getTemperature(), measureValue.getReportTime()), 0);
//        data.notifyDataChanged();

                set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createActivitySet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getActivity(), measureValue.getReportTime()), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(30);
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        chart.moveViewToX((float) (data.getEntryCount() - 1));
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
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setCircleColor(getResources().getColor(R.color.colorGreen, getTheme()));
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
