package com.xhy.weibo.ui.vh;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.ui.activity.StatusDetailActivity;
import com.xhy.weibo.ui.activity.WriteStatusActivity;
import com.xhy.weibo.ui.interfaces.PushMessage;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.Logger;
import com.xhy.weibo.utils.StringUtils;

import java.util.HashMap;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.utils.ScreenUtils;
import hk.xhy.android.commom.utils.ToastUtils;

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

        Log.e(TAG, ">>>" + GsonUtil.toJson(model));

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

            tv_subhead.setText(model.getUsername());
            tv_caption.setText(DateUtils.getShotTime(model.getTime()));

            //显示头像
            if (TextUtils.isEmpty(model.getFace())) {
                iv_avatar.setImageResource(R.drawable.user_avatar);
            } else {
                String url = URLs.AVATAR_IMG_URL + model.getFace();
                Glide.with(iv_avatar.getContext()).load(url).
                        fitCenter().into(iv_avatar);
            }


            /**
             * //头像跳转
             viewHolder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
            Intent data = new Intent(context, UserInfoActivity.class);
            data.putExtra(UserInfoActivity.USER_ID, status.getUid());
            context.startActivity(data);
            }
            });
             */

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
            if (!TextUtils.isEmpty(model.getMedium())) {

                String url = URLs.PIC_URL + model.getMedium();
                setImage(iv_image, url);
                include_status_image.setVisibility(View.VISIBLE);
                iv_image.setVisibility(View.VISIBLE);
                final float scale = include_status_image.getResources().getDisplayMetrics().density;
                int sixteen = (int) (scale * 16);
                int ten = (int) (scale * 10);
                include_status_image.setPadding(sixteen, ten, sixteen, 0);
                /**
                 iv_image.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                Intent intent = new Intent(context, ViewPicActivity.class);
                intent.putExtra(ViewPicActivity.PIC_URL, URLs.PIC_URL + model.getMax());
                context.startActivity(intent);
                }
                });*/

            } else {
                include_status_image.setVisibility(View.GONE);
                iv_image.setVisibility(View.GONE);
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

            /**
             * like type:0
             */
            ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemOtherClick(getAdapterPosition(), Constants.ITEM_LIKE_TPYE);
                    }
                }
            });

            /**
             * commet type:0
             */
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
            itemView.setTranslationY(ScreenUtils.getScreenHeight(itemView.getContext()) / 2);
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


        }

    }


}
