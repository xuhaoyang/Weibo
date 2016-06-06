package com.xhy.weibo.entity;

import java.util.List;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class CommentReciver {

    private int code;
    private String error;
    private List<Comment> info;
    private int totalPage;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommentReciver{");
        sb.append("code=").append(code);
        sb.append(", error='").append(error).append('\'');
        sb.append(", info=").append(info);
        sb.append(", totalPage=").append(totalPage);
        sb.append('}');
        return sb.toString();
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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

    public List<Comment> getInfo() {
        return info;
    }

    public void setInfo(List<Comment> info) {
        this.info = info;
    }
}
