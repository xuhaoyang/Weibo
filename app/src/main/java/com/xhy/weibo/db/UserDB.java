package com.xhy.weibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xhy.weibo.model.Login;
import com.xhy.weibo.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaoyang on 16/6/3.
 */
public class UserDB {

    private Context context;

    public UserDB(Context context) {
        this.context = context;
    }

    public List<Login> QueryLogin(SQLiteDatabase db, String selection, String[] selectionArgs) {

        List<Login> logins = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.USER_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Login login = new Login();
                login.setId(cursor.getInt(cursor.getColumnIndex("id")));
                login.setAccount(cursor.getString(cursor.getColumnIndex("account")));
                login.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                login.setRegistime(cursor.getInt(cursor.getColumnIndex("registime")));
                login.setToken(cursor.getString(cursor.getColumnIndex("token")));
                login.setTokenStartTime(cursor.getLong(cursor.getColumnIndex("tokenStartTime")));
                logins.add(login);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return logins;
    }

    public List<User> QueryUsers(SQLiteDatabase db, String selection, String[] selectionArgs) {

        List<User> users = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.USER_TABLE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUid(cursor.getInt(cursor.getColumnIndex("id")));
                user.setFace(cursor.getString(cursor.getColumnIndex("face")));
                user.setFollow(cursor.getInt(cursor.getColumnIndex("follow")));
                user.setFans(cursor.getInt(cursor.getColumnIndex("fans")));
                user.setWeibo(cursor.getInt(cursor.getColumnIndex("weibo")));
                user.setUptime(cursor.getLong(cursor.getColumnIndex("uptime")));
                user.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public boolean updateUser(SQLiteDatabase db, User user) {
        boolean flag;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("face", user.getFace());
            values.put("username", user.getUsername());
            values.put("follow", user.getFollow());
            values.put("fans", user.getFans());
            values.put("weibo", user.getWeibo());
            values.put("intro", user.getIntro());
            values.put("uptime", System.currentTimeMillis());
            String selection = "id=?";
            String[] selectionArgs = new String[]{user.getUid() + ""};
            db.update(DatabaseHelper.USER_TABLE, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            flag = true;
        }
        return flag;
    }

    public boolean insertUser(SQLiteDatabase db, User user) {
        boolean flag;
        //开启事务
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", user.getUid());
            values.put("face", user.getFace());
            values.put("username", user.getUsername());
            values.put("follow", user.getFollow());
            values.put("fans", user.getFans());
            values.put("weibo", user.getWeibo());
            values.put("intro", user.getIntro());
            values.put("uptime", System.currentTimeMillis());
            db.insert(DatabaseHelper.USER_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            flag = true;
        }

        return flag;
    }


    public boolean insertLogin(SQLiteDatabase db, Login login) {
        boolean flag;
        //开启事务
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", login.getId());
            values.put("account", login.getAccount());
            values.put("password", login.getPassword());
            values.put("registime", login.getRegistime());
            values.put("token", login.getToken());
            values.put("tokenStartTime", login.getTokenStartTime());
            values.put("used", 1);
            db.insert(DatabaseHelper.USER_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            flag = true;
        }

        return flag;
    }


}
