package com.xhy.weibo.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.HotAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.logic.HotLogic;
import com.xhy.weibo.model.Hot;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HotsActivity extends BaseActivity {

    @BindView(R.id.hot_Car)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swipeRefreshLayout_hot)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_hot)
    RecyclerView mRecyclerView;

    LinearLayoutManager linearLayoutManager;
    private List<Hot> hots = new ArrayList<>();
    private HotAdpater hotAdpater = new HotAdpater(this, hots);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hots);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("热门");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(hotAdpater);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                LoadData();
            }
        });

    }

    private void LoadData() {

        HotLogic.getHotList(this, AppConfig.ACCESS_TOKEN.getToken(), new HotLogic.GetHotListCallBack() {
            @Override
            public void onGetSuccess(List<Hot> hots) {
                HotsActivity.this.hots.clear();
                HotsActivity.this.hots.addAll(hots);
                hotAdpater.notifyDataSetChanged();
                stopSwipeRefresh();
            }

            @Override
            public void onGetFailure(String message) {
                showSnackbar(message);
                stopSwipeRefresh();
            }

            @Override
            public void onGetError(Throwable t) {
                showLog(t.getMessage());
                stopSwipeRefresh();
            }
        });

    }

    private void stopSwipeRefresh(){
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }
}
