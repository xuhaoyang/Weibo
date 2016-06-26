package com.xhy.weibo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.IMessageListener;
import com.xhy.weibo.IMessageServiceRemoteBinder;
import com.xhy.weibo.R;
import com.xhy.weibo.activity.MainActivity;
import com.xhy.weibo.constants.AccessToken;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.NotifyInfo;
import com.xhy.weibo.entity.NotifyReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.receiver.NotificationReceiver;
import com.xhy.weibo.utils.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageService extends Service {

    //获取消息线程
    private MessageThread messageThread = null;

    //点击查看
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;

    //通知栏消息
    private int messageNotificationID = 1000;
    private Notification messageNotification = null;
    private NotificationManager messageNotificatioManager = null;

    //通知栏
    private NotificationCompat.Builder builder;
    private TaskStackBuilder stackBuilder;

    private NotifyInfo oldInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    final Bundle data = msg.getData();
                    final NotifyInfo info = (NotifyInfo) data.getSerializable("NOTIFYINFO");
                    if (!info.equals(oldInfo)) {
                        builder.setTicker("您有新的消息!");
                        StringBuilder sb = new StringBuilder();
                        sb.append("您有");
                        if (info.getComment() != 0) {
                            sb.append(info.getComment());
                            sb.append("条评论");
                        }
                        if (info.getAtme() != 0) {
                            if (info.getComment() != 0) {
                                sb.append(",");
                            }
                            sb.append(info.getAtme());
                            sb.append("条@消息");
                        }
                        builder.setContentTitle(sb.toString());
                        builder.setContentText("点击打开");
                        builder.setWhen(System.currentTimeMillis());
                        Notification notification = builder.build();
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manager.notify(messageNotificationID, notification);
                    }
                    oldInfo = info;
                    break;
            }
        }
    };

    private IMessageListener mListener;

    @Override
    public IBinder onBind(Intent intent) {
//        return new Binder();
        return new IMessageServiceRemoteBinder.Stub() {


            @Override
            public void setAccount(String account) throws RemoteException {
                CommonConstants.account = account;
            }

            @Override
            public void setIsNotify(boolean flag) throws RemoteException {
                CommonConstants.isNotify = flag;
            }

            @Override
            public void setMessageListener(IMessageListener listener) throws RemoteException {
                mListener = listener;
            }

        };
    }


    public class Binder extends android.os.Binder {
        public void setAccount(String account) {
            CommonConstants.account = account;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CommonConstants.account = intent.getStringExtra("ACCOUNT");
        CommonConstants.password = intent.getStringExtra("PASSWORD");
        CommonConstants.USER_ID = Integer.parseInt(intent.getStringExtra("USERID"));
        String token = intent.getStringExtra("TOKEN");
        if (!TextUtils.isEmpty(CommonConstants.account) || !TextUtils.isEmpty(CommonConstants.password)) {
            CommonConstants.ACCESS_TOKEN = AccessToken.getInstance(CommonConstants.account, CommonConstants.password, getApplicationContext());
            CommonConstants.ACCESS_TOKEN.setToken(token);
        }

        builder = new NotificationCompat.Builder(this);
        //跳转到Activity
        messageIntent = new Intent(this, NotificationReceiver.class);
//        stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(messageIntent);
//        messagePendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        messagePendingIntent = PendingIntent.getBroadcast(this, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(messagePendingIntent);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(true);

        //振动级别
        builder.setVibrate(new long[]{0, 300, 500, 700});
        builder.setLights(0xff0000ff, 300, 300);

        //设置优先级
        builder.setPriority(Notification.PRIORITY_MAX);

        //振动级别
        builder.setVibrate(new long[]{0, 300, 500, 700});
        builder.setLights(0xff0000ff, 300, 300);

        //开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();

//        return super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    /**
     * 从服务器端获取消息
     */
    class MessageThread extends Thread {
        //运行状态，下一步骤有大用
        public boolean isRunning = true;

        public void run() {
            while (isRunning) {
                try {
                    Logger.show("MessageService", "进入了Thread");
                    //休息
                    Thread.sleep(15000);
                    Logger.show("MessageService", "休息时间过了,运行了");
                    if (CommonConstants.isNotify) {
//                        try {
//                            if (mListener != null) {
//                                mListener.setAccessToken(accessToken.getToken(), accessToken.getTokenStartTime());
//                            }
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
                        Intent data = new Intent();
                        data.setAction("com.xhy.weibo.UPDATE_TOKEN");
                        data.putExtra(CommonConstants.KEEP_TOKEN, CommonConstants.ACCESS_TOKEN.getToken());
                        data.putExtra(CommonConstants.KEEP_TOKEN_START_TIME, CommonConstants.ACCESS_TOKEN.getTokenStartTime());
                        //传递账户id比对是否是帐号
                        data.putExtra(CommonConstants.KEEP_TOKEN_ACCOUNT,CommonConstants.account);
                        data.putExtra(CommonConstants.KEEP_TOKEN_USER_ID,CommonConstants.account);
                        sendBroadcast(data);

                        GsonRequest<NotifyReciver> request = new GsonRequest<NotifyReciver>(Request.Method.POST,
                                URLs.WEIBO_GET_MSG, NotifyReciver.class, null, new Response.Listener<NotifyReciver>() {
                            @Override
                            public void onResponse(NotifyReciver response) {
                                if (response.getCode() == 200) {
                                    NotifyInfo info = response.getInfo();

                                    Message message = new Message();
                                    message.what = 0;
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("NOTIFYINFO", info);
                                    message.setData(bundle);
                                    mHandler.sendMessage(message);
                                } else {

                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                                map.put("uid", String.valueOf(CommonConstants.USER_ID));
                                return map;
                            }
                        };
                        VolleyQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
