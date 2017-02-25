package com.xhy.weibo.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.ui.base.ListActivity;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.ui.vh.StatusViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.utils.ViewUtils;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;


public class KeepStatusActivity extends ListActivity<ViewHolder, Status, Result<List<Status>>>
        implements OnListItemClickListener, PushMessage<Status> {

    private final String TAG = this.getClass().getSimpleName();

    public static final int REFRESH_DATA = 1;


    @ViewById(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_status);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("收藏");

        //设置item间间隔样式
        getRecyclerView().addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.VERTICAL));
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_ITEM:
                View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
                return new StatusViewHolder(currentView);
            case TYPE_FOOTER:
                if (mFooterLayout == null) {
                    mFooterLayout = new RelativeLayout(this);
                }
                ViewHolder viewHolder = ViewHolder.create(mFooterLayout);
                return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof StatusViewHolder) {
            ((StatusViewHolder) holder).bind(this, getItemsSource().get(position), this, 5);
        }
    }

    private boolean isThrows = true;

    @Override
    public Result<List<Status>> onLoadInBackground() throws Exception {

        int page = 0;

        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 1;
        }

//        if (!isEmpty()) {
//            Thread.sleep(1000);
//            if (getItemCount() >= 10 && isThrows) {
//                isThrows = false;
//                throw new Exception("Test error");
//            }
//        }

        Call<Result<List<Status>>> resultCall = ApiClient.getApi().
                getKeepStatusListByUid(AppConfig.getUserId(), page, AppConfig.getAccessToken().getToken());


        Result<List<Status>> result = null;
        try {
            result = resultCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    @Override
    public void onLoadComplete(final Result<List<Status>> data) {
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

    @Override
    public void onLoadError(Exception e) {
        super.onLoadError(e);
    }


    @Override
    public void OnListItemClick(int postion) {
        final Status status = getItemsSource().get(postion);


        //item点击跳转
        ActivityUtils.startActivity(this, StatusDetailActivity.class, new HashMap<String, Object>() {
            {
                put(Constants.STATUS_INTENT, GsonUtil.toJson(status));
            }
        });
    }

    @Override
    public void OnItemOtherClick(final int postion, int type) {
        final Status status = getItemsSource().get(postion);
        StatusViewHolder.bindOnItemOhterClick(this, status, type, this);
    }

    @Override
    public void pushString(String text) {
        showSnackbar(text);
    }

    @Override
    public void pushResult(boolean b, Status result) {
        if (b) {
            getItemsSource().remove(result);
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }


}

