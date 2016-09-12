package com.xhy.weibo.model;

import android.text.TextUtils;

import hk.xhy.android.commom.utils.GsonUtil;

/**
 * Created by xuhaoyang on 16/9/7.
 */
public class Model {


    public Model() {
        super();
    }

    public String toJSONString() {
        return GsonUtil.toJson(this);
    }

    public static <T extends Model> T parseObject(String json, Class<T> clazz) {
        return TextUtils.isEmpty(json) ? null : GsonUtil.parseJson(json, clazz);
    }

}
