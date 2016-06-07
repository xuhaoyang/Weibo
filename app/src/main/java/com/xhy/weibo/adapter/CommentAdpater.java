package com.xhy.weibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.activity.UserInfoActivity;
import com.xhy.weibo.activity.WriteStatusActivity;
import com.xhy.weibo.entity.Comment;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.DisplayUtils;
import com.xhy.weibo.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class CommentAdpater extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    private List<Comment> mComments;
    private int lastAnimatedPosition = -1;

    public void setLastAnimatedPosition(int lastAnimatedPosition) {
        this.lastAnimatedPosition = lastAnimatedPosition;
    }

    public CommentAdpater(Context mContext, List<Comment> comments) {
        this.mContext = mContext;
        this.mComments = comments;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar)
        public ImageView iv_avatar;
        @BindView(R.id.tv_subhead)
        public TextView tv_subhead;
        @BindView(R.id.tv_content)
        public TextView tv_content;
        @BindView(R.id.tv_caption)
        public TextView tv_caption;
        @BindView(R.id.cv_item)
        public CardView cv_item;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            ItemViewHolder root = new ItemViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
            return root;
        } else if (viewType == TYPE_FOOTER) {
            FootViewHolder root = new FootViewHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.item_comment_foot, parent, false));
            return root;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            final Comment comment = mComments.get(position);


            //设置头像
            if (TextUtils.isEmpty(comment.getFace())) {
                viewHolder.iv_avatar.setImageResource(R.drawable.user_avatar);
            } else {
                String url = URLs.AVATAR_IMG_URL + comment.getFace();
                Glide.with(viewHolder.iv_avatar.getContext()).load(url).error(R.drawable.user_avatar).
                        fitCenter().into(viewHolder.iv_avatar);
            }

            viewHolder.iv_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent(mContext, UserInfoActivity.class);
                    data.putExtra(UserInfoActivity.USER_ID, comment.getUid());
                    mContext.startActivity(data);
                }
            });

            //设置用户名
            viewHolder.tv_subhead.setText(comment.getUsername());
            //设置时间
            viewHolder.tv_caption.setText(DateUtils.getShotTime(comment.getTime()));
            //设置正文
            viewHolder.tv_content.setText(
                    StringUtils.getWeiboContent(mContext,
                            viewHolder.tv_content, comment.getContent()));

            viewHolder.cv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(viewHolder.tv_caption, comment);

                }
            });
        }

    }

    private void runEnterAnimation(View itemView, int position) {
//        if (!animateItems || position >= 10) {
//            return;
//        }
        if (position <= 8) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            itemView.setTranslationY(DisplayUtils.getScreenHeightPixels((Activity) mContext) / 2);
            itemView.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(500)
                    .start();

        }
    }

    public void showPopupMenu(View view, final Comment comment) {
        //参数View 是设置当前菜单显示的相对于View组件位置，具体位置系统会处理
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        //加载menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_status_detail_comment, popupMenu.getMenu());
        //设置menu中的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_commet_comment:
                        Intent data = new Intent(mContext, WriteStatusActivity.class);
                        //评论方式
                        data.putExtra(WriteStatusActivity.TYPE, WriteStatusActivity.COMMENT_TYPE);
                        data.putExtra(WriteStatusActivity.TAG, WriteStatusActivity.COMMENT_ADPATER_CODE);
                        data.putExtra(WriteStatusActivity.COMMENT_INTENT, comment);
                        mContext.startActivity(data);
                        break;
//                    case R.id.action_commet_forward:
//                        break;
                }
                return true;
            }
        });
        //设置popupWindow消失的点击事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });

        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mComments.size() + 1;
        //mComments.size() == 0 ? 0 :
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

}
