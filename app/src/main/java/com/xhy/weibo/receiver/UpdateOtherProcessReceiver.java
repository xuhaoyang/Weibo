package com.xhy.weibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xhy.weibo.constants.CommonConstants;

/**
 * Created by xuhaoyang on 16/6/26.
 */
public class UpdateOtherProcessReceiver extends BroadcastReceiver {

    public static final String ACTION_NAME = "com.xhy.weibo.UPDATE_OTHER_PROCESS";

    @Override
    public void onReceive(Context context, Intent intent) {

        CommonConstants.isNotify = intent.getBooleanExtra(CommonConstants.KEEP_SETTING_ISNOTIFY, false);

    }
}
