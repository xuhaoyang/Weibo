package com.xhy.weibo.activity;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.HotAdpater;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.Hot;
import com.xhy.weibo.entity.HotReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        GsonRequest<HotReciver> request = new GsonRequest<HotReciver>(Request.Method.POST,
                URLs.WEIBO_GET_HOTS, HotReciver.class, null, new Response.Listener<HotReciver>() {
            @Override
            public void onResponse(HotReciver response) {
                if (response.getCode() == 200) {
                    hots.clear();
                    hots.addAll(response.getInfo());
                    hotAdpater.notifyDataSetChanged();
                    showLog("-->" + hots.toString());
                } else {
                    Snackbar.make(mCoordinatorLayout, "没有啦", Snackbar.LENGTH_LONG)
                            .show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token", AppConfig.ACCESS_TOKEN.getToken());
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
