package com.xhy.weibo.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.base.BaseFragment;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.entity.StatusReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/22.
 */
public class NotifyStatusFragment extends BaseFragment {


    @BindView(R.id.swipeRefreshLayout_searchStatus)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_searchStatus)
    RecyclerView mRecyclerView;

    LinearLayoutManager linearLayoutManager;

    List<Status> statuses = new ArrayList<Status>();
    private View root;
    private StatusAdpater statusAdpater;
    private boolean isLoading = false;
    private int currPage = 1;
    private int totalPage = 1;
    private int lastVisibleItemPosition;

    public NotifyStatusFragment() {
    }


    public static NotifyStatusFragment newInstance() {
        return new NotifyStatusFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search_status, container, false);
        ButterKnife.bind(this, root);

        initRecyclerView();
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
                        && lastVisibleItemPosition + 1 == statusAdpater.getItemCount() && pastVisiblesItems != 0) {
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

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                LoadData();
            }
        });


        return root;
    }

    public void initRecyclerView() {
        statusAdpater = new StatusAdpater(statuses, getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(statusAdpater);
    }

    private void LoadData() {
        GsonRequest<StatusReciver> request = new GsonRequest<StatusReciver>(Request.Method.POST,
                URLs.WEIBO_ATM_LIST,
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
                    }
                    statusAdpater.notifyDataSetChanged();
                } else {
                    //错误信息处理
//                    Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    ToastUtils.showToast(getContext(), response.getError(), Toast.LENGTH_SHORT);

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
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                map.put("page", currPage + "");
                map.put("uid", CommonConstants.USER_ID + "");

                return map;
            }
        };

        VolleyQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}
