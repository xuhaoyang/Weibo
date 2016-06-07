package com.xhy.weibo.constants;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.entity.Login;
import com.xhy.weibo.entity.LoginReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xuhaoyang on 16/5/30.
 */
public class AccessToken implements Serializable {

    private static AccessToken mAccessToken;

    private final AtomicBoolean tokenRefreshing = new AtomicBoolean(false);
    private String token;
    private String account;
    private String password;
    private long tokenStartTime;
    private Context mContext;

    private AccessToken(String account, String password, Context context) {
        this.account = account;
        this.password = password;
        this.mContext = context;
        //初始化token
        long now = System.currentTimeMillis();
        initToken(now);
    }

    public long getTokenStartTime() {
        return tokenStartTime;
    }

    public static synchronized AccessToken getInstance(String account, String password, Context context) {
        if (mAccessToken == null) {
            mAccessToken = new AccessToken(account, password, context);
        }
        return mAccessToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenStartTime(long tokenStartTime) {
        this.tokenStartTime = tokenStartTime;
    }

    public String getToken() {
        long now = System.currentTimeMillis();
        long time = now - this.tokenStartTime;

        if (time > 7100000 && this.tokenRefreshing.compareAndSet(false, true)) {
            initToken(now);
        }
        return token;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    private void initToken(final long refreshTime) {
        final long oldTIme = this.tokenStartTime;
        this.tokenStartTime = refreshTime;
        GsonRequest<LoginReciver> request =
                new GsonRequest<LoginReciver>(Request.Method.POST, URLs.WEIBO_USER_LOGIN,
                        LoginReciver.class, null,
                        new Response.Listener<LoginReciver>() {
                            @Override
                            public void onResponse(LoginReciver response) {
                                if (response.getCode() == 200) {
                                    Login login = response.getInfo();
                                    token = login.getToken();
                                } else {
                                    tokenStartTime = oldTIme;
                                }
                                tokenRefreshing.set(false);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tokenRefreshing.set(false);
                        tokenStartTime = oldTIme;
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("account", account);
                        map.put("password", password);
                        return map;
                    }
                };

        VolleyQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }
}
