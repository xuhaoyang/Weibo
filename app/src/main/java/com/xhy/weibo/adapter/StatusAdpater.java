package com.xhy.weibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xhy.weibo.activity.KeepStatusActivity;
import com.xhy.weibo.activity.MainActivity;
import com.xhy.weibo.activity.StatusDetailActivity;
import com.xhy.weibo.activity.UserInfoActivity;
import com.xhy.weibo.activity.ViewPicActivity;
import com.xhy.weibo.activity.WriteStatusActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.DisplayUtils;
import com.xhy.weibo.utils.Logger;
import com.xhy.weibo.utils.StringUtils;
import com.xhy.weibo.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/12.
 */
public class StatusAdpater extends RecyclerView.Adapter {

    private static final String TAG = StatusAdpater.class.getSimpleName();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<Status> statuses;
    private Context context;
    private Handler mHandler;
    private boolean animateItems = false;
    private int lastAnimatedPosition = -1;

    public void setLastAnimatedPosition(int lastAnimatedPosition) {
        this.lastAnimatedPosition = lastAnimatedPosition;
    }

    public StatusAdpater(List<Status> statuses, Context context, Handler mHandler) {
        this.statuses = statuses;
        this.context = context;
        this.mHandler = mHandler;
    }

    public StatusAdpater(List<Status> statuses, Context context) {
        this.statuses = statuses;
        this.context = context;
    }


    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_content)
        public LinearLayout ll_content;

        //include_avatar
        @BindView(R.id.iv_avatar)
        public ImageView iv_avatar;
        @BindView(R.id.right_content)
        public RelativeLayout right_content;
        @BindView(R.id.tv_subhead)
        public TextView tv_subhead;
        @BindView(R.id.tv_caption)
        public TextView tv_caption;

        //主要的微博文字内容
        @BindView(R.id.tv_content)
        public TextView tv_content;
        //图片显示
        @BindView(R.id.include_status_image)
        public FrameLayout include_status_image;
        @BindView(R.id.iv_image)
        public ImageView iv_image;

        //转发内容
        @BindView(R.id.include_forward_status)
        public LinearLayout include_forward_status;
        @BindView(R.id.tv_retweeted_content)
        public TextView tv_retweeted_content;
        @BindView(R.id.include_status_image_forward)
        public FrameLayout include_status_image_forward;
        @BindView(R.id.iv_image_forward)
        public ImageView iv_image_forward;

        //按钮
        @BindView(R.id.ll_like)
        public LinearLayout ll_like;
        @BindView(R.id.ll_comment)
        public LinearLayout ll_comment;
        @BindView(R.id.ll_forward)
        public LinearLayout ll_forward;

        //收藏
        @BindView(R.id.iv_ic_like)
        public ImageView iv_ic_like;
        @BindView(R.id.tv_like_count)
        public TextView tv_like_count;
        //评论
        @BindView(R.id.iv_ic_comment)
        public ImageView iv_ic_comment;
        @BindView(R.id.tv_comment)
        public TextView tv_comment;
        //转发
        @BindView(R.id.iv_ic_forward)
        public ImageView iv_ic_forward;
        @BindView(R.id.tv_forward)
        public TextView tv_forward;


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            ItemViewHolder root = new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false));
            return root;
        } else if (viewType == TYPE_FOOTER) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent,false);
            FootViewHolder root = new FootViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foot, parent, false));
            return root;
        }
        return null;
//        ItemViewHolder root = new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false));
//        return root;
    }


    public StatusAdpater(List<Status> statuses) {
        this.statuses = statuses;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            final Status status = statuses.get(position);

            //动画
            runEnterAnimation(holder.itemView, position);

            //初始化
            viewHolder.include_status_image_forward.setVisibility(View.GONE);
            viewHolder.iv_image_forward.setVisibility(View.GONE);
            viewHolder.iv_image.setVisibility(View.GONE);
            viewHolder.include_status_image.setVisibility(View.GONE);

            //设置按钮数字
            viewHolder.tv_comment.setText(status.getComment() + "");
            viewHolder.tv_like_count.setText(status.getKeep() + "");
            viewHolder.tv_forward.setText(status.getTurn() + "");
            boolean flag = status.isKeep();
            if (status.isKeep()) {
                viewHolder.iv_ic_like.setImageResource(R.drawable.ic_like_full);
            } else {
                viewHolder.iv_ic_like.setImageResource(R.drawable.ic_like_empty);
            }


            viewHolder.tv_subhead.setText(status.getUsername());
            viewHolder.tv_caption.setText(DateUtils.getShotTime(status.getTime()));
            if (TextUtils.isEmpty(status.getFace())) {
                viewHolder.iv_avatar.setImageResource(R.drawable.user_avatar);
            } else {
                String url = URLs.AVATAR_IMG_URL + status.getFace();
                Glide.with(viewHolder.iv_avatar.getContext()).load(url).
                        fitCenter().into(viewHolder.iv_avatar);
            }
            viewHolder.iv_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent(context, UserInfoActivity.class);
                    data.putExtra(UserInfoActivity.USER_ID, status.getUid());
                    context.startActivity(data);
                }
            });

            //微博正文
            viewHolder.tv_content.setText(StringUtils.getWeiboContent(context, viewHolder.tv_content, status.getContent()));


            //带图片的微博

            if (!TextUtils.isEmpty(status.getMedium())) {

                String url = URLs.PIC_URL + status.getMedium();
                setImage(viewHolder.iv_image, url);
                viewHolder.include_status_image.setVisibility(View.VISIBLE);
                viewHolder.iv_image.setVisibility(View.VISIBLE);
                final float scale = viewHolder.include_status_image.getResources().getDisplayMetrics().density;
                int sixteen = (int) (scale * 16);
                int ten = (int) (scale * 10);
                viewHolder.include_status_image.setPadding(sixteen, ten, sixteen, 0);
                viewHolder.iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ViewPicActivity.class);
                        intent.putExtra(ViewPicActivity.PIC_URL, URLs.PIC_URL + status.getMax());
                        context.startActivity(intent);
                    }
                });

            } else {
                viewHolder.include_status_image.setVisibility(View.GONE);
                viewHolder.iv_image.setVisibility(View.GONE);
            }

            //显示转发内容
            final Status forward_status = status.getStatus();
            if (forward_status != null) {
                viewHolder.tv_retweeted_content
                        .setText(StringUtils.getWeiboContent(context, viewHolder.tv_retweeted_content, "@" + forward_status.getUsername() + ": " + forward_status.getContent()));
                viewHolder.include_forward_status.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(forward_status.getMedium())) {
                    viewHolder.include_status_image_forward.setVisibility(View.VISIBLE);
//                    final float scale = viewHolder.include_status_image_forward.getResources().getDisplayMetrics().density;
//                    int sixteen = (int) (scale * 16);
//                    int ten = (int) (scale * 10);
//                    viewHolder.include_status_image_forward.setPadding(sixteen, 0, sixteen, sixteen);
                    viewHolder.iv_image_forward.setVisibility(View.VISIBLE);
                    String url = URLs.PIC_URL + forward_status.getMedium();
                    setImage(viewHolder.iv_image_forward, url);
                    viewHolder.iv_image_forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ViewPicActivity.class);
                            intent.putExtra(ViewPicActivity.PIC_URL, URLs.PIC_URL + forward_status.getMax());
                            context.startActivity(intent);
                        }
                    });
                } else {
                    viewHolder.include_status_image_forward.setVisibility(View.GONE);
                    viewHolder.iv_image_forward.setVisibility(View.GONE);
                }
            } else {
                viewHolder.include_forward_status.setVisibility(View.GONE);
                viewHolder.iv_image_forward.setVisibility(View.GONE);
            }

            //转发微博被删除
            if (status.getIsturn() == -1) {
                viewHolder.tv_retweeted_content
                        .setText("该微博已被删除");
                viewHolder.include_forward_status.setVisibility(View.VISIBLE);
            }


            //点击事件
            viewHolder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra(StatusDetailActivity.STATUS_INTENT, status);
                    context.startActivity(intent);
                }
            });

            viewHolder.include_forward_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra(StatusDetailActivity.STATUS_INTENT, status.getStatus());
                    context.startActivity(intent);
                }
            });

            viewHolder.ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (status.isKeep()) {
                        StatusLogic.delKeepStatus(context, AppConfig.getUserId(), status.getId(),
                                AppConfig.ACCESS_TOKEN.getToken(), new StatusLogic.DelKeepStatusCallBack() {
                                    @Override
                                    public void onDelKeepSuccess(Result result) {
                                        status.setKeep(false);
                                        status.setKeep(status.getKeep() - 1);
                                        notifyItemChanged(position);
                                        ToastUtils.showToast(context, result.getMsg(), Toast.LENGTH_SHORT);
                                        if (mHandler != null) {
                                            Message message = new Message();
                                            message.what = KeepStatusActivity.REFRESH_DATA;
                                            mHandler.sendMessage(message);
                                        }
                                    }

                                    @Override
                                    public void onDelKeepFailure(String message) {

                                        ToastUtils.showToast(context, message, Toast.LENGTH_SHORT);

                                    }

                                    @Override
                                    public void onDelKeepError(Throwable t) {
                                        Logger.show(TAG, t.getMessage(), Log.ERROR);
                                    }
                                });
                    } else {
                        StatusLogic.addKeepStatus(context, AppConfig.getUserId(), status.getId(),
                                AppConfig.ACCESS_TOKEN.getToken(), new StatusLogic.AddKeepStatusCallBack() {
                                    @Override
                                    public void onAddKeepSuccess(Result result) {
                                        status.setKeep(true);
                                        status.setKeep(status.getKeep() + 1);
                                        notifyItemChanged(position);
                                        ToastUtils.showToast(context, result.getMsg(), Toast.LENGTH_SHORT);
                                    }

                                    @Override
                                    public void onAddKeepFailure(String message) {
                                        ToastUtils.showToast(context, message, Toast.LENGTH_SHORT);
                                    }

                                    @Override
                                    public void onAddKeepError(Throwable t) {
                                        Logger.show(TAG, t.getMessage(), Log.ERROR);
                                    }
                                });
                    }

                }
            });

            viewHolder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast(context, "这是评论", Toast.LENGTH_SHORT);

                    if (status.getComment() > 0) {
                        Intent intent = new Intent(context, StatusDetailActivity.class);
                        intent.putExtra(StatusDetailActivity.STATUS_INTENT, status);
                        context.startActivity(intent);
                    } else {
                        //发评论
                        Intent intent = new Intent(context, WriteStatusActivity.class);
                        intent.putExtra(WriteStatusActivity.TYPE, WriteStatusActivity.COMMENT_TYPE);
                        intent.putExtra(WriteStatusActivity.TAG, WriteStatusActivity.MAIN_ATY_CODE);
                        intent.putExtra(WriteStatusActivity.STATUS_INTENT, status);
                        context.startActivity(intent);
                    }
                }
            });

            viewHolder.ll_forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    ToastUtils.showToast(context, "这是转发", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(context, WriteStatusActivity.class);
                    intent.putExtra(WriteStatusActivity.TYPE, WriteStatusActivity.FORWARD_TYPE);
                    intent.putExtra(WriteStatusActivity.STATUS_INTENT, status);
                    ((MainActivity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_WRITE_FORWARD);
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
            itemView.setTranslationY(DisplayUtils.getScreenHeightPixels((Activity) context) / 2);
            itemView.animate()
                    .translationY(0)
//                    .setStartDelay(100 * position)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(500)
                    .start();

        }
    }

    /**
     * @param statuses
     * @param type     1完全更新数据,2包含数据
     */
    public void updateItems(List<Status> statuses, int type) {
        switch (type) {
            case 1:
                lastAnimatedPosition = -1;
                this.statuses.clear();
                statuses.addAll(statuses);
                notifyDataSetChanged();
                break;
            case 2:
                for (Status s : statuses) {
                    if (!this.statuses.contains(s)) {
                        this.statuses.add(s);
                    }
                }
                notifyDataSetChanged();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size() == 0 ? 0 : statuses.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    public void setImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).thumbnail(0.2f).fitCenter().into(view);
    }
}
