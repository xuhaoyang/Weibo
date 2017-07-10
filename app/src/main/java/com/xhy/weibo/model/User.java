package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 16/5/21.
 */
public class User {

    private String username;
    private String face;
    private int follow;
    private int fans;
    private int weibo;
    private int uid;
    private String intro;//一句话介绍自己
    private int mutual;//对方关注我
    private int followed;//我关注了对方
    private long uptime;//数据库更新/插入时间戳
    private String truename;
    private String sex;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("username='").append(username).append('\'');
        sb.append(", face='").append(face).append('\'');
        sb.append(", follow=").append(follow);
        sb.append(", fans=").append(fans);
        sb.append(", weibo=").append(weibo);
        sb.append(", uid=").append(uid);
        sb.append(", intro='").append(intro).append('\'');
        sb.append(", mutual=").append(mutual);
        sb.append(", followed=").append(followed);
        sb.append(", uptime=").append(uptime);
        sb.append('}');
        return sb.toString();
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getMutual() {
        return mutual;
    }

    public void setMutual(int mutual) {
        this.mutual = mutual;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
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
