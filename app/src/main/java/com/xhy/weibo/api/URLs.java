package com.xhy.weibo.api;

import android.os.Build;

import com.xhy.weibo.BuildConfig;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public interface URLs {

    String BASE_URL = BuildConfig.BASE_URL;
    String API_URL = BASE_URL + "/Api/Api/";

    String WEIBO_USER_LOGIN = API_URL + "userLogin.html";

    //图片地址
    String UPLOAD_URL = BASE_URL + "/Uploads/";
    String AVATAR_IMG_URL = UPLOAD_URL + "Face/";//头像
    String PIC_URL = UPLOAD_URL + "Pic/";


    //上传图片API
    String WEIBO_UPLOAD_PIC = API_URL + "uploadUserPic.html";

    //获取当前用户的微博内容
    String WEIBO_LIST = API_URL + "weiboList.html";
    //搜索微博
    String WEIBO_SEARCH_LIST = API_URL + "getSearchWeibo.html";
    //搜索微博
    String WEIBO_USER_SEARCH_LIST = API_URL + "getSearchUser.html";
    //获取收藏列表
    String WEIBO_GET_KEEP_LIST = API_URL + "getKeepList.html";
    //获得提及我的微博列表
    String WEIBO_ATM_LIST = API_URL + "getAtmList.html";


    //获取单条微博的评论
    String WEIBO_ONE_COMMENT_LIST = API_URL + "getComment.html";

    //获取该用户所有提及的评论
    String WEIBO_GET_COMMENT_LIST = API_URL + "getCommentList.html";

    //获取关注者的ID与粉丝者的ID列表
    String WEIBO_USER_FOLLOW_FANS_LIST = API_URL + "getUserFollowList.html";

    //微博添加收藏
    String WEIBO_KEEP = API_URL + "keepWeibo.html";
    //微博删除收藏
    String WEIBO_DEL_KEEP = API_URL + "delKeep.html";


    String WEIBO_SET_COMMENT = API_URL + "setComment.html";

    //转发微博
    String WEIBO_TURN_WEIBO = API_URL + "turnWeibo.html";
    //发送微博
    String WEIBO_SEND_WEIBO = API_URL + "sendWeibo.html";
    //获取分组信息
    String WEIBO_GET_GROUP = API_URL + "getGroup.html";
    //获取用户信息
    String WEIBO_GET_USERINFO = API_URL + "getUserInfo.html";

    //获取热门话题
    String WEIBO_GET_HOTS = API_URL + "getHots.html";

    //每条微博的转发列信息
    String WEIBO_TURN_LIST = API_URL + "getWeiboTurnList.html";

    //取消关注
    String WEIBO_DEL_FOLLOW = API_URL + "delFollow.html";
    String WEIBO_ADD_FOLLOW = API_URL + "addFollow.html";

    //消息推送
    String WEIBO_GET_MSG = API_URL + "getMsg.html";
    String WEIBO_SET_MSG = API_URL + "setMsg.html";
}
