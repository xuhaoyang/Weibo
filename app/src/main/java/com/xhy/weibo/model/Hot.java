package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 16/5/21.
 */
public class Hot {


    private int id;
    private int wid;
    private String keyword;
    private int count;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Hot{");
        sb.append("id=").append(id);
        sb.append(", wid=").append(wid);
        sb.append(", keyword='").append(keyword).append('\'');
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
