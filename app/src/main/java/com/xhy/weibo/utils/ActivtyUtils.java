package com.xhy.weibo.utils;

import android.content.Intent;

import com.xhy.weibo.activity.MainActivity;

import hk.xhy.android.commom.utils.ActivityUtils;

/**
 * Created by xuhaoyang on 16/8/24.
 */
public class ActivtyUtils extends ActivityUtils{

    public static void backHomeActivity() {
        backHomeActivity(-1);
    }

    public static void backHomeActivity(int tabId) {
        try {
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                if (null != activityStack.get(i)) {
                    if (activityStack.get(i).getClass() == MainActivity.class) {
                        if (tabId != -1) {
                            Intent intent = new Intent();
                            intent.putExtra("tab_id", tabId);
                            activityStack.get(i).setIntent(intent);
                        }
                        continue;
                    }
                    finishActivity(activityStack.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
