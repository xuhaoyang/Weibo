package com.xhy.weibo.logic;

import android.content.Context;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.entity.Login;
import com.xhy.weibo.entity.LoginReciver;
import com.xhy.weibo.entity.User;
import com.xhy.weibo.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public class UserLogic {
    public static final String TAG = UserLogic.class.getSimpleName();

    public static void login(final Context context, final String account, final String password, final LoginCallback callback) {
        Call<LoginReciver> callLogin = ApiClient.getApi().login(account, password);
        callLogin.enqueue(new Callback<LoginReciver>() {
            @Override
            public void onResponse(Call<LoginReciver> call, Response<LoginReciver> response) {
                if (response.isSuccessful()) {
                    LoginReciver loginReciver = response.body();
                    if (loginReciver.getCode() == 200) {
                        Login login = loginReciver.getInfo();
                        callback.onLoginSuccess(login);
                    } else {
                        callback.onLoginFailure(loginReciver.getCode(), "登录失败");
                    }
                } else {
                    Logger.show(TAG, "ErrorCode:" + response.code());
                    callback.onLoginFailure(response.code(), "登录失败");
                }
            }

            @Override
            public void onFailure(Call<LoginReciver> call, Throwable t) {
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
