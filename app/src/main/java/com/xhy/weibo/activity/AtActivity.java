package com.xhy.weibo.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.UserAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.User;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AtActivity extends BaseActivity implements UserLoginLogic.GetUserFollowListCallBack {


    @BindView(R.id.main_Car)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.recycler_view_at)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout_at)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    LinearLayoutManager linearLayoutManager;
    List<User> users = new ArrayList<User>();
    private UserAdpater userAdpater;
    private SearchView actionView;
    private String keyword;
    private boolean isLoading = false;
    private int currPage = 1;
    private int totalPage = 1;
    private int lastVisibleItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at);
        ButterKnife.bind(this);
        init();

    }

    private void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
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
                        && lastVisibleItemPosition + 1 == userAdpater.getItemCount() && pastVisiblesItems != 0) {
                    if (!isLoading) {
                        isLoading = true;
                        if (currPage <= totalPage && users.size() > 0) {
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

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                LoadData();
            }
        });
        initRecyclerView();
    }


    public void initRecyclerView() {
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        userAdpater = new UserAdpater(this, users, UserAdpater.TYPE_WRITE_FRIEND_LISTENER);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(userAdpater);
    }

    private void LoadData() {

        UserLoginLogic.getUserFollowList(AppConfig.getUserId(), currPage, keyword, 1, AppConfig.ACCESS_TOKEN.getToken(), this);
    }


    @Override
    public void onFollowListSuccess(List<User> users, int totalPage) {
        this.totalPage = totalPage;
        if (currPage == 1) {
            this.users.clear();
            this.users.addAll(users);
        } else {
            //要判断是否有重复的
            for (User u : users) {
                if (!this.users.contains(u)) {
                    this.users.add(u);
                }
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
        if (userAdpater != null) {
            userAdpater.notifyItemRemoved(userAdpater.getItemCount());
        }
        isLoading = false;
        userAdpater.notifyDataSetChanged();
    }

    @Override
    public void onFollowListFailure(String message) {
        showSnackbar(message);
        showLog(message);
        mSwipeRefreshLayout.setRefreshing(false);
        if (userAdpater != null) {
            userAdpater.notifyItemRemoved(userAdpater.getItemCount());
        }
        isLoading = false;
    }

    @Override
    public void onFollowListError(Throwable error) {
        showSnackbar(error.getMessage());
        mSwipeRefreshLayout.setRefreshing(false);
        if (userAdpater != null) {
            userAdpater.notifyItemRemoved(userAdpater.getItemCount());
        }
        isLoading = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_at, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
//        item.expandActionView();
        actionView = (SearchView) MenuItemCompat.getActionView(item);
        actionView.setIconifiedByDefault(false);
        actionView.setSubmitButtonEnabled(true);
        actionView.setIconified(false);
        //返回true,截取关闭事件,不让搜索框收起来
        actionView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });

        actionView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword = query;
                currPage = 1;
                LoadData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

        }
        return super.onOptionsItemSelected(item);
    }


    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }

}
