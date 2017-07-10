package com.xhy.weibo.logic;

import android.content.Context;
import android.util.Log;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.User;
import com.xhy.weibo.utils.Logger;

import java.io.IOException;
import java.util.List;

import hk.xhy.android.common.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuhaoyang on 16/7/19.
 */
public class UserLoginLogic {
    public static final String TAG = UserLoginLogic.class.getSimpleName();

    /**
     * 登录
     *
     * @param context
     * @param account  账户
     * @param password 密码
     * @param callback
     */
    public static void login(final Context context, final String account, final String password, final LoginCallback callback) {
        Call<Result<Login>> callLogin = ApiClient.getApi().login(account, password);
        callLogin.enqueue(new Callback<Result<Login>>() {
            @Override
            public void onResponse(Call<Result<Login>> call, Response<Result<Login>> response) {
                if (response.isSuccessful()) {
                    Result<Login> loginResult = response.body();
                    if (loginResult.isSuccess()) {
                        Login login = loginResult.getInfo();
                        callback.onLoginSuccess(login);
                    } else {
                        callback.onLoginFailure(loginResult.getCode(), loginResult.getMsg());
                    }
                } else {
                    Logger.show(TAG, "ErrorCode:" + response.code());
                    callback.onLoginFailure(response.code(), "登录失败");
                }
            }

            @Override
            public void onFailure(Call<Result<Login>> call, Throwable t) {
                callback.onLoginError(t);
            }
        });

    }

    /**
     * 注册新的账户
     *
     * @param account
     * @param password
     * @param uname
     * @param callback
     */
    public static void register(final String account, final String password,
                                final String uname, final RegisterCallback callback) {
        Call<Result> resultCall = ApiClient.getApi().register(account, password, uname);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    if (result.isSuccess()) {
                        callback.onRegisterSuccess(result.getMsg());
                    } else {
                        callback.onRegisterFailure(result.getMsg());
                    }
                } else {
                    LogUtils.w("ErrorCode:" + response.code());
                    callback.onRegisterFailure(response.code() + "：注册失败");

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callback.onRegisterError(t);
            }
        });


    }

    /**
     * 更新用户信息
     *
     * @param uid
     * @param username
     * @param truename
     * @param sex
     * @param intro
     * @param token
     * @param callback
     */
    public static void setUserinfo(final int uid, final String username, final String truename,
                                   final String sex, final String intro, final String token,
                                   final SetUserinfoCallBack callback) {
        final Call<Result> resultCall = ApiClient.getApi().setUserinfo(uid, username, truename, sex, intro, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    final Result result = response.body();
                    if (result.isSuccess()) {
                        callback.onUserInfoSuccess(result.getMsg());
                    } else {
                        callback.onUserInfoFailure(result.getMsg());
                    }
                } else {
                    LogUtils.w("ErrorCode:" + response.code());
                    callback.onUserInfoFailure(response.code() + "：用户信息更新失败");

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callback.onUserInfoError(t);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @param uid
     * @param username
     * @param userId
     * @param token
     * @param callBack
     */
    public static void getUserinfo(final Context context, int uid, String username, int userId,
                                   String token, final GetUserinfoCallBack callBack) {
        Call<Result<User>> callUserinfo = ApiClient.getApi().getUserinfo(uid, username, userId, token);
        callUserinfo.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                if (response.isSuccessful()) {
                    Result<User> body = response.body();
                    if (body.isSuccess()) {
                        callBack.onUserInfoSuccess(body.getInfo());
                    } else {
                        callBack.onUserInfoFailure(body.getCode(), body.getMsg());
                    }
                } else {
                    LogUtils.w("ErrorCode:" + response.code());
                    callBack.onUserInfoFailure(response.code(), "获取失败");
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                callBack.onUserInfoError(t);
            }
        });

    }

    /**
     * 添加关注
     *
     * @param uid
     * @param follow
     * @param gid
     * @param token
     * @param callBack
     */
    public static void addFollow(final int uid, final int follow, final int gid,
                                 final String token, final AddFollowCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().addFollow(uid, follow, gid, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onAddFollowSuccess(result);
                    } else {
                        callBack.onAddFollowFailure(result.getMsg());
                    }
                } else {
                    callBack.onAddFollowFailure("关注失败");
                    try {
                        LogUtils.w(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onAddFollowError(t);
            }
        });
    }

    /**
     * 取消关注
     *
     * @param currentUid 当前uid
     * @param beUid      被关注uid
     * @param type
     * @param token
     * @param callBack
     */
    public static void delFollow(final int currentUid, final int beUid, final int type,
                                 final String token, final DelFollowCallBack callBack) {
        Call<Result> resultCall = ApiClient.getApi().delFollow(currentUid, beUid, type, token);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (response.isSuccessful()) {
                    if (result.isSuccess()) {
                        callBack.onDelFollowSuccess(result);
                    } else {
                        callBack.onDelFollowFailure(result.getMsg());
                    }
                } else {
                    callBack.onDelFollowFailure("取消关注失败");
                    try {
                        LogUtils.w(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callBack.onDelFollowError(t);

            }
        });
    }

    /**
     * 获取关注者的ID或粉丝者的ID列表
     *
     * @param uid      查询来自用户id
     * @param page     查询多少页[默认10条一页]
     * @param keyword  关键字查询
     * @param type     1:关注 0:粉丝
     * @param token
     * @param callBack
     */
    public static void getUserFollowList(final Context context, final int uid, final int page, final String keyword,
                                         final int type, final String token,
                                         final GetUserFollowListCallBack callBack) {
        Call<Result<List<User>>> resultCall = ApiClient.getApi().getUserFollowList(uid, page, keyword, type, token);
        resultCall.enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                if (response.isSuccessful()) {
                    Result<List<User>> result = response.body();
                    if (result.isSuccess()) {
                        callBack.onFollowListSuccess(result.getInfo(), result.getTotalPage());
                    } else {
                        callBack.onFollowListFailure(result.getMsg());
                    }
                } else {
                    callBack.onFollowListFailure(context.getResources().getString(R.string.search_fail));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                callBack.onFollowListError(t);
            }
        });
    }

    /**
     * 关键字搜索用户 排除已关注的通过uid
     *
     * @param context
     * @param uid
     * @param keyword
     * @param page
     * @param token
     * @param callBack
     */
    public static void getSearchUserList(final Context context, final int uid, final String keyword,
                                         final int page, final String token, final GetSearchUserCallBack callBack) {
        Call<Result<List<User>>> resultCall = ApiClient.getApi().getSearchUserList(token, keyword, uid, page);
        resultCall.enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                if (response.isSuccessful()) {
                    Result<List<User>> listResult = response.body();
                    if (listResult.isSuccess()) {
                        callBack.onSearchUserSuccess(listResult.getInfo(), listResult.getTotalPage());
                    } else {
                        callBack.onSearchUserFailure(listResult.getMsg());
                    }
                } else {
                    callBack.onSearchUserFailure(context.getResources().getString(R.string.search_fail));
                    try {
                        LogUtils.w(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                callBack.onSearchUserError(t);
            }
        });

    }

    public interface AddFollowCallBack {
        void onAddFollowSuccess(Result result);

        void onAddFollowFailure(String message);

        void onAddFollowError(Throwable t);
    }

    public interface DelFollowCallBack {
        void onDelFollowSuccess(Result result);

        void onDelFollowFailure(String message);

        void onDelFollowError(Throwable t);
    }


    public interface LoginCallback {
        void onLoginSuccess(Login login);

        void onLoginFailure(int errorCode, String errorMessage);

        void onLoginError(Throwable error);
    }

    public interface RegisterCallback {
        void onRegisterSuccess(String message);

        void onRegisterFailure(String message);

        void onRegisterError(Throwable t);
    }

    public interface GetUserinfoCallBack {
        void onUserInfoSuccess(User user);

        void onUserInfoFailure(int errorCode, String errorMessage);

        void onUserInfoError(Throwable error);
    }

    public interface GetUserFollowListCallBack {
        void onFollowListSuccess(List<User> users, int totalPage);

        void onFollowListFailure(String message);

        void onFollowListError(Throwable error);
    }

    public interface GetSearchUserCallBack {
        void onSearchUserSuccess(List<User> users, int totalPage);

        void onSearchUserFailure(String message);

        void onSearchUserError(Throwable error);
    }

    public interface SetUserinfoCallBack {
        void onUserInfoSuccess(String message);

        void onUserInfoFailure(String message);

        void onUserInfoError(Throwable error);
    }


}
