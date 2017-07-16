package com.xhy.weibo.ui.vh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.URLs;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Map;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.ui.activity.StatusDetailActivity;
import com.xhy.weibo.ui.activity.UserInfoActivity;
import com.xhy.weibo.ui.activity.ViewPicActivity;
import com.xhy.weibo.ui.activity.WriteStatusActivity;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.Logger;
import com.xhy.weibo.utils.StringUtils;

import java.util.HashMap;

import hk.xhy.android.common.bind.ViewById;
import hk.xhy.android.common.ui.vh.OnListItemClickListener;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.utils.ActivityUtils;
import hk.xhy.android.common.utils.GsonUtil;
import hk.xhy.android.common.utils.ScreenUtils;

/**
 * Created by xuhaoyang on 16/9/12.
 */
public class StatusViewHolder extends ViewHolder {

    private static final String TAG = StatusViewHolder.class.getSimpleName();
    @ViewById(R.id.ll_content)
    public LinearLayout ll_content;

    //include_avatar
    @ViewById(R.id.iv_avatar)
    public ImageView iv_avatar;
    @ViewById(R.id.right_content)
    public RelativeLayout right_content;
    @ViewById(R.id.tv_subhead)
    public TextView tv_subhead;
    @ViewById(R.id.tv_caption)
    public TextView tv_caption;

    //主要的微博文字内容
    @ViewById(R.id.tv_content)
    public TextView tv_content;
    //图片显示
    @ViewById(R.id.include_status_image)
    public FrameLayout include_status_image;
    @ViewById(R.id.iv_image)
    public ImageView iv_image;

    //转发内容
    @ViewById(R.id.include_forward_status)
    public LinearLayout include_forward_status;
    @ViewById(R.id.tv_retweeted_content)
    public TextView tv_retweeted_content;
    @ViewById(R.id.include_status_image_forward)
    public FrameLayout include_status_image_forward;
    @ViewById(R.id.iv_image_forward)
    public ImageView iv_image_forward;

    //按钮
    @ViewById(R.id.ll_like)
    public LinearLayout ll_like;
    @ViewById(R.id.ll_comment)
    public LinearLayout ll_comment;
    @ViewById(R.id.ll_forward)
    public LinearLayout ll_forward;

    //收藏
    @ViewById(R.id.iv_ic_like)
    public ImageView iv_ic_like;
    @ViewById(R.id.tv_like_count)
    public TextView tv_like_count;
    //评论
    @ViewById(R.id.iv_ic_comment)
    public ImageView iv_ic_comment;
    @ViewById(R.id.tv_comment)
    public TextView tv_comment;
    //转发
    @ViewById(R.id.iv_ic_forward)
    public ImageView iv_ic_forward;
    @ViewById(R.id.tv_forward)
    public TextView tv_forward;

    @ViewById(R.id.ll_location)
    public LinearLayout ll_location;
    @ViewById(R.id.tv_location)
    public TextView tv_location;

    private boolean animateItems = false;
    private int lastAnimatedPosition = -1;

    public StatusViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * @param context
     * @param model
     * @param listener
     * @param lastAnimatedPosition 动画启用的postion
     */
    public void bind(final Context context, final Status model, final OnListItemClickListener listener,
                     final int lastAnimatedPosition) {

        this.lastAnimatedPosition = lastAnimatedPosition;

        //初始化动画
//        runEnterAnimation(this.itemView, getAdapterPosition());

        //初始化
        include_status_image_forward.setVisibility(View.GONE);
        iv_image_forward.setVisibility(View.GONE);
        iv_image.setVisibility(View.GONE);
        include_status_image.setVisibility(View.GONE);
        if (model != null) {
            //设置按钮数字
            tv_comment.setText(model.getComment() + "");
            tv_like_count.setText(model.getKeep() + "");
            tv_forward.setText(model.getTurn() + "");

            //是否收藏
            if (model.isKeep()) {
                iv_ic_like.setImageResource(R.drawable.ic_like_full);
            } else {
                iv_ic_like.setImageResource(R.drawable.ic_like_empty);
            }

            tv_subhead.setText(model.getUserinfo().getUsername());
            tv_caption.setText(DateUtils.getShotTime(model.getTime()));

            //显示头像
            if (TextUtils.isEmpty(model.getUserinfo().getFace50())) {
                iv_avatar.setImageResource(R.drawable.user_avatar);
            } else {
                String url = URLs.AVATAR_IMG_URL + model.getUserinfo().getFace50();
                Glide.with(iv_avatar.getContext()).load(url).
                        fitCenter().into(iv_avatar);
            }

            iv_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startActivityByContext(context, UserInfoActivity.class, new HashMap<String, Object>() {{
                        put(Constants.USER_ID, model.getUid());
                    }});
                }
            });


            //显示转发内容
            final Status forward_status = model.getStatus();
            if (forward_status != null) {
                tv_retweeted_content
                        .setText(StringUtils.getWeiboContent(context, tv_retweeted_content, "@" + forward_status.getUserinfo().getUsername() + ": " + forward_status.getContent()));
                include_forward_status.setVisibility(View.VISIBLE);
                if (forward_status.getPicture() != null) {
                    include_status_image_forward.setVisibility(View.VISIBLE);
                    iv_image_forward.setVisibility(View.VISIBLE);
                    String url = URLs.PIC_URL + forward_status.getPicture().getMedium();
                    setImage(iv_image_forward, url);
                    iv_image_forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ViewPicActivity.class);
                            intent.putExtra(ViewPicActivity.PIC_URL, URLs.PIC_URL + forward_status.getPicture().getMax());
                            context.startActivity(intent);
                        }
                    });
                } else {
                    include_status_image_forward.setVisibility(View.GONE);
                    iv_image_forward.setVisibility(View.GONE);
                }
            } else {
                include_forward_status.setVisibility(View.GONE);
                iv_image_forward.setVisibility(View.GONE);
            }

            //微博正文
            tv_content.setText(StringUtils.getWeiboContent(context,
                    tv_content, model.getContent()));

            //转发微博被删除
            if (model.getIsturn() == -1) {
                tv_retweeted_content
                        .setText("该微博已被删除");
                include_forward_status.setVisibility(View.VISIBLE);
            }

            //带图片的微博
            if (model.getPicture() != null) {

                String url = URLs.PIC_URL + model.getPicture().getMedium();
                setImage(iv_image, url);
                include_status_image.setVisibility(View.VISIBLE);
                iv_image.setVisibility(View.VISIBLE);
                final float scale = include_status_image.getResources().getDisplayMetrics().density;
                int sixteen = (int) (scale * 16);
                int ten = (int) (scale * 10);
                include_status_image.setPadding(sixteen, ten, sixteen, 0);

                iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.startActivityByContext(context, ViewPicActivity.class, new HashMap<String, Object>() {{
                            put(ViewPicActivity.PIC_URL, URLs.PIC_URL + model.getPicture().getMax());
                        }});
                    }
                });

            } else {
                include_status_image.setVisibility(View.GONE);
                iv_image.setVisibility(View.GONE);
            }

            //地图信息
            if (model.getMaps() != null) {
                Map maps = model.getMaps();
                ll_location.setVisibility(View.VISIBLE);
                tv_location.setText(maps.getName());
            }else {
                ll_location.setVisibility(View.GONE);
            }

            //item范围的跳转
            ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.OnListItemClick(getAdapterPosition());
                    }
                }
            });

            include_forward_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemOtherClick(getAdapterPosition(), Constants.ITEM_FORWARD_STATUS_TYPE);
                    }
                }
            });


            ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemOtherClick(getAdapterPosition(), Constants.ITEM_LIKE_TPYE);
                    }
                }
            });


            ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemOtherClick(getAdapterPosition(), Constants.ITEM_COMMENT_TPYE);
                    }
                }
            });

            ll_forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemOtherClick(getAdapterPosition(), Constants.ITEM_FORWARD_TPYE);
                    }
                }
            });

        }
    }


    private void runEnterAnimation(View itemView, int position) {
        animateItems = true;
//        if (!animateItems || position >= 10) {
//            return;
//        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            itemView.setTranslationY(ScreenUtils.getScreenHeight() / 2);
            itemView.animate()
                    .translationY(0)
//                    .setStartDelay(100 * position)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(500)
                    .start();

        }
    }

    /**
     * 加载缩略图
     *
     * @param view
     * @param url
     */
    private void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).thumbnail(0.2f).fitCenter().into(view);
    }


    public static void bindOnItemOhterClick(final Activity activity, final Status status,
                                            int type, final PushMessage<Status> pushMessage) {

        switch (type) {
            case Constants.ITEM_LIKE_TPYE:
                if (status.isKeep()) {

                    StatusLogic.delKeepStatus(activity, AppConfig.getUserId(),
                            status.getId(), AppConfig.getAccessToken().getToken(),
                            new StatusLogic.DelKeepStatusCallBack() {
                                @Override
                                public void onDelKeepSuccess(Result result) {
                                    status.setKeep(false);
                                    status.setKeep(status.getKeep() - 1);
                                    pushMessage.pushResult(result.isSuccess(), status);
                                    pushMessage.pushString(result.getMsg());
                                }

                                @Override
                                public void onDelKeepFailure(String message) {
                                    pushMessage.pushString(message);
                                }

                                @Override
                                public void onDelKeepError(Throwable t) {
                                    Logger.show(TAG, t.getMessage(), Log.ERROR);

                                }
                            });

                } else {
                    StatusLogic.addKeepStatus(activity, AppConfig.getUserId(), status.getId(),
                            AppConfig.getAccessToken().getToken(), new StatusLogic.AddKeepStatusCallBack() {
                                @Override
                                public void onAddKeepSuccess(Result result) {
                                    status.setKeep(true);
                                    status.setKeep(status.getKeep() + 1);
                                    pushMessage.pushResult(result.isSuccess(), status);
                                    pushMessage.pushString(result.getMsg());
                                }

                                @Override
                                public void onAddKeepFailure(String message) {
                                    pushMessage.pushString(message);

                                }

                                @Override
                                public void onAddKeepError(Throwable t) {
                                    Logger.show(TAG, t.getMessage(), Log.ERROR);
                                }
                            });
                }
                break;
            case Constants.ITEM_COMMENT_TPYE:

                if (status.getComment() > 0) {
                    //跳转到评论页
                    ActivityUtils.startActivity(activity, StatusDetailActivity.class, new HashMap<String, Object>() {
                        {
                            put(Constants.STATUS_INTENT, GsonUtil.toJson(status));
                        }
                    });
                } else {
                    //发评论
                    ActivityUtils.startActivity(activity, WriteStatusActivity.class, new HashMap<String, Object>() {
                        {
                            put(Constants.STATUS_INTENT, GsonUtil.toJson(status));
                            put(Constants.TYPE, Constants.COMMENT_TYPE);
                            put(Constants.TAG, Constants.MAIN_ATY_CODE);
                        }
                    });
                }
                break;

            case Constants.ITEM_FORWARD_TPYE:

                ActivityUtils.startActivity(activity, WriteStatusActivity.class, new HashMap<String, Object>() {
                    {
                        put(Constants.TYPE, Constants.FORWARD_TYPE);
                        put(Constants.STATUS_INTENT, GsonUtil.toJson(status));

                    }
                }, Constants.REQUEST_CODE_WRITE_FORWARD);

                break;
            case Constants.ITEM_FORWARD_STATUS_TYPE:
                ActivityUtils.startActivity(activity, StatusDetailActivity.class, new HashMap<String, Object>() {{
                    put(Constants.STATUS_INTENT, GsonUtil.toJson(status.getStatus()));
                }});
                break;

        }

    }


}
