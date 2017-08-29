package com.xhy.weibo.event;


import com.xhy.weibo.model.JPushMessage;

/**
 * Created by zhujianheng on 4/25/16.
 */
public class JPushEvent {
    private JPushMessage jPushMessage;

    public JPushEvent(JPushMessage jPushMessage) {
        this.jPushMessage = jPushMessage;
    }

    public JPushMessage getjPushMessage() {
        return jPushMessage;
    }

    public void setjPushMessage(JPushMessage jPushMessage) {
        this.jPushMessage = jPushMessage;
    }
}
