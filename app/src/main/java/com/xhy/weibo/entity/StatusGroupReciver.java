package com.xhy.weibo.entity;

import com.xhy.weibo.model.StatusGroup;

import java.util.List;

/**
 * Created by xuhaoyang on 16/5/20.
 */
public class StatusGroupReciver {

    private int code;
    private List<StatusGroup> info;
    private String error;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StatusGroupReciver{");
        sb.append("code=").append(code);
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

    public List<StatusGroup> getInfo() {
        return info;
    }

    public void setInfo(List<StatusGroup> info) {
        this.info = info;
    }
}
