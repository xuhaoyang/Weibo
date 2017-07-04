package com.xhy.weibo.ui.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.event.CommentListChangeEvent;
import com.xhy.weibo.model.Comment;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.vh.CommentViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.RecycleViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

import hk.xhy.android.common.ui.vh.OnListItemClickListener;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.widget.PullToRefreshMode;
import retrofit2.Call;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class CommentFragment extends ListFragment<ViewHolder, Comment, Result<List<Comment>>, CardView> implements OnListItemClickListener {

    private static final String TAG = CommentFragment.class.getSimpleName();

    public final static int REFRESH_DATA = 100;


    private int wid;
    private int currentPage = 1;

    public CommentFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        return view;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                CommentViewHolder root = new CommentViewHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.item_comment, parent, false));
                return root;
            case TYPE_FOOTER:
                if (mFooterLayout == null) {
                    mFooterLayout = new CardView(parent.getContext());
                    mFooterLayout.setLayoutParams(
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                ViewHolder viewHolder = ViewHolder.create(mFooterLayout);
                return viewHolder;
        }

        return null;
    }

    @Override
    public void addFooterView(View footerView) {

        Context mContext = mParentContext == null ? getContext() : mParentContext;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (footerView == null) {
            return;
        }
        if (mFooterLayout == null) {
            mFooterLayout = new CardView(mContext);
            mFooterLayout.setLayoutParams(params);
        }
        removeFooterView();

        mFooterLayout.addView(footerView, params);
        mFooterLayout.requestLayout();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).bind(getContext(), getItemsSource().get(position), this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle i = getArguments();
        wid = i.getInt(Constants.STATUS_ID);

        //设置item间间隔样式
        getRecyclerView().addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        //设置下拉刷新颜色
        getPullToRefreshLayout().setColorSchemeResources(R.color.colorPrimary);
        /* 解决刷新动画出不来的问题 */
        getPullToRefreshLayout().setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        setMode(PullToRefreshMode.BOTH);
        initLoader();

        setFooterShowEnable(true);
        setLoadingView(R.layout.item_comment_footer_loading);
        setLoadEndView(R.layout.item_comment_footer_end);
        setLoadFailedView(R.layout.item_comment_footer_fail);
    }

    @Override
    public Result<List<Comment>> onLoadInBackground() throws Exception {
        int page = 0;
        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 0;
        }

        Call<Result<List<Comment>>> resultCall = ApiClient.getApi().
                getStatusOnlyCommentList(AppConfig.getAccessToken().getToken(), wid);
        Result<List<Comment>> result = null;
        try {
            result = resultCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    @Override
    public void onLoadComplete(Result<List<Comment>> data) {
        if (data != null) {
            if (data.isSuccess()) {
                if (!isLoadMore()) {
                    getItemsSource().clear();
                    currentPage = 1;
                } else if (data.getInfo().size() > 0) {
                    currentPage += 1;
                }

                getItemsSource().addAll(data.getInfo());

            }
            getAdapter().notifyDataSetChanged();
            onRefreshComplete();

        }
    }

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }


    @Override
    public void OnListItemClick(int postion) {

    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }

    @Subscribe
    public void onListChangeEvent(CommentListChangeEvent event) {
        Log.e(TAG, ">>>CommentListChangeEvent");
        onRefresh();
    }
}
