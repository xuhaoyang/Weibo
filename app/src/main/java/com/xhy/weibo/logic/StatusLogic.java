package com.xhy.weibo.logic;

import android.content.Context;
import android.util.Log;

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
                                     final String token, final String gid, final int type, final GetStatusListCallBack callBack) {
        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getStatusList(uid, page, token, gid, type);
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
     * 获得该用户收藏的微博列表 根据uid
     *
     * @param context
     * @param uid
     * @param page
     * @param token
     * @param callBack
     */
    public static void getKeepStatusListByUid(final Context context, final int uid, final int page,
                                              final String token, final GetKeepStatusByUidCallBack callBack) {
        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getKeepStatusListByUid(uid, page, token);
        resultCall.enqueue(new Callback<Result<List<Status>>>() {
            @Override
            public void onResponse(Call<Result<List<Status>>> call, Response<Result<List<Status>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Status>> listResult = response.body();
                    if (listResult.isSuccess()) {
                        callBack.onKeepStatusSuccess(listResult.getInfo(), listResult.getTotalPage());
                    } else {
                        callBack.onKeepStatusFailure(listResult.getMsg());
                    }
                } else {
                    callBack.onKeepStatusFailure("获取失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Status>>> call, Throwable t) {
                callBack.onKeepStatusError(t);
            }
        });
    }


    /**
     * 获得转发微博列表
     *
     * @param context
     * @param wid
     * @param page
     * @param token
     * @param callBack
     */
    public static void getTurnStatusList(final Context context, final int wid, final int page,
                                         final String token, final GetTurnStatusCallBack callBack) {

        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getTurnStatusList(wid, token, page);
        resultCall.enqueue(new Callback<Result<List<Status>>>() {
            @Override
            public void onResponse(Call<Result<List<Status>>> call, Response<Result<List<Status>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Status>> result = response.body();
                    if (result.isSuccess()) {
                        callBack.onTurnStatusListSuccess(result.getInfo(), result.getTotalPage());
                    } else {
                        callBack.onTurnStatusListFailure(result.getMsg());
                    }
                } else {
                    callBack.onTurnStatusListFailure("获取失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Status>>> call, Throwable t) {
                callBack.onTurnStatusListError(t);
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
                callBack.onSendError(t);
            }
        });
    }

    public static void getAtStatusList(final Context context, final int uid, final int page, final String token,
                                       final GetAtStatusListCallBack callBack) {

        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getAtStatusList(token, uid, page);
        resultCall.enqueue(new Callback<Result<List<Status>>>() {
            @Override
            public void onResponse(Call<Result<List<Status>>> call, Response<Result<List<Status>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Status>> result = response.body();
                    if (result.isSuccess()) {
                        callBack.onGetAtStatusSuccess(result.getInfo(), result.getTotalPage());
                    } else {
                        callBack.onGetAtStatusFailure(result.getMsg());
                    }
                } else {
                    callBack.onGetAtStatusFailure("获取失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Logger.show(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Status>>> call, Throwable t) {
                callBack.onGetAtStatusError(t);
            }
        });

    }

    /**
     * 转发微博
     *
     * @param uid
     * @param wid
     * @param tid
     * @param content
     * @param token
     * @param callBack
     */
    public static void turnWeibo(final int uid, final int wid, final int tid,
                                 final String content, final String token, final TurnWeiboCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().turnWeibo(uid, wid, tid, content, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    if (result.isSuccess()) {
                        callBack.onTurnSuccess(result);
                    } else {
                        callBack.onTurnFailure(result.getMsg());
                    }
                } else {
                    callBack.onTurnFailure("请求失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onTurnError(t);
            }
        });
    }

    public static void delWeibo(final int wid, final String token, final DelStatusCallback callback) {
        final Call<Result> resultCall = ApiClient.getApi().delWeibo(wid, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    if (result.isSuccess()) {
                        callback.onDelStatusSuccess(result.getMsg());
                    } else {
                        callback.onDelStatusFailure(result.getMsg());
                    }
                } else {
                    callback.onDelStatusFailure("请求失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callback.onDelStatusError(t);
            }
        });
    }

    public static void getSearchStatusList(final Context context, final String keyword, final int page,
                                           final String token, final GetSearchStatusListCallBack callBack) {
        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getSearchStatusList(token, keyword, page);
        resultCall.enqueue(new Callback<Result<List<Status>>>() {
            @Override
            public void onResponse(Call<Result<List<Status>>> call, Response<Result<List<Status>>> response) {
                if (response.isSuccessful()) {
                    Result<List<Status>> result = response.body();
                    if (result.isSuccess()) {
                        callBack.onGetSearchStatusSuccess(result.getInfo(), result.getTotalPage());
                    } else {
                        callBack.onGetSearchStatusFailure(result.getMsg());
                    }
                } else {
                    callBack.onGetSearchStatusFailure("请求失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<Status>>> call, Throwable t) {
                callBack.onGetSearchStatusError(t);
            }
        });
    }

    public interface SendWeiboCallBack {
        void onSendSuccess(Result result);

        void onSendFailure(String message);

        void onSendError(Throwable t);
    }

    public interface TurnWeiboCallBack {
        void onTurnSuccess(Result result);

        void onTurnFailure(String message);

        void onTurnError(Throwable t);
    }

    public interface GetStatusGroupCallBack {
        void onGroupSuccess(List<StatusGroup> statusGroups);

        void onGroupFailure(String message);

        void onGroupError(Throwable t);
    }

    public interface GetKeepStatusByUidCallBack {
        void onKeepStatusSuccess(List<Status> statuses, int totalPage);

        void onKeepStatusFailure(String message);

        void onKeepStatusError(Throwable t);
    }

    public interface GetTurnStatusCallBack {
        void onTurnStatusListSuccess(List<Status> statuses, int totalPage);

        void onTurnStatusListFailure(String message);

        void onTurnStatusListError(Throwable t);
    }

    public interface GetStatusListCallBack {
        void onStatusListSuccess(List<Status> statuses, int totalPage);

        void onStatusListFailure(String message);

        void onStatusListError(Throwable t);
    }

    public interface DelStatusCallback {
        void onDelStatusSuccess(String message);

        void onDelStatusFailure(String message);

        void onDelStatusError(Throwable t);

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

    public interface GetAtStatusListCallBack {
        void onGetAtStatusSuccess(List<Status> statuses, int totalPage);

        void onGetAtStatusFailure(String message);

        void onGetAtStatusError(Throwable t);
    }

    public interface GetSearchStatusListCallBack {
        void onGetSearchStatusSuccess(List<Status> statuses, int totalPage);

        void onGetSearchStatusFailure(String message);

        void onGetSearchStatusError(Throwable t);
    }

}
