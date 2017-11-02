package com.xhy.weibo.api;

import android.text.TextUtils;

import com.xhy.weibo.BuildConfig;
import com.xhy.weibo.utils.HttpNetUtil;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import hk.xhy.android.common.utils.LogUtils;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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

    /**
     * 切换内网和外网的拦截器
     */
    static class RetryAndChangeIpInterceptor implements Interceptor {


//        private final FramePerfUtil mFramePer;
        //这里的url定义不是很规范,可以的话请自己定义一个集合之类的直接通过集合来传比较合适
        private static final String INNER_IP = BuildConfig.BASE_URL;
        private static final String COMMON_IP = BuildConfig.BASE_URL_OUT;
        private final int mRetryTimes;


        //retrytimes 重试次数
        RetryAndChangeIpInterceptor(int retryTimes) {
            mRetryTimes = retryTimes;
//            mFramePer = FramePerfUtil.getFramePer();
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            //这里做了url的判断来保存sp里面的内外网标识,访问2次成功就更改一下,成功就不进行操作
            Request request = chain.request();
            Response response = doRequest(chain, request);
            int tryCount = 0;
            String url = request.url().toString();
            while (response == null && tryCount < mRetryTimes) {
                if (url.contains(INNER_IP)) {
                    url = url.replace(INNER_IP, COMMON_IP);
//                    mFramePer.setIsInnerIP("false");
                } else {
                    url = url.replace(COMMON_IP, INNER_IP);
//                    mFramePer.setIsInnerIP("true");
                }
                Request newRequest = request.newBuilder().url(url).build();
                tryCount++;
                //这里在为空的response的时候进行请求
                response = doRequest(chain, newRequest);
            }
            if (response == null) {
                throw new IOException();
            }
            return response;
        }


        private Response doRequest(Chain chain, Request request) {
            Response response = null;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                LogUtils.e(e);
            }
            return response;
        }
    }


    static {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
//                现在尚未在服务端配合,会出现 504 Unsatisfiable Request (only-if-cached)错误
//                .addInterceptor(cacheInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .retryOnConnectionFailure(true)//连接失败重连
                .addInterceptor(new RetryAndChangeIpInterceptor(2))//失败两次换成外网服务器
                .retryOnConnectionFailure(true)//错误重连
                .connectTimeout(5, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(5, TimeUnit.SECONDS)//设置读取超时时间a
                .writeTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
//        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        Cache cache = new Cache(.getContext().getCacheDir(), cacheSize);
//        builder.cache(cache);
//        builder.addInterceptor(interceptor);
        mOkHttpClient = builder.build();

        api = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URLs.API_VERSION).client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(ApiClientImpl.class);
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
