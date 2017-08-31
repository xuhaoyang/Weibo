package com.xhy.weibo.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.StartActivty;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.ui.base.BaseActivity;

import cn.jpush.android.api.JPushInterface;
import hk.xhy.android.common.bind.ViewById;
import hk.xhy.android.common.utils.ActivityUtils;

import com.xhy.weibo.utils.TagAliasOperatorHelper.TagAliasBean;

import org.greenrobot.eventbus.EventBus;

import static com.xhy.weibo.utils.TagAliasOperatorHelper.ACTION_GET;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements OnClickListener, UserLoginLogic.LoginCallback {


    @ViewById(R.id.et_username)
    EditText etUsername;
    @ViewById(R.id.et_password)
    EditText etPassword;
    @ViewById(R.id.bt_go)
    Button btGo;
    @ViewById(R.id.cv)
    CardView cv;
    @ViewById(R.id.fab)
    FloatingActionButton fab;
    @ViewById(R.id.login_progress)
    ProgressBar mProgressView;

    private String username;
    private String password;
    private UserDB userDB;
    private View focusView;

    private static int sequence = 1;
    private String mAlias;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        fab.setOnClickListener(this);
        btGo.setOnClickListener(this);

        userDB = new UserDB(this);

        //注册EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void attemptLogin() {

        showProgress(true);
        etUsername.setError(null);
        etPassword.setError(null);

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        boolean cancel = false;
        focusView = null;

        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }


        if (cancel) {
            showProgress(false);
            focusView.requestFocus();
        } else {
            UserLoginLogic.login(this, username, password, this);
        }

    }

    @Override
    public void onLoginSuccess(Login login) {

        //设置密码 请求返回中不会返回密码 返回不安全
        login.setTokenStartTime(System.currentTimeMillis());
        login.setPassword(password);

        //数据库操作
        DBManager dbManager = new DBManager(LoginActivity.this);
        dbManager.openDatabase();
        SQLiteDatabase db = dbManager.getDatabase();
        if (userDB.insertLogin(db, login)) {

            //设置当前用户
            AppConfig.setAccount(login.getAccount());
            AppConfig.setPassword(login.getPassword());

            //获得TOKEN维护
            AppConfig.getAccessToken();
            AppConfig.getAccessToken().setToken(login.getToken());
            AppConfig.getAccessToken().setTokenStartTime(login.getTokenStartTime());

            //保存当前用户登录信息
            Login.setCurrentLoginUser(login);

            //查看
            if (JPushInterface.isPushStopped(getApplicationContext())) {
                //TODO regid和alias 看有没有在开启推送
            }

            String registrationID = JPushInterface.getRegistrationID(getApplicationContext());
            TagAliasBean tagAliasBean = new TagAliasBean();
            tagAliasBean.setAction(ACTION_GET);
            tagAliasBean.setAliasAction(true);
            sequence++;
            TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);


            ActivityUtils.goHome(this, StartActivty.class);
            finish();
        } else {
            showProgress(false);
            etPassword.setError("数据库错误");
            focusView = etPassword;
            focusView.requestFocus();
        }
        dbManager.closeDatabase();
    }

    @Override
    public void onLoginFailure(int errorCode, String errorMessage) {
        showProgress(false);
        etPassword.setError(errorMessage);
        focusView = etPassword;
        focusView.requestFocus();
    }

    @Override
    public void onLoginError(Throwable error) {
        showProgress(false);
        etPassword.setError("错误,请稍后重试");
    }

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setExitTransition(null);
                    getWindow().setEnterTransition(null);
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    ActivityOptionsCompat compat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this, fab, getString(R.string.tr_fab_name));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        startActivity(new Intent(this, RegisterActivity.class), compat.toBundle());
                    }
                }
                break;
            case R.id.bt_go:
                attemptLogin();
                break;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        cv.setVisibility(show ? View.GONE : View.VISIBLE);
        fab.setVisibility(show ? View.GONE : View.VISIBLE);

        cv.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cv.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        // The ViewPropertyAnimator APIs are not available, so simply show
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        cv.setVisibility(show ? View.GONE : View.VISIBLE);
    }


}

