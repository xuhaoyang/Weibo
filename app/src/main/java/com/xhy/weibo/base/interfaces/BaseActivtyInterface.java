package com.xhy.weibo.base.interfaces;

import android.app.Activity;

/**
 * Created by xuhaoyang on 16/8/24.
 */
public interface BaseActivtyInterface {

    void intent2Activity(Class<? extends Activity> tarActivity);

    void showLog(String msg);

    void showToast(String msg);
}
