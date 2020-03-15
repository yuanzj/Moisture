package com.drt.moisture.report;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.format.bg.IBackgroundFormat;
import com.bin.david.form.data.table.PageTableData;
import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.MainActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.measure.MeasureActivity;
import com.drt.moisture.util.AppPermission;
import com.drt.moisture.util.ExcelUtil;
import com.drt.moisture.util.StatusBarUtil;
import com.inuker.bluetooth.library.Constants;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import butterknife.BindView;
import butterknife.OnClick;


public class ReportActivity extends BluetoothBaseActivity<ReportPresenter> implements ReportContract.View {

    private static final String TAG = ReportActivity.class.getSimpleName();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BindView(R.id.export)
    ImageButton export;

    @BindView(R.id.table)
    SmartTable table;

    @BindView(R.id.progress)
    LinearLayout progress;

    @BindView(R.id.mesasureName)
    EditText mesasureName;

    @BindView(R.id.history)
    ImageButton history;

    @BindView(R.id.search)
    View search;

    @BindView(R.id.previous)
    Button previous;

    @BindView(R.id.page)
    TextView page;

    @BindView(R.id.total)
    TextView total;

    @BindView(R.id.next)
    Button next;


    @BindView(R.id.startTime)
    Button startTime;


    @BindView(R.id.endTime)
    Button endTime;

    @BindView(R.id.spinner1)
    Spinner spinner1;

    ProgressDialog progressDialog;

    boolean isBleConnected;

    List<MeasureValue> currentData;

    int pageSize;

    volatile int currentPage;

    volatile int currentSelectIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        export.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    public void initView() {
        mPresenter = new ReportPresenter();
        mPresenter.attachView(this);

        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
        table.getConfig().setMinTableWidth((int) (outSize.x * 1.5 / 2.5) - getResources().getDimensionPixelSize(R.dimen.dividing_line));

        table.getConfig().setContentBackground(new IBackgroundFormat() {

            @Override
            public void drawBackground(Canvas canvas, Rect rect, Paint paint) {

            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        Log.d("yzj", "widthPixels" + dm.widthPixels);
        Log.d("yzj", "screenHeight" + screenHeight);
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Log.d("yzj", "statusBarHeight" + statusBarHeight);
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
            Log.d("yzj", "actionBarHeight" + actionBarHeight);
        }
        int bottomBarHeight = getResources().getDimensionPixelOffset(R.dimen.btn_height_default);
        int tableViewHeight = screenHeight - statusBarHeight - actionBarHeight;

        Log.d("yzj", "tableViewHeight" + tableViewHeight);
        pageSize = tableViewHeight / getResources().getDimensionPixelOffset(R.dimen.table_cell_height_default) - 3;

        startTime.setText(sdf.format(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)));
        endTime.setText(sdf.format(new Date()));

        // 建立数据源
        int pointCount = App.getInstance().getLocalDataService().queryAppConfig().getPointCount();
        final String[] mItems = new String[pointCount];
        for (int i = 0; i < pointCount; i++) {
            mItems[i] = "测点" + (i + 1);
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                currentSelectIndex = (pos + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Override
    public void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
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

    @Override
    public void onSuccess(final List<MeasureValue> measureValues) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (table.getVisibility() != View.VISIBLE) {
                    table.setVisibility(View.VISIBLE);
                }
                currentData.addAll(measureValues);
                PageTableData<MeasureValue> pageTableData = table.setData(currentData);
                pageTableData.setPageSize(pageSize);
                pageTableData.setCurrentPage(currentPage);

                page.setText((pageTableData.getCurrentPage() + 1) + "/" + pageTableData.getTotalPage());
                total.setText("共" + currentData.size() + "条");
                if (pageTableData.getCurrentPage() == 0) {
                    previous.setAlpha(0.54f);
                } else {
                    previous.setAlpha(1.0f);
                }
                if ((pageTableData.getCurrentPage() + 1) == pageTableData.getTotalPage()) {
                    next.setAlpha(0.54f);
                } else {
                    next.setAlpha(1.0f);
                }

                if (progressDialog != null) {
                    progressDialog.setMessage("已经加载" + currentData.size() + "条数据请稍后...");
                }
            }
        });
    }

    @Override
    public void onDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                history.setEnabled(true);
                mesasureName.setEnabled(true);
                search.setEnabled(true);
                next.setEnabled(true);
                previous.setEnabled(true);
                progress.setVisibility(View.GONE);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                        .setMessage("获取结束").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                final AlertDialog alertDialog = builder.create();

                alertDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                }, 3000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.stop();
        super.onDestroy();
    }

    @Override
    public void setBleConnectStatus(int status) {
        if (status != Constants.STATUS_CONNECTED) {
            isBleConnected = false;
        } else {
            isBleConnected = true;
        }
    }

    @OnClick(R.id.export)
    public void export() {

        if (AppPermission.isGrantExternalRW(this)) {
            if (currentData != null && currentData.size() > 0) {
                String[] title = { "测点", "时间", "样品名称", "温度", "水分活度", "环境值"};

                File file = new File(ExcelUtil.getSDPath() + "/水分活度测量");
                ExcelUtil.makeDir(file);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String time = sdf.format(new Date());//Calendar.getInstance().toString();

                String fileName = file.toString() + "/" + time + ".xls";

                ExcelUtil.initExcel(fileName, title);
                ExcelUtil.writeObjListToExcel(getRecordData(currentData), fileName, this);

                Toast.makeText(this, "数据导出在" + fileName + "中", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "尚未加载完数据...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @OnClick(R.id.history)
    public void onClickHistory() {
        List<String> historyList = App.getInstance().getLocalDataService().queryHistory(currentSelectIndex);
        Collections.reverse(historyList);
        final String[] items = historyList.toArray(new String[historyList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("历史样品名称")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mesasureName.setText(items[i]);
                    }
                });
        builder.create().show();
    }

    @OnClick(R.id.search)
    public void search() {
        if (!isBleConnected) {
            Toast.makeText(this, "设备尚未连接，请点击右上角蓝牙按钮连接设备", Toast.LENGTH_SHORT).show();
            return;
        }
        table.setVisibility(View.INVISIBLE);
        currentData = new ArrayList<>();
        currentPage = 0;
        page.setText("0/0");
        total.setText("共0条");
        previous.setAlpha(0.54f);
        next.setAlpha(0.54f);

        try {
            mPresenter.queryReport(currentSelectIndex, mesasureName, sdf.parse(startTime.getText().toString()), sdf.parse(endTime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        search.setEnabled(mesasureName.isEnabled());
        history.setEnabled(mesasureName.isEnabled());
        if (!mesasureName.isEnabled()) {
            Toast.makeText(this, "开始加载数据过程中不能进行底部分页切换!", Toast.LENGTH_LONG).show();
            previous.setEnabled(false);
            next.setEnabled(false);
        }
        progressDialog = new ProgressDialog(ReportActivity.this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("查询中，请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @OnClick(R.id.previous)
    public void pre(View view) {
        if (!mesasureName.isEnabled()) {
            Toast.makeText(this, "数据加载中不能进行页面切换", Toast.LENGTH_SHORT).show();
            return;
        }

        PageTableData<MeasureValue> pageTableData = (PageTableData<MeasureValue>) table.getTableData();
        if (pageTableData.getCurrentPage() == 0) {
            return;
        }
        pageTableData.setCurrentPage(pageTableData.getCurrentPage() - 1);
        table.notifyDataChanged();
        currentPage = pageTableData.getCurrentPage();
        page.setText((pageTableData.getCurrentPage() + 1) + "/" + pageTableData.getTotalPage());
        total.setText("共" + currentData.size() + "条");
        if (pageTableData.getCurrentPage() == 0) {

            previous.setAlpha(0.54f);
        } else {
            previous.setAlpha(1.0f);
        }
        if ((pageTableData.getCurrentPage() + 1) == pageTableData.getTotalPage()) {

            next.setAlpha(0.54f);
        } else {
            next.setAlpha(1.0f);
        }
    }

    @OnClick(R.id.next)
    public void next(View view) {
        if (!mesasureName.isEnabled()) {
            Toast.makeText(this, "数据加载中不能进行页面切换", Toast.LENGTH_SHORT).show();
            return;
        }

        PageTableData<MeasureValue> pageTableData = (PageTableData<MeasureValue>) table.getTableData();
        if (pageTableData.getCurrentPage() >= (pageTableData.getTotalPage() - 1)) {
            return;
        }
        pageTableData.setCurrentPage(pageTableData.getCurrentPage() + 1);
        table.notifyDataChanged();
        currentPage = pageTableData.getCurrentPage();
        page.setText((pageTableData.getCurrentPage() + 1) + "/" + pageTableData.getTotalPage());
        total.setText("共" + currentData.size() + "条");
        if (pageTableData.getCurrentPage() == 0) {
            previous.setAlpha(0.54f);
        } else {
            previous.setAlpha(1.0f);
        }
        if ((pageTableData.getCurrentPage() + 1) == pageTableData.getTotalPage()) {
            next.setAlpha(0.54f);
        } else {
            next.setAlpha(1.0f);
        }
    }


    String startDateTime, endDateTime;

    @OnClick(R.id.startTime)
    public void onStartTime() {
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(sdf.parse(startTime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);

        final int hour = ca.get(Calendar.HOUR_OF_DAY);
        final int minute = ca.get(Calendar.MINUTE);
        final int second = ca.get(Calendar.SECOND);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        startDateTime = year + "-" + (month + 1) + "-" + dayOfMonth;

                        new TimePickerDialog(ReportActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                startDateTime += (" " + String.format("%2d", hourOfDay).replace(" ", "0") + ":" + String.format("%2d", minute).replace(" ", "0") + ":" + String.format("%2d", second).replace(" ", "0"));

                                startTime.setText(startDateTime);
                            }
                        }, hour, minute, true).show();
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @OnClick(R.id.endTime)
    public void onEndTime() {
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(sdf.parse(endTime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);

        final int hour = ca.get(Calendar.HOUR_OF_DAY);
        final int minute = ca.get(Calendar.MINUTE);
        final int second = ca.get(Calendar.SECOND);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        endDateTime = year + "-" + (month + 1) + "-" + dayOfMonth;

                        new TimePickerDialog(ReportActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                endDateTime += (" " + String.format("%2d", hourOfDay).replace(" ", "0") + ":" + String.format("%2d", minute).replace(" ", "0") + ":" + String.format("%2d", second).replace(" ", "0"));

                                endTime.setText(endDateTime);
                            }
                        }, hour, minute, true).show();
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData(List<MeasureValue> values) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            MeasureValue measureValue = values.get(i);
            ArrayList<String> beanList = new ArrayList<>();
            beanList.add(measureValue.getReportTime());
            beanList.add(String.valueOf(measureValue.getIndex()));
            beanList.add(String.valueOf(measureValue.getName()));
            beanList.add(String.valueOf(measureValue.getTemperature()));
            beanList.add(String.valueOf(measureValue.getActivity()));
            beanList.add(String.valueOf(measureValue.getHumidity()));
            recordList.add(beanList);
        }
        return recordList;
    }


}
