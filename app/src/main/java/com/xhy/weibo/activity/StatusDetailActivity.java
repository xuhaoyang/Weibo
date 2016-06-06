package com.xhy.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.activity.fragment.CommentFragment;
import com.xhy.weibo.activity.fragment.KeepFragment;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.NormalInfo;
import com.xhy.weibo.entity.Status;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.NetParams;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusDetailActivity extends BaseActivity {


    // 跳转到写评论页面code
    private static final int REQUEST_CODE_WRITE_COMMENT = 1;
    private static final int REQUEST_CODE_WRITE_FORWARD = 2;

    public static final String STATUS_INTENT = "status";


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.include_status_detail_image)
    FrameLayout include_status_detail_image;
    @BindView(R.id.iv_image)
    ImageView iv_image;

    //转发内容
    @BindView(R.id.tv_retweeted_content)
    TextView tv_retweeted_content;
    @BindView(R.id.include_forward_detail_status)
    LinearLayout include_forward_detail_status;
    @BindView(R.id.layout_forward_img)
    FrameLayout layout_forward_img;
    @BindView(R.id.iv_image_forward)
    ImageView iv_image_forward;

    @BindView(R.id.detail_Car)
    public CoordinatorLayout mCoordinatorLayout;

    private Intent fromIntent;

    private Status status;
    private TabsAdapter tabsAdapter;

    //操作fragment中的数据
    public Handler mHandler;


    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获得传入的信息
        fromIntent = getIntent();
        status = (Status) fromIntent.getSerializableExtra(WriteStatusActivity.STATUS_INTENT);


        if (status != null) {
            tabsAdapter = new TabsAdapter(getSupportFragmentManager(), status);
            viewPager.setAdapter(tabsAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }


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
//            layout_forward_img.setVisibility(View.GONE);
//            iv_image_forward.setVisibility(View.GONE);
        }

        //转发微博被删除
        if (status.getIsturn() == -1) {
            tv_retweeted_content.setText("该微博已被删除");
            tv_retweeted_content.setVisibility(View.VISIBLE);
            include_forward_detail_status.setVisibility(View.VISIBLE);
        }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    public void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).thumbnail(0.2f).fitCenter().into(view);

    }

    class TabsAdapter extends FragmentPagerAdapter {
        private Status status;
        private CommentFragment commentFragment = CommentFragment.newInstance();
        private KeepFragment keepFragment = KeepFragment.newInstance();

        public CommentFragment getCommentFragment() {
            return commentFragment;
        }

        public TabsAdapter(FragmentManager fm, Status status) {
            super(fm);
            this.status = status;
        }

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle i;
            switch (position) {
                case 0:
                    i = new Bundle();
                    i.putInt(CommentFragment.WID, status.getId());
                    commentFragment.setArguments(i);
                    return commentFragment;
                case 1:
                    i = new Bundle();
                    i.putInt(CommentFragment.WID, status.getId());
                    keepFragment.setArguments(i);
                    return keepFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "评论";
                case 1:
                    return "转发";
            }
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_status_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_comment:
                Intent intent = new Intent(this, WriteStatusActivity.class);
                intent.putExtra(WriteStatusActivity.TYPE, WriteStatusActivity.COMMENT_TYPE);
                intent.putExtra(WriteStatusActivity.STATUS_INTENT, status);
                intent.putExtra(WriteStatusActivity.TAG, WriteStatusActivity.DETAIL_ATY_CODE);
                startActivityForResult(intent, REQUEST_CODE_WRITE_COMMENT);
                break;

            case R.id.action_forward:
                Intent forwardIntent = new Intent(this, WriteStatusActivity.class);
                forwardIntent.putExtra(WriteStatusActivity.TYPE, WriteStatusActivity.FORWARD_TYPE);
                forwardIntent.putExtra(WriteStatusActivity.STATUS_INTENT, status);
                startActivityForResult(forwardIntent, REQUEST_CODE_WRITE_FORWARD);

                break;
            case R.id.action_keep:
                if (status.isKeep()) {
                    GsonRequest<NormalInfo> request = new GsonRequest<NormalInfo>(Request.Method.GET,
                            NetParams.delkeepWeibo(CommonConstants.USER_ID, status.getId(), CommonConstants.ACCESS_TOKEN.getToken()), NormalInfo.class, null,
                            new Response.Listener<NormalInfo>() {
                                @Override
                                public void onResponse(NormalInfo response) {
                                    if (response.getCode() == 200) {
                                        status.setKeep(false);
                                        status.setKeep(status.getKeep() - 1);
                                        showToast(response.getInfo());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
                } else {
                    GsonRequest<NormalInfo> request = new GsonRequest<NormalInfo>(Request.Method.GET,
                            NetParams.keepWeibo(CommonConstants.USER_ID, status.getId(), CommonConstants.ACCESS_TOKEN.getToken()), NormalInfo.class, null,
                            new Response.Listener<NormalInfo>() {
                                @Override
                                public void onResponse(NormalInfo response) {
                                    if (response.getCode() == 200) {
                                        status.setKeep(true);
                                        status.setKeep(status.getKeep() + 1);
                                        showToast(response.getInfo());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
                }
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
                        data.getBooleanExtra(WriteStatusActivity.SEND_COMMENT_SUCCESS, false);
                if (sendCommentSuccess) {
                    //通过Handler进行刷新茶瓯哦
                    Message message = new Message();
                    message.what = CommentFragment.REFRESH_DATA;
                    mHandler.sendMessage(message);
                }
                break;
            case REQUEST_CODE_WRITE_FORWARD:
                boolean sendForwardSuccess = data.getBooleanExtra(WriteStatusActivity.SEND_FORWORD_SUCCESS, false);
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
