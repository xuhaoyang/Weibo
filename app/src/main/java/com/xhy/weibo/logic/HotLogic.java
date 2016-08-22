package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Hot;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/8/4.
 */
public class HotLogic {
    public static final String TAG = HotLogic.class.getSimpleName();


    /**
     * @param context
     * @param token
     * @param callBack
     */
    public static void getHotList(final Context context, final String token,
                                  final GetHotListCallBack callBack) {
        Call<Result<List<Hot>>> resultCall = ApiClient.getApi().getHotList(token);
        resultCall.enqueue(new Callback<Result<List<Hot>>>() {
            @Override
            public void onResponse(Call<Result<List<Hot>>> call, Response<Result<List<Hot>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Hot>> result = response.body();
                    if (result.isSuccess()) {
                        callBack.onGetSuccess(result.getInfo());
                    } else {
                        callBack.onGetFailure(result.getMsg());
                    }
                } else {
                    callBack.onGetFailure("获取失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Hot>>> call, Throwable t) {
                callBack.onGetError(t);
            }
        });

    }

    public interface GetHotListCallBack {
        void onGetSuccess(List<Hot> hots);

        void onGetFailure(String message);

        void onGetError(Throwable t);
    }
}
