package com.xhy.weibo.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.ui.base.ListActivity;
import com.xhy.weibo.ui.vh.KeepStatusViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.Logger;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;


public class KeepStatusActivity extends ListActivity<KeepStatusViewHolder, Status, Result<List<Status>>>
        implements OnListItemClickListener {

    private final String TAG = this.getClass().getSimpleName();

    public static final int REFRESH_DATA = 1;


    @ViewById(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;


    private int currentPage = 1;

//    public Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case REFRESH_DATA:
//                    Snackbar.make(mCoordinatorLayout, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
//                    currentPage = 1;
//
//                    onRefresh();
//                    break;
//            }
//        }
//    };


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

//        init();
//        initListener();
//        initRecyclerView();
//        mSwipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mSwipeRefreshLayout.setRefreshing(true);
//                LoadData();
//            }
//        });

    }

    @Override
    public KeepStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new KeepStatusViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(KeepStatusViewHolder holder, int position) {
        holder.bind(this, getItemsSource().get(position), this);
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
        switch (type) {
            case Constants.ITEM_LIKE_TPYE:
                if (status.isKeep()) {

                    StatusLogic.delKeepStatus(this, AppConfig.getUserId(),
                            status.getId(), AppConfig.getAccessToken().getToken(),
                            new StatusLogic.DelKeepStatusCallBack() {
                                @Override
                                public void onDelKeepSuccess(Result result) {
                                    if (result.isSuccess()) {
                                        getItemsSource().remove(status);
                                        getAdapter().notifyDataSetChanged();
                                        showSnackbar(result.getMsg());
                                    }
                                }

                                @Override
                                public void onDelKeepFailure(String message) {
                                    showSnackbar(message);
                                }

                                @Override
                                public void onDelKeepError(Throwable t) {
                                    Logger.show(TAG, t.getMessage(), Log.ERROR);

                                }
                            });

                }
                break;
            case Constants.ITEM_COMMENT_TPYE:

                if (status.getComment() > 0) {
                    //跳转到评论页
                    ActivityUtils.startActivity(this, StatusDetailActivity.class, new HashMap<String, Object>() {
                        {
                            put(Constants.STATUS_INTENT, GsonUtil.toJson(status));
                        }
                    });
                } else {
                    //发评论
                    ActivityUtils.startActivity(this, WriteStatusActivity.class, new HashMap<String, Object>() {
                        {
                            put(Constants.STATUS_INTENT, GsonUtil.toJson(status));
                            put(Constants.TYPE, Constants.COMMENT_TYPE);
                            put(Constants.TAG, Constants.MAIN_ATY_CODE);
                        }
                    });
                }
                break;

            case Constants.ITEM_FORWARD_TPYE:
                /**
                 * ...尚未写完
                 */

                ActivityUtils.startActivity(this, WriteStatusActivity.class, new HashMap<String, Object>() {
                    {
                        put(Constants.TYPE, Constants.FORWARD_TYPE);
                        put(Constants.STATUS_INTENT, GsonUtil.toJson(status));

                    }
                }, Constants.REQUEST_CODE_WRITE_FORWARD);


                break;


        }
    }


//    private void LoadData() {
//
//        StatusLogic.getKeepStatusListByUid(this, AppConfig.getUserId(), currPage,
//                AppConfig.getAccessToken().getToken(), this);
//    }


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

