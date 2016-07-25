package com.xhy.weibo.logic;


import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

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


    public interface SetCommentCallBack {
        void onSetCommentSuccess(Result result);

        void onSetCommentFailure(String message);

        void onSetCommentError(Throwable t);
    }


}
