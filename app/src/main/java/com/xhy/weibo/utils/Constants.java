package com.xhy.weibo.utils;

/**
 * Created by xuhaoyang on 16/9/8.
 */
public class Constants {


    public static boolean isNotify = true;


    //SearchActivity 搜索内容
    public static final String SEARCH_CONTENT = "search_content";
    //StatusDetailActivity status
    public static final String STATUS_INTENT = "status";
    public static final String TOKEN = "token";


    public static final int TYPE_WRITE_FRIEND_LISTENER = 101;
    public static final String RESULT_DATA_USERNAME_AT = "USERNAME_AT";


    public static final int ITEM_LIKE_TPYE = 0;
    public static final int ITEM_COMMENT_TPYE = 1;
    public static final int ITEM_FORWARD_TPYE = 2;
    public static final int ITEM_FORWARD_STATUS_TYPE = 3;


    //UserInfoActivty
    public static final String USER_ID = "UID";
    public static final String STATUS_ID = "wid";
    public static final String USER_NAME = "NAME";
    public static final String SEND_FORWORD_SUCCESS = "SEND_FORWORD_OK";
    public static final String SEND_STATUS_SUCCESS = "SEND_STATUS_OK";

    /**
     * WriteStatusActivity
     */
    //打上TAG标签 判断来自哪个Activity
    public static final String TAG = "TAG";
    public static final int MAIN_ATY_CODE = 1;
    public static final int DETAIL_ATY_CODE = 2;
    public static final int COMMENT_ADPATER_CODE = 3;
    public static final String TYPE = "WRITECOMMENTACTIVITY_TYPE";
    public static final int COMMENT_TYPE = 101;
    public static final int FORWARD_TYPE = 102;
    public static final int NEW_STATUS_TYPE = 103;

    public static final String COMMENT_INTENT = "comment";
    public static final String SEND_COMMENT_SUCCESS = "SEND_COMMENT_OK";


    //Request
    public static final int REQUEST_CODE_WRITE_FORWARD = 2;
    public static final int REQUEST_CODE_WRITE_STATUS = 3;

    /**
     * Settings
     */
    public static final String SETTING_ITEM_CONTENT = "setting_item_content";


    //jpush
    public static final String EXTRA_JPUSH_MESSAGE = "extra_jpush_message";


    //WebView
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_TITLE = "extra_browser";
}
