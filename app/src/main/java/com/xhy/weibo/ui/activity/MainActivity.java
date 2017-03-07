package com.xhy.weibo.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.event.StatusMainListChangeEvent;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.model.StatusGroup;
import com.xhy.weibo.model.User;
import com.xhy.weibo.api.URLs;
import com.xhy.weibo.service.MessageService;
import com.xhy.weibo.ui.base.ListActivity;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.ui.vh.StatusViewHolder;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.ImageUtils;
import com.xhy.weibo.utils.RecycleViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.widget.PullToRefreshMode;
import hk.xhy.android.commom.widget.Toaster;
import retrofit2.Call;

public class MainActivity extends ListActivity<ViewHolder, Status, Result<List<Status>>>
        implements NavigationView.OnNavigationItemSelectedListener, StatusLogic.GetStatusGroupCallBack, UserLoginLogic.GetUserinfoCallBack, OnListItemClickListener, PushMessage<Status> {

    private final String TAG = this.getClass().getSimpleName();

    public static final int STATUS_GROUP = 0;
    public static final int STATUS_GROUP_ITEMID = 0;
    public static final int SETTING_GROUP = 1;
    public static final int SETTING_ITEMID = 1000;
    public static final int SETTING_LOGOUT_ITEMID = SETTING_ITEMID + 1;


    @ViewById(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.drawer_layout)
    DrawerLayout drawer;
    @ViewById(R.id.nav_view)
    NavigationView navigationView;
    @ViewById(R.id.fab)
    FloatingActionButton fab;

    //再按一次确定退出
    private boolean mConfirmExit = false;

    Button btnHot;
    Button btnKeep;
    Button btnSettings;
    ImageView headerView_iv_avatar;
    TextView headerView_tv_username;

    SearchView actionView;
    View headerView;

    private ActionBarDrawerToggle toggle;
    private String gid = "";
    private final Handler handler = new Handler();
    private int currentPage = 1;


    private SearchView.SearchAutoComplete mEditSearch;
    private DBManager dbManager;
    private SQLiteDatabase db;
    private UserDB userDB;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUserinfo();
    }

    private void init() {
        initView();
        initNavigationMenu();
        initListener();

        //启动推送
        Intent intent = new Intent(this, MessageService.class);
        intent.putExtra("TOKEN", AppConfig.getAccessToken().getToken());
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

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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

        //开启自定义底部加载item
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

    @Override
    public Result<List<Status>> onLoadInBackground() throws Exception {
        int page = 0;
        if (isLoadMore()) {
            page = currentPage + 1;
        } else {
            page = 1;
        }

        Call<Result<List<Status>>> resultCall = ApiClient.getApi().getStatusList(AppConfig.getUserId(), page, AppConfig.getAccessToken().getToken(), gid, 0);
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

        if (data != null) {
            if (data.isSuccess()) {
                if (!isLoadMore()) {
                    getItemsSource().clear();
                    currentPage = 1;
                } else if (data.getInfo().size() > 0) {
                    currentPage += 1;
                }

                getItemsSource().addAll(data.getInfo());
            } else if (currentPage == 1) {
                getItemsSource().clear();

            }
            getAdapter().notifyDataSetChanged();
            onRefreshComplete();
        }

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
    public void OnItemOtherClick(int postion, int type) {
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
            getAdapter().notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onListChangeEvent(StatusMainListChangeEvent event) {
        Log.e(TAG, ">>>StatusMainListChangeEvent");
        onRefresh();
    }


    private void initUserinfo() {
        initDB();
        List<User> users = userDB.QueryUsers(db, "id=?", new String[]{AppConfig.getUserId() + ""});
        dbManager.closeDatabase();
        if (!users.isEmpty()) {
            long time = System.currentTimeMillis() - users.get(0).getUptime();

            //加载本地已有数据
            if (TextUtils.isEmpty(users.get(0).getUsername())) {
                String url = URLs.AVATAR_IMG_URL + users.get(0).getFace();
                ImageUtils.setImage(headerView_iv_avatar, url);
                headerView_tv_username.setText(users.get(0).getUsername());
            }

            //大于半天刷新下缓存的用户数据
            if (time > 43200000) {
                UpdataUserinfo();
            }

        } else {
            UpdataUserinfo();
        }
    }

    private void UpdataUserinfo() {

        UserLoginLogic.getUserinfo(this, AppConfig.getUserId(), null, 0, AppConfig.getAccessToken().getToken(), this);

    }

    @Override
    public void onUserInfoSuccess(User user) {
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

    @Override
    public void onUserInfoFailure(int errorCode, String errorMessage) {
        showSnackbar("错误：" + errorCode + "," + errorMessage);
    }

    @Override
    public void onUserInfoError(Throwable error) {
        showSnackbar("获取用户信息失败");
    }


    private void initDB() {
        //获取数据
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        db = dbManager.getDatabase();
        userDB = new UserDB(this);
    }

    /**
     * 记得做缓存
     */
    private void initNavigationMenu() {
        menu = navigationView.getMenu();
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(STATUS_GROUP, STATUS_GROUP_ITEMID, STATUS_GROUP_ITEMID, "全部");

        menu.add(SETTING_GROUP, SETTING_LOGOUT_ITEMID, SETTING_LOGOUT_ITEMID, "注销登录");

        StatusLogic.getStatusGroup(this, AppConfig.getUserId(), AppConfig.getAccessToken().getToken(), this);

    }

    @Override
    public void onGroupSuccess(List<StatusGroup> statusGroups) {

        for (StatusGroup sg : statusGroups) {
            menu.add(STATUS_GROUP, sg.getId(), sg.getId(), sg.getName());
        }
    }

    @Override
    public void onGroupFailure(String message) {
        showSnackbar(message);
    }

    @Override
    public void onGroupError(Throwable t) {

    }


    private void initListener() {
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.startActivity(MainActivity.this, WriteStatusActivity.class, new HashMap<String, Object>() {{
                    put(Constants.TYPE, Constants.NEW_STATUS_TYPE);
                }}, Constants.REQUEST_CODE_WRITE_STATUS);
            }
        });

        btnHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(MainActivity.this, HotsActivity.class);
            }
        });
        btnKeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(MainActivity.this, KeepStatusActivity.class);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(MainActivity.this, SettingsActivity.class);
            }
        });


        headerView_iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(MainActivity.this, UserInfoActivity.class);
                data.putExtra(Constants.USER_ID, AppConfig.getUserId());
                startActivity(data);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!mConfirmExit) {
                mConfirmExit = true;
                Toaster.showShort(this, R.string.app_exit_warning);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mConfirmExit = false;
                    }
                }, 2000);
            } else {
                ActivityUtils.appExit(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE_WRITE_FORWARD:
                onRefresh();
                break;
            case Constants.REQUEST_CODE_WRITE_STATUS:
                boolean sendSuccess = data.getBooleanExtra(Constants.SEND_STATUS_SUCCESS, false);
                if (sendSuccess) {
                    Snackbar.make(mCoordinatorLayout, "发送成功", Snackbar.LENGTH_LONG)
                            .show();
                    onRefresh();
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
            public boolean onQueryTextSubmit(final String query) {
                if (TextUtils.isEmpty(query)) {
                    return false;
                }

                ActivityUtils.startActivity(MainActivity.this, SearchActivity.class,
                        new HashMap<String, Object>() {
                            {
                                put(Constants.SEARCH_CONTENT, query);
                            }
                        });
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
        int id = item.getItemId();

        switch (id) {
            case R.id.action_notifications:
                ActivityUtils.startActivity(this, NotifyActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        currentPage = 1;
        int id = item.getItemId();
        switch (id) {
            case STATUS_GROUP_ITEMID:
                gid = "";
                onRefresh();
                break;
            case SETTING_LOGOUT_ITEMID://注销登录
                Login.setCurrentLoginUser(null);
                AppConfig.setAccount(null);
                AppConfig.setPassword(null);
                ActivityUtils.goHome(MainActivity.this, LoginActivity.class);
                break;
            default:
                gid = id + "";
                onRefresh();
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }


}
