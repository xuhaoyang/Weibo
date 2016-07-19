package com.xhy.weibo.api;

import com.google.gson.Gson;
import com.xhy.weibo.base.BaseApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public class ApiClient {
    private static final String TAG = ApiClient.class.getSimpleName();
    private static ApiClientImpl api;

    private static OkHttpClient mOkHttpClient;

    static {


        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
//        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        Cache cache = new Cache(.getContext().getCacheDir(), cacheSize);
//        builder.cache(cache);
//        builder.addInterceptor(interceptor);
        mOkHttpClient = builder.build();

        api = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(URLs.API_URL).client(mOkHttpClient).
                build().create(ApiClientImpl.class);
    }

    public static ApiClientImpl getApi() {
        return api;
    }

//    public static String getCookie() {
//        return AppConfig.getString("api_cookie", "");
//    }
//
//    public static void setCookie(String cookie) {
//        if (cookie == null) {
//            cookie = "";
//        }
//        AppConfig.putString("api_cookie", cookie);
//    }
}
