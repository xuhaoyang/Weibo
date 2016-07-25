package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.User;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public class UserLoginLogic {
    public static final String TAG = UserLoginLogic.class.getSimpleName();

    public static void login(final Context context, final String account, final String password, final LoginCallback callback) {
        Call<Result<Login>> callLogin = ApiClient.getApi().login(account, password);
        callLogin.enqueue(new Callback<Result<Login>>() {
            @Override
            public void onResponse(Call<Result<Login>> call, Response<Result<Login>> response) {
                if (response.isSuccessful()) {
                    Result<Login> loginResult = response.body();
                    if (loginResult.isSuccess()) {
                        Login login = loginResult.getInfo();
                        callback.onLoginSuccess(login);
                    } else {
                        callback.onLoginFailure(loginResult.getCode(), loginResult.getMsg());
                    }
                } else {
                    Logger.show(TAG, "ErrorCode:" + response.code());
                    callback.onLoginFailure(response.code(), "登录失败");
                }
            }

            @Override
            public void onFailure(Call<Result<Login>> call, Throwable t) {
                callback.onLoginError(t);
            }
        });

    }

    /**
     * 获取用户信息
     *
     * @param context
     * @param uid
     * @param username
     * @param userId
     * @param token
     * @param callBack
     */
    public static void getUserinfo(final Context context, int uid, String username, int userId,
                                   String token, final GetUserinfoCallBack callBack) {
        Call<Result<User>> callUserinfo = ApiClient.getApi().getUserinfo(uid, username, userId, token);
        callUserinfo.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                if (response.isSuccessful()) {
                    Result<User> body = response.body();
                    if (body.isSuccess()) {
                        callBack.onUserInfoSuccess(body.getInfo());
                    } else {
                        callBack.onUserInfoFailure(body.getCode(), body.getMsg());
                    }
                } else {
                    Logger.show(TAG, "ErrorCode:" + response.code());
                    callBack.onUserInfoFailure(response.code(), "获取失败");
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                callBack.onUserInfoError(t);
            }
        });

    }

    /**
     * 添加关注
     *
     * @param uid
     * @param follow
     * @param gid
     * @param token
     * @param callBack
     */
    public static void addFollow(final int uid, final int follow, final int gid,
                                 final String token, final AddFollowCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().addFollow(uid, follow, gid, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onAddFollowSuccess(result);
                    } else {
                        callBack.onAddFollowFailure(result.getMsg());
                    }
                } else {
                    callBack.onAddFollowFailure("关注失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onAddFollowError(t);
            }
        });
    }

    public static void delFollow(final int currentUid, final int beUid, final int type,
                                 final String token, final DelFollowCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().delFollow(currentUid, beUid, type, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onDelFollowSuccess(result);
                    } else {
                        callBack.onDelFollowFailure(result.getMsg());
                    }
                } else {
                    callBack.onDelFollowFailure("取消关注失败");
                    try {
                        Logger.show(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onDelFollowError(t);

            }
        });

    }

    public interface AddFollowCallBack {
        void onAddFollowSuccess(Result result);

        void onAddFollowFailure(String message);

        void onAddFollowError(Throwable t);
    }

    public interface DelFollowCallBack {
        void onDelFollowSuccess(Result result);

        void onDelFollowFailure(String message);

        void onDelFollowError(Throwable t);
    }


    public interface LoginCallback {
        void onLoginSuccess(Login login);

        void onLoginFailure(int errorCode, String errorMessage);

        void onLoginError(Throwable error);
    }

    public interface GetUserinfoCallBack {
        void onUserInfoSuccess(User user);

        void onUserInfoFailure(int errorCode, String errorMessage);

        void onUserInfoError(Throwable error);
    }


}
