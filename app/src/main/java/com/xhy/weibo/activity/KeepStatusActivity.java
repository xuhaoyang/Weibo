package com.xhy.weibo.activity;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.Status;
import com.xhy.weibo.entity.StatusReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeepStatusActivity extends BaseActivity {


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
                    Snackbar.make(mCoordinatorLayout,"取消收藏成功",Snackbar.LENGTH_SHORT).show();
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

//        NetParams.getWeiboList(CommonConstants.USER_ID,
        //CommonConstants.TOKEN, begin, CommonConstants.STATUS_COUNT_PAGE)
        GsonRequest<StatusReciver> request = new GsonRequest<StatusReciver>(Request.Method.POST,
                URLs.WEIBO_GET_KEEP_LIST,
                StatusReciver.class, null, new Response.Listener<StatusReciver>() {
            @Override
            public void onResponse(StatusReciver response) {
                if (response.getCode() == 200) {
                    totalPage = response.getTotalPage();
                    if (statuses != null) {
                        if (currPage == 1) {
                            statuses.clear();
                            statuses.addAll(response.getInfo());
                        } else {
                            //要判断是否有重复的
                            for (Status s : response.getInfo()) {
                                if (!statuses.contains(s)) {
                                    statuses.add(s);
                                }
                            }
                        }
                    } else {
                        //第一次获取到数据
                        statuses = response.getInfo();
//                        updateRecyclerView();
                    }
                    statusAdpater.notifyDataSetChanged();
                    showLog("-->statuses size:" + statuses.size());
                    showLog("-->statusAdpater size:" + statusAdpater.getItemCount());
                } else {
                    //错误信息处理
                    Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                if (statusAdpater != null) {
                    statusAdpater.notifyItemRemoved(statusAdpater.getItemCount());
                }
                isLoading = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", CommonConstants.USER_ID + "");
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                map.put("page", currPage + "");
                return map;
            }
        };
        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
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
}

