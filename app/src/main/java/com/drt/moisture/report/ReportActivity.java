package com.drt.moisture.report;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.format.bg.IBackgroundFormat;
import com.bin.david.form.data.table.TableData;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.MeasureStatus;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.util.AppPermission;
import com.drt.moisture.util.ExcelUtil;
import com.inuker.bluetooth.library.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @BindView(R.id.search)
    View search;

    boolean isBleConnected;

    List<MeasureValue> currentData;


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
                table.setData(currentData);
            }
        });
    }

    @Override
    public void onDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mesasureName.setEnabled(true);
                search.setEnabled(true);
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
    @OnClick(R.id.search)
    public void search(){
        if (!isBleConnected) {
            Toast.makeText(this, "设备尚未连接，请点击右上角蓝牙按钮连接设备", Toast.LENGTH_SHORT).show();
            return;
        }
        currentData = new ArrayList<>();
        mPresenter.queryReport(mesasureName);
        search.setEnabled(mesasureName.isEnabled());
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
