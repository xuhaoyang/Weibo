package com.xhy.weibo.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xhy.weibo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewPicActivity extends AppCompatActivity {

    public static final String PIC_URL = "MAX_PIC";

    @BindView(R.id.iv_view_pic)
    ImageView iv_view_pic;
    private PhotoViewAttacher mAttacher;
    private Bitmap thisBitmap;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("详细图片");

        Intent data = getIntent();
        String url = data.getStringExtra(PIC_URL);
        mAttacher = new PhotoViewAttacher(iv_view_pic);
        setImage(iv_view_pic, url);
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

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            thisBitmap = bitmap;
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Glide.with(ViewPicActivity.this).load(thisBitmap).into(iv_view_pic);
//                }
//            });
            iv_view_pic.setImageBitmap(bitmap);
            mAttacher.update();
        }
    };
    ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
            // if it's a custom view class, cast it here
            // then find subviews and do the animations
            // here, we just use the entire view for the fade animation
            view.setAlpha(0f);

            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeAnim.setDuration(600);
            fadeAnim.start();
        }
    };

    public void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).asBitmap().error(R.drawable.ic_photo_light).into(target);
    }

    @Override
    protected void onStop() {
        super.onStop();
        iv_view_pic.setImageBitmap(null);
        thisBitmap = null;
    }


}
