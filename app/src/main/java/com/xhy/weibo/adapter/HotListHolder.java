package com.xhy.weibo.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xhy.weibo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/8/25.
 */
public class HotListHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_nav)
    public TextView tv_nav;
    @BindView(R.id.cv_item)
    public CardView cardView;


    public HotListHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
