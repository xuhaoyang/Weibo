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
import android.view.View;

import com.xhy.weibo.R;
import com.xhy.weibo.activity.fragment.SearchStatusFragment;
import com.xhy.weibo.activity.fragment.SearchUsersFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {


    public static final String SEARCH_CONTENT = "CONTENT";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private PagerAdapter mPagerAdapter;

    private ViewPager mViewPager;
    private Intent fromIntent;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("搜索结果");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fromIntent = getIntent();
        content = fromIntent.getStringExtra(SEARCH_CONTENT);


        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), content);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class PagerAdapter extends FragmentPagerAdapter {
        private String keyword;
        private SearchStatusFragment searchStatusFragment = SearchStatusFragment.newInstance();
        private SearchUsersFragment searchUsersFragment = SearchUsersFragment.newInstance();

        public PagerAdapter(FragmentManager fm, String keyword) {
            super(fm);
            this.keyword = keyword;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle i;
            switch (position) {
                case 0:
                    i = new Bundle();
                    i.putString(SearchStatusFragment.SEARCH_CONTENT, keyword);
                    searchStatusFragment.setArguments(i);
                    return searchStatusFragment;
                case 1:
                    i = new Bundle();
                    i.putString(SearchUsersFragment.SEARCH_CONTENT, keyword);
                    searchUsersFragment.setArguments(i);
                    return searchUsersFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "微博";
                case 1:
                    return "用户";
            }
            return null;
        }
    }
}
