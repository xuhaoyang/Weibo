package com.xhy.weibo.api;

import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.model.StatusGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public interface ApiClientImpl {

    @FormUrlEncoded
    @POST(URLs.WEIBO_USER_LOGIN)
    Call<Result<Login>> login(@Field("account") String account,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_GROUP)
    Call<Result<List<StatusGroup>>> getStatusGroup(@Field("uid") int uid,
                                                   @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_LIST)
    Call<Result<List<Status>>> getStatusList(@Field("uid") int uid,
                                             @Field("page") int page,
                                             @Field("token") String token,
                                             @Field("gid") String gid,
                                             @Field("type")int type);

}
