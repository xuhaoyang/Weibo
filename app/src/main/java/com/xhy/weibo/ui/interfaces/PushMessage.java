package com.xhy.weibo.ui.interfaces;

import com.xhy.weibo.model.Result;

/**
 * Created by xuhaoyang on 2017/1/10.
 */

public interface PushMessage<T> {
    void pushString(String text);

    void pushResult(boolean b, T result);
}
