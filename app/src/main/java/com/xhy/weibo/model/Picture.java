package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 16/5/31.
 */
public class Picture extends Model{

    private int id;
    private int wid;//微博的id
    private String max;
    private String medium;
    private String mini;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Picture{");
        sb.append("max='").append(max).append('\'');
        sb.append(", medium='").append(medium).append('\'');
        sb.append(", mini='").append(mini).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getMini() {
        return mini;
    }

    public void setMini(String mini) {
        this.mini = mini;
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
}
