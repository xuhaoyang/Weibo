package com.xhy.weibo.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.model.Hot;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.ui.base.ListActivity;
import com.xhy.weibo.ui.vh.HotViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.Logger;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;

public class HotsActivity extends ListActivity<HotViewHolder, Hot, Result<List<Hot>>> implements OnListItemClickListener {

    private final String TAG = this.getClass().getSimpleName();


    @ViewById(R.id.hot_Car)
    CoordinatorLayout mCoordinatorLayout;

    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hots);
        getSupportActionBar().setTitle("热门");

        //设置item间间隔样式
        getRecyclerView().addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.VERTICAL));
        //设置下拉刷新颜色
        getPullToRefreshLayout().setColorSchemeResources(R.color.colorPrimary);
        /* 解决刷新动画出不来的问题 */
        getPullToRefreshLayout().setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        setMode(PullToRefreshMode.PULL_FROM_START);
        initLoader();


    }

    @Override
    public Result<List<Hot>> onLoadInBackground() throws Exception {
        int page = 0;
        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 1;
        }

        Call<Result<List<Hot>>> resultCall = ApiClient.getApi().getHotList(AppConfig.getAccessToken().getToken());
        Result<List<Hot>> result = null;
        try {
            result = resultCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onLoadComplete(Result<List<Hot>> data) {
        Logger.show(TAG, ">>>onLoadComplete");

        if (data != null) {
            if (data.isSuccess()) {
                if (!isLoadMore()) {
                    getItemsSource().clear();
                } else if (data.getInfo().size() > 0) {
                    currentPage += 1;
                }

                getItemsSource().addAll(data.getInfo());

                if (getItemsSource().size() == 0) {
                    setEmptyText("暂时没有热点话题");
                    setEmptyShow(true);
                }


            }
        }
        getAdapter().notifyDataSetChanged();
        onRefreshComplete();

    }

    @Override
    public HotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav, parent, false);
        return new HotViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(HotViewHolder holder, int position) {
        holder.bind(getItemsSource().get(position), this);
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

    @Override
    public void OnListItemClick(int postion) {
        final Hot hotModel = getItemsSource().get(postion);
        ActivityUtils.startActivity(this, SearchActivity.class, new HashMap<String, Object>() {
            {
                put(Constants.SEARCH_CONTENT, hotModel.getKeyword());
            }
        });
    }
}
