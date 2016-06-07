package com.xhy.weibo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xuhaoyang on 16/6/7.
 */
public class SharedUtils {
    private SharedPreferences preferences;
    private Context mContext;
    private String Path;

    public SharedUtils(Context mContext, String path) {
        this.mContext = mContext;
        Path = path;
        preferences = mContext.getSharedPreferences(path, Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }
}
