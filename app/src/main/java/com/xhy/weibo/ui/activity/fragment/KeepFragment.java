package com.xhy.weibo.ui.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.KeepListAdpater;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.ui.activity.StatusDetailActivity;
import com.xhy.weibo.ui.base.BaseFragment;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.ui.vh.StatusViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class KeepFragment extends ListFragment<ViewHolder, Status, Result<List<Status>>, RelativeLayout> implements OnListItemClickListener, PushMessage<Status> {


    public final static int REFRESH_DATA = 100;

    private int wid;
    private int currentPage = 1;
    private StatusDetailActivity mActivity;


    public KeepFragment() {
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
                View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
                return new StatusViewHolder(currentView);
            case TYPE_FOOTER:
                if (mFooterLayout == null) {
                    mFooterLayout = new RelativeLayout(getContext());
                }
                ViewHolder viewHolder = ViewHolder.create(mFooterLayout);
                return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof StatusViewHolder) {
            ((StatusViewHolder) holder).bind(getContext(), getItemsSource().get(position), this, 5);
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

        setFooterShowEnable(true);
        setLoadingView(R.layout.item_footer_loading);
        setLoadEndView(R.layout.item_footer_end);
        setLoadFailedView(R.layout.item_footer_fail);

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
        final Status status = getItemsSource().get(postion);

        //item点击跳转
        ActivityUtils.startActivity(getActivity(), StatusDetailActivity.class, new HashMap<String, Object>() {
            {
                put(Constants.STATUS_INTENT, GsonUtil.toJson(status));
            }
        });
    }

    @Override
    public void OnItemOtherClick(int postion, int type) {
        final Status status = getItemsSource().get(postion);
        StatusViewHolder.bindOnItemOhterClick(getActivity(), status, type, this);
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

}
