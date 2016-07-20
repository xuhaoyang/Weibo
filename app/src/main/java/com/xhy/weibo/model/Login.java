package com.xhy.weibo.model;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/5/19.
 */
public class Login implements Serializable {

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
