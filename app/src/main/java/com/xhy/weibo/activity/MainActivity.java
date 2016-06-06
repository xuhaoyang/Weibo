package com.xhy.weibo.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.StatusAdpater;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.entity.Login;
import com.xhy.weibo.entity.StatusGroup;
import com.xhy.weibo.entity.StatusGroupReciver;
import com.xhy.weibo.entity.StatusReciver;
import com.xhy.weibo.entity.Status;
import com.xhy.weibo.entity.User;
import com.xhy.weibo.entity.UserReciver;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.service.MessageService;
import com.xhy.weibo.utils.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_WRITE_FORWARD = 2;
    public static final int REQUEST_CODE_WRITE_STATUS = 3;

    public static final int GROUP = 0;
    public static final int ALL_ITEMID = 0;

//    RecyclerView mNavRecyclerView;

    @BindView(R.id.recycler_view_home)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_Car)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.swipeRefreshLayout_home)
    SwipeRefreshLayout mSwipeRefreshLayout;

    Button btnHot;
    Button btnKeep;
    Button btnSettings;
    ImageView headerView_iv_avatar;
    TextView headerView_tv_username;

    SearchView actionView;
    View headerView;
    LinearLayoutManager linearLayoutManager;

    List<Status> statuses = new ArrayList<Status>();
    private ActionBarDrawerToggle toggle;
    private List<StatusGroup> statusGroups;
    private String gid = "";
    //    private HotAdpater navAdpater ;
    private StatusAdpater statusAdpater = new StatusAdpater(statuses, this);
    private boolean isLoading;
    private Handler handler = new Handler();
    private int currPage = 1;
    private int totalPage = 1;
    private int lastVisibleItemPosition;
    private SearchView.SearchAutoComplete mEditSearch;
    private DBManager dbManager;
    private SQLiteDatabase db;
    private UserDB userDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initView();
        initNavigationMenu();
        initListener();
        initRecyclerView();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                LoadData();
            }
        });

        Intent intent = new Intent(this, MessageService.class);
        intent.putExtra("ACCOUNT", CommonConstants.account);
        intent.putExtra("PASSWORD", CommonConstants.password);
        intent.putExtra("USERID", CommonConstants.USER_ID + "");
        intent.putExtra("TOKEN", CommonConstants.ACCESS_TOKEN.getToken());
        startService(intent);
    }

    private void initView() {
        setSupportActionBar(toolbar);

        headerView = navigationView.getHeaderView(0);
        btnHot = (Button) headerView.findViewById(R.id.btnHot);
        btnKeep = (Button) headerView.findViewById(R.id.btnKeep);
        btnSettings = (Button) headerView.findViewById(R.id.btnSettings);
        headerView_iv_avatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        headerView_tv_username = (TextView) headerView.findViewById(R.id.username);

        initDB();
        List<User> users = userDB.QueryUsers(db, "id=?", new String[]{CommonConstants.USER_ID + ""});
        dbManager.closeDatabase();
        if (!users.isEmpty()) {
            long time = System.currentTimeMillis() - users.get(0).getUptime();
            if (time > 0) {
                initUserInfo();
            } else {
                String url = URLs.AVATAR_IMG_URL + users.get(0).getFace();
                ImageUtils.setImage(headerView_iv_avatar, url);
                headerView_tv_username.setText(users.get(0).getUsername());
            }
        } else {
            initUserInfo();
        }


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    private void initUserInfo() {
        GsonRequest<UserReciver> request = new GsonRequest<UserReciver>(Request.Method.POST,
                URLs.WEIBO_GET_USERINFO, UserReciver.class, null, new Response.Listener<UserReciver>() {
            @Override
            public void onResponse(UserReciver response) {
                showLog(response.toString());
                if (response.getCode() == 200) {
                    User user = response.getInfo();
                    initDB();
                    List<User> users = userDB.QueryUsers(db, "id=?", new String[]{user.getUid() + ""});
                    if (!users.isEmpty()) {
                        userDB.updateUser(db, user);
                    } else {
                        userDB.insertUser(db, user);
                    }
                    String url = URLs.AVATAR_IMG_URL + user.getFace();
                    ImageUtils.setImage(headerView_iv_avatar, url);
                    headerView_tv_username.setText(user.getUsername());
                    dbManager.closeDatabase();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLog(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("uid", CommonConstants.USER_ID + "");
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                return map;
            }
        };
        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
    }

    private void initDB() {
        //获取数据
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        db = dbManager.getDatabase();
        userDB = new UserDB(this);
    }

    private void initNavigationMenu() {
        final Menu menu = navigationView.getMenu();
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(GROUP, ALL_ITEMID, ALL_ITEMID, "全部");
        GsonRequest<StatusGroupReciver> request = new GsonRequest<StatusGroupReciver>(Request.Method.POST,
                URLs.WEIBO_GET_GROUP, StatusGroupReciver.class, null, new Response.Listener<StatusGroupReciver>() {
            @Override
            public void onResponse(StatusGroupReciver response) {
                showLog("-->" + response.toString());
                if (response.getCode() == 200) {
                    statusGroups = response.getInfo();
                    for (StatusGroup sg : statusGroups) {
                        menu.add(GROUP, sg.getId(), sg.getId(), sg.getName());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", CommonConstants.USER_ID + "");
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                return map;
            }
        };

        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);

    }

    private void LoadData() {

//        NetParams.getWeiboList(CommonConstants.USER_ID,
        //CommonConstants.TOKEN, begin, CommonConstants.STATUS_COUNT_PAGE)
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
                    }
                    statusAdpater.notifyDataSetChanged();
                    showLog("-->statuses size:" + statuses.size());
                    showLog("-->statusAdpater size:" + statusAdpater.getItemCount());
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
                MainActivity.this.showLog("-->" + error.toString());
                isLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", CommonConstants.USER_ID + "");
                map.put("token", CommonConstants.ACCESS_TOKEN.getToken());
                map.put("page", currPage + "");
                if (!TextUtils.isEmpty(gid)) {
                    map.put("gid", gid);
                }
                return map;
            }
        };
        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
    }

    private void initListener() {
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent(MainActivity.this, WriteStatusActivity.class);
                data.putExtra(WriteStatusActivity.TYPE, WriteStatusActivity.NEW_STATUS_TYPE);
                startActivityForResult(data, REQUEST_CODE_WRITE_STATUS);
            }
        });

        btnHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HotsActivity.class));
            }
        });
        btnKeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, KeepStatusActivity.class));
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
//                showLog("StateChanged = " + newState);
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_WRITE_FORWARD:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        currPage = 1;
                        LoadData();
                        linearLayoutManager.scrollToPosition(0);
                    }
                });
                break;
            case REQUEST_CODE_WRITE_STATUS:
                boolean sendSuccess = data.getBooleanExtra(WriteStatusActivity.SEND_STATUS_SUCCESS, false);
                if (sendSuccess) {
                    Snackbar.make(mCoordinatorLayout, "发送成功", Snackbar.LENGTH_LONG)
                            .show();
                    mSwipeRefreshLayout.setRefreshing(true);
                    currPage = 1;
                    LoadData();
                    linearLayoutManager.scrollToPosition(0);
                } else {
                    Snackbar.make(mCoordinatorLayout, "发送失败", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        actionView = (SearchView) MenuItemCompat.getActionView(item);
        mEditSearch = (SearchView.SearchAutoComplete) actionView.findViewById(R.id.search_src_text);
        actionView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    return false;
                }

                Intent data = new Intent(MainActivity.this, SearchActivity.class);
                data.putExtra(SearchActivity.SEARCH_CONTENT, query);
                startActivity(data);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (id) {
            case R.id.action_notifications:
                intent2Activity(NotifyActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        currPage = 1;
        int id = item.getItemId();
        showLog("onNavigationItemSelected:" + id);
        switch (id) {
            case ALL_ITEMID:
                gid = "";
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        LoadData();
                    }
                });
                break;
            default:
                gid = id + "";
                statuses.clear();
                statusAdpater.notifyDataSetChanged();
                showLog("--> currPage" + currPage);
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        LoadData();
                    }
                });
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
