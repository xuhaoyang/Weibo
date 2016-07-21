package com.xhy.weibo.entity;

import com.xhy.weibo.model.User;

/**
 * Created by xuhaoyang on 16/5/21.
 */
public class UserReciver {
    private int code;
    private String error;
    private User info;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserReciver{");
        sb.append("code=").append(code);
        sb.append(", error='").append(error).append('\'');
        sb.append(", info=").append(info);
        sb.append('}');
        return sb.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public User getInfo() {
        return info;
    }

    public void setInfo(User info) {
        this.info = info;
    }
}
