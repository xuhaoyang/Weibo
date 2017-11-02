package com.xhy.weibo.ui.activity;

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
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.xhy.weibo.R;
import com.xhy.weibo.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPicActivity extends BaseActivity implements View.OnClickListener {

    public static final String PIC_URL = "MAX_PIC";

    @BindView(R.id.iv_view_pic)
    PhotoView iv_view_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic);
        ButterKnife.bind(this);

        Intent data = getIntent();
        String url = data.getStringExtra(PIC_URL);

        iv_view_pic.setOnClickListener(this);
        setImage(iv_view_pic, url);
    }

    @Override
    protected void onStart() {
        super.onStart();

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

//    private SimpleTarget target = new SimpleTarget<Bitmap>() {
//        @Override
//        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//            thisBitmap = bitmap;
////            mHandler.post(new Runnable() {
////                @Override
////                public void run() {
////                    Glide.with(ViewPicActivity.this).load(thisBitmap).into(iv_view_pic);
////                }
////            });
//            iv_view_pic.setImageBitmap(bitmap);
//            mAttacher.update();
//        }
//    };


    public void setImage(ImageView view, String url) {
//        Glide.with(view.getContext()).load(url).asBitmap().error(R.drawable.ic_photo_light).into(target);
        Glide.with(view.getContext()).load(url).error(R.drawable.ic_photo_light).into(view);
    }

    @Override
    protected void onStop() {
        super.onStop();
        iv_view_pic.setImageBitmap(null);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_view_pic:
                onBackPressed();
                break;
        }
    }
}
