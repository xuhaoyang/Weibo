package com.xhy.weibo.ui.vh;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xhy.weibo.R;
import com.xhy.weibo.model.Setting;

import org.w3c.dom.Text;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.ViewHolder;

/**
 * Created by xuhaoyang on 2017/3/8.
 */

public class SettingHeadViewHolder extends ViewHolder {

    @ViewById(R.id.item)
    TextView itemLayout;

    public SettingHeadViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(final Context context, final Setting item) {
        if (item != null) {
            itemLayout.setText(item.getMainHead());
        }

    }
}
