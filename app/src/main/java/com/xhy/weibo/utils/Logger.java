package com.xhy.weibo.utils;

import android.util.Log;

import com.xhy.weibo.AppConfig;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class Logger {

    /**
     * 显示LOG(默认info级别)
     *
     * @param TAG
     * @param msg
     */
    public static void show(String TAG, String msg) {
        if (!AppConfig.isShowLog) {
            return;
        }
        show(TAG, msg, Log.INFO);
    }

    /**
     * 显示LOG
     *
     * @param TAG
     * @param msg
     * @param level
     *            1-info; 2-debug; 3-verbose
     */
    public static void show(String TAG, String msg, int level) {
        if (!AppConfig.isShowLog) {
            return;
        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, msg);
                break;
            case Log.DEBUG:
                Log.d(TAG, msg);
                break;
            case Log.INFO:
                Log.i(TAG, msg);
                break;
            case Log.WARN:
                Log.w(TAG, msg);
                break;
            case Log.ERROR:
                Log.e(TAG, msg);
                break;
            default:
                Log.i(TAG, msg);
                break;
        }
    }

}
