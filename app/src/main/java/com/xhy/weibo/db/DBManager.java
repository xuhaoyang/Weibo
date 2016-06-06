package com.xhy.weibo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by xuhaoyang on 16/6/3.
 */
public class DBManager {

    private static String TAG = DBManager.class.getSimpleName();
    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "Weibo.db";
    public static final String PACKAGE_NAME = "com.xhy.weibo";
    public static final String DATABASE_PATH = "databases";
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" +
            PACKAGE_NAME + "/" + DATABASE_PATH;
    //SQLiteDatabase: /data/data/com.xhy.weibo/databases/Weibo.db
    private SQLiteDatabase database;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void openDatabase() {
        Log.e(TAG, DB_PATH + "/" + DB_NAME);
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String dbfile) {

//        try {
//        if (!(new File(dbfile).exists())) {
//                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
//                InputStream is = this.context.getResources().openRawResource(R.raw.china_city); //欲导入的数据库
//                FileOutputStream fos = new FileOutputStream(dbfile);
//                byte[] buffer = new byte[BUFFER_SIZE];
//                int count = 0;
//                while ((count = is.read(buffer)) > 0) {
//                    fos.write(buffer, 0, count);
//                }
//                fos.close();
//                is.close();
        DatabaseHelper userDatabaseHelper = new DatabaseHelper(context, DB_NAME, null, DatabaseHelper.version);
        return userDatabaseHelper.getWritableDatabase();
//        }
//        return SQLiteDatabase.openOrCreateDatabase(dbfile, null);

//            return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
//        } catch (FileNotFoundException e) {
//            Log.e("Database", "File not found");
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.e("Database", "IO exception");
//            e.printStackTrace();
//        }

//        return null;
    }

    public void closeDatabase() {
        this.database.close();
    }

}

