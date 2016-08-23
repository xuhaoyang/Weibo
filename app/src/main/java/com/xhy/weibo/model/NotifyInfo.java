package com.xhy.weibo.model;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/6/5.
 */
public class NotifyInfo implements Serializable {

    private int atme;
    private int letter;
    private int comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotifyInfo info = (NotifyInfo) o;

        if (atme != info.atme) return false;
        if (letter != info.letter) return false;
        return comment == info.comment;

    }

    @Override
    public int hashCode() {
        int result = atme;
        result = 31 * result + letter;
        result = 31 * result + comment;
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NotifyInfo{");
        sb.append("atme=").append(atme);
        sb.append(", letter=").append(letter);
        sb.append(", comment=").append(comment);
        sb.append('}');
        return sb.toString();
    }

    public int getAtme() {
        return atme;
    }

    public void setAtme(int atme) {
        this.atme = atme;
    }

    public int getLetter() {
        return letter;
    }

    public void setLetter(int letter) {
        this.letter = letter;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }
}
