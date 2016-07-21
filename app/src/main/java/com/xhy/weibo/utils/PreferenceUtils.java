package com.xhy.weibo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;

/**
 * Created by xuhaoyang on 16/7/21.
 */
public class PreferenceUtils {
    private static final String TAG = PreferenceUtils.class.getSimpleName();

    private static boolean sIsInitialized;
    private static Context sContext;

    public static synchronized void initialize(Context context) {
        if (sIsInitialized) {
            android.util.Log.v(TAG, "PreferenceUtils already initialized.");
            return;
        }
        sContext = context;
        sIsInitialized = true;

        android.util.Log.v(TAG, "PreferenceUtils initialized successfully.");
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    public static Map<String, ?> getAll() {
        return getSharedPreferences().getAll();
    }

    public static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(String key, Set<String> defValues) {
        return getSharedPreferences().getStringSet(key, defValues);
    }

    public static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    public static boolean contains(String key) {
        return getSharedPreferences().contains(key);
    }

    public static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void putStringSet(String key, Set<String> values) {
        getSharedPreferences().edit().putStringSet(key, values).commit();
    }

    public static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).commit();
    }

    public static void putLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).commit();
    }

    public static void putFloat(String key, float value) {
        getSharedPreferences().edit().putFloat(key, value).commit();
    }

    public static void putBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public static void remove(String key) {
        getSharedPreferences().edit().remove(key).commit();
    }

    public static void clear() {
        getSharedPreferences().edit().clear().commit();
    }

    public static Context getContext() {
        return sContext;
    }
}
