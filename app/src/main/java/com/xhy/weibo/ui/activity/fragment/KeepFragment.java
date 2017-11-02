package com.xhy.weibo.ui.activity.fragment;

import android.content.Context;
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
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.event.KeepListChangeEvent;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.ui.activity.StatusDetailActivity;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.ui.vh.KeepViewHolder;
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
public class KeepFragment extends ListFragment<ViewHolder, Status, Result<List<Status>>, CardView> implements OnListItemClickListener, PushMessage<Status> {

    private final static String TAG = KeepFragment.class.getSimpleName();

    public final static int REFRESH_DATA = 100;

    private int wid;
    private int currentPage = 1;
    private StatusDetailActivity mActivity;


    public KeepFragment() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (StatusDetailActivity) context;
        mActivity.setmHandler(mHandler);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
                return new KeepViewHolder(currentView);
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

    /**
     * 覆写方法
     *
     * @param footerView
     */
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
        if (holder instanceof KeepViewHolder) {
            ((KeepViewHolder) holder).bind(getContext(), getItemsSource().get(position), this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //获得当前微博id
        Bundle i = getArguments();
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

        //开启多状态Footer
        setFooterShowEnable(true);
        setLoadingView(R.layout.item_comment_footer_loading);
        setLoadEndView(R.layout.item_comment_footer_end);
        setLoadFailedView(R.layout.item_comment_footer_fail);

    }

    @Override
    public Result<List<Status>> onLoadInBackground() throws Exception {
        int page = 0;
        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 0;
        }
        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getTurnStatusList(wid, AppConfig.getAccessToken().getToken(), page);

        Result<List<Status>> result = null;
        try {
            result = resultCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    @Override
    public void onLoadComplete(Result<List<Status>> data) {

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

    @Override
    public void OnListItemClick(int postion) {

    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_DATA:
                    currentPage = 1;
                    onRefresh();
                    break;
            }
        }
    };


    public static KeepFragment newInstance() {
        return new KeepFragment();
    }


    @Override
    public void pushString(String text) {

    }

    @Override
    public void pushResult(boolean b, Status result) {
        if (b) {
            getAdapter().notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onListChangeEvent(KeepListChangeEvent event) {
        Log.e(TAG, ">>>KeepListChangeEvent");
        onRefresh();
    }

}
