package com.xhy.weibo.model;

import android.text.TextUtils;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.utils.Constants;

/**
 * Created by xuhaoyang on 16/9/9.
 */
public class TokenModel extends Model {

    private String account;
    private String password;
    private String token;
    private long oldTIme;
    private long accessTokenStartTime;

    /**
     * 转换json为Model
     *
     * @param json
     * @return
     */
    public static TokenModel parseObject(final String json) {
        return Model.parseObject(json, TokenModel.class);
    }

    /**
     * 保存token
     *
     * @param tokenModel
     */
    public static void setCurrentToken(TokenModel tokenModel) {
        String json = "";
        if (tokenModel != null) {
            json = tokenModel.toJSONString();
        }
        AppConfig.putString(Constants.TOKEN, json);
    }

    /**
     * 获取当前token
     * @return
     */
    public static TokenModel getCurrentToken() {
        String json = AppConfig.getString(Constants.TOKEN, "");
        if (TextUtils.isEmpty(json)) return null;

        TokenModel tokenModel = parseObject(json);
        return tokenModel;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TokenModel{");
        sb.append("account='").append(account).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", oldTIme=").append(oldTIme);
        sb.append(", accessTokenStartTime=").append(accessTokenStartTime);
        sb.append('}');
        return sb.toString();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getOldTIme() {
        return oldTIme;
    }

    public void setOldTIme(long oldTIme) {
        this.oldTIme = oldTIme;
    }

    public long getAccessTokenStartTime() {
        return accessTokenStartTime;
    }

    public void setAccessTokenStartTime(long accessTokenStartTime) {
        this.accessTokenStartTime = accessTokenStartTime;
    }
}
