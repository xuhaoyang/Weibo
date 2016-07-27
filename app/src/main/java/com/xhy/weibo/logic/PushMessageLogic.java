package com.xhy.weibo.logic;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/7/26.
 */
public class PushMessageLogic {

    public static final String TAG = PushMessageLogic.class.getSimpleName();

    public static void setMsg(final int uid, final int flush,
                              final String token, final SetMsgCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().setMsg(uid, flush, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    if (result.isSuccess()) {
                        callBack.onSetMsgSuccess(result);
                    } else {
                        callBack.onSetMsgFailure(result.getMsg());
                    }
                } else {
                    callBack.onSetMsgFailure("取消关注失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onSetMsgError(t);
            }
        });
    }

    public interface SetMsgCallBack {
        void onSetMsgSuccess(Result result);

        void onSetMsgFailure(String message);

        void onSetMsgError(Throwable t);
    }
}
