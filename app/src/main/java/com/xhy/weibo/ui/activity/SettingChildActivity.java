package com.xhy.weibo.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import com.xhy.weibo.R;
import com.xhy.weibo.model.Setting;
import com.xhy.weibo.ui.activity.fragment.SettingAboutFragment;
import com.xhy.weibo.ui.activity.fragment.SettingNotificationFragment;
import com.xhy.weibo.ui.activity.fragment.SettingUserFragment;
import com.xhy.weibo.ui.base.BaseActivity;

import hk.xhy.android.common.ui.fragment.utils.FragmentBackUtils;

import com.xhy.weibo.utils.Constants;

import java.util.ArrayList;

import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.android.common.utils.FragmentUtils;

public class SettingChildActivity extends BaseActivity {

    private static final String TAG = SettingChildActivity.class.getSimpleName();
    public Fragment rootFragment;

    private FragmentManager fm;
    private Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_child);

        Intent intent = getIntent();
        setting = Setting.parseObject(intent.getStringExtra(Constants.SETTING_ITEM_CONTENT));
        fm = getSupportFragmentManager();
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (setting != null && setting.getFunctionConfig() == Setting.FUNCTION_ITEM_OPTIONS) {
            switch (setting.getMainHead()) {
                case "通知设置":
                    if (FragmentUtils.findFragment(fm, SettingNotificationFragment.class) == null) {
                        FragmentUtils.addFragment(fm,
                                SettingNotificationFragment.newInstance(),
                                R.id.fragment,
                                false,
                                true);
                    }
                    break;
                case "用户设置":
                    if (FragmentUtils.findFragment(fm, SettingUserFragment.class) == null) {
                        FragmentUtils.addFragment(fm,
                                SettingUserFragment.newInstance(),
                                R.id.fragment,
                                false,
                                true);
                    }
                    break;
                case "关于":
                    if (FragmentUtils.findFragment(fm, SettingAboutFragment.class) == null) {
                        FragmentUtils.addFragment(fm,
                                SettingAboutFragment.newInstance(),
                                R.id.fragment,
                                false,
                                true);
                    }
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
        if (!FragmentUtils.dispatchBackPress(getSupportFragmentManager())) {
            ActivityUtils.finishActivity();
        }
    }
}
