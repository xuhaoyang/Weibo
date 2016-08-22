package com.xhy.weibo.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.activity.StatusDetailActivity;
import com.xhy.weibo.adapter.KeepListAdpater;
import com.xhy.weibo.base.BaseFragment;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class KeepFragment extends BaseFragment implements StatusLogic.GetTurnStatusCallBack {

    private View root;
    @BindView(R.id.swipeRefreshLayout_comment)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_comment)
    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;

    public final static String WID = "wid";
    public final static int REFRESH_DATA = 100;

    private int wid;
    private int currPage = 1;
    private boolean isLoading = false;
    private int totalPage = 1;
    private int lastVisibleItemPosition;
    private List<Status> statuses = new ArrayList<>();
    private KeepListAdpater keepListAdpater;
    private StatusDetailActivity mActivity;


    public KeepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.bind(this, root);
        //获得当前微博id
        final Bundle i = getArguments();
        wid = i.getInt(WID);
        initRecyclerView();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition + 1 == keepListAdpater.getItemCount() && pastVisiblesItems != 0) {
                    //这里的判断条件还会导致有点BUG,假设条数不足5条,上拉是无法刷新的,只能通过下拉
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
                isLoading = true;
                LoadData();
            }
        });
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (StatusDetailActivity) activity;
        ((StatusDetailActivity) activity).setmHandler(mHandler);
    }


    public void initRecyclerView() {
        keepListAdpater = new KeepListAdpater(getContext(), statuses);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(keepListAdpater);
    }

    private void LoadData() {

        StatusLogic.getTurnStatusList(getContext(), wid, currPage, AppConfig.ACCESS_TOKEN.getToken(), this);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    currPage = 1;
                    LoadData();
                    break;
            }
        }
    };


    public static KeepFragment newInstance() {
        return new KeepFragment();
    }

    @Override
    public void onTurnStatusListSuccess(List<Status> statuses, int totalPage) {
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
        keepListAdpater.notifyDataSetChanged();
        stopRefresh();
    }


    @Override
    public void onTurnStatusListFailure(String message) {
        showLog(message);
        stopRefresh();
    }

    @Override
    public void onTurnStatusListError(Throwable t) {
        showLog(t.getMessage());
        stopRefresh();
    }

    private void stopRefresh() {
        isLoading = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
