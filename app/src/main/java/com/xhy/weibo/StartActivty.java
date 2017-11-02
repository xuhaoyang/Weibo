package com.xhy.weibo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;

import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.ui.activity.LoginActivity;
import com.xhy.weibo.ui.activity.MainActivity;
import com.xhy.weibo.ui.base.StartUpActivity;
import com.xhy.weibo.utils.Utils;

import cn.jpush.android.api.JPushInterface;
import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.android.common.utils.LogUtils;
import hk.xhy.android.common.utils.Logger;
import hk.xhy.android.common.utils.ServiceUtils;
import hk.xhy.android.common.widget.Toaster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivty extends StartUpActivity {

    public static final String TAG = StartActivty.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }


    @Override
    public void onAnimationStart(Animation animation) {
        final String account = AppConfig.getAccount();
        final String password = AppConfig.getPassword();

        final String registrationID = JPushInterface.getRegistrationID(getApplicationContext());


        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {


            Call<Result> resultCall = ApiClient.getApi().setRegistrationID(registrationID, AppConfig.getAccessToken().getToken());
            resultCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()) {
                        LogUtils.d(response.body().getMsg());
                    } else {
                        LogUtils.w("ErrorCode:" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    LogUtils.w(t);
                }
            });

            UserLoginLogic.login(this, account, password, new UserLoginLogic.LoginCallback() {
                @Override
                public void onLoginSuccess(Login login) {
                    login.setPassword(password);
                    Login.setCurrentLoginUser(login);


                    //获得TOKEN 同时更新该用户UID
                    AppConfig.getAccessToken();
                }

                @Override
                public void onLoginFailure(int errorCode, String errorMessage) {
                    Toaster.showShort(StartActivty.this, errorMessage);
                    Logger.show(TAG, String.format("Code:%d", errorCode) + ":" + errorMessage, Log.WARN);
                    ActivityUtils.startActivity(StartActivty.this, LoginActivity.class);

                }

                @Override
                public void onLoginError(Throwable error) {
                    Toaster.showShort(StartActivty.this, "网络故障");
                    Logger.show(
                            TAG, error.getLocalizedMessage(), Log.ERROR);
                    ActivityUtils.appExit(StartActivty.this);
                }
            });


        } else {
            //当前用户信息设置为空
            Login.setCurrentLoginUser(null);

        }


    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Login currentLoginUser = Login.getCurrentLoginUser();
        if (currentLoginUser != null) {
            ActivityUtils.goHome(StartActivty.this, MainActivity.class);
            LogUtils.d("是否开启推送: "+AppConfig.isNotify());
            //推送管理
            Utils.switchPushService(AppConfig.getNotifyMode(),AppConfig.isNotify());
            //TODO 是否要管理已经登录状态下的REGID?
            finish();
        } else {
            ActivityUtils.goHome(StartActivty.this, LoginActivity.class);
        }

    }
}
