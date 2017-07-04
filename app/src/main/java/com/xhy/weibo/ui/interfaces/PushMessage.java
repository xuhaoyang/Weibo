package com.xhy.weibo.ui.interfaces;


/**
 * Created by xuhaoyang on 2017/1/10.
 */

public interface PushMessage<T> {
    void pushString(String text);

    void pushResult(boolean b, T result);
}
