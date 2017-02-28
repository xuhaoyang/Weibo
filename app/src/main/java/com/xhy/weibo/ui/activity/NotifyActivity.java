package com.xhy.weibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.ui.activity.fragment.NotifyCommentFragment;
import com.xhy.weibo.ui.activity.fragment.NotifyAtFragment;
import com.xhy.weibo.ui.base.BaseActivity;
import com.xhy.weibo.logic.PushMessageLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.ui.base.ViewPagerAdapter;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.utils.ActivityUtils;

public class NotifyActivity extends BaseActivity {


    public static final String PUSH = "1";
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.tabs)
    TabLayout tabLayout;
    @ViewById(R.id.container)
    ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("通知");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.finishActivity();
            }
        });

        setUpViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setUpViewPager(ViewPager mViewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFrag(NotifyCommentFragment.newInstance(), "评论");
        mAdapter.addFrag(NotifyAtFragment.newInstance(), "提及");

        mViewPager.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final Intent intent = getIntent();
        final boolean booleanExtra = intent.getBooleanExtra(PUSH, false);
        if (booleanExtra) {
            clearMsg();
        }
    }

    private void clearMsg() {

        PushMessageLogic.setMsg(this, AppConfig.getUserId(), 1, AppConfig.getAccessToken().getToken(),
                new PushMessageLogic.SetMsgCallBack() {
                    @Override
                    public void onSetMsgSuccess(Result result) {
                        showLog(result.getMsg());
                    }

                    @Override
                    public void onSetMsgFailure(String message) {
                        showLog(message);
                    }

                    @Override
                    public void onSetMsgError(Throwable t) {
                        showLog(t.getMessage());
                    }
                });


    }


}
