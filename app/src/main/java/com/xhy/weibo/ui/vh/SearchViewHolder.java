package com.xhy.weibo.ui.vh;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.model.User;
import com.xhy.weibo.api.URLs;

import hk.xhy.android.common.bind.ViewById;
import hk.xhy.android.common.ui.vh.OnListItemClickListener;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.utils.GsonUtil;

/**
 * Created by xuhaoyang on 2016/12/24.
 */
public class SearchViewHolder extends ViewHolder {


    private static final String TAG = SearchViewHolder.class.getSimpleName();

    @ViewById(R.id.iv_avatar)
    public ImageView iv_avatar;
    @ViewById(R.id.tv_subhead)
    public TextView tv_subhead;
    @ViewById(R.id.cv_item)
    public CardView cv_item;


    public SearchViewHolder(View itemView) {
        super(itemView);
    }


    public void bind(final User user, final OnListItemClickListener listener) {
        Log.e(TAG, ">>>" + GsonUtil.toJson(user));

        if (TextUtils.isEmpty(user.getFace())) {
            iv_avatar.setImageResource(R.mipmap.ic_launcher);
        } else {
            String url = URLs.AVATAR_IMG_URL + user.getFace();
            Glide.with(iv_avatar.getContext()).load(url).
                    fitCenter().into(iv_avatar);
        }

        //设置用户名
        tv_subhead.setText(user.getUsername());
        cv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnListItemClick(getAdapterPosition());
                }
            }
        });

    }
}
