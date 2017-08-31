package com.xhy.weibo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushMessage;
import hk.xhy.android.common.utils.LogUtils;
import hk.xhy.android.common.utils.NetworkUtils;
import hk.xhy.android.common.utils.ToastUtils;

/**
 * Created by xuhaoyang on 2017/8/30.
 */

public class JPushTagAliasUtils {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Jpush接口 要求用户用序列保持唯一性
     */
    public static int sequence = 1;

    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;

    public static final int ACTION_CHECK = 6;

    public static final int DELAY_SEND_ACTION = 101;


    private Context mContext;

    private static JPushTagAliasUtils mIntstance;

    private JPushTagAliasUtils() {

    }

    public static JPushTagAliasUtils getIntstance() {
        if (mIntstance == null) {
            synchronized (JPushTagAliasUtils.class) {
                if (mIntstance == null) {
                    mIntstance = new JPushTagAliasUtils();
                }
            }
        }
        return mIntstance;
    }

    public void init(Context context) {
        if (context != null && context != null) {
            this.mContext = context.getApplicationContext();
        }
    }

    private HashMap<Integer, TagAliasModel> tagAliasActionCache = new HashMap<Integer, TagAliasModel>();


    public TagAliasModel get(int sequence) {
        return tagAliasActionCache.get(sequence);
    }

    public TagAliasModel remove(int sequence) {
        return tagAliasActionCache.get(sequence);
    }

    public void put(int sequence, TagAliasModel tagAliasModel) {
        tagAliasActionCache.put(sequence, tagAliasModel);
    }

    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasModel) {
                        LogUtils.i(TAG, "on delay time");
                        sequence++;
                        TagAliasModel tagAliasBean = (TagAliasModel) msg.obj;
                        tagAliasActionCache.put(sequence, tagAliasBean);
                        if (mContext != null) {
//                            handleAction(context, sequence, tagAliasBean);
                        } else {
                            LogUtils.e(TAG, "#unexcepted - context was null");
                        }
                    } else {
                        LogUtils.w(TAG, "#unexcepted - msg obj was incorrect");
                    }
                    break;
            }
        }
    };

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LogUtils.i(TAG, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        LogUtils.i(TAG, "tags size:" + jPushMessage.getTags().size());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasModel tagAliasModel = tagAliasActionCache.get(sequence);
        if (tagAliasModel == null) {
            ToastUtils.showShort("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LogUtils.i(TAG, "action - modify tag Success,sequence:" + sequence);
            tagAliasActionCache.remove(sequence);
            String logs = getActionStr(tagAliasModel.action) + " tags success";
            LogUtils.i(TAG, logs);
            ToastUtils.showShort(logs);

        } else {
            String logs = "Failed to " + getActionStr(tagAliasModel.action) + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            LogUtils.e(TAG, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasModel)) {
                ToastUtils.showShort(logs);

            }
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LogUtils.i(TAG, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasModel tagAliasModel = tagAliasActionCache.get(sequence);
        if (tagAliasModel == null) {
            ToastUtils.showShort("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LogUtils.i(TAG, "tagBean:" + tagAliasModel);
            tagAliasActionCache.remove(sequence);
            String logs = getActionStr(tagAliasModel.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            LogUtils.i(TAG, logs);
            ToastUtils.showShort(logs);

        } else {
            String logs = "Failed to " + getActionStr(tagAliasModel.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
            LogUtils.e(TAG, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasModel)) {
                ToastUtils.showShort(logs);

            }
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LogUtils.i(TAG, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasModel tagAliasModel = tagAliasActionCache.get(sequence);
        if (tagAliasModel == null) {
            ToastUtils.showShort("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LogUtils.i(TAG, "action - modify alias Success,sequence:" + sequence);
            tagAliasActionCache.remove(sequence);
            String logs = getActionStr(tagAliasModel.action) + " alias success";
            LogUtils.i(TAG, logs);
            ToastUtils.showShort(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasModel.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
            LogUtils.e(TAG, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasModel)) {
                ToastUtils.showShort(logs);
            }
        }
    }


    private boolean RetryActionIfNeeded(int errorCode, TagAliasModel tagAliasModel) {
        if (!NetworkUtils.isConnected()) {
            LogUtils.w(TAG, "no network");
            return false;
        }
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            LogUtils.d(TAG, "need retry");
            if (tagAliasModel != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasModel;
                delaySendHandler.sendMessageDelayed(message, 1000 * 60);
                String logs = getRetryStr(tagAliasModel != null ? tagAliasModel.isAliasAction
                        : false, tagAliasModel != null ? tagAliasModel.action : -1, errorCode);
                ToastUtils.showShort(logs);

                return true;
            }
        }
        return false;
    }

    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
        String str = "Failed to %s %s due to %s. Try again after 60s.";
        str = String.format(Locale.ENGLISH, str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"), (errorCode == 6002 ? "timeout" : "server too busy"));
        return str;
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_ADD:
                return "add";
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
            case ACTION_GET:
                return "get";
            case ACTION_CLEAN:
                return "clean";
            case ACTION_CHECK:
                return "check";
        }
        return "unkonw operation";
    }

    public static class TagAliasModel {
        int action;
        Set<String> tags;
        String alias;
        boolean isAliasAction;

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public boolean isAliasAction() {
            return isAliasAction;
        }

        public void setAliasAction(boolean aliasAction) {
            isAliasAction = aliasAction;
        }

        @Override
        public String toString() {
            return "TagAliasModel{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }
}
