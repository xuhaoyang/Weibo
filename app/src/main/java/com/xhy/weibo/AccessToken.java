package com.xhy.weibo;

import android.content.Context;
import android.util.Log;

import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.TokenModel;
import com.xhy.weibo.utils.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

import hk.xhy.android.common.utils.LogUtils;

/**
 * Created by xuhaoyang on 16/5/30.
 */
public class AccessToken implements UserLoginLogic.LoginCallback {

    private static final String TAG = AccessToken.class.getSimpleName();

//    private static AccessToken mAccessToken;

    private final AtomicBoolean tokenRefreshing = new AtomicBoolean(false);
    private Context mContext;

    public AccessToken(String account, String password, Context context) {
        //获得
        TokenModel tokenModel = getTokenModel();
        if (tokenModel == null) {
            tokenModel = new TokenModel();
        }

        //保存
        tokenModel.setAccount(account);
        tokenModel.setPassword(password);
        setTokenModel(tokenModel);
        this.mContext = context;
        //初始化token
        long now = System.currentTimeMillis();
        initToken(now);
    }


    public long getTokenStartTime() {
        return getTokenModel().getAccessTokenStartTime();
    }


    public TokenModel getTokenModel() {

        return TokenModel.getCurrentToken();
    }

    public void setTokenModel(TokenModel tokenModel) {
        TokenModel.setCurrentToken(tokenModel);

    }

    public void setToken(String token) {
        TokenModel model = getTokenModel();
        model.setToken(token);
        setTokenModel(model);
    }

    public void setTokenStartTime(long tokenStartTime) {
        TokenModel model = getTokenModel();
        model.setAccessTokenStartTime(tokenStartTime);
        setTokenModel(model);

    }

    public String getToken() {
        long now = System.currentTimeMillis();
        long time = now - getTokenModel().getAccessTokenStartTime();

        if (time > 7100000 && this.tokenRefreshing.compareAndSet(false, true)) {
            initToken(now);
        }
        return getTokenModel().getToken();
    }

    public String getAccount() {
        return getTokenModel().getAccount();
    }

    public String getPassword() {
        return getTokenModel().getAccount();
    }

    public void setPassword(String password) {
        TokenModel model = getTokenModel();
        model.setPassword(password);
        setTokenModel(model);
    }

    public void setAccount(String account) {
        TokenModel model = getTokenModel();
        model.setAccount(account);
        setTokenModel(model);
    }

    private void initToken(final long refreshTime) {

        TokenModel model = getTokenModel();
        model.setOldTIme(model.getAccessTokenStartTime());
        model.setAccessTokenStartTime(refreshTime);

        UserLoginLogic.login(mContext, model.getAccount(), model.getPassword(), this);

        //保存model
        setTokenModel(model);
    }

    @Override
    public void onLoginSuccess(Login login) {

        //更新当前用户数据
        Login.setCurrentLoginUser(login);
        AppConfig.setUserId(login.getId());

        //更新数据
        TokenModel model = getTokenModel();
        model.setAccessTokenStartTime(login.getTokenStartTime());
        model.setToken(login.getToken());

        tokenRefreshing.set(false);

        //保存
        setTokenModel(model);

    }

    @Override
    public void onLoginFailure(int errorCode, String errorMessage) {
        TokenModel model = getTokenModel();
        model.setAccessTokenStartTime(model.getOldTIme());
        tokenRefreshing.set(false);
        Logger.show(TAG, errorMessage + errorCode);

        setTokenModel(model);
    }

    @Override
    public void onLoginError(Throwable error) {
        TokenModel model = getTokenModel();
        model.setAccessTokenStartTime(model.getOldTIme());
        tokenRefreshing.set(false);
        LogUtils.e(TAG, error.getMessage());
        setTokenModel(model);
    }

//    /**
//     * AccessToken保存在SP
//     *
//     * @param mAccessToken
//     */
//    public static void setCurrentAccessToken(AccessToken mAccessToken) {
//        String jsonAccessToken = "";
//        if (mAccessToken != null) {
//            jsonAccessToken = mAccessToken.toJSONString();
//        }
//
//        Logger.show(TAG,">>>>>"+jsonAccessToken);
//        AppConfig.putString("AccessToken", jsonAccessToken);
//    }
//
//
//    /**
//     * 转换json为Model
//     *
//     * @param json
//     * @return
//     */
//    public static AccessToken parseObject(final String json) {
//        return Model.parseObject(json, AccessToken.class);
//    }
//
//    /**
//     * 获得当前AccessToken Model
//     *
//     * @return
//     */
//    public static AccessToken getCurrentAccessToken() {
//        String jsonAccessToken = AppConfig.getString("AccessToken", "");
//        if (TextUtils.isEmpty(jsonAccessToken)) {
//            return null;
//        }
//        AccessToken accessToken = parseObject(jsonAccessToken);
//        return accessToken;
//    }
}
