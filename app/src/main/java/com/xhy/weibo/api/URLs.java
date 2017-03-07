package com.xhy.weibo.api;

import com.xhy.weibo.BuildConfig;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public interface URLs {

    String BASE_URL = BuildConfig.BASE_URL;
    String API_URL = BASE_URL + "/Api/Api/";
    String API_VERSION = BASE_URL + "/Api/Api/";

    //登录
    String WEIBO_USER_LOGIN = API_VERSION + "userLogin.html";
    //注册
    String WEIBO_USER_REGISTER= API_VERSION + "userRegister.html";
    //微博分组
    String WEIBO_GET_GROUP = API_VERSION + "getGroup.html";
    //获取微博列表
    String WEIBO_GET_LIST = API_VERSION + "weiboList.html";
    //获取收藏列表
    String WEIBO_GET_KEEP_LIST = API_VERSION + "getKeepList.html";

    //用户信息
    String WEIBO_GET_USERINFO = API_VERSION + "getUserInfo.html";

    //微博添加收藏
    String WEIBO_ADD_KEEP = API_VERSION + "keepWeibo.html";
    //微博删除收藏
    String WEIBO_DEL_KEEP = API_VERSION + "delKeep.html";

    //转发微博
    String WEIBO_TURN_WEIBO = API_VERSION + "turnWeibo.html";
    //发送微博
    String WEIBO_SEND_WEIBO = API_VERSION + "sendWeibo.html";
    //删除微博
    String WEIBO_DELETE_WEIBO = API_VERSION + "delWeibo.html";
    //发布评论
    String WEIBO_SET_COMMENT = API_VERSION + "setComment.html";

    //取消关注
    String WEIBO_DEL_FOLLOW = API_VERSION + "delFollow.html";
    //添加关注
    String WEIBO_ADD_FOLLOW = API_VERSION + "addFollow.html";


    //获取热门话题
    String WEIBO_GET_HOTS = API_VERSION + "getHots.html";

    //获取单条微博的评论
    String WEIBO_GET_STATUS_ONLY_COMMENT_LIST = API_VERSION + "getStatusOnlyComment.html";

    //获取该用户所有提及的评论
    String WEIBO_GET_COMMENT_LIST = API_VERSION + "getCommentList.html";

    //每条微博的转发列信息
    String WEIBO_GET_TURN_LIST = API_VERSION + "getWeiboTurnList.html";

    //获得提及我的微博信息 列表
    String WEIBO_ATM_LIST = API_VERSION + "getAtmList.html";



    //搜索微博
    String WEIBO_SEARCH_LIST = API_VERSION + "getSearchWeibo.html";
    //搜索微博
    String WEIBO_USER_SEARCH_LIST = API_VERSION + "getSearchUser.html";

    //图片地址
    String UPLOAD_URL = BASE_URL + "/Uploads/";
    String AVATAR_IMG_URL = UPLOAD_URL + "Face/";//头像
    String PIC_URL = UPLOAD_URL + "Pic/";

    //上传图片API
    String WEIBO_UPLOAD_PIC = API_VERSION + "uploadUserPic.html";









    //获取关注者的ID或粉丝者的ID列表
    String WEIBO_USER_FOLLOW_FANS_LIST = API_VERSION + "getUserFollowList.html";















    //消息推送
    String WEIBO_GET_MSG = API_VERSION + "getMsg.html";
    String WEIBO_SET_MSG = API_VERSION + "setMsg.html";
}
