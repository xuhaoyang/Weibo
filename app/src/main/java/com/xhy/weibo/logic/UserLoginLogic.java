package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.api.ApiClient;
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

    public interface LoginCallback {
        void onLoginSuccess(Login login);

        void onLoginFailure(int errorCode, String errorMessage);

        void onLoginError(Throwable error);
    }
}
