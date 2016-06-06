package com.xhy.weibo.base;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.xhy.weibo.constants.AccessToken;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.entity.Login;
import com.xhy.weibo.entity.LoginReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.NetParams;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class BaseApplication extends Application {

    private DBManager mDBManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Glide.get(this)
                .register(GlideUrl.class, InputStream.class,
                        new VolleyUrlLoader.Factory(VolleyQueueSingleton.getInstance(this.getApplicationContext()).
                                getRequestQueue()));


        mDBManager = new DBManager(getApplicationContext());
        mDBManager.openDatabase();
        mDBManager.closeDatabase();
    }
}
