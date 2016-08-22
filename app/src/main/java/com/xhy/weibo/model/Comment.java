package com.xhy.weibo.model;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class Comment implements Serializable {


    private int id;//该条评论的ID
    private String content;//评论内容
    private long time;//评论时间
    private int wid;//被评论的微博ID
    private String username;//用户昵称
    private String face;//用户头像地址
    private int uid;//评论者的用户ID

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Comment{");
        sb.append("id=").append(id);
        sb.append(", content='").append(content).append('\'');
        sb.append(", time=").append(time);
        sb.append(", wid=").append(wid);
        sb.append(", username='").append(username).append('\'');
        sb.append(", face='").append(face).append('\'');
        sb.append(", uid=").append(uid);
        sb.append('}');
        return sb.toString();
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

    public int getWid() {
        return wid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (id != comment.id) return false;
        if (time != comment.time) return false;
        if (wid != comment.wid) return false;
        if (uid != comment.uid) return false;
        if (content != null ? !content.equals(comment.content) : comment.content != null)
            return false;
        if (username != null ? !username.equals(comment.username) : comment.username != null)
            return false;
        return face != null ? face.equals(comment.face) : comment.face == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + wid;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (face != null ? face.hashCode() : 0);
        result = 31 * result + uid;
        return result;
    }

    public void setWid(int wid) {
        this.wid = wid;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
