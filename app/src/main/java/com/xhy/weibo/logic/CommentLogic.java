package com.xhy.weibo.logic;


import android.content.Context;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Comment;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/7/22.
 */
public class CommentLogic {

    private static final String TAG = CommentLogic.class.getSimpleName();

    /**
     * 发表评论
     *
     * @param uid      发布评论的用户ID
     * @param wid      被评论的微博ID
     * @param pid      父级微博的用户ID
     * @param content  评论内容
     * @param token
     * @param callBack
     */
    public static void setComment(final int uid, final int wid, final int pid,
                                  final String content, final String token, final SetCommentCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().setComment(uid, wid, pid, content, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onSetCommentSuccess(result);
                    } else {
                        callBack.onSetCommentFailure(result.getMsg());
                    }
                } else {
                    callBack.onSetCommentFailure("发送失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onSetCommentError(t);
            }
        });
    }

    /**
     * 获得单个微博数据
     *
     * @param context
     * @param wid
     * @param token
     * @param callBack
     */
    public static void getStatusOnlyCommentList(final Context context, final int wid,
                                                final String token, final GetCommentCallBack callBack) {
        Call<Result<List<Comment>>> resultCall = ApiClient.getApi().getStatusOnlyCommentList(token, wid);
        resultCall.enqueue(new Callback<Result<List<Comment>>>() {
            @Override
            public void onResponse(Call<Result<List<Comment>>> call, Response<Result<List<Comment>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Comment>> listResult = response.body();
                    if (listResult.isSuccess()) {
                        callBack.onGetCommentSuccess(listResult.getInfo(), listResult.getTotalPage());
                    } else {
                        callBack.onGetCommentFailure(listResult.getMsg());
                    }
                } else {
                    callBack.onGetCommentFailure("获取信息失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Comment>>> call, Throwable t) {
                callBack.onGetCommentError(t);
            }
        });
    }

    /**
     * 获取该用户所有有关评论信息
     *
     * @param context
     * @param uid
     * @param page
     * @param token
     * @param callBack
     */
    public static void getUserCommentList(final Context context, final int uid, final int page,
                                          final String token, final GetUserCommentListCallBack callBack) {
        Call<Result<List<Comment>>> resultCall = ApiClient.getApi().getUserCommentList(token, uid, page);
        resultCall.enqueue(new Callback<Result<List<Comment>>>() {
            @Override
            public void onResponse(Call<Result<List<Comment>>> call, Response<Result<List<Comment>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Comment>> listResult = response.body();
                    if (listResult.isSuccess()) {
                        callBack.onGetUserCommentSuccess(listResult.getInfo(), listResult.getTotalPage());
                    } else {
                        callBack.onGetUserCommentFailure(listResult.getMsg());
                    }
                } else {
                    callBack.onGetUserCommentFailure("获取信息失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Comment>>> call, Throwable t) {
                callBack.onGetUserCommentError(t);
            }
        });
    }


    public interface SetCommentCallBack {
        void onSetCommentSuccess(Result result);

        void onSetCommentFailure(String message);

        void onSetCommentError(Throwable t);
    }

    public interface GetCommentCallBack {
        void onGetCommentSuccess(List<Comment> comments, int totalPage);

        void onGetCommentFailure(String message);

        void onGetCommentError(Throwable t);
    }

    public interface GetUserCommentListCallBack {
        void onGetUserCommentSuccess(List<Comment> comments, int totalPage);

        void onGetUserCommentFailure(String message);

        void onGetUserCommentError(Throwable t);
    }

}
