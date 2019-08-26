package com.drt.moisture.setting;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.drt.moisture.BluetoothBaseActivity;
import com.drt.moisture.R;
import com.drt.moisture.data.DeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SettingActivity extends BluetoothBaseActivity<SettingPresenter> implements SettingContract.View, AdapterView.OnItemClickListener {


    @BindView(R.id.list_view)
    ListView listView;

    View deviceInfoView;

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
        item1.put("title", "系统重置");
        data.add(item1);

        item1 = new HashMap<>();
        item1.put("icon", R.mipmap.icons_data_configuration);
        item1.put("title", "工厂模式");
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
        switch (position) {
            case 0:
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
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }
}
