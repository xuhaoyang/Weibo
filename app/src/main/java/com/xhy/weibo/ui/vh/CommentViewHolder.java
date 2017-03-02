package com.xhy.weibo.ui.vh;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.model.Comment;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.ui.activity.UserInfoActivity;
import com.xhy.weibo.ui.activity.WriteStatusActivity;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.DateUtils;
import com.xhy.weibo.utils.StringUtils;

import java.util.HashMap;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.GsonUtil;
import hk.xhy.android.commom.utils.ScreenUtils;

/**
 * Created by xuhaoyang on 2017/2/25.
 */

public class CommentViewHolder extends ViewHolder {

    private static final String TAG = CommentViewHolder.class.getSimpleName();

    @ViewById(R.id.iv_avatar)
    public ImageView iv_avatar;
    @ViewById(R.id.tv_subhead)
    public TextView tv_subhead;
    @ViewById(R.id.tv_content)
    public TextView tv_content;
    @ViewById(R.id.tv_caption)
    public TextView tv_caption;
    @ViewById(R.id.cv_item)
    public CardView cv_item;

    private boolean animateItems = false;
    private int lastAnimatedPosition = -1;


    public CommentViewHolder(View itemView) {
        super(itemView);
    }


    /**
     * @param context
     * @param model
     * @param listener
     */
    public void bind(final Context context, final Comment model,
                     final OnListItemClickListener listener) {
        if (model != null) {

            runEnterAnimation(this.itemView, getAdapterPosition());

            //设置头像
            if (TextUtils.isEmpty(model.getFace())) {
                iv_avatar.setImageResource(R.drawable.user_avatar);
            } else {
                String url = URLs.AVATAR_IMG_URL + model.getFace();
                Glide.with(iv_avatar.getContext()).load(url).error(R.drawable.user_avatar).
                        fitCenter().into(iv_avatar);
            }

            iv_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startActivityByContext(itemView.getContext(), UserInfoActivity.class
                            , new HashMap<String, Object>() {{
                                put(Constants.USER_ID, model.getUid());
                            }});
                }
            });

            //设置用户名
            tv_subhead.setText(model.getUsername());
            //设置时间
            tv_caption.setText(DateUtils.getShotTime(model.getTime()));
            //设置正文
            tv_content.setText(
                    StringUtils.getWeiboContent(itemView.getContext(),
                            tv_content, model.getContent()));

            cv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(tv_caption, model);
//                    listener.OnListItemClick(getAdapterPosition());
                }
            });
        }
    }

    private void runEnterAnimation(View itemView, int position) {
        animateItems = true;

        /**
         * 动画效果不好 暂时性改成能接受
         */
        if (position <= 8) {
            return;
        }

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

    public void showPopupMenu(View view, final Comment comment) {
        //参数View 是设置当前菜单显示的相对于View组件位置，具体位置系统会处理
        PopupMenu popupMenu = new PopupMenu(itemView.getContext(), view);
        //加载menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_status_detail_comment, popupMenu.getMenu());
        //设置menu中的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_commet_comment:
                        ActivityUtils.startActivityByContext(itemView.getContext(), WriteStatusActivity.class,
                                new HashMap<String, Object>() {{
                                    put(Constants.TYPE, Constants.COMMENT_TYPE);
                                    put(Constants.TAG, Constants.COMMENT_ADPATER_CODE);
                                    put(Constants.COMMENT_INTENT, GsonUtil.toJson(comment));

                                }});
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

}
