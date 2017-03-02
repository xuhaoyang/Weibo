package com.xhy.weibo.api;


/**
 * Created by xuhaoyang on 16/5/13.
 */
public class NetParams {

    public static String uploadUserPic(String token, int height, int width) {
        String result = com.xhy.weibo.network.URLs.WEIBO_UPLOAD_PIC + "?token=" + token + "&height=" + height
                + "&width=" + width;
        return result;
    }
}
