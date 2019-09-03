package com.drt.moisture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.ArrayList;
import java.util.List;

public class BleScanActivity extends Activity {


    private ListView mListView;

    private List<SearchResult> mDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);

        mDevices = new ArrayList<>();

        mListView = findViewById(R.id.list_view);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("SearchResult", mDevices.get(position));
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        searchDevice();

    }

    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2).build();

        App.getInstance().getBluetoothClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("BleScanActivity.onSearchStarted");
            mDevices.clear();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);

                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("BleScanActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("BleScanActivity.onSearchCanceled");

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        App.getInstance().getBluetoothClient().stopSearch();
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mDevices.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getApplication());
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_device_item, parent, false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.title);
                holder.tvAddress = convertView.findViewById(R.id.desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(mDevices.get(position).getName());
            holder.tvAddress.setText(mDevices.get(position).getAddress());
            return convertView;
        }
    };

    static class ViewHolder {
        TextView tvName;
        TextView tvAddress;
    }
}
