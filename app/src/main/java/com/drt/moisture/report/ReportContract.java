package com.drt.moisture.report;

import android.widget.EditText;
import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import net.yzj.android.common.base.BaseView;

import java.util.Date;
import java.util.List;

public interface ReportContract {

    interface Model {
        void queryReport(int index, String measureName, Date startTime, Date endTime, final MeasureDataCallback<List<MeasureValue>> report);

        void stop();
    }

    interface View extends BaseView {

        void onSuccess(List<MeasureValue> measureValues);

        void onDone();

    }

    interface Presenter {
        void queryReport(int index, EditText measureName, Date startTime, Date endTime);

        void stop();
    }
}
