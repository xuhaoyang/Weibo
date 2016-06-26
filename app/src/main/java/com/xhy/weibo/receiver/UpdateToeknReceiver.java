package com.xhy.weibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.xhy.weibo.constants.AccessToken;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.entity.Login;
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
        String token = intent.getStringExtra(CommonConstants.KEEP_TOKEN);
        long tokenStartTime = intent.getLongExtra(CommonConstants.KEEP_TOKEN_START_TIME, 0);
        Logger.show(UpdateToeknReceiver.class.getName(), "-->获得Token:" + token);

        if (CommonConstants.ACCESS_TOKEN != null) {
            CommonConstants.ACCESS_TOKEN.setToken(token);
            CommonConstants.ACCESS_TOKEN.setTokenStartTime(tokenStartTime);
        } else {
            initDB(context);
            String selection = "used=?";
            String[] selectionArgs = new String[]{"1"};
            List<Login> logins = userDB.QueryLogin(db, selection, selectionArgs);
            if (!logins.isEmpty()) {
                Login login = logins.get(0);
                String account = intent.getStringExtra(CommonConstants.KEEP_TOKEN_ACCOUNT);
                int user_id = intent.getIntExtra(CommonConstants.KEEP_TOKEN_USER_ID, 0);
                if (user_id == login.getId() && account.equals(login.getAccount())) {
                    CommonConstants.ACCESS_TOKEN = AccessToken.getInstance(login.getAccount(), login.getPassword(), context);
                    CommonConstants.ACCESS_TOKEN.setToken(token);
                    CommonConstants.ACCESS_TOKEN.setTokenStartTime(tokenStartTime);
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
