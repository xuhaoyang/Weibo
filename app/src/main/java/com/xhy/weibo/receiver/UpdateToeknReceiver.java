package com.xhy.weibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.utils.Logger;

/**
 * Created by xuhaoyang on 16/6/24.
 */
public class UpdateToeknReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.show(UpdateToeknReceiver.class.getName(), "更新Token广播被启动");


        String token = intent.getStringExtra(CommonConstants.KEEP_TOKEN);
        long tokenStartTime = intent.getLongExtra(CommonConstants.KEEP_TOKEN_START_TIME, 0);
        Logger.show(UpdateToeknReceiver.class.getName(), "-->获得Token:"+token);

        if (CommonConstants.ACCESS_TOKEN!=null){
            CommonConstants.ACCESS_TOKEN.setToken(token);
            CommonConstants.ACCESS_TOKEN.setTokenStartTime(tokenStartTime);
        }

    }
}
