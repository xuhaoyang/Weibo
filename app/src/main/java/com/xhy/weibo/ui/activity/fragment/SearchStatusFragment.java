package com.xhy.weibo.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.ui.activity.StatusDetailActivity;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.ui.vh.FootViewHolder;
import com.xhy.weibo.ui.vh.StatusViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.ErrorUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;


/**
 * Created by xuhaoyang on 16/5/22.
 */
public class SearchStatusFragment extends ListFragment<ViewHolder, Status, Result<List<Status>>> implements OnListItemClickListener, PushMessage<Status> {

    private final static String TAG = SearchStatusFragment.class.getSimpleName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    @ViewById(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    private Bundle fromBundle;
    private String keyword;
    private int currentPage = 1;

    public SearchStatusFragment() {
    }


    public static SearchStatusFragment newInstance() {
        return new SearchStatusFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        fromBundle = getArguments();
        keyword = fromBundle.getString(Constants.SEARCH_CONTENT);

        return view;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            StatusViewHolder root = new StatusViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_status, parent, false));
            return root;
        } else if (viewType == TYPE_FOOTER) {
            FootViewHolder root = new FootViewHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.item_footer_loading, parent, false));
            return root;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof StatusViewHolder) {
            ((StatusViewHolder) holder).bind(getActivity(), getItemsSource().get(position), this, 5);

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
    }

    @Override
    public Result<List<Status>> onLoadInBackground() throws Exception {
        int page = 0;

        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 1;
        }

        Call<Result<List<Status>>> resultCall = ApiClient.getApi().
                getSearchStatusList(AppConfig.getAccessToken().getToken(), keyword, currentPage);

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
        Log.e(TAG, ">>>onLoadComplete");
        Log.e(TAG, ">>>" + GsonUtil.toJson(data));

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
    public void onLoadError(Exception e) {
        super.onLoadError(e);
        ErrorUtils.show(getContext(), e);
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (getItemsSource().isEmpty()) {
            return 0;

        }
        return getItemsSource().size() + 1;

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
    public void OnItemOtherClick(final int postion, int type) {
        final Status status = getItemsSource().get(postion);
        StatusViewHolder.bindOnItemOhterClick(getActivity(), status, type, this);
    }

    @Override
    public void pushString(String text) {
        showSnackbar(text);
    }

    @Override
    public void pushResult(boolean b, Status result) {
        if (b) {
//            getItemsSource().remove(result);
            getAdapter().notifyDataSetChanged();
        }
    }


    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }


}
