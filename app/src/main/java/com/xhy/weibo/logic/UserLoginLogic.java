package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.User;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.utils.Logger;

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

    public static void getUserinfo(final Context context, int uid, String username,
                                   String token, final GetUserinfoCallBack callBack) {
        Call<Result<User>> callUserinfo = ApiClient.getApi().getUserinfo(uid, username, token);
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
