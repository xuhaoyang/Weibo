package com.xhy.weibo.ui.activity.fragment;

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
import com.xhy.weibo.ui.activity.StatusDetailActivity;
import com.xhy.weibo.adapter.CommentAdpater;
import com.xhy.weibo.ui.base.BaseFragment;
import com.xhy.weibo.logic.CommentLogic;
import com.xhy.weibo.model.Comment;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hk.xhy.android.commom.widget.PullToRefreshLayout;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class CommentFragment extends BaseFragment implements CommentLogic.GetCommentCallBack {

    public final static String WID = "wid";
    public final static int REFRESH_DATA = 100;

    View root;
    @BindView(R.id.pull_to_refresh)
    PullToRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;


    private int wid;
    private int currPage = 1;
    private boolean isLoading = false;
    private int totalPage = 1;
    private int lastVisibleItemPosition;
    private List<Comment> comments = new ArrayList<>();
    private CommentAdpater commentAdpater;
    private StatusDetailActivity mActivity;


    public CommentFragment() {
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
                        && lastVisibleItemPosition + 1 == commentAdpater.getItemCount() && pastVisiblesItems != 0) {
                    //这里的判断条件还会导致有点BUG,假设条数不足5条,上拉是无法刷新的,只能通过下拉
                    if (!isLoading) {
                        isLoading = true;
                        if (currPage <= totalPage && comments.size() > 0) {
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
        commentAdpater = new CommentAdpater(getContext(), comments);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(commentAdpater);
    }

    private void LoadData() {
        CommentLogic.getStatusOnlyCommentList(getContext(), wid, AppConfig.getAccessToken().getToken(), this);
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


    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    @Override
    public void onGetCommentSuccess(List<Comment> comments, int totalPage) {
        this.totalPage = totalPage;
        if (currPage == 1) {
            this.comments.clear();
            this.comments.addAll(comments);
            commentAdpater.setLastAnimatedPosition(-1);
        } else {
            //要判断是否有重复的
            for (Comment c : comments) {
                if (!this.comments.contains(c)) {
                    this.comments.add(c);
                }
            }
        }
        commentAdpater.notifyDataSetChanged();
        stopRrefresh();
    }

    @Override
    public void onGetCommentFailure(String message) {
        showLog(message);
        stopRrefresh();
    }

    @Override
    public void onGetCommentError(Throwable t) {
        showLog(t.getMessage());
        stopRrefresh();
    }

    private void stopRrefresh() {
        isLoading = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
