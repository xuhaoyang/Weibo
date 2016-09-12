package com.xhy.weibo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;

import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.ui.activity.LoginActivity;
import com.xhy.weibo.ui.activity.MainActivity;
import com.xhy.weibo.ui.base.StartUpActivity;

import java.util.List;

import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.Logger;
import hk.xhy.android.commom.widget.Toaster;

public class StartActivty extends StartUpActivity {

    public static final String TAG = StartActivty.class.getSimpleName();

    private static final int TIME = 1100;
    private static final int GO_MAIN = 1000;
    private static final int GO_LOGIN = 1001;

    private DBManager dbManager;
    private SQLiteDatabase db;
    private Login login;
    private UserDB userDB;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAIN:
                    ActivityUtils.goHome(StartActivty.this, MainActivity.class);
                    finish();
                    break;
                case GO_LOGIN:
                    ActivityUtils.startActivity(StartActivty.this, LoginActivity.class);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    private void init() {

        initDB();

        String selection = "used=?";
        String[] selectionArgs = new String[]{"1"};

        List<Login> logins = userDB.QueryLogin(db, selection, selectionArgs);
        if (!logins.isEmpty()) {
            login = logins.get(0);
            AppConfig.setAccount(login.getAccount());
            AppConfig.setPassword(login.getPassword());
            AppConfig.setUserId(login.getId());
            AppConfig.getAccessToken();
            mHandler.sendEmptyMessageDelayed(GO_MAIN, TIME);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
        }
        dbManager.closeDatabase();

    }

    private void initDB() {
        //查看是否有使用帐号
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        db = dbManager.getDatabase();
        userDB = new UserDB(this);
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
                    Logger.show(TAG, error.getLocalizedMessage(), Log.ERROR);
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
            ActivityUtils.startActivity(StartActivty.this, LoginActivity.class);
        }

    }
}
