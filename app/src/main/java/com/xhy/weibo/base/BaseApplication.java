package com.xhy.weibo.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.network.VolleyQueueSingleton;

import java.io.InputStream;

import im.fir.sdk.FIR;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class BaseApplication extends MultiDexApplication {

    private DBManager mDBManager;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext();
        context = getApplicationContext();
        AppConfig.initialize(this);
        FIR.init(this);

        Glide.get(this)
                .register(GlideUrl.class, InputStream.class,
                        new VolleyUrlLoader.Factory(VolleyQueueSingleton.getInstance(this.getApplicationContext()).
                                getRequestQueue()));

        SharedPreferences preferences = getSharedPreferences("setting", MODE_PRIVATE);
        mDBManager = new DBManager(context);
        mDBManager.openDatabase();
        mDBManager.closeDatabase();
    }


}
