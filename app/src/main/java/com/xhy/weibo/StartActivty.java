package com.xhy.weibo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;

import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.ui.activity.LoginActivity;
import com.xhy.weibo.ui.activity.MainActivity;
import com.xhy.weibo.ui.base.StartUpActivity;

import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.Logger;
import hk.xhy.android.commom.widget.Toaster;

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

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {

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
                    Toaster.showShort(StartActivty.this,errorMessage);
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
            finish();
        } else {
            ActivityUtils.goHome(StartActivty.this, LoginActivity.class);
        }

    }
}
