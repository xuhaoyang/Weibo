package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.NotifyInfo;
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

    /**
     * 清理消息
     *
     * @param uid
     * @param flush
     * @param token
     * @param callBack
     */
    public static void setMsg(final Context context, final int uid, final int flush,
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
                    callBack.onSetMsgFailure(context.getString(R.string.push_msg_clear_fail));
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

    /**
     * 获得消息
     *
     * @param context
     * @param uid
     * @param token
     * @param callBack
     */
    public static void getMsg(final Context context, final int uid, final String token,
                              final GetMsgCallBack callBack) {
        Call<Result<NotifyInfo>> resultCall = ApiClient.getApi().getPushMsg(token, uid);
        resultCall.enqueue(new Callback<Result<NotifyInfo>>() {
            @Override
            public void onResponse(Call<Result<NotifyInfo>> call, Response<Result<NotifyInfo>> response) {
                if (response.isSuccessful()) {
                    Result<NotifyInfo> result = response.body();
                    if (result.isSuccess()) {
                        callBack.onGetMsgSuccess(result.getInfo());
                    } else {
                        callBack.onGetMsgFailure(result.getMsg());
                    }
                } else {
                    callBack.onGetMsgFailure(context.getString(R.string.push_msg_get_fail));
                    try {
                        Logger.show(TAG,response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<NotifyInfo>> call, Throwable t) {
                callBack.onGetMsgError(t);
            }
        });
    }


    public interface SetMsgCallBack {
        void onSetMsgSuccess(Result result);

        void onSetMsgFailure(String message);

        void onSetMsgError(Throwable t);
    }

    public interface GetMsgCallBack {
        void onGetMsgSuccess(NotifyInfo info);

        void onGetMsgFailure(String message);

        void onGetMsgError(Throwable t);
    }
}
