package com.xhy.weibo.entity;

/**
 * Created by xuhaoyang on 16/5/17.
 */
public class NormalInfo {

    private int code;
    private String info;
    private String error;


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NormalInfo{");
        sb.append("code=").append(code);
        sb.append(", info='").append(info).append('\'');
        sb.append(", error='").append(error).append('\'');
        sb.append('}');
        return sb.toString();
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
