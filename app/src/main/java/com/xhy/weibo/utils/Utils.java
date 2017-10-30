package com.xhy.weibo.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xhy.weibo.ui.base.BaseApplication;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import hk.xhy.android.common.utils.ServiceUtils;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class Utils {

    /**
     * 只关注是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 提供自带的推送服务intent
     *
     * @return
     */
    public static Intent getPushServiceIntent() {
        return getExplicitIntent(hk.xhy.android.common.utils.Utils.getContext(), new Intent("com.xhy.weibo.intent.action.MessageService"));
    }

    /**
     * 隐式启动Service 5.0之后需要转成显式
     *
     * @param context
     * @param implicitIntent
     * @return
     */
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * 推送服务切换 关闭
     * @param value 0自带/1极光推送
     * @param on_off
     */
    public static void switchPushService(int value, boolean on_off) {
        if (on_off) {
            switch (value){
                case 0:
                    //自带

                    //关闭极光推送
                    if (!JPushInterface.isPushStopped(getContext().getApplicationContext())) {
                        JPushInterface.stopPush(getContext().getApplicationContext());
                    }
                    //开启自带推送
//                    getContext().startService(Utils.getPushServiceIntent());
                    ServiceUtils.startService(Constants.SERVICE_MESSAGE);
                    break;
                case 1:
                    //极光
                    //关闭另一个推送
//                    getmActivity().stopService(Utils.getPushServiceIntent());
                    ServiceUtils.stopService(Constants.SERVICE_MESSAGE);

                    //开启极光推送
                    if (JPushInterface.isPushStopped(getContext().getApplicationContext())) {
                        JPushInterface.resumePush(getContext().getApplicationContext());
                    }
                    break;

            }
        } else {
            if (ServiceUtils.isServiceRunning(Constants.SERVICE_MESSAGE)) {
                ServiceUtils.stopService(Constants.SERVICE_MESSAGE);
            }
            if (!JPushInterface.isPushStopped(getContext().getApplicationContext())) {
                JPushInterface.stopPush(getContext().getApplicationContext());
            }
        }
    }

    /**
     * 获取Context
     * @return
     */
    public static Context getContext() {
        return hk.xhy.android.common.utils.Utils.getContext();
    }

}
