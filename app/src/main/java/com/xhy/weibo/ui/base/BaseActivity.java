package com.xhy.weibo.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xhy.weibo.utils.Logger;

import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.ToastUtils;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public abstract class BaseActivity extends hk.xhy.android.commom.ui.BaseActivity {

    protected String TAG = getClass().getSimpleName();
    protected BaseApplication application;
    protected SharedPreferences sp;


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

}
