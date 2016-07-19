package com.xhy.weibo.api;

import com.xhy.weibo.entity.LoginReciver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public interface ApiClientImpl {

    @FormUrlEncoded
    @POST(URLs.WEIBO_USER_LOGIN)
    Call<LoginReciver> login(@Field("account") String account,
                             @Field("password") String password);

}
