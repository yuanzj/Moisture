package com.drt.moisture.report;

import com.drt.moisture.data.MeasureValue;
import com.drt.moisture.data.source.MeasureDataCallback;

import net.yzj.android.common.base.BaseView;

import java.util.List;

public interface ReportContract {

    interface Model {
        void queryReport(final MeasureDataCallback<List<MeasureValue>> report);
    }

    interface View extends BaseView {

        void onSuccess(List<MeasureValue> measureValues);

    }

    interface Presenter {
        void queryReport();
    }
}
