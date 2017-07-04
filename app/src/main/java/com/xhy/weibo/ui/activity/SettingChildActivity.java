package com.xhy.weibo.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import com.xhy.weibo.R;
import com.xhy.weibo.model.Setting;
import com.xhy.weibo.ui.activity.fragment.SettingNotificationFragment;
import com.xhy.weibo.ui.base.BaseActivity;
import hk.xhy.android.common.ui.fragment.utils.FragmentBackUtils;
import com.xhy.weibo.utils.Constants;

import hk.xhy.android.common.utils.ActivityUtils;

public class SettingChildActivity extends BaseActivity {

    private static final String TAG = SettingChildActivity.class.getSimpleName();

    private FragmentManager fm;
    private Fragment currentFragment = null;
    private SettingNotificationFragment SNotificationFragment;
    private Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_child);


        fm = getSupportFragmentManager();

        Intent intent = getIntent();
        setting = Setting.parseObject(intent.getStringExtra(Constants.SETTING_ITEM_CONTENT));

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (setting != null && setting.getFunctionConfig() == Setting.FUNCTION_ITEM_OPTIONS) {
            switch (setting.getMainHead()) {
                case "通知设置":
                    if (SNotificationFragment == null) {
                        SNotificationFragment = SettingNotificationFragment.newInstance();
                    }
                    currentFragment = SNotificationFragment;
                    fm.beginTransaction().addToBackStack(null).replace(R.id.fragment, currentFragment).commit();
                    break;
                default:
                    ActivityUtils.finishActivity();
                    break;
            }
        } else {
            ActivityUtils.finishActivity();
        }
    }

    /**
     * 拦截back
     */
    @Override
    public void onBackPressed() {
        if (!FragmentBackUtils.handleBackPress(this)){
            ActivityUtils.finishActivity();
        }
    }
}
