package com.xhy.weibo.ui.vh;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.xhy.weibo.R;
import com.xhy.weibo.model.Hot;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;

/**
 * Created by xuhaoyang on 16/9/8.
 */
public class HotViewHolder extends ViewHolder {

    @ViewById(R.id.tv_nav)
    public TextView tv_nav;
    @ViewById(R.id.cv_item)
    CardView cardView;

    public HotViewHolder(View itemView) {
        super(itemView);
    }


    public void bind(Hot hotModel, final OnListItemClickListener listener){
        if (hotModel!=null){
            tv_nav.setText(hotModel.getKeyword());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        listener.OnListItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }


}
