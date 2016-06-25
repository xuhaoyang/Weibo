package com.xhy.weibo.constants;

import java.io.Serializable;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class CommonConstants implements Serializable {

    private CommonConstants() {
    }

    public static final String KEEP_TOKEN = "TOKEN";
    public static final String KEEP_TOKEN_START_TIME = "TOKEN_START_TIME";

    public static final boolean isShowLog = true;
    public static String TOKEN = "VlBTTQRNAhBRTwZUAU4GGAIQBxgHL1FhB2UAYVtpVD8BZQJqAzMHYVcy";
    public static final int TOKEN_EXPIRES_IN = 7200;
    public static final int TOKEN_MAKE = 1463170105;
    public static int USER_ID;
    public static String account;
    public static String password;
    public static final int STATUS_COUNT_PAGE = 10;//加载多少条为一页
    public static AccessToken ACCESS_TOKEN;
    public static boolean isNotify = true;

}
