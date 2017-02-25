package com.xhy.weibo.ui.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.xhy.weibo.adapter.CommentAdpater;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.logic.CommentLogic;
import com.xhy.weibo.model.Comment;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.User;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.vh.CommentViewHolder;
import com.xhy.weibo.ui.vh.SearchViewHolder;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class NotifyCommentFragment extends ListFragment<ViewHolder, Comment, Result<List<Comment>>> implements OnListItemClickListener {

    private static final String TAG = NotifyCommentFragment.class.getSimpleName();

    public final static int REFRESH_DATA = 100;

    private int currentPage = 1;


    public NotifyCommentFragment() {
    }


    public static NotifyCommentFragment newInstance() {
        return new NotifyCommentFragment();
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
//                    mFooterLayout = (CardView) ViewUtils.inflate(parent, R.layout.item_comment_cardview);
                }
                ViewHolder viewHolder = ViewHolder.create(mFooterLayout);
                return viewHolder;
        }

        return null;
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
        setLoadingView(R.layout.item_footer_loading);
        setLoadEndView(R.layout.item_comment_footer_end);

    }

    @Override
    public Result<List<Comment>> onLoadInBackground() throws Exception {
        int page = 0;
        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 0;
        }

        Call<Result<List<Comment>>> resultCall = ApiClient.getApi().getUserCommentList(AppConfig.getAccessToken().getToken(),
                AppConfig.getUserId(), page);

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
        Log.e(TAG, ">>>onLoadComplete");

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

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    currentPage = 1;
                    break;
            }
        }
    };


    @Override
    public void OnListItemClick(int postion) {
        /**
         * 暂时不需要
         */
    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }
}
