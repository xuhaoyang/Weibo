package com.xhy.weibo.model;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class Status implements Serializable {


    private int id;
    private String content;
    private long time;
    private int turn;//转发次数
    private int keep;//收藏次数
    private int comment;//评论次数
    private int uid;//所属用户的ID
    private String username;
    private String face;
    private String mini;
    private String medium;
    private String max;
    private Status status;
    private int isturn;
    private boolean isKeep;//是否被收藏
    private int kid;
    private int ktime;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Status{");
        sb.append("id=").append(id);
        sb.append(", content='").append(content).append('\'');
        sb.append(", time=").append(time);
        sb.append(", turn=").append(turn);
        sb.append(", keep=").append(keep);
        sb.append(", comment=").append(comment);
        sb.append(", uid=").append(uid);
        sb.append(", username='").append(username).append('\'');
        sb.append(", face='").append(face).append('\'');
        sb.append(", mini='").append(mini).append('\'');
        sb.append(", medium='").append(medium).append('\'');
        sb.append(", max='").append(max).append('\'');
        sb.append(", status=").append(status);
        sb.append(", isturn=").append(isturn);
        sb.append(", isKeep=").append(isKeep);
        sb.append(", kid=").append(kid);
        sb.append(", ktime=").append(ktime);
        sb.append('}');
        return sb.toString();
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

    public String getMini() {
        return mini;
    }

    public void setMini(String mini) {
        this.mini = mini;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
}
