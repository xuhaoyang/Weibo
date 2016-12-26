package com.xhy.weibo.ui.vh;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.model.Model;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.ui.activity.UserInfoActivity;
import com.xhy.weibo.ui.activity.ViewPicActivity;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.StringUtils;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.GsonUtil;

import static android.R.attr.data;

/**
 * Created by xuhaoyang on 16/9/12.
 */
public class KeepStatusViewHolder extends ViewHolder {

    private static final String TAG = KeepStatusViewHolder.class.getSimpleName();
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

    public KeepStatusViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * @param model
     * @param listener
     */
    public void bind(final Context context, final Status model, final OnListItemClickListener listener) {

        Log.e(TAG, ">>>" + GsonUtil.toJson(model));


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

    /**
     * 加载缩略图
     *
     * @param view
     * @param url
     */
    private void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).thumbnail(0.2f).fitCenter().into(view);
    }


}
