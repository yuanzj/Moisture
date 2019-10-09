package com.drt.moisture.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.drt.moisture.data.HumidityParame;
import com.drt.moisture.data.MeasureParame;
import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;
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
    public void onMeasureConfigSuccess(final SetMeasureParameRequest setMeasureParameRequest) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogParameSet != null) {
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

                    csA.setText("" + setMeasureParameRequest.getA() / 1000000.0);
                    csB.setText("" + setMeasureParameRequest.getB() / 1000000.0);
                    csC.setText("" + setMeasureParameRequest.getC() / 1000000.0);
                    csD.setText("" + setMeasureParameRequest.getD() / 1000000.0);
                    csE.setText("" + setMeasureParameRequest.getE() / 1000000.0);
                    csF.setText("" + setMeasureParameRequest.getF() / 1000000.0);
                    csG.setText("" + setMeasureParameRequest.getG() / 1000000.0);
                    csH.setText("" + setMeasureParameRequest.getH() / 1000000.0);
                    csI.setText("" + setMeasureParameRequest.getI() / 1000000.0);
                    csJ.setText("" + setMeasureParameRequest.getJ() / 1000000.0);
                    csK.setText("" + setMeasureParameRequest.getK() / 1000000.0);
                    csL.setText("" + setMeasureParameRequest.getL() / 1000000.0);
                    csM.setText("" + setMeasureParameRequest.getM() / 1000000.0);
                    csN.setText("" + setMeasureParameRequest.getN());
                }
            }
        });
    }

    @Override
    public void onCorrectConfigSuccess(final SetCorrectParameRequest setCorrectParameRequest) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogParameSet != null) {
                    final EditText csA_1 = dialogParameSet.findViewById(R.id.csA_1);
                    final EditText csB_1 = dialogParameSet.findViewById(R.id.csB_1);
                    final EditText csC_1 = dialogParameSet.findViewById(R.id.csC_1);
                    final EditText csD_1 = dialogParameSet.findViewById(R.id.csD_1);
                    final EditText csE_1 = dialogParameSet.findViewById(R.id.csE_1);
                    final EditText csF_1 = dialogParameSet.findViewById(R.id.csF_1);
                    final EditText csG_1 = dialogParameSet.findViewById(R.id.csG_1);
                    final EditText csH_1 = dialogParameSet.findViewById(R.id.csH_1);
                    final EditText csI_1 = dialogParameSet.findViewById(R.id.csI_1);

                    csA_1.setText("" + setCorrectParameRequest.getA() / 1000000.0);
                    csB_1.setText("" + setCorrectParameRequest.getB() / 1000000.0);
                    csC_1.setText("" + setCorrectParameRequest.getC() / 1000000.0);
                    csD_1.setText("" + setCorrectParameRequest.getD() / 1000000.0);
                    csE_1.setText("" + setCorrectParameRequest.getE() / 1000000.0);
                    csF_1.setText("" + setCorrectParameRequest.getF() / 1000000.0);
                    csG_1.setText("" + setCorrectParameRequest.getG() / 1000000.0);
                    csH_1.setText("" + setCorrectParameRequest.getH() / 1000000.0);
                    csI_1.setText("" + setCorrectParameRequest.getI() / 1000000.0);
                }
            }
        });
    }

    @Override
    public void onHumidityConfigSuccess(final SetHumidityParameRequest setMeasureParameRequest) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogParameSet != null) {
                    final EditText csA = dialogParameSet.findViewById(R.id.csA_2);
                    final EditText csB = dialogParameSet.findViewById(R.id.csB_2);
                    final EditText csC = dialogParameSet.findViewById(R.id.csC_2);
                    final EditText csD = dialogParameSet.findViewById(R.id.csD_2);
                    final EditText csE = dialogParameSet.findViewById(R.id.csE_2);
                    final EditText csF = dialogParameSet.findViewById(R.id.csF_2);
                    final EditText csG = dialogParameSet.findViewById(R.id.csG_2);
                    final EditText csH = dialogParameSet.findViewById(R.id.csH_2);
                    final EditText csI = dialogParameSet.findViewById(R.id.csI_2);
                    final EditText csJ = dialogParameSet.findViewById(R.id.csJ_2);
                    final EditText csK = dialogParameSet.findViewById(R.id.csK_2);
                    final EditText csL = dialogParameSet.findViewById(R.id.csL_2);
                    final EditText csM = dialogParameSet.findViewById(R.id.csM_2);
                    final EditText csN = dialogParameSet.findViewById(R.id.csN_2);
                    final EditText csO = dialogParameSet.findViewById(R.id.csO_2);

                    csA.setText("" + setMeasureParameRequest.getA() / 1000000.0);
                    csB.setText("" + setMeasureParameRequest.getB() / 1000000.0);
                    csC.setText("" + setMeasureParameRequest.getC() / 1000000.0);
                    csD.setText("" + setMeasureParameRequest.getD() / 1000000.0);
                    csE.setText("" + setMeasureParameRequest.getE() / 1000000.0);
                    csF.setText("" + setMeasureParameRequest.getF() / 1000000.0);
                    csG.setText("" + setMeasureParameRequest.getG() / 1000000.0);
                    csH.setText("" + setMeasureParameRequest.getH() / 1000000.0);
                    csI.setText("" + setMeasureParameRequest.getI() / 1000000.0);
                    csJ.setText("" + setMeasureParameRequest.getJ() / 1000000.0);
                    csK.setText("" + setMeasureParameRequest.getK() / 1000000.0);
                    csL.setText("" + setMeasureParameRequest.getL() / 1000000.0);
                    csM.setText("" + setMeasureParameRequest.getM() / 1000000.0);
                    csN.setText("" + setMeasureParameRequest.getN() / 1000000.0);
                    csO.setText("" + setMeasureParameRequest.getO() / 1000000.0);

                }
            }
        });
    }

    @Override
    public void onRateSuccess(final SetRateRequest deviceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogRate != null) {
                    final Spinner spinner2 = dialogRate.findViewById(R.id.spinner2);
                    if (deviceInfo.getRate() == 1500) {
                        spinner2.setSelection(0, false);
                    } else if (deviceInfo.getRate() == 3000) {
                        spinner2.setSelection(1, false);
                    } else if (deviceInfo.getRate() == 5000) {
                        spinner2.setSelection(2, false);
                    } else {
                        spinner2.setSelection(0, false);
                    }
                    AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
                    appConfig.setPeriod(Integer.parseInt(spinner2.getSelectedItem().toString()));
                    App.getInstance().getLocalDataService().setAppConfig(appConfig);
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
                                appConfig.setPeriod((int) (Double.parseDouble(spinner2.getSelectedItem().toString()) * 1000));
                                App.getInstance().getLocalDataService().setAppConfig(appConfig);
                                mPresenter.setRateParame(appConfig.getPeriod(), appConfig.getRatio());

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
                mPresenter.queryRate();
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

                // 添加标签tab3
                tabhost.addTab(tabhost
                        .newTabSpec("湿度参数")
                        // 设置tab1标签图片
                        .setIndicator("湿度参数")
                        .setContent(R.id.tv3));

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

                final EditText csA_2 = dialogParameSet.findViewById(R.id.csA_2);
                final EditText csB_2 = dialogParameSet.findViewById(R.id.csB_2);
                final EditText csC_2 = dialogParameSet.findViewById(R.id.csC_2);
                final EditText csD_2 = dialogParameSet.findViewById(R.id.csD_2);
                final EditText csE_2 = dialogParameSet.findViewById(R.id.csE_2);
                final EditText csF_2 = dialogParameSet.findViewById(R.id.csF_2);
                final EditText csG_2 = dialogParameSet.findViewById(R.id.csG_2);
                final EditText csH_2 = dialogParameSet.findViewById(R.id.csH_2);
                final EditText csI_2 = dialogParameSet.findViewById(R.id.csI_2);
                final EditText csJ_2 = dialogParameSet.findViewById(R.id.csJ_2);
                final EditText csK_2 = dialogParameSet.findViewById(R.id.csK_2);
                final EditText csL_2 = dialogParameSet.findViewById(R.id.csL_2);
                final EditText csM_2 = dialogParameSet.findViewById(R.id.csM_2);
                final EditText csN_2 = dialogParameSet.findViewById(R.id.csN_2);
                final EditText csO_2 = dialogParameSet.findViewById(R.id.csO_2);



                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("参数设置")
                        .setView(dialogParameSet)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (tabhost.getCurrentTab() == 0) {
                                    MeasureParame measureParame = new MeasureParame();

                                    if (csA.getText().length() > 0) {
                                        measureParame.setA((int) (Double.parseDouble(csA.getText().toString()) * 1000000));
                                    }
                                    if (csB.getText().length() > 0) {
                                        measureParame.setB((int) (Double.parseDouble(csB.getText().toString()) * 1000000));
                                    }
                                    if (csC.getText().length() > 0) {
                                        measureParame.setC((int) (Double.parseDouble(csC.getText().toString()) * 1000000));
                                    }
                                    if (csD.getText().length() > 0) {
                                        measureParame.setD((int) (Double.parseDouble(csD.getText().toString()) * 1000000));
                                    }
                                    if (csE.getText().length() > 0) {
                                        measureParame.setE((int) (Double.parseDouble(csE.getText().toString()) * 1000000));
                                    }
                                    if (csF.getText().length() > 0) {
                                        measureParame.setF((int) (Double.parseDouble(csF.getText().toString()) * 1000000));
                                    }
                                    if (csG.getText().length() > 0) {
                                        measureParame.setG((int) (Double.parseDouble(csG.getText().toString()) * 1000000));
                                    }
                                    if (csH.getText().length() > 0) {
                                        measureParame.setH((int) (Double.parseDouble(csH.getText().toString()) * 1000000));
                                    }
                                    if (csI.getText().length() > 0) {
                                        measureParame.setI((int) (Double.parseDouble(csI.getText().toString()) * 1000000));
                                    }
                                    if (csJ.getText().length() > 0) {
                                        measureParame.setJ((int) (Double.parseDouble(csJ.getText().toString()) * 1000000));
                                    }
                                    if (csK.getText().length() > 0) {
                                        measureParame.setK((int) (Double.parseDouble(csK.getText().toString()) * 1000000));
                                    }
                                    if (csL.getText().length() > 0) {
                                        measureParame.setL((int) (Double.parseDouble(csL.getText().toString()) * 1000000));
                                    }
                                    if (csM.getText().length() > 0) {
                                        measureParame.setM((int) (Double.parseDouble(csM.getText().toString()) * 1000000));
                                    }
                                    if (csN.getText().length() > 0) {
                                        measureParame.setN((int) (Double.parseDouble(csN.getText().toString())));
                                    }

                                    mPresenter.setMeasureParame(measureParame);
                                } else if (tabhost.getCurrentTab() == 1) {
                                    CorrectParame correctParame = new CorrectParame();

                                    if (csA_1.getText().length() > 0) {
                                        correctParame.setA((int) (Double.parseDouble(csA_1.getText().toString()) * 1000000));
                                    }
                                    if (csB_1.getText().length() > 0) {
                                        correctParame.setB((int) (Double.parseDouble(csB_1.getText().toString()) * 1000000));
                                    }
                                    if (csC_1.getText().length() > 0) {
                                        correctParame.setC((int) (Double.parseDouble(csC_1.getText().toString()) * 1000000));
                                    }
                                    if (csD_1.getText().length() > 0) {
                                        correctParame.setD((int) (Double.parseDouble(csD_1.getText().toString()) * 1000000));
                                    }
                                    if (csE_1.getText().length() > 0) {
                                        correctParame.setE((int) (Double.parseDouble(csE_1.getText().toString()) * 1000000));
                                    }
                                    if (csF_1.getText().length() > 0) {
                                        correctParame.setF((int) (Double.parseDouble(csF_1.getText().toString()) * 1000000));
                                    }
                                    if (csG_1.getText().length() > 0) {
                                        correctParame.setG((int) (Double.parseDouble(csG_1.getText().toString()) * 1000000));
                                    }
                                    if (csH_1.getText().length() > 0) {
                                        correctParame.setH((int) (Double.parseDouble(csH_1.getText().toString()) * 1000000));
                                    }
                                    if (csI_1.getText().length() > 0) {
                                        correctParame.setI((int) (Double.parseDouble(csI_1.getText().toString()) * 1000000));
                                    }

                                    mPresenter.setCorrectParame(correctParame);
                                } else {
                                    HumidityParame humidityParame = new HumidityParame();


                                    if (csA_2.getText().length() > 0) {
                                        humidityParame.setA((int) (Double.parseDouble(csA_2.getText().toString()) * 1000000));
                                    }
                                    if (csB_2.getText().length() > 0) {
                                        humidityParame.setB((int) (Double.parseDouble(csB_2.getText().toString()) * 1000000));
                                    }
                                    if (csC_2.getText().length() > 0) {
                                        humidityParame.setC((int) (Double.parseDouble(csC_2.getText().toString()) * 1000000));
                                    }
                                    if (csD_2.getText().length() > 0) {
                                        humidityParame.setD((int) (Double.parseDouble(csD_2.getText().toString()) * 1000000));
                                    }
                                    if (csE_2.getText().length() > 0) {
                                        humidityParame.setE((int) (Double.parseDouble(csE_2.getText().toString()) * 1000000));
                                    }
                                    if (csF_2.getText().length() > 0) {
                                        humidityParame.setF((int) (Double.parseDouble(csF_2.getText().toString()) * 1000000));
                                    }
                                    if (csG_2.getText().length() > 0) {
                                        humidityParame.setG((int) (Double.parseDouble(csG_2.getText().toString()) * 1000000));
                                    }
                                    if (csH_2.getText().length() > 0) {
                                        humidityParame.setH((int) (Double.parseDouble(csH_2.getText().toString()) * 1000000));
                                    }
                                    if (csI_2.getText().length() > 0) {
                                        humidityParame.setI((int) (Double.parseDouble(csI_2.getText().toString()) * 1000000));
                                    }
                                    if (csJ_2.getText().length() > 0) {
                                        humidityParame.setJ((int) (Double.parseDouble(csJ_2.getText().toString()) * 1000000));
                                    }
                                    if (csK_2.getText().length() > 0) {
                                        humidityParame.setK((int) (Double.parseDouble(csK_2.getText().toString()) * 1000000));
                                    }
                                    if (csL_2.getText().length() > 0) {
                                        humidityParame.setL((int) (Double.parseDouble(csL_2.getText().toString()) * 1000000));
                                    }
                                    if (csM_2.getText().length() > 0) {
                                        humidityParame.setM((int) (Double.parseDouble(csM_2.getText().toString()) * 1000000));
                                    }
                                    if (csN_2.getText().length() > 0) {
                                        humidityParame.setN((int) (Double.parseDouble(csN_2.getText().toString())) * 1000000);
                                    }
                                    if (csO_2.getText().length() > 0) {
                                        humidityParame.setO((int) (Double.parseDouble(csO_2.getText().toString())) * 1000000);
                                    }
//                                    mPresenter.setHumidityParame(humidityParame);
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
                tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {
                        if (tabId.equals("测量参数")) {
                            mPresenter.queryMeasureConfig();
                        } else if (tabId.equals("校准参数")) {
                            mPresenter.queryCorrectConfig();
                        } else if (tabId.equals("湿度参数")) {
                            mPresenter.queryHumidityConfig();
                        }
                    }
                });

                mPresenter.queryMeasureConfig();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 立即执行
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            App.getInstance().getBluetoothService().setTime(System.currentTimeMillis() / 1000);
            // 每2S执行1次唤醒设备确保活跃状态
            handler.postDelayed(this, 1000);
        }
    };
}
