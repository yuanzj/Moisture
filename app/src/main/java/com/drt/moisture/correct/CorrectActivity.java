package com.drt.moisture.correct;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author yuanzhijian
 */
public class CorrectActivity extends BluetoothBaseActivity<CorrectPresenter> implements CorrectContract.View {

    private static final String TAG = CorrectActivity.class.getSimpleName();

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
    TextView correctTitle;

    @BindView(R.id.alreadyRunning)
    TextView alreadyRunning;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.temperature)
    TextView temperature;

    @BindView(R.id.activeness)
    TextView activeness;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spMeasureTime.setSelection(mPresenter.getMeasureTime() - 1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_correct;
    }

    @Override
    public void initView() {
        mPresenter = new CorrectPresenter();
        mPresenter.attachView(this);

        initChartView();

        spMeasureModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spMeasureModel:onItemSelected:position:" + position);
                if (position == 0) {
                    correctTitle.setText("请放置氯化钠饱和液");
                } else if (position == 1) {
                    correctTitle.setText("请放置氯化镁饱和液");
                } else {
                    correctTitle.setText("请先放置氯化钠饱和液");
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
                mPresenter.setMeasureTime(position + 1);
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
    public void onSuccess(final MeasureValue measureValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addEntry(measureValue);
                time.setText(measureValue.getReportTime());
                temperature.setText(measureValue.getTemperature() + "°C");
                activeness.setText(measureValue.getActivity() + "%");
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
        mPresenter.startCorrect(spMeasureModel.getSelectedItemPosition());
    }

    @OnClick(R.id.btnStopMeasure)
    public void stopMeasure() {
        mPresenter.stopCorrect();
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
                            Toast.makeText(getApplicationContext(), "校正完成", Toast.LENGTH_LONG).show();
                        }
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
            }
        });
    }

    @Override
    public void alreadyRunning(final String time) {
        Log.d(TAG, "alreadyRunning" + time);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alreadyRunning.setText(time);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stopCorrect();
    }

    private void addEntry(MeasureValue measureValue) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createTemperatureSet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getTemperature()), 0);
        data.notifyDataChanged();

        set = data.getDataSetByIndex(1);
        if (set == null) {
            set = createActivitySet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(set.getEntryCount(), (float) measureValue.getActivity()), 1);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(6);
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createTemperatureSet() {

        LineDataSet d1 = new LineDataSet(null, "温度");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(getResources().getColor(R.color.colorAccent, getTheme()));
        d1.setColor(getResources().getColor(R.color.colorAccent, getTheme()));
        d1.setCircleColor(getResources().getColor(R.color.colorAccent, getTheme()));
        d1.setDrawValues(true);
        return d1;
    }

    private LineDataSet createActivitySet() {

        LineDataSet d2 = new LineDataSet(null, "水分活度");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setCircleColor(getResources().getColor(R.color.colorGreen, getTheme()));
        d2.setDrawValues(true);
        return d2;
    }

    private void initChartView() {
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
        chart.setMinimumHeight((outSize.x - getResources().getDimensionPixelSize(R.dimen.padding_default) * 2) / 2);

        // apply styling
        // holder.chart.setValueTypeface(mTf);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setNoDataText("没有校正数据。请点击右上角蓝牙按钮连接设备后开始校正!");

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

    }
}