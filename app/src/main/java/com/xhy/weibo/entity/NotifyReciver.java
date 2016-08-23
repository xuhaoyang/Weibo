package com.xhy.weibo.entity;

import com.xhy.weibo.model.NotifyInfo;

/**
 * Created by xuhaoyang on 16/6/5.
 */
public class NotifyReciver {
    private int code;
    private String error;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NotifyReciver{");
        sb.append("code=").append(code);
        sb.append(", error='").append(error).append('\'');
        sb.append(", info=").append(info);
        sb.append('}');
        return sb.toString();
    }

    public NotifyInfo getInfo() {
        return info;
    }

    public void setInfo(NotifyInfo info) {
        this.info = info;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private NotifyInfo info;
}
