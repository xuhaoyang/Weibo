package com.xhy.weibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xuhaoyang on 16/6/3.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int version = 3;
    public static final String USER_TABLE = "user";
    private static final String CREATE_USER = "create table user(" +
            "id integer primary key ," +
            "account text," +
            "password text," +
            "registime integer," +
            "token text," +
            "tokenStartTime long," +
            "username text," +
            "face text," +
            "follow integer," +
            "fans integer," +
            "weibo integer," +
            "intro text," +
            "uptime long," +
            "used integer)";


    private Context mContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 3:
                db.execSQL("drop table if exists user");
                db.execSQL(CREATE_USER);
                break;
        }
    }
}
