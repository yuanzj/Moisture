package com.drt.moisture.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.drt.moisture.data.SetDeviceInfoParame;
import com.drt.moisture.data.source.bluetooth.resquest.SetCorrectParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetHumidityParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetMeasureParameRequest;
import com.drt.moisture.data.source.bluetooth.resquest.SetRateRequest;
import com.drt.moisture.util.ExcelUtil;
import com.inuker.bluetooth.library.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BluetoothBaseActivity<SettingPresenter> implements SettingContract.View, AdapterView.OnItemClickListener {


    @BindView(R.id.list_view)
    ListView listView;

    View deviceInfoView, dialogDateTime, dialogRate, dialogParameSet, dialogSetDeviceInfo;

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
                    ((TextView) deviceInfoView.findViewById(R.id.title4)).setText("水分活度测量仪");
                    ((TextView) deviceInfoView.findViewById(R.id.title5)).setText(deviceInfo.getBattery());
                }

                if (dialogSetDeviceInfo != null) {

                    byte[] snTemp = deviceInfo.getSN().getBytes();

                    ArrayList<Byte> sn = new ArrayList<>();
                    for (int i = 0 ; i < snTemp.length ; i ++){
                        if (snTemp[i] != 0) {
                            sn.add(snTemp[i]);
                        }
                    }
                    byte[] snFinal = new byte[sn.size()];
                    for (int i = 0 ; i < snFinal.length ; i ++) {
                        snFinal[i] = sn.get(i);
                    }

                    ((EditText) dialogSetDeviceInfo.findViewById(R.id.xlh)).setText(new String(snFinal));

                    byte[] modelTemp = deviceInfo.getModel().getBytes();

                    ArrayList<Byte> model = new ArrayList<>();
                    for (int i = 0 ; i < modelTemp.length ; i ++){
                        if (modelTemp[i] != 0) {
                            model.add(modelTemp[i]);
                        }
                    }
                    byte[] modelFinal = new byte[model.size()];
                    for (int i = 0 ; i < modelFinal.length ; i ++) {
                        modelFinal[i] = model.get(i);
                    }

                    ((EditText) dialogSetDeviceInfo.findViewById(R.id.xh)).setText(new String(modelFinal));

                    byte[] NameTemp = deviceInfo.getName().getBytes();

                    ArrayList<Byte> Name = new ArrayList<>();
                    for (int i = 0 ; i < NameTemp.length ; i ++){
                        if (NameTemp[i] != 0) {
                            Name.add(NameTemp[i]);
                        }
                    }
                    byte[] NameFinal = new byte[Name.size()];
                    for (int i = 0 ; i < NameFinal.length ; i ++) {
                        NameFinal[i] = Name.get(i);
                    }
                    ((EditText) dialogSetDeviceInfo.findViewById(R.id.mc)).setText(new String(NameFinal));

                    byte[] getBatteryTemp = deviceInfo.getBattery().getBytes();

                    ArrayList<Byte> getBattery = new ArrayList<>();
                    for (int i = 0 ; i < getBatteryTemp.length ; i ++){
                        if (getBatteryTemp[i] != 0) {
                            getBattery.add(getBatteryTemp[i]);
                        }
                    }
                    byte[] getBatteryFinal = new byte[getBattery.size()];
                    for (int i = 0 ; i < getBatteryFinal.length ; i ++) {
                        getBatteryFinal[i] = getBattery.get(i);
                    }
                    ((EditText) dialogSetDeviceInfo.findViewById(R.id.dc)).setText(new String(getBatteryFinal));
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

//        item1 = new HashMap<>();
//        item1.put("icon", R.mipmap.icons_recurring_appointment);
//        item1.put("title", "查询频率");
//        data.add(item1);
//
//        item1 = new HashMap<>();
//        item1.put("icon", R.mipmap.icons_data_configuration);
//        item1.put("title", "参数设置");
//        data.add(item1);

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
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
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

                final String[] items = { "出厂参数设置", "用户参数设置" };
                AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
                listDialog.setTitle("请选择参数设置类型");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // which 下标从0开始
                        // ...To-do
                        switch (which) {
                            case 0:
                                startActivity(new Intent(SettingActivity.this, com.drt.moisture.syssetting.SettingActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(SettingActivity.this, com.drt.moisture.usersetting.SettingActivity.class));
                                break;
                            default:
                                break;
                        }
                    }
                });
                listDialog.show();
//                if (id >= 0) {
//                    final EditText edit = new EditText(this);
//                    AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
//                    editDialog.setTitle("请输入密码");
//                    //设置dialog布局
//                    editDialog.setView(edit);
//                    //设置按钮
//                    editDialog.setPositiveButton("确认"
//                            , new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    if (edit.getText().toString().equals(readFromFile(ExcelUtil.getSDPath() + "/水分活度测量/config.txt"))) {
//                                        onItemClick(parent, view, position, -1);
//                                    } else {
//                                        Toast.makeText(SettingActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
//                                    }
//                                    dialog.dismiss();
//                                    hideInput();
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // TODO Auto-generated method stub
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setCancelable(false);
//                    editDialog.create().show();
//                    return;
//                }
//
//                dialogRate = LayoutInflater.from(this).inflate(R.layout.dialog_rate, null);
//                final Spinner spinner2 = dialogRate.findViewById(R.id.spinner2);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("查询频率")
//                        .setView(dialogRate)
//                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Log.d("yzj", "查询频率：" + Integer.parseInt(spinner2.getSelectedItem().toString()));
//
//                                AppConfig appConfig = App.getInstance().getLocalDataService().queryAppConfig();
//                                appConfig.setPeriod((int) (Double.parseDouble(spinner2.getSelectedItem().toString()) * 1000));
//                                App.getInstance().getLocalDataService().setAppConfig(appConfig);
//                                mPresenter.setRateParame(appConfig.getPeriod(), appConfig.getRatio());
//
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO Auto-generated method stub
//                                dialog.dismiss();
//                            }
//                        })
//                        .setCancelable(false);
//                builder.show();
//                mPresenter.queryRate();
            }
            break;
            case 3: {
                if (id >= 0) {
                    final EditText edit = new EditText(this);
                    AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
                    editDialog.setTitle("请输入密码");
                    //设置dialog布局
                    editDialog.setView(edit);
                    //设置按钮
                    editDialog.setPositiveButton("确认"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (edit.getText().toString().equals(readFromFile(ExcelUtil.getSDPath() + "/水分活度测量/config.txt"))) {
                                        onItemClick(parent, view, position, -1);
                                    } else {
                                        Toast.makeText(SettingActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                    hideInput();
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
                    editDialog.create().show();
                    return;
                }
                dialogSetDeviceInfo = LayoutInflater.from(this).inflate(R.layout.dialog_set_device_info, null);
                final EditText xlh = dialogSetDeviceInfo.findViewById(R.id.xlh);
                final EditText xh = dialogSetDeviceInfo.findViewById(R.id.xh);
                final EditText mc = dialogSetDeviceInfo.findViewById(R.id.mc);
                final EditText dc = dialogSetDeviceInfo.findViewById(R.id.dc);

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("信息设置")
                        .setView(dialogSetDeviceInfo)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SetDeviceInfoParame mSetDeviceInfoParame = new SetDeviceInfoParame();
                                mSetDeviceInfoParame.setBattery(dc.getText().toString());
                                mSetDeviceInfoParame.setModel(xh.getText().toString());
                                mSetDeviceInfoParame.setName(mc.getText().toString());
                                mSetDeviceInfoParame.setSN(xlh.getText().toString());
                                mPresenter.setDeviceInfo(mSetDeviceInfoParame);
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

                mPresenter.queryDeviceInfo();
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
//            handler.postDelayed(this, 45000);
        }
    };


    private int COUNTS = 6;// 点击次数
    private long[] mHits = new long[COUNTS];//记录点击次数
    private long DURATION = 3000;//有效时间


    @OnClick(R.id.set_parent)
    public void onTap5(View view) {
        //将mHints数组内的所有元素左移一个位置
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //获得当前系统已经启动的时间
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            // 相关逻辑操作

            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> item1 = new HashMap<>();
            item1.put("icon", R.mipmap.icons_device_information);
            item1.put("title", "设备信息");
            data.add(item1);

            item1 = new HashMap<>();
            item1.put("icon", R.mipmap.icons_clock_settings);
            item1.put("title", "时间设置");
            data.add(item1);

//            item1 = new HashMap<>();
//            item1.put("icon", R.mipmap.icons_recurring_appointment);
//            item1.put("title", "查询频率");
//            data.add(item1);

            item1 = new HashMap<>();
            item1.put("icon", R.mipmap.icons_data_configuration);
            item1.put("title", "参数设置");
            data.add(item1);

            item1 = new HashMap<>();
            item1.put("icon", R.mipmap.icons_data_configuration);
            item1.put("title", "信息设置");
            data.add(item1);

            listView.setAdapter(new SimpleAdapter(this, data,
                    R.layout.adapter_setting_item, new String[]{"icon", "title"}, new int[]{R.id.icon, R.id.title}));

            //初始化点击次数
            mHits = new long[COUNTS];
        }
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private String readFromFile(String path) {

        String ret = "";

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "667788";
            }
            inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }
            ret = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

}
