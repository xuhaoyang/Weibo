package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 2017/7/13.
 */

public class Userinfo extends Model {

    /**
     * id : 8
     * username : 测试
     * truename : 陈建宇
     * sex : 男
     * location : 广东 深圳
     * constellation : 双子座
     * intro : 煎鱼悲剧的一生
     * face50 : 2017_02/mini_58b0169070d3f.jpg
     * face80 : 2017_02/medium_58b0169070d3f.jpg
     * face180 : 2017_02/max_58b0169070d3f.jpg
     * style : default
     * follow : 2
     * fans : 2
     * weibo : 9
     * uid : 8
     */

    private int id;
    private String username;
    private String truename;
    private String sex;
    private String location;
    private String constellation;
    private String intro;
    private String face50;
    private String face80;
    private String face180;
    private String style;
    private int follow;
    private int fans;
    private int weibo;
    private int uid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getFace50() {
        return face50;
    }

    public void setFace50(String face50) {
        this.face50 = face50;
    }

    public String getFace80() {
        return face80;
    }

    public void setFace80(String face80) {
        this.face80 = face80;
    }

    public String getFace180() {
        return face180;
    }

    public void setFace180(String face180) {
        this.face180 = face180;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getWeibo() {
        return weibo;
    }

    public void setWeibo(int weibo) {
        this.weibo = weibo;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
