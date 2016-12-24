package com.xhy.weibo.api;

import android.text.TextUtils;

import com.xhy.weibo.utils.HttpNetUtil;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public class ApiClient {
    private static final String TAG = ApiClient.class.getSimpleName();
    private static ApiClientImpl api;

    private static OkHttpClient mOkHttpClient;

    private static final Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (!HttpNetUtil.isConnected()) {
                request = request.newBuilder()
                        //强制使用缓存
                        .cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);

            if (HttpNetUtil.isConnected()) {
                String cacheControl = request.cacheControl().toString();
                return response.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .addHeader("Accept", "application/json")
                        .removeHeader("Pragma")//移除干扰信息
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }

        }
    };

    static {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .addInterceptor(cacheInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .retryOnConnectionFailure(true)//错误重连
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
//        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        Cache cache = new Cache(.getContext().getCacheDir(), cacheSize);
//        builder.cache(cache);
//        builder.addInterceptor(interceptor);
        mOkHttpClient = builder.build();

        api = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URLs.API_VERSION).client(mOkHttpClient).
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
