package com.xhy.weibo.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.xhy.weibo.R;
import com.xhy.weibo.event.JPushEvent;
import com.xhy.weibo.model.JPushMessage;
import com.xhy.weibo.ui.activity.BrowserActivity;
import com.xhy.weibo.ui.activity.MainActivity;
import com.xhy.weibo.ui.activity.NotifyActivity;
import com.xhy.weibo.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;
import hk.xhy.android.common.utils.AppUtils;
import hk.xhy.android.common.utils.GsonUtil;
import hk.xhy.android.common.utils.LogUtils;

/**
 * Created by xuhaoyang on 2017/8/7.
 */

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = JPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, ">>>onReceive");
        Bundle bundle = intent.getExtras();
        LogUtils.d(TAG, "Action : " + intent.getAction());

        LogUtils.d("[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        JPushMessage jPushMessage = null;

        // 自定义消息或点击通知栏
        if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED) ||
                intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            String pushMsg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtils.d(TAG, GsonUtil.toJson(pushMsg));
            if (TextUtils.isEmpty(pushMsg)) {
                return;
            }
            jPushMessage = GsonUtil.parseJson(pushMsg, JPushMessage.class);
        }
        if (jPushMessage == null) {
            return;
        }

        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            if (AppUtils.isAppForeground()) {
                EventBus.getDefault().post(new JPushEvent(jPushMessage));
            } else {
                // 创建点击要进入的Activity
                Intent intent_ = new Intent(context, MainActivity.class);
                intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent_.putExtra(Constants.EXTRA_JPUSH_MESSAGE, GsonUtil.toJson(jPushMessage));
                context.startActivity(intent_);
            }
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED
                .equals(intent.getAction())) {// 收到自定义消息
//            if (AppUtils.isAppForeground()) {
//                EventBus.getDefault().post(new JPushEvent(jPushMessage));
//            } else {
//                String title = context.getString(R.string.app_name);
            String title = jPushMessage.getTitle();
            String content = jPushMessage.getMsg();
            if (TextUtils.isEmpty(content)) {
                return;
            }


            /**
             * 备注 如果要先跳 B ，需设定B的parentActivityName在AndroidManifest
             * 其次将B添加入栈，添加B的NextIntent
             */
            // 创建通知
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_my)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // 创建点击要进入的Activity
            Intent intent_ = new Intent(context, NotifyActivity.class);
            intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent_.putExtra(Constants.EXTRA_JPUSH_MESSAGE, GsonUtil.toJson(jPushMessage));
            stackBuilder.addParentStack(NotifyActivity.class);

            //显示通知
            stackBuilder.addNextIntent(intent_);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
//            }

            LogUtils.d("jPushMessage:" + GsonUtil.toJson(jPushMessage));
        }
    }

    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    LogUtils.i(TAG, "XHY new log:" + json.toString());
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
