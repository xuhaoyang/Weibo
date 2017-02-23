package com.xhy.weibo.ui.activity.fragment;

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
import com.xhy.weibo.model.Result;
import com.xhy.weibo.ui.activity.UserInfoActivity;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.vh.SearchViewHolder;
import com.xhy.weibo.model.User;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.utils.ViewUtils;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import retrofit2.Call;

/**
 * Created by xuhaoyang on 16/5/22.
 */
public class SearchUsersFragment extends ListFragment<ViewHolder, User, Result<List<User>>> implements OnListItemClickListener {


    private static final String TAG = SearchUsersFragment.class.getSimpleName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;


    private Bundle fromBundle;
    private String keyword;
    private int currPage = 1;

    public SearchUsersFragment() {
    }


    public static SearchUsersFragment newInstance() {
        return new SearchUsersFragment();
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
//        if (viewType == TYPE_ITEM) {
//            SearchViewHolder root = new SearchViewHolder(
//                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user, parent, false));
//            return root;
//        } else if (viewType == TYPE_FOOTER) {
//            FootViewHolder root = new FootViewHolder(
//                    LayoutInflater.from(parent.getContext()).
//                            inflate(R.layout.item_comment_footer_end, parent, false));
//            return root;
//        }
        mParentContext = parent.getContext();
        switch (viewType) {
            case TYPE_ITEM:
                SearchViewHolder root = new SearchViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user, parent, false));
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
        if (holder instanceof SearchViewHolder) {
            ((SearchViewHolder) holder).bind(getItemsSource().get(position), this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
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
    public Result<List<User>> onLoadInBackground() throws Exception {
        int page = 0;
        if (isLoadMore()) {
            page = currPage + 1;
        } else {
            page = 0;
        }

        Call<Result<List<User>>> resultCall =
                ApiClient.getApi().getSearchUserList(AppConfig.getAccessToken().getToken(),
                        keyword, AppConfig.getUserId(), page);
        Result<List<User>> result = null;
        try {
            result = resultCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }


    @Override
    public void onLoadComplete(Result<List<User>> data) {
        Log.e(TAG, ">>>onLoadComplete");
        Log.e(TAG, ">>>" + GsonUtil.toJson(data));

        if (data != null) {
            if (data.isSuccess()) {
                if (!isLoadMore()) {
                    getItemsSource().clear();
                    currPage = 1;

                } else if (data.getInfo().size() > 0) {
                    currPage += 1;

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
//        ErrorUtils.show(getContext(), e);
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position + 1 == getItemCount()) {
//            return TYPE_FOOTER;
//        } else {
//            return TYPE_ITEM;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (!getItemsSource().isEmpty()) {
//            return getItemsSource().size() + 1;
//        }
//
//
//        return 0;
//    }

    //    public void initRecyclerView() {
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
//        userAdpater = new UserAdpater(getContext(), users);
//        linearLayoutManager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mRecyclerView.setAdapter(userAdpater);
//    }

//    private void LoadData() {
//
//        UserLoginLogic.getSearchUserList(getContext(), AppConfig.getUserId(), keyword, currPage, AppConfig.getAccessToken().getToken(), this);
//
//    }
//
//    @Override
//    public void onSearchUserSuccess(List<User> users, int totalPage) {
//        this.totalPage = totalPage;
//        if (currPage == 1) {
//            this.users.clear();
//            this.users.addAll(users);
//        } else {
//            for (User u : users) {
//                if (!this.users.contains(u)) {
//                    this.users.add(u);
//                }
//            }
//        }
//        userAdpater.notifyDataSetChanged();
//        stopRefresh();
//
//    }
//
//    @Override
//    public void onSearchUserFailure(String message) {
//        showLog(message);
//        stopRefresh();
//    }
//
//    @Override
//    public void onSearchUserError(Throwable error) {
//        showLog(error.getMessage());
//        stopRefresh();
//    }
//
//    private void stopRefresh() {
//        isLoading = false;
//        mSwipeRefreshLayout.setRefreshing(false);
//    }

    @Override
    public void OnListItemClick(final int postion) {
        ActivityUtils.startActivity(getActivity(), UserInfoActivity.class, new HashMap<String, Object>() {
            {
                put(Constants.USER_ID, getItemsSource().get(postion).getUid());
            }
        });
    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }
}
