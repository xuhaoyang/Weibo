package com.xhy.weibo.network;

/**
 * Created by xuhaoyang on 16/5/13.
 */
public class NetParams {



    public static String getComment(int wid, int page, String token) {
//getComment.html?wid=102&page=1&token=BgBXSQFIUkBXSQFTBkkCHFBCAxwGLgY2VjQAYQAyAWZbPVIxBDMDYANj
        String result = URLs.WEIBO_ONE_COMMENT_LIST + "?wid=" + wid
                + "&token=" + token + "&page=" + page;
        return result;
    }

    //添加微博收藏
    public static String keepWeibo(int uid, int wid, String token) {

        String result = URLs.WEIBO_KEEP + "?wid=" + wid
                + "&token=" + token + "&uid=" + uid;
        return result;

    }

    public static String delkeepWeibo(int uid, int wid, String token) {
        String result = URLs.WEIBO_DEL_KEEP + "?wid=" + wid
                + "&token=" + token + "&uid=" + uid;
        return result;
    }

    public static String setComment(int uid, int wid, String content, String token) {

        String result = URLs.WEIBO_SET_COMMENT + "?wid=" + wid
                + "&token=" + token + "&uid=" + uid + "&content=" + content;
        return result;
    }

    public static String turnWeibo(int uid, int wid, int tid, String content, String token) {

        String result = URLs.WEIBO_TURN_WEIBO + "?uid=" + uid + "&wid=" + wid
                + "&content=" + content + "&token=" + token;
        if (tid != 0) {
            result = result + "&tid=" + tid;
        }
        return result;
    }

    public static String sendWeibo(int uid, String content, String max, String medium, String mini, String token) {

        String result = URLs.WEIBO_SEND_WEIBO + "?uid=" + uid + "&content=" + content + "&token=" + token;
        return result;
    }

    public static String uploadUserPic(String token, int height, int width) {
        String result = URLs.WEIBO_UPLOAD_PIC + "?token=" + token + "&height=" + height
                + "&width=" + width;
        return result;
    }
}
