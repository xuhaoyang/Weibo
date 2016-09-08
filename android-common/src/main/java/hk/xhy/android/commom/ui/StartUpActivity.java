package hk.xhy.android.commom.ui;

import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by zhujianheng on 2/24/16.
 */
public abstract class StartUpActivity extends BaseActivity implements Animation.AnimationListener {

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
    public void onAnimationRepeat(Animation anim) {

    }

}
