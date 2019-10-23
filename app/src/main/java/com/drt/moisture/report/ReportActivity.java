package com.drt.moisture.report;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.bin.david.form.data.table.TableData;
import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.util.AppPermission;
import com.drt.moisture.util.ExcelUtil;
import com.drt.moisture.util.StatusBarUtil;
import com.inuker.bluetooth.library.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class ReportActivity extends BluetoothBaseActivity<ReportPresenter> implements ReportContract.View {

    private static final String TAG = ReportActivity.class.getSimpleName();

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

    boolean isBleConnected;

    List<MeasureValue> currentData;

    int pageSize;

    volatile int currentPage;

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
        table.getConfig().setMinTableWidth(outSize.x);

        table.getConfig().setContentBackground(new IBackgroundFormat() {

            @Override
            public void drawBackground(Canvas canvas, Rect rect, Paint paint) {

            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
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
        int tableViewHeight = screenHeight - statusBarHeight - actionBarHeight - bottomBarHeight;

        Log.d("yzj", "tableViewHeight" + tableViewHeight);
        pageSize = tableViewHeight / getResources().getDimensionPixelOffset(R.dimen.table_cell_height_default) - 5;
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
                currentData.addAll(measureValues);
                PageTableData<MeasureValue> pageTableData =  table.setData(currentData);
                pageTableData.setPageSize(pageSize);
                pageTableData.setCurrentPage(currentPage);

                page.setText((pageTableData.getCurrentPage() + 1) + "/" + pageTableData.getTotalPage());
                total.setText("共" + currentData.size()  + "条");
                if (pageTableData.getCurrentPage() == 0) {
                    previous.setEnabled(false);
                    previous.setAlpha(0.54f);
                } else {
                    previous.setEnabled(true);
                    previous.setAlpha(1.0f);
                }
                if ((pageTableData.getCurrentPage() + 1) == pageTableData.getTotalPage()){
                    next.setEnabled(false);
                    next.setAlpha(0.54f);
                } else {
                    next.setEnabled(true);
                    next.setAlpha(1.0f);
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
                progress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"获取结束", Toast.LENGTH_LONG).show();
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
                String[] title = {"时间", "样品名称", "温度", "水分活度"};

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
        List<String> historyList = App.getInstance().getLocalDataService().queryHistory();
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
    public void search(){
        if (!isBleConnected) {
            Toast.makeText(this, "设备尚未连接，请点击右上角蓝牙按钮连接设备", Toast.LENGTH_SHORT).show();
            return;
        }
        currentData = new ArrayList<>();
        mPresenter.queryReport(mesasureName);
        search.setEnabled(mesasureName.isEnabled());
        history.setEnabled(mesasureName.isEnabled());
    }

    @OnClick(R.id.previous)
    public void pre(View view) {
        if(!mesasureName.isEnabled()) {
            Toast.makeText(this, "数据加载中不能进行页面切换", Toast.LENGTH_SHORT).show();
            return;
        }

        PageTableData<MeasureValue> pageTableData =  (PageTableData<MeasureValue>)table.getTableData();
        pageTableData.setPageSize(pageSize);
        if (pageTableData.getCurrentPage() == 0) {
            return;
        }
        pageTableData.setCurrentPage(pageTableData.getCurrentPage() - 1);
        currentPage = pageTableData.getCurrentPage();
        page.setText((pageTableData.getCurrentPage() + 1) + "/" + pageTableData.getTotalPage());
        total.setText("共" + currentData.size()  + "条");
        if (pageTableData.getCurrentPage() == 0) {
            previous.setEnabled(false);
            previous.setAlpha(0.54f);
        } else {
            previous.setEnabled(true);
            previous.setAlpha(1.0f);
        }
        if ((pageTableData.getCurrentPage() + 1) == pageTableData.getTotalPage()){
            next.setEnabled(false);
            next.setAlpha(0.54f);
        } else {
            next.setEnabled(true);
            next.setAlpha(1.0f);
        }
    }

    @OnClick(R.id.next)
    public void next(View view) {
        if(!mesasureName.isEnabled()) {
            Toast.makeText(this, "数据加载中不能进行页面切换", Toast.LENGTH_SHORT).show();
            return;
        }
        PageTableData<MeasureValue> pageTableData =  (PageTableData<MeasureValue>)table.getTableData();
        pageTableData.setPageSize(pageSize);
        if (currentPage >= (pageTableData.getTotalPage()-1)){
            return;
        }
        pageTableData.setCurrentPage(pageTableData.getCurrentPage() + 1);
        currentPage = pageTableData.getCurrentPage();
        page.setText((pageTableData.getCurrentPage() + 1) + "/" + pageTableData.getTotalPage());
        total.setText("共" + currentData.size()  + "条");
        if (pageTableData.getCurrentPage() == 0) {
            previous.setEnabled(false);
            previous.setAlpha(0.54f);
        } else {
            previous.setEnabled(true);
            previous.setAlpha(1.0f);
        }
        if ((pageTableData.getCurrentPage() + 1) == pageTableData.getTotalPage()){
            next.setEnabled(false);
            next.setAlpha(0.54f);
        } else {
            next.setEnabled(true);
            next.setAlpha(1.0f);
        }
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
            beanList.add(String.valueOf(measureValue.getName()));
            beanList.add(String.valueOf(measureValue.getTemperature()));
            beanList.add(String.valueOf(measureValue.getActivity()));
            recordList.add(beanList);
        }
        return recordList;
    }


}
