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
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.UserAdpater;
import com.xhy.weibo.base.BaseFragment;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.model.User;
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

/**
 * Created by xuhaoyang on 16/5/22.
 */
public class SearchUsersFragment extends BaseFragment {


    public static final String SEARCH_CONTENT = "CONTENT";


    private View root;
    @BindView(R.id.swipeRefreshLayout_searchStatus)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_searchStatus)
    RecyclerView mRecyclerView;

    LinearLayoutManager linearLayoutManager;

    List<User> users = new ArrayList<User>();
    private UserAdpater userAdpater;

    private boolean isLoading = false;
    private Bundle fromBundle;
    private String keyword;
    private int currPage = 1;
    private int totalPage = 1;
    private int lastVisibleItemPosition;

    public SearchUsersFragment() {
    }


    public static SearchUsersFragment newInstance() {
        return new SearchUsersFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search_status, container, false);
        ButterKnife.bind(this, root);

        fromBundle = getArguments();
        keyword = fromBundle.getString(SEARCH_CONTENT);
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


        return root;
    }

    public void initRecyclerView() {
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        userAdpater = new UserAdpater(getContext(), users);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(userAdpater);
    }

    private void LoadData() {
        GsonRequest<UsersReciver> request = new GsonRequest<UsersReciver>(Request.Method.POST,
                URLs.WEIBO_USER_SEARCH_LIST,
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
//                    Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    ToastUtils.showToast(getContext(), response.getError(), Toast.LENGTH_SHORT);

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("token", AppConfig.ACCESS_TOKEN.getToken());
                map.put("page", currPage + "");
                map.put("keyword", keyword);
                map.put("uid", AppConfig.getUserId() + "");

                return map;
            }
        };

        VolleyQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}
