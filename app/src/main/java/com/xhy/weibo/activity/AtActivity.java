package com.xhy.weibo.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.UserAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.User;
import com.xhy.weibo.entity.UsersReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.utils.RecycleViewDivider;
import com.xhy.weibo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AtActivity extends BaseActivity {


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
        GsonRequest<UsersReciver> request = new GsonRequest<UsersReciver>(Request.Method.POST,
                URLs.WEIBO_USER_FOLLOW_FANS_LIST,
                UsersReciver.class, null, new Response.Listener<UsersReciver>() {
            @Override
            public void onResponse(UsersReciver response) {

                if (response.getCode() == 200) {
                    totalPage = response.getTotalPage();
                    if (users != null) {
                        if (currPage == 1) {
                            users.clear();
                            users.addAll(response.getInfo());
                        } else {
                            //要判断是否有重复的
                            for (User u : response.getInfo()) {
                                if (!users.contains(u)) {
                                    users.add(u);
                                }
                            }
                        }
                    } else {
                        //第一次获取到数据
                        users = response.getInfo();
                    }
                    userAdpater.notifyDataSetChanged();
                } else {
                    //错误信息处理
                    Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
                            .show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                if (userAdpater != null) {
                    userAdpater.notifyItemRemoved(userAdpater.getItemCount());
                }
                isLoading = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                map.put("page", currPage + "");
                if (!TextUtils.isEmpty(keyword)) {
                    map.put("keyword", keyword);
                }
                map.put("uid", CommonConstants.USER_ID + "");
                map.put("type", "1");

                return map;
            }
        };

        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
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
//                if (TextUtils.isEmpty(query)) {
//                    return false;
//                }
                //做点动作
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
}
