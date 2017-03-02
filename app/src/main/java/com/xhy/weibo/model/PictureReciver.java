package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 16/5/31.
 */
public class PictureReciver {
    private int code;
    private Picture info;
    private String error;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PictureReciver{");
        sb.append("code=").append(code);
        sb.append(", info=").append(info);
        sb.append(", error='").append(error).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Picture getInfo() {
        return info;
    }

    public void setInfo(Picture info) {
        this.info = info;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
