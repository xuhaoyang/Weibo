package com.xhy.weibo.entity;


/**
 * Created by xuhaoyang on 16/5/19.
 */
public class LoginReciver {
    private int code;
    private String error;
    private Login info;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LoginReciver{");
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

    public Login getInfo() {
        return info;
    }

    public void setInfo(Login info) {
        this.info = info;
    }
}
