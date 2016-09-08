package com.xhy.weibo;

import android.content.Context;
import android.util.Log;

import com.xhy.weibo.model.Login;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.utils.Logger;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xuhaoyang on 16/5/30.
 */
public class AccessToken implements Serializable, UserLoginLogic.LoginCallback {

    private static final String TAG = AccessToken.class.getSimpleName();

    private static AccessToken mAccessToken;

    private final AtomicBoolean tokenRefreshing = new AtomicBoolean(false);
    private String account;
    private String password;
    private Context mContext;
    private long oldTIme;

    private AccessToken(String account, String password, Context context) {
        this.account = account;
        this.password = password;
        this.mContext = context;
        //初始化token
        long now = System.currentTimeMillis();
        initToken(now);
    }

    public long getTokenStartTime() {
        return AppConfig.getAccessTokenStartTime();
    }

    public static synchronized AccessToken getInstance(String account, String password, Context context) {
        if (mAccessToken == null) {
            mAccessToken = new AccessToken(account, password, context);
        }
        return mAccessToken;
    }

    public void setToken(String token) {
        AppConfig.setAccessToken(token);
    }

    public void setTokenStartTime(long tokenStartTime) {
        AppConfig.setAccessTokenStartTime(tokenStartTime);
    }

    public String getToken() {
        long now = System.currentTimeMillis();
        long time = now - AppConfig.getAccessTokenStartTime();

        if (time > 7100000 && this.tokenRefreshing.compareAndSet(false, true)) {
            initToken(now);
        }
        return AppConfig.getAccessToken();
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    private void initToken(final long refreshTime) {
        oldTIme = AppConfig.getAccessTokenStartTime();
        AppConfig.setAccessTokenStartTime(refreshTime);
        UserLoginLogic.login(mContext, account, password, this);
    }

    @Override
    public void onLoginSuccess(Login login) {
        Login.setCurrentLoginUser(login);
        AppConfig.setAccessToken(login.getToken());
        AppConfig.setAccessTokenStartTime(login.getTokenStartTime());
        AppConfig.setUserId(login.getId());
        tokenRefreshing.set(false);

    }

    @Override
    public void onLoginFailure(int errorCode, String errorMessage) {
        AppConfig.setAccessTokenStartTime(oldTIme);
        tokenRefreshing.set(false);
        Logger.show(TAG, errorMessage + errorCode);
    }

    @Override
    public void onLoginError(Throwable error) {
        AppConfig.setAccessTokenStartTime(oldTIme);
        tokenRefreshing.set(false);
        Logger.show(TAG, error.getMessage(), Log.ERROR);

    }
}
