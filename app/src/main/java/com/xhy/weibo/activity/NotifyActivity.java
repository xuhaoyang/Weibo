package com.xhy.weibo.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.activity.fragment.MaterialUpConceptFakePage;
import com.xhy.weibo.activity.fragment.NotifyCommentFragment;
import com.xhy.weibo.activity.fragment.NotifyStatusFragment;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.NormalInfo;
import com.xhy.weibo.logic.PushMessageLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotifyActivity extends BaseActivity {


    public static final String PUSH = "1";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager mViewPager;

    private PagerAdapter mSectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("通知");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mSectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        final Intent intent = getIntent();
        final boolean booleanExtra = intent.getBooleanExtra(PUSH, false);
        if (booleanExtra) {
            clearMsg();
        }


    }

    private void clearMsg() {


        PushMessageLogic.setMsg(AppConfig.getUserId(), 1, AppConfig.ACCESS_TOKEN.getToken(),
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_notify, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class PagerAdapter extends FragmentPagerAdapter {

        private NotifyStatusFragment notifyStatusFragment = NotifyStatusFragment.newInstance();
        private NotifyCommentFragment notifyCommentFragment = NotifyCommentFragment.newInstance();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return notifyCommentFragment;
                case 1:
                    return notifyStatusFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "评论";
                case 1:
                    return "提及";
//                case 2:
//                    return "SECTION 3";
            }
            return null;
        }
    }
}
