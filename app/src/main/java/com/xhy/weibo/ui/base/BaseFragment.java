package com.xhy.weibo.ui.base;

import android.support.v4.app.Fragment;

import com.xhy.weibo.utils.Logger;

import hk.xhy.android.common.utils.ToastUtils;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG = getClass().getSimpleName();


    protected void showLog(String msg) {
        Logger.show(TAG, msg);
    }

    protected void showToast(String msg) {
        ToastUtils.showShort(msg);
    }
}
