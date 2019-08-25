package com.drt.moisture.report;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.format.bg.IBackgroundFormat;
import com.bin.david.form.data.table.TableData;
import com.drt.moisture.CustomActionBarActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.util.AppPermission;
import com.drt.moisture.util.ExcelUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class ReportActivity extends CustomActionBarActivity<ReportPresenter> implements ReportContract.View {

    private static final String TAG = ReportActivity.class.getSimpleName();

    @BindView(R.id.title_rightImage)
    ImageButton btnBluetooth;

    @BindView(R.id.export)
    ImageButton export;

    @BindView(R.id.table)
    SmartTable table;

    @BindView(R.id.progress)
    LinearLayout progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBack();
        btnBluetooth.setVisibility(View.VISIBLE);
        btnBluetooth.setImageResource(R.mipmap.ic_bluetooth);

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
        mPresenter.queryReport();

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
                table.setData(measureValues);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.export)
    public void export() {

        if (AppPermission.isGrantExternalRW(this)) {
            TableData<MeasureValue> tableData = table.getTableData();
            if (tableData != null && tableData.getT() != null) {
                String[] title = {"时间", "样品名称", "温度", "水分活度", "环境值"};

                File file = new File(ExcelUtil.getSDPath() + "/水分活度测量");
                ExcelUtil.makeDir(file);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String time = sdf.format(new Date());//Calendar.getInstance().toString();

                String fileName = file.toString() + "/" + time + ".xls";

                ExcelUtil.initExcel(fileName, title);
                ExcelUtil.writeObjListToExcel(getRecordData(tableData.getT()), fileName, this);

                Toast.makeText(this, "数据导出在" + fileName + "中", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "尚未加载完数据...", Toast.LENGTH_SHORT).show();
            }
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
            beanList.add(String.valueOf(measureValue.getHumidity()));
            recordList.add(beanList);
        }
        return recordList;
    }


}
