package com.xhy.weibo.api;

import com.xhy.weibo.model.Comment;
import com.xhy.weibo.model.Hot;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.NotifyInfo;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.model.StatusGroup;
import com.xhy.weibo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public interface ApiClientImpl {

    @FormUrlEncoded
    @POST(URLs.WEIBO_USER_LOGIN)
    Call<Result<Login>> login(@Field("account") String account,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_USERINFO)
    Call<Result<User>> getUserinfo(@Field("uid") int uid,
                                   @Field("username") String username,
                                   @Field("userid") int userId,
                                   @Field("token") String token);

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
                                             @Field("type") int type);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_KEEP_LIST)
    Call<Result<List<Status>>> getKeepStatusListByUid(@Field("uid") int uid,
                                                      @Field("page") int page,
                                                      @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_ADD_KEEP)
    Call<Result> addKeepStatus(@Field("uid") int uid,
                               @Field("wid") int wid,
                               @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_DEL_KEEP)
    Call<Result> delKeepStatus(@Field("uid") int uid,
                               @Field("wid") int wid,
                               @Field("token") String token);


    @FormUrlEncoded
    @POST(URLs.WEIBO_SET_COMMENT)
    Call<Result> setComment(@Field("uid") int uid,
                            @Field("wid") int wid,
                            @Field("pwid") int pid,
                            @Field("content") String content,
                            @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_SEND_WEIBO)
    Call<Result> sendWeibo(@Field("uid") int uid,
                           @Field("content") String content,
                           @Field("token") String token,
                           @Field("mini") String picMini,
                           @Field("medium") String picMedium,
                           @Field("max") String picMax);

    @FormUrlEncoded
    @POST(URLs.WEIBO_TURN_WEIBO)
    Call<Result> turnWeibo(@Field("uid") int uid,
                           @Field("wid") int wid,
                           @Field("tid") int tid,
                           @Field("content") String content,
                           @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_DELETE_WEIBO)
    Call<Result> delWeibo(@Query("wid") int wid,
                           @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_ADD_FOLLOW)
    Call<Result> addFollow(@Field("uid") int uid,
                           @Field("follow") int follow,
                           @Field("gid") int gid,
                           @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_DEL_FOLLOW)
    Call<Result> delFollow(@Field("current_uid") int current_uid,
                           @Field("be_uid") int be_uid,
                           @Field("type") int type,
                           @Field("token") String token);


    @FormUrlEncoded
    @POST(URLs.WEIBO_SET_MSG)
    Call<Result> setMsg(@Field("uid") int uid,
                        @Field("flush") int flush,
                        @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_USER_FOLLOW_FANS_LIST)
    Call<Result<List<User>>> getUserFollowList(@Field("uid") int uid,
                                               @Field("page") int page,
                                               @Field("keyword") String keyword,
                                               @Field("type") int type,
                                               @Field("token") String token);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_HOTS)
    Call<Result<List<Hot>>> getHotList(@Field("token") String token);


    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_STATUS_ONLY_COMMENT_LIST)
    Call<Result<List<Comment>>> getStatusOnlyCommentList(@Field("token") String token,
                                                         @Query("wid") int wid);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_COMMENT_LIST)
    Call<Result<List<Comment>>> getUserCommentList(@Field("token") String token,
                                                   @Query("uid") int uid,
                                                   @Query("page") int page);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_TURN_LIST)
    Call<Result<List<Status>>> getTurnStatusList(@Query("wid") int wid,
                                                 @Field("token") String token,
                                                 @Query("page") int page);

    @FormUrlEncoded
    @POST(URLs.WEIBO_ATM_LIST)
    Call<Result<List<Status>>> getAtStatusList(@Field("token") String token,
                                               @Field("uid") int uid,
                                               @Query("page") int page);

    @FormUrlEncoded
    @POST(URLs.WEIBO_SEARCH_LIST)
    Call<Result<List<Status>>> getSearchStatusList(@Field("token") String token,
                                                   @Query("keyword") String keyword,
                                                   @Query("page") int page);

    @FormUrlEncoded
    @POST(URLs.WEIBO_USER_SEARCH_LIST)
    Call<Result<List<User>>> getSearchUserList(@Field("token") String token,
                                               @Query("keyword") String keyword,
                                               @Query("uid") int uid,
                                               @Query("page") int page);

    @FormUrlEncoded
    @POST(URLs.WEIBO_GET_MSG)
    Call<Result<NotifyInfo>> getPushMsg(@Field("token") String token,
                                        @Query("uid") int uid);


}
