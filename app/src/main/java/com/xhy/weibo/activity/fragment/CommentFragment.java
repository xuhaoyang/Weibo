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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.activity.StatusDetailActivity;
import com.xhy.weibo.adapter.CommentAdpater;
import com.xhy.weibo.base.BaseFragment;
import com.xhy.weibo.entity.Comment;
import com.xhy.weibo.entity.CommentReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.NetParams;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.utils.Logger;
import com.xhy.weibo.utils.RecycleViewDivider;
import com.xhy.weibo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class CommentFragment extends BaseFragment {

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
                LoadData(currPage = 1, wid);
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
                        LoadData(currPage, wid);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
        LoadData(1, wid);
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

    private void LoadData(final int page, int wid) {
        Logger.show("当前页", "当前页:" + page);
        GsonRequest<CommentReciver> request = new GsonRequest<CommentReciver>(Request.Method.GET,
                NetParams.getComment(wid, page, AppConfig.ACCESS_TOKEN.getToken()), CommentReciver.class, null, new Response.Listener<CommentReciver>() {
            @Override
            public void onResponse(CommentReciver response) {
                Logger.show(getClass().getName(), response.toString());
                if (response.getCode() == 200) {
                    totalPage = response.getTotalPage();
                    if (comments != null) {
                        if (page == 1) {
                            comments.clear();
                            comments.addAll(response.getInfo());
                            commentAdpater.setLastAnimatedPosition(-1);
                        } else {
                            //要判断是否有重复的
                            for (Comment c : response.getInfo()) {
                                if (!comments.contains(c)) {
                                    comments.add(c);
                                }
                            }
                        }
                    } else {
                        //第一次获取到数据
                        comments = response.getInfo();
                    }
                    commentAdpater.notifyDataSetChanged();
                } else {
                    if (comments.size() != 0) {
                        ToastUtils.showToast(getContext(), response.getError(), Toast.LENGTH_SHORT);
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
//                Logger.show(getClass().getName(),commentAdpater.getItemCount()+"个");
                isLoading = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
            }
        });

        VolleyQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    LoadData(1, wid);
                    break;
            }
        }
    };


    public static CommentFragment newInstance() {
        return new CommentFragment();
    }
}
