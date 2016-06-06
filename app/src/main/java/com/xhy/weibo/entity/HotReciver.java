package com.xhy.weibo.entity;

import java.util.List;

/**
 * Created by xuhaoyang on 16/5/21.
 */
public class HotReciver {


    private int code;
    private String error;
    private List<Hot> info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HotReciver{");
        sb.append("code=").append(code);
        sb.append(", error='").append(error).append('\'');
        sb.append(", info=").append(info);
        sb.append('}');
        return sb.toString();
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Hot> getInfo() {
        return info;
    }

    public void setInfo(List<Hot> info) {
        this.info = info;
    }
}
