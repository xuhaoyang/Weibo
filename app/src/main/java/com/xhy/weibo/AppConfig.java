package com.xhy.weibo;

import com.xhy.weibo.model.Login;

import hk.xhy.android.commom.utils.Logger;
import hk.xhy.android.commom.utils.PreferenceUtils;

/**
 * Created by xuhaoyang on 16/7/21.
 */
public class AppConfig extends PreferenceUtils {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean isShowLog = DEBUG;
    public static final String KEEP_TOKEN = "TOKEN";
    public static final String KEEP_TOKEN_START_TIME = "TOKEN_START_TIME";
    public static final String KEEP_TOKEN_ACCOUNT = "TOKEN_NAME";
    public static final String KEEP_TOKEN_USER_ID = "TOKEN_USER_ID";
    public static final String KEEP_SETTING_ISNOTIFY = "SETTING_ISNOTIFY";

    public static AccessToken ACCESS_TOKEN;

    /**
     * 当前账户
     *
     * @return
     */
    public static String getAccount() {
        return getString("account", "");
//        Login login = Login.getCurrentLoginUser();
//        return login != null ? login.getAccount() : null;
    }

    /**
     * 设置当前账户
     *
     * @param account
     */
    public static void setAccount(String account) {
        putString("account", account);
    }

    /**
     * 获得当前用户id
     *
     * @return
     */
    public static int getUserId() {
//        return getInt("USER_ID", 0);
        return Login.getCurrentId();
    }

    /**
     * 设置当前用户id
     *
     * @param id
     */
    public static void setUserId(int id) {
        putInt("USER_ID", id);
    }

    /**
     * 当前账户密码
     *
     * @return
     */
    public static String getPassword() {
        return getString("password", "");
    }

    /**
     * 设置当前账户密码
     *
     * @param password
     */
    public static void setPassword(String password) {
        putString("password", password);
    }

    /**
     * 是否推送通知
     *
     * @return
     */
    public static boolean isNotify() {
        return getBoolean("isNotify", false);
    }

    /**
     * 设置是否推送通知
     *
     * @return
     */
    public static void setNotify(boolean flag) {
        putBoolean("isNotify", flag);
    }


    /**
     * 设置Token String
     *
     * @param token
     */
    public static void setAccessToken(String token) {
        putString("AccessToken", token);
    }

    /**
     * 获取Token
     *
     * @return
     */
    public static String getAccessToken() {
        return getString("AccessToken", "");
    }

    /**
     * 设置Token获取初始时间
     *
     * @param time
     */
    public static void setAccessTokenStartTime(long time) {
        putLong("AccessTokenStartTime", time);
    }

    /**
     * 获得Token获取初始时间
     *
     * @return
     */
    public static long getAccessTokenStartTime() {
        return getLong("AccessTokenStartTime", 0);
    }
}
