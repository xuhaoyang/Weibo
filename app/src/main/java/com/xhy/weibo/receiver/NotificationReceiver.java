package com.xhy.weibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xhy.weibo.activity.InitActivity;
import com.xhy.weibo.activity.MainActivity;
import com.xhy.weibo.activity.NotifyActivity;
import com.xhy.weibo.utils.SystemUtils;

/**
 * Created by xuhaoyang on 16/6/5.
 */
public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //判断app进程是否存活
        if (SystemUtils.isAppAlive(context,"com.xhy.weibo")){
            Log.i("NotificationReceiver", "the app process is alive");
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent notifyIntent = new Intent(context, NotifyActivity.class);
            notifyIntent.putExtra(NotifyActivity.PUSH,true);
            Intent[] intents = {mainIntent, notifyIntent};
            context.startActivities(intents);
        }else {
            Log.i("NotificationReceiver", "the app process is dead");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("com.xhy.weibo");
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
        }


    }
}
