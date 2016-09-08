package com.xhy.weibo.base;

import android.content.Context;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.db.DBManager;

import im.fir.sdk.FIR;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class BaseApplication extends hk.xhy.android.commom.Application {

    private DBManager mDBManager;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext();
        context = getApplicationContext();
        AppConfig.initialize(this);
        FIR.init(this);

        mDBManager = new DBManager(context);
        mDBManager.openDatabase();
        mDBManager.closeDatabase();
    }


}
