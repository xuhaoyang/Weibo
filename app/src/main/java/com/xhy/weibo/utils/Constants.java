package com.xhy.weibo.utils;

/**
 * Created by xuhaoyang on 16/9/8.
 */
public class Constants {


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

    //UserInfoActivty
    public static final String USER_ID = "UID";
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

    //Request
    public static final int REQUEST_CODE_WRITE_FORWARD = 2;
    public static final int REQUEST_CODE_WRITE_STATUS = 3;
}
