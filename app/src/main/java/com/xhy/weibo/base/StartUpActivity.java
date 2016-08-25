package com.xhy.weibo.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.xhy.weibo.utils.Logger;
import hk.xhy.android.commom.utils.ToastUtils;

import org.blankapp.app.LoaderActivity;

import hk.xhy.android.commom.utils.ActivityUtils;

/**
 * Created by xuhaoyang on 16/8/24.
 */
public abstract class StartUpActivity<D> extends LoaderActivity<D> implements com.xhy.weibo.base.interfaces.BaseActivtyInterface,Animation.AnimationListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, 0.9f, 1f, 2000);
    }

    public void setContentView(int layoutResID, float fromAlpha, float toAlpha, long durationMillis) {
        // 启动面隐藏4.x的虚拟按键
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;

                // Navigation bar hiding:  Backwards compatible to ICS.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                }

                // Status bar hiding: Backwards compatible to Jellybean
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                }
                getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final View view = View.inflate(this, layoutResID, null);
        setContentView(view);
        // 渐变展示启动屏
        AlphaAnimation anim = new AlphaAnimation(fromAlpha, toAlpha);
        anim.setDuration(durationMillis);
        anim.setAnimationListener(this);
        view.startAnimation(anim);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.addActivity(this);
        // 禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onAnimationRepeat(Animation anim) {
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void intent2Activity(Class<? extends Activity> tarActivity) {
        ActivityUtils.goHome(this, tarActivity);
    }

    @Override
    public void showLog(String msg) {
        Logger.show(TAG, msg);
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
    }
}
