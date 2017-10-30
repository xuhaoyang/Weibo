package com.xhy.weibo;

import com.xhy.weibo.model.Login;

import hk.xhy.android.common.utils.ConstUtils;
import hk.xhy.android.common.utils.PreferenceUtils;
import hk.xhy.android.common.utils.TimeUtils;
import hk.xhy.android.common.utils.Utils;

/**
 * Created by xuhaoyang on 16/7/21.
 */
public class AppConfig extends PreferenceUtils {

    private static final String TAG = AppConfig.class.getSimpleName();

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
     * 获得通知间隔
     *
     * @return 单位毫秒
     */
    public static long getNotificaitonInterval() {
        return getLong("notificationinterval", TimeUtils.convertTimeMillis(2, ConstUtils.MIN));
    }

    /**
     * 保存通知间隔时间
     *
     * @param time 单位分
     */
    public static void setNotificationInterval(int time) {
        putLong("notificationinterval", TimeUtils.convertTimeMillis(time, ConstUtils.MIN));
    }

    /**
     * 设置免打扰开关
     *
     * @param flag true开/false关
     */
    public static void setDoNotDisturb(boolean flag) {
        putBoolean("DoNotDisturb", flag);
    }

    /**
     * 获得免打扰开关值
     *
     * @return
     */
    public static boolean getDoNotDisturb() {
        return getBoolean("DoNotDisturb", false);
    }

    /**
     * 获得推送方式 自建/极推送
     *
     * @return
     */
    public static int getNotifyMode() {
        return getInt("NotifyMode", 0);
    }

    /**
     * 设置推送方式
     * @param value 自建0/极光推送1
     */
    public static void setNotifyMode(int value) {
        putInt("NotifyMode", value);
    }

    /**
     * 获取AccessToken
     *
     * @return
     */
    public static AccessToken getAccessToken() {
        if (ACCESS_TOKEN == null) {
            ACCESS_TOKEN = new AccessToken(getAccount(), getPassword(), Utils.getContext());
        }
        return ACCESS_TOKEN;
    }


}
