package com.xhy.weibo.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.ui.base.BaseFragment;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/22.
 */
public class NotifyStatusFragment extends BaseFragment implements StatusLogic.GetAtStatusListCallBack {


    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list)
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

        StatusLogic.getAtStatusList(getContext(), AppConfig.getUserId(), currPage,
                AppConfig.getAccessToken().getToken(), this);

    }

    @Override
    public void onGetAtStatusSuccess(List<Status> statuses, int totalPage) {
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
    public void onGetAtStatusFailure(String message) {
        showLog(message);
        stopRefresh();
    }

    @Override
    public void onGetAtStatusError(Throwable t) {
        showLog(t.getMessage());
        stopRefresh();
    }

    private void stopRefresh() {
        isLoading = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }
}