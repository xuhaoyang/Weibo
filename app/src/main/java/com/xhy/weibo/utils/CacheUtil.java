package com.xhy.weibo.utils;

import com.bobomee.android.common.util.StorageUtil;
import com.bobomee.android.common.util.UIUtil;

import java.io.File;

import okhttp3.Cache;

/**
 * Created by xuhaoyang on 16/7/20.
 */
public class CacheUtil {
    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

    private static File getCacheDir() {
        //设置缓存路径
        final File baseDir = StorageUtil.getCacheDir(UIUtil.getContext());
        final File cacheDir = new File(baseDir, "HttpResponseCache");
        return cacheDir;
    }

    public static Cache getCache() {
        return new Cache(getCacheDir(), HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }
}
