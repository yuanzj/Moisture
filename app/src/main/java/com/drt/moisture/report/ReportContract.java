package com.drt.moisture.report;

import android.widget.EditText;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import net.yzj.android.common.base.BaseView;

import java.util.List;

public interface ReportContract {

    interface Model {
        void queryReport(String measureName, final MeasureDataCallback<List<MeasureValue>> report);

        void stop();
    }

    interface View extends BaseView {

        void onSuccess(List<MeasureValue> measureValues);

        void onDone();

    }

    interface Presenter {
        void queryReport(EditText measureName);

        void stop();
    }
}
