package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.model.StatusGroup;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/7/20.
 */
public class StatusLogic {

    public static final String TAG = StatusLogic.class.getSimpleName();

    /**
     * 获得微博分组
     *
     * @param context
     * @param uid
     * @param token
     * @param callBack
     */
    public static void getStatusGroup(final Context context, final int uid,
                                      final String token, final GetStatusGroupCallBack callBack) {
        Call<Result<List<StatusGroup>>> resultCall = ApiClient.getApi().getStatusGroup(uid, token);
        resultCall.enqueue(new Callback<Result<List<StatusGroup>>>() {
            @Override
            public void onResponse(Call<Result<List<StatusGroup>>> call, Response<Result<List<StatusGroup>>> response) {
                Result<List<StatusGroup>> result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onGroupSuccess(result.getInfo());
                    } else {
                        callBack.onGroupFailure(result.getMsg());
                    }
                } else {
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<Result<List<StatusGroup>>> call, Throwable t) {
                callBack.onGroupError(t);
            }
        });
    }

    /**
     * 获得微博列表数据
     *
     * @param context
     * @param uid
     * @param page
     * @param token
     * @param gid
     * @param type
     * @param callBack
     */
    public static void getStatusList(final Context context, final int uid, final int page,
                                     final String token, final String gid, final int type, final GetStatusList callBack) {
        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getStatusList(uid, page, token, gid, type);
        Logger.show(TAG, "uid:" + uid + ",page" + page + ",gid" + gid + "type" + type);
        resultCall.enqueue(new Callback<Result<List<Status>>>() {
            @Override
            public void onResponse(Call<Result<List<Status>>> call, Response<Result<List<Status>>> response) {

                Result<List<Status>> listResult = response.body();
                if (response.isSuccessful()) {
                    if (listResult.isSuccess()) {
                        callBack.onStatusListSuccecc(listResult.getInfo(), listResult.getTotalPage());
                    } else {
                        callBack.onStatusListFailure(listResult.getMsg());
                    }
                } else {
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Result<List<Status>>> call, Throwable t) {
                callBack.onStatusListError(t);
            }
        });
    }


    public interface GetStatusGroupCallBack {
        void onGroupSuccess(List<StatusGroup> statusGroups);

        void onGroupFailure(String message);

        void onGroupError(Throwable t);
    }

    public interface GetStatusList {
        void onStatusListSuccecc(List<Status> statuses, int totalPage);

        void onStatusListFailure(String message);

        void onStatusListError(Throwable t);
    }

}
