package com.xhy.weibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.xhy.weibo.AccessToken;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.utils.Logger;

import java.util.List;

/**
 * Created by xuhaoyang on 16/6/24.
 */
public class UpdateToeknReceiver extends BroadcastReceiver {

    private DBManager dbManager;
    private SQLiteDatabase db;
    private UserDB userDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        String token = intent.getStringExtra(AppConfig.KEEP_TOKEN);
        long tokenStartTime = intent.getLongExtra(AppConfig.KEEP_TOKEN_START_TIME, 0);
        Logger.show(UpdateToeknReceiver.class.getName(), "-->获得Token:" + token);

        if (AppConfig.ACCESS_TOKEN != null) {
            AppConfig.getAccessToken().setToken(token);
            AppConfig.getAccessToken().setTokenStartTime(tokenStartTime);
        } else {
            initDB(context);
            String selection = "used=?";
            String[] selectionArgs = new String[]{"1"};
            List<Login> logins = userDB.QueryLogin(db, selection, selectionArgs);
            if (!logins.isEmpty()) {
                Login login = logins.get(0);
                String account = intent.getStringExtra(AppConfig.KEEP_TOKEN_ACCOUNT);
                int user_id = intent.getIntExtra(AppConfig.KEEP_TOKEN_USER_ID, 0);
                if (user_id == login.getId() && account.equals(login.getAccount())) {
                    //更新当前用户 密码
                    AppConfig.setAccount(login.getAccount());
                    AppConfig.setPassword(login.getPassword());
                    //刷新Accesstoken
                    AppConfig.getAccessToken();
                    AppConfig.getAccessToken().setToken(token);
                    AppConfig.getAccessToken().setTokenStartTime(tokenStartTime);
                }
            }
        }

    }

    private void initDB(Context context) {
        //查看是否有使用帐号
        dbManager = new DBManager(context);
        dbManager.openDatabase();
        db = dbManager.getDatabase();
        userDB = new UserDB(context);
    }
}
