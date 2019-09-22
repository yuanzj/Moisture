package com.drt.moisture.setting;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.App;
import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.AppConfig;
import com.drt.moisture.data.CorrectParame;
import com.drt.moisture.data.DeviceInfo;
import com.drt.moisture.data.MeasureParame;
import com.inuker.bluetooth.library.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SettingActivity extends BluetoothBaseActivity<SettingPresenter> implements SettingContract.View, AdapterView.OnItemClickListener {


    @BindView(R.id.list_view)
    ListView listView;

    View deviceInfoView, dialogDateTime, dialogRate, dialogParameSet;

    boolean isBleConnected;

    @Override
    public void onDeviceInfoSuccess(final DeviceInfo deviceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (deviceInfoView != null) {
                    ((TextView) deviceInfoView.findViewById(R.id.title1)).setText(deviceInfo.getSN());
                    ((TextView) deviceInfoView.findViewById(R.id.title2)).setText(deviceInfo.getVersion());
                    ((TextView) deviceInfoView.findViewById(R.id.title3)).setText(deviceInfo.getModel());
                    ((TextView) deviceInfoView.findViewById(R.id.title4)).setText(deviceInfo.getName());
                    ((TextView) deviceInfoView.findViewById(R.id.title5)).setText(deviceInfo.getBattery());
                }
            }
        });
    }

    @Override
    public void onSetTimeSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSetParameSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        mPresenter = new SettingPresenter();
        mPresenter.attachView(this);

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("icon", R.mipmap.icons_device_information);
        item1.put("title", "设备信息");
        data.add(item1);

        item1 = new HashMap<>();
        item1.put("icon", R.mipmap.icons_clock_settings);
        item1.put("title", "时间设置");
        data.add(item1);

        item1 = new HashMap<>();
        item1.put("icon", R.mipmap.icons_recurring_appointment);
        item1.put("title", "查询频率");
        data.add(item1);

        item1 = new HashMap<>();
        item1.put("icon", R.mipmap.icons_data_configuration);
        item1.put("title", "参数设置");
        data.add(item1);

        listView.setAdapter(new SimpleAdapter(this, data,
                R.layout.adapter_setting_item, new String[]{"icon", "title"}, new int[]{R.id.icon, R.id.title}));

        listView.setOnItemClickListener(this);

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isBleConnected) {
            Toast.makeText(this, "设备尚未连接，请点击右上角蓝牙按钮连接设备", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (position) {
            case 0: {
                // 取得自定义View
                deviceInfoView = LayoutInflater.from(this).inflate(R.layout.dialog_device_info, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("设备信息")
                        .setView(deviceInfoView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setCancelable(false);
                builder.show();
                mPresenter.queryDeviceInfo();
            }
            break;
            case 1: {
                dialogDateTime = LayoutInflater.from(this).inflate(R.layout.dialog_date_time, null);
                final EditText etYear = dialogDateTime.findViewById(R.id.year);
                final EditText etMonth = dialogDateTime.findViewById(R.id.month);
                final EditText etDay = dialogDateTime.findViewById(R.id.day);
                final EditText etHour = dialogDateTime.findViewById(R.id.hour);
                final EditText etMinute = dialogDateTime.findViewById(R.id.minute);

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                etYear.setText(String.valueOf(year));
                etMonth.setText(String.valueOf(month));
                etDay.setText(String.valueOf(day));
                etHour.setText(String.valueOf(hour));
                etMinute.setText(String.valueOf(minute));

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("时间设置")
                        .setView(dialogDateTime)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int year = Integer.parseInt(etYear.getText().toString());
                                int month = Integer.parseInt(etMonth.getText().toString()) - 1;
                                int day = Integer.parseInt(etDay.getText().toString());
                                int hour = Integer.parseInt(etHour.getText().toString());
                                int minute = Integer.parseInt(etMinute.getText().toString());

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day, hour, minute);
                                long timeValue = calendar.getTime().getTime() / 1000;

                                mPresenter.setTime(timeValue);
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
            break;
            case 2: {
                dialogRate = LayoutInflater.from(this).inflate(R.layout.dialog_rate, null);
                final Spinner spinner2 = dialogRate.findViewById(R.id.spinner2);

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("查询频率")
                        .setView(dialogRate)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("yzj", "查询频率：" + Integer.parseInt(spinner2.getSelectedItem().toString()));

                                AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
                                appConfig.setPeriod(Integer.parseInt(spinner2.getSelectedItem().toString()));
                                App.getInstance().getLocalDataService().setAppConfig(appConfig);
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
            break;
            case 3: {
                dialogParameSet = LayoutInflater.from(this).inflate(R.layout.dialog_parame_set, null);
                final TabHost tabhost = dialogParameSet.findViewById(android.R.id.tabhost);
                // 必须调用该方法，才能设置tab样式
                tabhost.setup();

                // 添加标签tab1
                tabhost.addTab(tabhost
                        .newTabSpec("测量参数")
                        // 设置tab1标签图片
                        .setIndicator("测量参数")
                        .setContent(R.id.tv1));


                // 添加标签tab2
                tabhost.addTab(tabhost
                        .newTabSpec("校准参数")
                        // 设置tab1标签图片
                        .setIndicator("校准参数")
                        .setContent(R.id.tv2));
                tabhost.setCurrentTab(0);

                final EditText csA = dialogParameSet.findViewById(R.id.csA);
                final EditText csB = dialogParameSet.findViewById(R.id.csB);
                final EditText csC = dialogParameSet.findViewById(R.id.csC);
                final EditText csD = dialogParameSet.findViewById(R.id.csD);
                final EditText csE = dialogParameSet.findViewById(R.id.csE);
                final EditText csF = dialogParameSet.findViewById(R.id.csF);
                final EditText csG = dialogParameSet.findViewById(R.id.csG);
                final EditText csH = dialogParameSet.findViewById(R.id.csH);
                final EditText csI = dialogParameSet.findViewById(R.id.csI);
                final EditText csJ = dialogParameSet.findViewById(R.id.csJ);
                final EditText csK = dialogParameSet.findViewById(R.id.csK);
                final EditText csL = dialogParameSet.findViewById(R.id.csL);
                final EditText csM = dialogParameSet.findViewById(R.id.csM);
                final EditText csN = dialogParameSet.findViewById(R.id.csN);


                final EditText csA_1 = dialogParameSet.findViewById(R.id.csA_1);
                final EditText csB_1 = dialogParameSet.findViewById(R.id.csB_1);
                final EditText csC_1 = dialogParameSet.findViewById(R.id.csC_1);
                final EditText csD_1 = dialogParameSet.findViewById(R.id.csD_1);
                final EditText csE_1 = dialogParameSet.findViewById(R.id.csE_1);
                final EditText csF_1 = dialogParameSet.findViewById(R.id.csF_1);
                final EditText csG_1 = dialogParameSet.findViewById(R.id.csG_1);
                final EditText csH_1 = dialogParameSet.findViewById(R.id.csH_1);
                final EditText csI_1 = dialogParameSet.findViewById(R.id.csI_1);


                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("参数设置")
                        .setView(dialogParameSet)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (tabhost.getCurrentTab() == 0) {
                                    MeasureParame measureParame = new MeasureParame();

                                    if (csA.getText().length() > 0) {
                                        measureParame.setA(Long.parseLong(csA.getText().toString()));
                                    }
                                    if (csB.getText().length() > 0) {
                                        measureParame.setB(Long.parseLong(csB.getText().toString()));
                                    }
                                    if (csC.getText().length() > 0) {
                                        measureParame.setC(Long.parseLong(csC.getText().toString()));
                                    }
                                    if (csD.getText().length() > 0) {
                                        measureParame.setD(Long.parseLong(csD.getText().toString()));
                                    }
                                    if (csE.getText().length() > 0) {
                                        measureParame.setE(Long.parseLong(csE.getText().toString()));
                                    }
                                    if (csF.getText().length() > 0) {
                                        measureParame.setF(Long.parseLong(csF.getText().toString()));
                                    }
                                    if (csG.getText().length() > 0) {
                                        measureParame.setG(Long.parseLong(csG.getText().toString()));
                                    }
                                    if (csH.getText().length() > 0) {
                                        measureParame.setH(Long.parseLong(csH.getText().toString()));
                                    }
                                    if (csI.getText().length() > 0) {
                                        measureParame.setI(Long.parseLong(csI.getText().toString()));
                                    }
                                    if (csJ.getText().length() > 0) {
                                        measureParame.setJ(Long.parseLong(csJ.getText().toString()));
                                    }
                                    if (csK.getText().length() > 0) {
                                        measureParame.setK(Long.parseLong(csK.getText().toString()));
                                    }
                                    if (csL.getText().length() > 0) {
                                        measureParame.setL(Long.parseLong(csL.getText().toString()));
                                    }
                                    if (csM.getText().length() > 0) {
                                        measureParame.setM(Long.parseLong(csM.getText().toString()));
                                    }
                                    if (csN.getText().length() > 0) {
                                        measureParame.setN(Integer.parseInt(csN.getText().toString()));
                                    }


                                    mPresenter.setMeasureParame(measureParame);
                                } else {
                                    CorrectParame correctParame = new CorrectParame();

                                    if (csA_1.getText().length() > 0) {
                                        correctParame.setA(Long.parseLong(csA_1.getText().toString()));
                                    }
                                    if (csB_1.getText().length() > 0) {
                                        correctParame.setB(Long.parseLong(csB_1.getText().toString()));
                                    }
                                    if (csC_1.getText().length() > 0) {
                                        correctParame.setC(Long.parseLong(csC_1.getText().toString()));
                                    }
                                    if (csD_1.getText().length() > 0) {
                                        correctParame.setD(Long.parseLong(csD_1.getText().toString()));
                                    }
                                    if (csE_1.getText().length() > 0) {
                                        correctParame.setE(Long.parseLong(csE_1.getText().toString()));
                                    }
                                    if (csF_1.getText().length() > 0) {
                                        correctParame.setF(Long.parseLong(csF_1.getText().toString()));
                                    }
                                    if (csG_1.getText().length() > 0) {
                                        correctParame.setG(Long.parseLong(csG_1.getText().toString()));
                                    }
                                    if (csH_1.getText().length() > 0) {
                                        correctParame.setH(Long.parseLong(csH_1.getText().toString()));
                                    }
                                    if (csI_1.getText().length() > 0) {
                                        correctParame.setI(Long.parseLong(csI_1.getText().toString()));
                                    }

                                    mPresenter.setCorrectParame(correctParame);
                                }

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
            break;
            default:
                break;
        }
    }

    @Override
    public void setBleConnectStatus(int status) {
        if (status != Constants.STATUS_CONNECTED) {
            isBleConnected = false;
        } else {
            isBleConnected = true;
        }
    }
}
