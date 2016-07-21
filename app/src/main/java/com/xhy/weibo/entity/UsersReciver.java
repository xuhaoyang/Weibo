package com.xhy.weibo.entity;

import com.xhy.weibo.model.User;

import java.util.List;

/**
 * Created by xuhaoyang on 16/5/21.
 */
public class UsersReciver {
    private int code;
    private String error;
    private List<User> info;
    private int totalPage;

    public void setInfo(List<User> info) {
        this.info = info;
    }

    public List<User> getInfo() {
        return info;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UsersReciver{");
        sb.append("code=").append(code);
        sb.append(", error='").append(error).append('\'');
        sb.append(", info=").append(info);
        sb.append(", totalPage=").append(totalPage);
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

}
