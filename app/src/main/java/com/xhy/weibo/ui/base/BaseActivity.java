package com.xhy.weibo.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xhy.weibo.utils.Logger;

import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.android.common.utils.ToastUtils;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public abstract class BaseActivity extends hk.xhy.android.common.ui.BaseActivity {

    protected String TAG = getClass().getSimpleName();
    protected BaseApplication application;
    protected ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtils.addActivity(this);

        /**
         * 竖屏
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        application = (BaseApplication) getApplication();
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        ActivityUtils.startActivity(this, tarActivity);
    }

    protected void showLog(String msg) {
        Logger.show(TAG, msg);
    }

    protected void showToast(String msg) {
        ToastUtils.showShort(msg);
    }

    protected void showProgressDialog(int resId) {
        showProgressDialog(getString(resId));
    }

    protected void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
