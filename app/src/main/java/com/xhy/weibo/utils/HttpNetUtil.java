package com.xhy.weibo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xhy.weibo.receiver.NetWorkReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaoyang on 16/7/20.
 */
public class HttpNetUtil {

    private static List<Networkreceiver> networkreceivers;

    public interface Networkreceiver {

        void onConnected(boolean collect);
    }

    public static void addNetWorkListener(Networkreceiver networkreceiver) {
        if (null == networkreceivers) {
            networkreceivers = new ArrayList<>();
        }
        networkreceivers.add(networkreceiver);
    }

    public static void removeNetWorkListener(NetWorkReceiver listener) {
        if (networkreceivers != null) {
            networkreceivers.remove(listener);
        }
    }

    public static void clearNetWorkListeners() {
        if (networkreceivers != null) {
            networkreceivers.clear();
        }
    }


    private static boolean isConnected = true;

    /**
     * 获取是否连接
     */
    public static boolean isConnected() {
        return isConnected;
    }

    private static void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * 判断网络连接是否存在
     *
     * @param context
     */
    public static void setConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            setConnected(false);


            if (networkreceivers != null) {
                for (int i = 0, z = networkreceivers.size(); i < z; i++) {
                    Networkreceiver listener = networkreceivers.get(i);
                    if (listener != null) {
                        listener.onConnected(false);
                    }
                }
            }

        }

        NetworkInfo info = manager.getActiveNetworkInfo();

        boolean connected = info != null && info.isConnected();
        setConnected(connected);

        if (networkreceivers != null) {
            for (int i = 0, z = networkreceivers.size(); i < z; i++) {
                Networkreceiver listener = networkreceivers.get(i);
                if (listener != null) {
                    listener.onConnected(connected);
                }
            }
        }

    }
}
