package com.drt.moisture;

import net.yzj.android.common.base.BasePresenter;

public class MainPresenter extends BasePresenter {

    private MainContract.Model model;

    public MainPresenter() {
        model = new MainModel();
    }

}
