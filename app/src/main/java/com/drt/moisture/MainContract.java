package com.drt.moisture;

import net.yzj.android.common.base.BaseView;

public interface MainContract {

    interface Model {
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

    }

    interface Presenter {
    }
}
