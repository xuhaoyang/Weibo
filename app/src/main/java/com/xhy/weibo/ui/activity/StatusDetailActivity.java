package com.xhy.weibo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.event.CommentListChangeEvent;
import com.xhy.weibo.event.StatusMainListChangeEvent;
import com.xhy.weibo.ui.activity.fragment.CommentFragment;
import com.xhy.weibo.ui.activity.fragment.KeepFragment;
import com.xhy.weibo.ui.base.BaseActivity;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.api.URLs;
import com.xhy.weibo.ui.base.ViewPagerAdapter;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.Logger;
import com.xhy.weibo.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import hk.xhy.android.common.bind.ViewById;
import hk.xhy.android.common.utils.ActivityUtils;

public class StatusDetailActivity extends BaseActivity {


    // 跳转到写评论页面code
    private static final int REQUEST_CODE_WRITE_COMMENT = 1;
    private static final int REQUEST_CODE_WRITE_FORWARD = 2;
    private static final int MENU_DELETE_STATUS_ITEMID = 1003;


    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.viewpager)
    ViewPager mViewPager;
    @ViewById(R.id.tabs)
    TabLayout tabLayout;
    @ViewById(R.id.tv_content)
    TextView tv_content;
    @ViewById(R.id.include_status_detail_image)
    FrameLayout include_status_detail_image;
    @ViewById(R.id.iv_image)
    ImageView iv_image;

    //转发内容
    @ViewById(R.id.tv_retweeted_content)
    TextView tv_retweeted_content;
    @ViewById(R.id.include_forward_detail_status)
    LinearLayout include_forward_detail_status;
    @ViewById(R.id.layout_forward_img)
    FrameLayout layout_forward_img;
    @ViewById(R.id.iv_image_forward)
    ImageView iv_image_forward;

    @ViewById(R.id.detail_Car)
    public CoordinatorLayout mCoordinatorLayout;

    private Intent fromIntent;

    private Status status;

    private ViewPagerAdapter mAdapter;

    //操作fragment中的数据
    public Handler mHandler;


    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //界面
        setContentView(R.layout.activity_status_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.finishActivity();
            }
        });

        //获得传入的信息
        fromIntent = getIntent();
        /**
         * 因为这里传输方式 会变成json形式 保留原有的获取数据的方式
         */
        Object val = fromIntent.getSerializableExtra(Constants.STATUS_INTENT);
        if (val instanceof String) {//前者如果不是序列化来的数据,会未空
            status = Status.parseObject(fromIntent.getStringExtra(Constants.STATUS_INTENT));
        } else if (val instanceof Status) {
            status = (Status) val;
        } else {
            ActivityUtils.finishActivity();
        }


        if (status != null) {
            setUpViewPager(mViewPager);
            tabLayout.setupWithViewPager(mViewPager);
        }

        initData();


    }

    private void initData() {
        getSupportActionBar().setTitle(status.getUsername());
        getSupportActionBar().setSubtitle(DateUtils.getShotTime(status.getTime()));
        //微博正文
        tv_content.setText(StringUtils.getWeiboContent(this, tv_content, status.getContent()));
        //带图片的微博
        if (!TextUtils.isEmpty(status.getMedium())) {

            String url = URLs.PIC_URL + status.getMedium();
            setImage(iv_image, url);
            include_status_detail_image.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.VISIBLE);
            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StatusDetailActivity.this, ViewPicActivity.class);
                    intent.putExtra(ViewPicActivity.PIC_URL, URLs.PIC_URL + status.getMax());
                    startActivity(intent);
                }
            });

        } else {
            include_status_detail_image.setVisibility(View.GONE);
            iv_image.setVisibility(View.GONE);
        }

        //显示转发内容
        final Status forward_status = status.getStatus();
        if (forward_status != null) {
            tv_retweeted_content.setText(StringUtils.getWeiboContent(this, tv_retweeted_content, "@" + forward_status.getUsername() + ": " + forward_status.getContent()));
            include_forward_detail_status.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(forward_status.getMedium())) {
                layout_forward_img.setVisibility(View.VISIBLE);
                iv_image_forward.setVisibility(View.VISIBLE);
                String url = URLs.PIC_URL + forward_status.getMedium();
                setImage(iv_image_forward, url);
                iv_image_forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StatusDetailActivity.this, ViewPicActivity.class);
                        intent.putExtra(ViewPicActivity.PIC_URL, URLs.PIC_URL + forward_status.getMax());
                        startActivity(intent);
                    }
                });
            } else {
                iv_image_forward.setVisibility(View.GONE);
                layout_forward_img.setVisibility(View.GONE);
            }
        } else {
            include_forward_detail_status.setVisibility(View.GONE);
            tv_retweeted_content.setVisibility(View.GONE);
        }

        //转发微博被删除
        if (status.getIsturn() == -1) {
            tv_retweeted_content.setText("该微博已被删除");
            tv_retweeted_content.setVisibility(View.VISIBLE);
            include_forward_detail_status.setVisibility(View.VISIBLE);
        }
    }

    private void setUpViewPager(ViewPager mViewPager) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.STATUS_ID, status.getId());
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFrag(CommentFragment.newInstance(), "评论", bundle);
        mAdapter.addFrag(KeepFragment.newInstance(), "转发", bundle);
        mViewPager.setAdapter(mAdapter);

    }


    public void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).thumbnail(0.2f).fitCenter().into(view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_status_detail, menu);
        if (status.getUid() == AppConfig.getUserId()) {
            menu.add(0, MENU_DELETE_STATUS_ITEMID, MENU_DELETE_STATUS_ITEMID, "删除微博");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_comment:
                Intent intent = new Intent(this, WriteStatusActivity.class);
                intent.putExtra(Constants.TYPE, Constants.COMMENT_TYPE);
                intent.putExtra(Constants.STATUS_INTENT, status);
                intent.putExtra(Constants.TAG, Constants.DETAIL_ATY_CODE);
                startActivityForResult(intent, REQUEST_CODE_WRITE_COMMENT);
                break;

            case R.id.action_forward:
                Intent forwardIntent = new Intent(this, WriteStatusActivity.class);
                forwardIntent.putExtra(Constants.TYPE, Constants.FORWARD_TYPE);
                forwardIntent.putExtra(Constants.STATUS_INTENT, status);
                startActivityForResult(forwardIntent, REQUEST_CODE_WRITE_FORWARD);

                break;
            case R.id.action_keep:
                if (status.isKeep()) {

                    StatusLogic.delKeepStatus(this, AppConfig.getUserId(), status.getId(),
                            AppConfig.getAccessToken().getToken(), new StatusLogic.DelKeepStatusCallBack() {
                                @Override
                                public void onDelKeepSuccess(Result result) {
                                    status.setKeep(false);
                                    status.setKeep(status.getKeep() - 1);
                                    showToast(result.getMsg());
                                }

                                @Override
                                public void onDelKeepFailure(String message) {
                                    showToast(message);
                                }

                                @Override
                                public void onDelKeepError(Throwable t) {
                                    Logger.show(TAG, t.getMessage(), Log.ERROR);
                                }
                            });

                } else {
                    StatusLogic.addKeepStatus(this, AppConfig.getUserId(), status.getId(),
                            AppConfig.getAccessToken().getToken(), new StatusLogic.AddKeepStatusCallBack() {
                                @Override
                                public void onAddKeepSuccess(Result result) {
                                    status.setKeep(true);
                                    status.setKeep(status.getKeep() + 1);
                                    showToast(result.getMsg());
                                }

                                @Override
                                public void onAddKeepFailure(String message) {
                                    showToast(message);
                                }

                                @Override
                                public void onAddKeepError(Throwable t) {
                                    Logger.show(TAG, t.getMessage(), Log.ERROR);
                                }
                            });

                }
                break;
            case MENU_DELETE_STATUS_ITEMID:
                showToast("删除微博");
                StatusLogic.delWeibo(status.getId(), AppConfig.getAccessToken().getToken(),
                        new StatusLogic.DelStatusCallback() {
                            @Override
                            public void onDelStatusSuccess(String message) {
                                showToast(message);
                                ActivityUtils.finishActivity();
                                //刷新Mainactivity的list
                                EventBus.getDefault().post(new StatusMainListChangeEvent());
                            }

                            @Override
                            public void onDelStatusFailure(String message) {
                                showToast(message);
                            }

                            @Override
                            public void onDelStatusError(Throwable t) {
                                Logger.show(TAG, t.getMessage(), Log.ERROR);
                            }
                        });

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 如果Back键返回,取消发评论等情况,则直接return,不做后续处理
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_WRITE_COMMENT:
                // 如果是评论发送成功的返回结果,则重新加载最新评论,同时要求滚动至评论部分
                boolean sendCommentSuccess =
                        data.getBooleanExtra(Constants.SEND_COMMENT_SUCCESS, false);
                if (sendCommentSuccess) {
                    //通过Handler进行刷新
//                    Message message = new Message();
//                    message.what = CommentFragment.REFRESH_DATA;
//                    mHandler.sendMessage(message);
                    EventBus.getDefault().post(new CommentListChangeEvent());
                }
                break;
            case REQUEST_CODE_WRITE_FORWARD:
                boolean sendForwardSuccess = data.getBooleanExtra(Constants.SEND_FORWORD_SUCCESS, false);
                if (sendForwardSuccess) {
                    Snackbar.make(mCoordinatorLayout, "转发成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Message message = new Message();
                    message.what = CommentFragment.REFRESH_DATA;
                    mHandler.sendMessage(message);
                }
            default:
                break;
        }
    }
}
