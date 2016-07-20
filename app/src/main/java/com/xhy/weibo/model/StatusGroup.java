package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 16/5/20.
 */
public class StatusGroup {

    private int id;
    private String name;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StatusGroup{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
