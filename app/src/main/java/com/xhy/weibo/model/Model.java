package com.xhy.weibo.model;

import android.text.TextUtils;

import com.bobomee.android.common.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by xuhaoyang on 16/9/7.
 */
public class Model {
    private final static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    public Model() {
        super();
    }

    public String toJSONString() {
        return gson.toJson(this);
    }

    public static <T extends Model> T parseObject(String json, Class<T> clazz) {
        return TextUtils.isEmpty(json) ? null : gson.fromJson(json, clazz);
    }

}
