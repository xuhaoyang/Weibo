package com.xhy.weibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xhy.weibo.utils.HttpNetUtil;

/**
 * Created by xuhaoyang on 16/7/20.
 */
public class NetWorkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        HttpNetUtil.setConnected(context);
    }
}
