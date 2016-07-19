package com.xhy.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.NormalInfo;
import com.xhy.weibo.entity.Status;
import com.xhy.weibo.entity.StatusReciver;
import com.xhy.weibo.entity.User;
import com.xhy.weibo.entity.UserReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by xuhaoyang on 16/5/21.
 */
public class UserInfoActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {


    public static final String USER_ID = "UID";
    public static final String USER_NAME = "NAME";

    @BindView(R.id.main_Car)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appbarLayout;
    @BindView(R.id.swipeRefreshLayout_ohter_weibo)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_ohter_weibo)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_fans_count)
    TextView tv_fans_count;
    @BindView(R.id.tv_follow_count)
    TextView tv_follow_count;
    @BindView(R.id.tv_weibo_count)
    TextView tv_weibo_count;
    @BindView(R.id.profile_image)
    CircleImageView profile_image;
    @BindView(R.id.btnGZ)
    Button btnGZ;


    LinearLayoutManager linearLayoutManager;
    private boolean isLoading;
    List<Status> statuses = new ArrayList<Status>();
    private StatusAdpater statusAdpater = new StatusAdpater(statuses, this);
    private int lastVisibleItemPosition;
    private int currPage = 1;
    private int totalPage = 1;

    private int mMaxScrollSize;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private Intent userdata;
    private int uid;
    private String username;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_userinfo);
        ButterKnife.bind(this);


        userdata = getIntent();
        uid = userdata.getIntExtra(USER_ID, 0);
        username = userdata.getStringExtra(USER_NAME);
        //请求user信息
        LoadUserData();

        init();
        initListener();
        initRecyclerView();

    }

    private void LoadUserData() {


        GsonRequest<UserReciver> request = new GsonRequest<UserReciver>(Request.Method.POST,
                URLs.WEIBO_GET_USERINFO, UserReciver.class, null, new Response.Listener<UserReciver>() {
            @Override
            public void onResponse(UserReciver response) {
                showLog(response.toString());
                if (response.getCode() == 200) {
                    user = response.getInfo();
                    tv_title.setText(user.getUsername());
                    tv_fans_count.setText(user.getFans() + "");
                    tv_follow_count.setText(user.getFollow() + "");
                    tv_weibo_count.setText(user.getWeibo() + "");
                    setImage(profile_image, URLs.AVATAR_IMG_URL + user.getFace());
                    uid = user.getUid();
                    if(user.getUid()==CommonConstants.USER_ID){
                        btnGZ.setVisibility(View.GONE);
                    }else {
                        btnGZ.setVisibility(View.VISIBLE);
                    }
                    switch (user.getFollowed()) {
                        case 0:
                            btnGZ.setText("关注");
                            break;
                        case 1:
                            btnGZ.setText("取消关注");
                            break;
                    }
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                            LoadData();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (uid != 0) {
                    map.put("uid", uid + "");
                }
                if (!TextUtils.isEmpty(username)) {
                    map.put("username", username);
                }
                map.put("userid", "" + CommonConstants.USER_ID);
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                return map;
            }
        };

        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);

    }


    private void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appbarLayout.addOnOffsetChangedListener(this);

        if (uid == CommonConstants.USER_ID) {
            btnGZ.setVisibility(View.GONE);
        } else {
            btnGZ.setVisibility(View.VISIBLE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initListener() {


        btnGZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GsonRequest<NormalInfo> request = null;
                switch (user.getFollowed()) {
                    case 0:
                        //不涉及分组
                        request = new GsonRequest<NormalInfo>(Request.Method.POST,
                                URLs.WEIBO_ADD_FOLLOW, NormalInfo.class, null,
                                new Response.Listener<NormalInfo>() {
                                    @Override
                                    public void onResponse(NormalInfo response) {
                                        if (response.getCode() == 200) {
                                            Snackbar.make(mCoordinatorLayout, response.getInfo(), Snackbar.LENGTH_LONG)
                                                    .show();
                                            btnGZ.setText("取消关注");
                                            user.setFollowed(1);
                                        } else {
                                            Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                                map.put("uid", CommonConstants.USER_ID + "");
                                map.put("follow", uid + "");
                                return map;
                            }
                        };
                        break;
                    case 1:
                        //取消关注
                        request = new GsonRequest<NormalInfo>(Request.Method.POST,
                                URLs.WEIBO_DEL_FOLLOW, NormalInfo.class, null,
                                new Response.Listener<NormalInfo>() {
                                    @Override
                                    public void onResponse(NormalInfo response) {
                                        if (response.getCode() == 200) {
                                            Snackbar.make(mCoordinatorLayout, response.getInfo(), Snackbar.LENGTH_LONG)
                                                    .show();
                                            btnGZ.setText("关注");
                                            user.setFollowed(0);
                                        } else {
                                            Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
                                                    .show();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                                map.put("current_uid", CommonConstants.USER_ID + "");
                                map.put("be_uid", uid + "");
                                map.put("type", "1");
                                return map;
                            }
                        };
                        break;
                }
                VolleyQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPage = 1;
                LoadData();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition + 1 == statusAdpater.getItemCount() && pastVisiblesItems != 0) {
                    //这里的判断条件还会导致有点BUG,假设条数不足5条,上拉是无法刷新的,只能通过下拉
                    if (!isLoading) {
                        isLoading = true;
                        if (currPage <= totalPage && statuses.size() > 0) {
                            currPage += 1;
                        }
                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                        LoadData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

            }
        });
    }

    public void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(statusAdpater);
    }

    private void LoadData() {
        GsonRequest<StatusReciver> request = new GsonRequest<StatusReciver>(Request.Method.POST,
                URLs.WEIBO_LIST,
                StatusReciver.class, null, new Response.Listener<StatusReciver>() {
            @Override
            public void onResponse(StatusReciver response) {
                if (response.getCode() == 200) {
                    totalPage = response.getTotalPage();
                    if (statuses != null) {
                        if (currPage == 1) {
                            statuses.clear();
                            statuses.addAll(response.getInfo());
                        } else {
                            //要判断是否有重复的
                            for (Status s : response.getInfo()) {
                                if (!statuses.contains(s)) {
                                    statuses.add(s);
                                }
                            }
                        }
                    } else {
                        //第一次获取到数据
                        statuses = response.getInfo();
//                        updateRecyclerView();
                    }
                    statusAdpater.notifyDataSetChanged();
                } else {
                    //错误信息处理
                    Snackbar.make(mCoordinatorLayout, response.getError(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                if (statusAdpater != null) {
                    statusAdpater.notifyItemRemoved(statusAdpater.getItemCount());
                }
                isLoading = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", uid + "");
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                map.put("page", currPage + "");
                map.put("type", "1");
                return map;
            }
        };

        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;
        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
//			mProfileImage.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
        }
    }

    public void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).error(R.drawable.user_avatar).fitCenter().into(view);
    }
}
