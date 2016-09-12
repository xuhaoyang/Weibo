package com.xhy.weibo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.ui.base.BaseActivity;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class KeepStatusActivity extends BaseActivity implements StatusLogic.GetKeepStatusByUidCallBack {


    public static final int REFRESH_DATA = 1;

    @BindView(R.id.recycler_view_keep)
    RecyclerView mRecyclerView;
    @BindView(R.id.keep_Car)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swipeRefreshLayout_keep)
    SwipeRefreshLayout mSwipeRefreshLayout;

    LinearLayoutManager linearLayoutManager;

    List<Status> statuses = new ArrayList<Status>();
    private boolean isLoading;
    private int currPage = 1;
    private int totalPage = 1;
    private int begin = 0;
    private int lastVisibleItemPosition;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    Snackbar.make(mCoordinatorLayout, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
                    currPage = 1;
                    LoadData();

                    break;
            }
        }
    };
    private StatusAdpater statusAdpater = new StatusAdpater(statuses, this, mHandler);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_status);
        ButterKnife.bind(this);


        init();
        initListener();
        initRecyclerView();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                LoadData();
            }
        });

    }

    private void init() {

        getSupportActionBar().setTitle("收藏");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

    }

    private void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPage = 1;
                LoadData();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition + 1 == statusAdpater.getItemCount()
                        && pastVisiblesItems != 0) {
                    if (!isLoading) {
                        isLoading = true;
                        if (currPage <= totalPage && statuses.size() > 0) {
                            currPage += 1;
                        }
                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                        LoadData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    public void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(statusAdpater);
    }

    private void LoadData() {

        StatusLogic.getKeepStatusListByUid(this, AppConfig.getUserId(), currPage,
                AppConfig.getAccessToken().getToken(), this);
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


    private void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        isLoading = false;
        if (statusAdpater != null) {
            statusAdpater.notifyItemRemoved(statusAdpater.getItemCount());
        }
    }

    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }

    @Override
    public void onKeepStatusSuccess(List<Status> statuses, int totalPage) {
        this.totalPage = totalPage;
        if (currPage == 1) {
            this.statuses.clear();
            this.statuses.addAll(statuses);
        } else {
            for (Status s : statuses) {
                if (!this.statuses.contains(s)) {
                    this.statuses.add(s);
                }
            }
        }
        statusAdpater.notifyDataSetChanged();
        stopRefresh();
    }

    @Override
    public void onKeepStatusFailure(String message) {
        showSnackbar(message);
        stopRefresh();
    }

    @Override
    public void onKeepStatusError(Throwable t) {
        showLog(t.getMessage());
        stopRefresh();
    }
}

