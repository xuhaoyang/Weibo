package com.xhy.weibo.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.xhy.weibo.R;
import com.xhy.weibo.StartActivty;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.ui.base.BaseActivity;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.widget.Toaster;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, UserLoginLogic.RegisterCallback {

    @ViewById(R.id.et_username)
    EditText etUsername;
    @ViewById(R.id.et_password)
    EditText etPassword;
    @ViewById(R.id.et_repeatpassword)
    EditText etRepeatpassword;
    @ViewById(R.id.et_uname)
    EditText etUname;
    @ViewById(R.id.bt_go)
    Button btGo;

    @ViewById(R.id.fab)
    FloatingActionButton fab;
    @ViewById(R.id.cv_add)
    CardView cvAdd;
    private String username;
    private String password;
    private String repeat_password;
    private String uname;
    private View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }

        fab.setOnClickListener(this);
        btGo.setOnClickListener(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(
                cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }


    public void animateRevealClose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator mAnimator = ViewAnimationUtils.createCircularReveal(
                    cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cvAdd.setVisibility(View.INVISIBLE);
                    super.onAnimationEnd(animation);
                    fab.setImageResource(R.drawable.plus);
                    RegisterActivity.super.onBackPressed();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            mAnimator.start();
        } else {
            cvAdd.setVisibility(View.INVISIBLE);
            RegisterActivity.super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                animateRevealClose();
                break;
            case R.id.bt_go:
                attemptLogin();
                break;
        }
    }

    private void attemptLogin() {

        etUsername.setError(null);
        etPassword.setError(null);

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        repeat_password = etRepeatpassword.getText().toString();
        uname = etUname.getText().toString();

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

        if (TextUtils.isEmpty(uname)) {
            etUname.setError(getString(R.string.error_field_required));
            focusView = etUname;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !password.equals(repeat_password)) {
            etRepeatpassword.setError(getString(R.string.error_not_match_password));
            focusView = etRepeatpassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            UserLoginLogic.register(username, password, uname, this);
        }

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onRegisterSuccess(String message) {
        showToast(message);
        ActivityUtils.goHome(this, StartActivty.class);

    }

    @Override
    public void onRegisterFailure(String message) {
        etUname.setError(message);

    }

    @Override
    public void onRegisterError(Throwable t) {
        etUname.setError("错误,请稍后重试");
    }
}
