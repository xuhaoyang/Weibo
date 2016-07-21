package com.xhy.weibo.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.AccessToken;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.model.Login;

import java.util.List;

public class InitActivity extends BaseActivity {

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
                    intent2Activity(MainActivity.class);
                    finish();
                    break;
                case GO_LOGIN:
                    intent2Activity(LoginActivity.class);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        init();
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
            AppConfig.ACCESS_TOKEN = AccessToken.getInstance(AppConfig.getAccount(), AppConfig.getPassword(), this);
            mHandler.sendEmptyMessageDelayed(GO_MAIN, TIME);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
        }
        dbManager.closeDatabase();

    }

    private void initDB(){
        //查看是否有使用帐号
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        db = dbManager.getDatabase();
        userDB = new UserDB(this);
    }


}
