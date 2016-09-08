package com.xhy.weibo.model;

import android.text.TextUtils;
import android.util.Log;

import com.xhy.weibo.AppConfig;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/5/19.
 */
public class Login extends Model implements Serializable {
    private static final String CURRENT_USER = "current_login_user";

    /**
     * 转换json为Model
     *
     * @param json
     * @return
     */
    public static Login parseObject(final String json) {
        return Model.parseObject(json, Login.class);
    }

    /**
     * 保存用户登录信息
     *
     * @param login
     */
    public static void setCurrentLoginUser(Login login) {
        String jsonLoginUser = "";
        if (login != null) {
            jsonLoginUser = login.toJSONString();
        }
        AppConfig.putString(CURRENT_USER, jsonLoginUser);
    }

    public static int getCurrentId() {
        Login login = Login.getCurrentLoginUser();
        if (login != null) {
            return login.getId();
        }
        return 0;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public static Login getCurrentLoginUser() {
        String jsonLoginUser = AppConfig.getString(CURRENT_USER, "");
        if (TextUtils.isEmpty(jsonLoginUser)) {
            return null;
        }

        Login login = parseObject(jsonLoginUser);
        return login;
    }


    private int id;
    private String account;
    private String password;
    private long registime;
    private int lock;
    private String token;
    private int expires_in;
    private long tokenStartTime;
    private long uptime;//数据库更新/插入时间戳

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Login{");
        sb.append("id=").append(id);
        sb.append(", account='").append(account).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", registime=").append(registime);
        sb.append(", lock=").append(lock);
        sb.append(", token='").append(token).append('\'');
        sb.append(", expires_in=").append(expires_in);
        sb.append(", tokenStartTime=").append(tokenStartTime);
        sb.append(", uptime=").append(uptime);
        sb.append('}');
        return sb.toString();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTokenStartTime() {
        return tokenStartTime;
    }

    public void setTokenStartTime(long tokenStartTime) {
        this.tokenStartTime = tokenStartTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getRegistime() {
        return registime;
    }

    public void setRegistime(long registime) {
        this.registime = registime;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
