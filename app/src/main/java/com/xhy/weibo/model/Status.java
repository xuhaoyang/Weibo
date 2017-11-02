package com.xhy.weibo.model;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class Status extends Model implements Serializable {


    private int id;
    private String content;
    private long time;
    private int turn;//转发次数
    private int keep;//收藏次数
    private int comment;//评论次数
    private int uid;//所属用户的ID
    private Status status;
    private int isturn;
    private boolean isKeep;//是否被收藏
    private int kid;
    private int ktime;
    private Userinfo userinfo;
    private Picture picture;
    private Map maps;

    /**
     * 转换json为Model
     *
     * @param json
     * @return
     */
    public static Status parseObject(final String json) {
        return Model.parseObject(json, Status.class);
    }


    public int getKid() {
        return kid;
    }

    public void setKid(int kid) {
        this.kid = kid;
    }

    public int getKtime() {
        return ktime;
    }

    public void setKtime(int ktime) {
        this.ktime = ktime;
    }

    public boolean isKeep() {
        return isKeep;
    }

    public void setKeep(boolean keep) {
        isKeep = keep;
    }

    public int getIsturn() {
        return isturn;
    }

    public void setIsturn(int isturn) {
        this.isturn = isturn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getKeep() {
        return keep;
    }

    public void setKeep(int keep) {
        this.keep = keep;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Userinfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(Userinfo userinfo) {
        this.userinfo = userinfo;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Map getMaps() {
        return maps;
    }

    public void setMaps(Map maps) {
        this.maps = maps;
    }
}
