package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.AppConfig;
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
import retrofit2.http.Field;

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
                                     final String token, final String gid, final int type, final GetStatusListCallBack callBack) {
        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getStatusList(uid, page, token, gid, type);
        Logger.show(TAG, "uid:" + uid + ",page" + page + ",gid" + gid + "type" + type);
        resultCall.enqueue(new Callback<Result<List<Status>>>() {
            @Override
            public void onResponse(Call<Result<List<Status>>> call, Response<Result<List<Status>>> response) {

                Result<List<Status>> listResult = response.body();
                if (response.isSuccessful()) {
                    if (listResult.isSuccess()) {
                        callBack.onStatusListSuccess(listResult.getInfo(), listResult.getTotalPage());
                    } else {
                        callBack.onStatusListFailure(listResult.getMsg());
                    }
                } else {
                    callBack.onStatusListFailure("获取失败");
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

    /**
     * 微博添加收藏
     *
     * @param context
     * @param uid
     * @param wid
     * @param token
     * @param callBack
     */
    public static void addKeepStatus(final Context context, final int uid, final int wid,
                                     final String token, final AddKeepStatusCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().addKeepStatus(uid, wid, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onAddKeepSuccess(result);
                    } else {
                        callBack.onAddKeepFailure(result.getMsg());
                    }
                } else {
                    callBack.onAddKeepFailure("收藏失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onAddKeepError(t);
            }
        });
    }

    /**
     * 微博取消收藏
     *
     * @param context
     * @param uid
     * @param wid
     * @param token
     * @param callBack
     */
    public static void delKeepStatus(final Context context, final int uid, final int wid,
                                     final String token, final DelKeepStatusCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().delKeepStatus(uid, wid, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onDelKeepSuccess(result);
                    } else {
                        callBack.onDelKeepFailure(result.getMsg());
                    }
                } else {
                    callBack.onDelKeepFailure("取消收藏失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onDelKeepError(t);
            }
        });
    }

    /**
     * 发送微博
     *
     * @param uid       用户id
     * @param content
     * @param token
     * @param picMini
     * @param picMedium
     * @param picMax
     */
    public static void sendWeibo(int uid, String content, String token,
                                 String picMini, String picMedium, String picMax, final SendWeiboCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().sendWeibo(uid, content, token, picMini, picMedium, picMax);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onSendSuccess(result);
                    } else {
                        callBack.onSendFailure(result.getMsg());
                    }
                } else {
                    callBack.onSendFailure("发送失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public interface SendWeiboCallBack {
        void onSendSuccess(Result result);

        void onSendFailure(String message);

        void onSendError(Throwable t);
    }

    public interface GetStatusGroupCallBack {
        void onGroupSuccess(List<StatusGroup> statusGroups);

        void onGroupFailure(String message);

        void onGroupError(Throwable t);
    }

    public interface GetStatusListCallBack {
        void onStatusListSuccess(List<Status> statuses, int totalPage);

        void onStatusListFailure(String message);

        void onStatusListError(Throwable t);
    }

    public interface AddKeepStatusCallBack {
        void onAddKeepSuccess(Result result);

        void onAddKeepFailure(String message);

        void onAddKeepError(Throwable t);
    }

    public interface DelKeepStatusCallBack {
        void onDelKeepSuccess(Result result);

        void onDelKeepFailure(String message);

        void onDelKeepError(Throwable t);
    }

}
